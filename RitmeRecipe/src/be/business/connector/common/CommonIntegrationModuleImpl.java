package be.business.connector.common;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.ProviderException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import be.apb.gfddpp.common.exceptions.GFDDPPException;
import be.apb.gfddpp.common.utils.JaxContextCentralizer;
import be.apb.gfddpp.domain.PharmacyIdType;
import be.apb.gfddpp.productfilter.ProductFilter;
import be.apb.gfddpp.systemservices.v2.SystemServices;
import be.apb.standards.gfddpp.request.configuration.GetConfigurationRequestParameters;
import be.business.connector.core.cache.SAMLTokenCache;
import be.business.connector.core.domain.CareProviderType;
import be.business.connector.core.ehealth.services.SessionService;
import be.business.connector.core.ehealth.services.SessionServiceImpl;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.technical.connector.utils.Crypto;
import be.business.connector.core.utils.ConfigUtils;
import be.business.connector.core.utils.ETKHelper;
import be.business.connector.core.utils.EncryptionUtils;
import be.business.connector.core.utils.Exceptionutils;
import be.business.connector.core.utils.I18nHelper;
import be.business.connector.core.utils.IOUtils;
import be.business.connector.core.utils.MessageDumper;
import be.business.connector.core.utils.PropertyHandler;
import be.business.connector.core.utils.SAML10Converter;
import be.business.connector.core.utils.STSHelper;
import be.business.connector.projects.common.services.tipsystem.TipSystemServiceImpl;
import be.business.connector.projects.common.utils.ProductFilterSingleton;
import be.business.connector.projects.common.utils.SystemServicesUtils;
import be.ehealth.apb.gfddpp.services.tipsystem.RoutedSealedRequestType;
import be.ehealth.apb.gfddpp.services.tipsystem.RoutedSealedResponseType;
import be.ehealth.technicalconnector.beid.BeIDInfo;
import be.ehealth.technicalconnector.config.ConfigFactory;
import be.fgov.ehealth.etee.crypto.encrypt.EncryptionToken;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommonIntegrationModuleImpl implements CommonIntegrationModule {

    private static final Logger LOG = LoggerFactory.getLogger(CommonIntegrationModuleImpl.class);

    private SessionService sessionService = SessionServiceImpl.getInstance();
    private SAMLTokenCache samlTokenCache = SAMLTokenCache.getInstance();

    private Map<String,EncryptionUtils> encryptionUtils = new HashMap<String,EncryptionUtils>();
    private Map<String,ETKHelper> etkHelper = new HashMap<String,ETKHelper>();

    private PropertyHandler propertyHandler;
    private String niss = "";

    private JaxContextCentralizer jaxContextCentralizer;

    public CommonIntegrationModuleImpl(String propertyfile) throws IntegrationModuleException {
        LOG.info("Constructor CommonIntegrationModuleImpl with [" + propertyfile + "].");
		// When running in DOTNET, the current context class loader must be
        // overriden to avoid class not found exceptions!!!
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        System.setProperty("javax.xml.soap.SOAPFactory", "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl");

//		if (propertyHandler == null)
        propertyHandler = new PropertyHandler(propertyfile);

        init();
    }

    public CommonIntegrationModuleImpl(String propertyfile, String urlConf) throws IntegrationModuleException {
        LOG.info("Constructor CommonIntegrationModuleImpl with [" + propertyfile + "] en [" + urlConf + "].");
        // When running in DOTNET, the current context class loader must be
        // overriden to avoid class not found exceptions!!!
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        System.setProperty("javax.xml.soap.SOAPFactory", "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl");

//		if (propertyHandler == null)
        propertyHandler = new PropertyHandler(propertyfile, urlConf);

        init();
    }

    private void init() throws IntegrationModuleException {
        MessageDumper.getInstance().init(propertyHandler);

        jaxContextCentralizer = JaxContextCentralizer.getInstance();

        if (propertyHandler.hasProperty("care.provider.type") && propertyHandler.getProperty("care.provider.type").toUpperCase().equals(CareProviderType.PHARMACIST.toString())) {
            ProductFilterSingleton.getInstance(propertyHandler);
        }

    }

    public CommonIntegrationModuleImpl() {
        super();
        LOG.info("Constructor CommonIntegrationModuleImpl without Args");
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        System.setProperty("javax.xml.soap.SOAPFactory", "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl");
        propertyHandler = null;
    }

    @Override
    public void unloadSession(final String nihiiOrg) throws IntegrationModuleException {
        sessionService.unloadSession(nihiiOrg);
        samlTokenCache.clear(nihiiOrg);
    }

    @Override
    public String createMandatePhysicalPerson(final String nihiiOrg) throws IntegrationModuleException {
        try {
            this.setNiss("");
            // Shall we use the EID to identify the user ? if the property user
            // (inami) is defined, the EID is not used.
            if (getPropertyHandler().hasProperty("user")) {
                setNiss(getPropertyHandler().getProperty("user"));
                ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty("user.inss", getNiss());
            } else {
                setNiss(BeIDInfo.getInstance("test").getIdentity().getNationalNumber());
                ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty("user.inss", getNiss());
            }

            //initDataUnSealer();
            this.getPropertyHandler().setDefaultSessionProperties(this.getNiss());
            return sessionService.createMandateSession(new String(getEncryptionUtils(nihiiOrg).getSystemKeystorePassword(null)), new String(getEncryptionUtils().get(nihiiOrg).getSystemKeystorePassword(null)), nihiiOrg);
        } catch (Throwable t) {
            if (t.getMessage().contains("Message Expires") || t.getMessage().contains("Message Created time past")) {
                LOG.error("Create Session Failed", t);
                throw new IntegrationModuleException(I18nHelper.getLabel("error.date"), t);
            } else if (t instanceof ProviderException) {
                LOG.error("Create Session Failed", t);
                throw new IntegrationModuleException(I18nHelper.getLabel("error.eid.read"), t);
            }
            Exceptionutils.errorHandler(t);
        }
        return null;
    }

    @Override
    public void setProperty(String key, String value) {
        propertyHandler.setProperty(key, value);
        ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty(key, value);
    }

    @Override
    public String createSession(final String nihiiOrg) throws IntegrationModuleException {
        String samlTokenXml = null;
        try {
            LOG.info("******************* Creating new session blabla************************");
            setNiss("");
            validateSystemCertificate(nihiiOrg);

			// Shall we use the EID to identify the user ? if the property user
            // (inami) is defined, the EID is not used.
            if (propertyHandler.hasProperty("user")) {
                setNiss(propertyHandler.getProperty("user"));
                ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty("user.inss", getNiss());
            } else {
                setNiss(BeIDInfo.getInstance("test").getIdentity().getNationalNumber());
                ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty("user.inss", getNiss());
            }
            this.propertyHandler.setDefaultSessionProperties(getNiss());

            samlTokenXml = sessionService.createSession(new String(getEncryptionUtils(nihiiOrg).getSystemKeystorePassword(null)), new String(getEncryptionUtils().get(nihiiOrg).getSystemKeystorePassword(null)), nihiiOrg);

            if (propertyHandler.hasProperty("care.provider.type") && propertyHandler.getProperty("care.provider.type").toUpperCase().equals(CareProviderType.PHARMACIST.toString())) {
                LOG.info("*********************** Init product Filter ******************* ");
                getLatestProductFilter(nihiiOrg);

                LOG.info("*********************** Init latest System Services **********************");
                getLatestSystemServices(nihiiOrg);
            }
            if (this.hasValidSession(nihiiOrg)) {
                LOG.info("*************** Session created ******************");
            } else {
                LOG.info("*************** Session creation failure ***************");
            }
            unlockLockedFilesOnQueue();
            return samlTokenXml;
        } catch (Throwable t) {
            LOG.error("Session creation Failed", t);
            t.printStackTrace();
            if (t.getMessage().contains("Message Expires") || t.getMessage().contains("Message Created time past")) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.date"), t);
            } else if (StringUtils.contains(t.getMessage().toLowerCase(), "EID is not present".toLowerCase())) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.eid.read"), t);
            }
            Exceptionutils.errorHandler(t);

        }

        return null;
    }

    private void unlockLockedFilesOnQueue() {
        if (propertyHandler.hasProperty("MESSAGE_QUEUE_FOLDER")) {
            String messageQueueFolderPath = propertyHandler.getProperty("MESSAGE_QUEUE_FOLDER");
            File messageQueueFolder = new File(messageQueueFolderPath);
            if (messageQueueFolder.exists()) {
                String lockedFileSuffix = "_LOCK";
                SuffixFileFilter suffixFileFilter = new SuffixFileFilter(lockedFileSuffix);

                Integer numberOfMinutes = propertyHandler.getIntegerProperty("locked.file.retention", "2");

                AgeFileFilter ageFileFilter = new AgeFileFilter(System.currentTimeMillis() - (numberOfMinutes * 60 * 1000));

                Collection<File> lockedFiles = FileUtils.listFiles(messageQueueFolder, FileFilterUtils.and(suffixFileFilter, ageFileFilter), TrueFileFilter.INSTANCE);
                for (File file : lockedFiles) {
                    String lockedFileName = file.getAbsolutePath();
                    File unlockedFile = new File(StringUtils.remove(lockedFileName, lockedFileSuffix));
                    file.setLastModified(new Date().getTime());
                    Boolean succesFullyUnlocked = file.renameTo(unlockedFile);
                    if (succesFullyUnlocked) {
                        LOG.info("File: " + lockedFileName + " successfully unlocked.");
                    }
                }
            } else {
                LOG.info("No directory found on location: " + messageQueueFolderPath + ". No files unlocked");
            }
        } else {
            LOG.info("No MESSAGE_QUEUE_FOLDER property in properties file. No files unlocked.");
        }
    }

    private void validateSystemCertificate(final String nihiiOrg) throws IntegrationModuleException {
        X509Certificate certificate = getEncryptionUtils(nihiiOrg).getCertificate();
        if (certificate != null) {
            try {
                certificate.checkValidity();
            } catch (CertificateExpiredException e) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.expired.system.certificate"), e);
            } catch (CertificateNotYetValidException e) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.invalid.system.certificate"), e);
            }
        } else {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.notfound.system.certificate"));
        }

    }

    @Override
    public String createFallbackSession(final String niss, final String passphrase, final String nihiiOrg) throws IntegrationModuleException {
        String samlTokenXml = null;
        try {
            // Test only ====================
            if (propertyHandler != null && propertyHandler.hasProperty("test.session.path") && propertyHandler.hasProperty("test.session.type")) {
                File extSession = new File(new File(propertyHandler.getProperty("test.session.path")), "session-" + propertyHandler.getProperty("test.session.type") + ".xml");
                if (extSession.exists()) {
                    LOG.info("Loading " + extSession.getCanonicalPath());
                    String session = new String(IOUtils.loadResource(extSession.getCanonicalPath()), "UTF-8");
                    String systemKeystorePath = propertyHandler.getProperty(EncryptionUtils.PROP_KEYSTORE_FILE);
                    sessionService.loadSession(session, systemKeystorePath, getEncryptionUtils(nihiiOrg).getSystemKeystorePassword(null), EncryptionUtils.AUTHENTICATION_ALIAS, getEncryptionUtils(nihiiOrg).getSystemKeystorePassword(null), nihiiOrg);
                    return session;
                } // ============================
            }
            LOG.info("******************* Creating new Fallback session ************************");
            setNiss(niss);
            ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty("user.inss", getNiss());
            validateSystemCertificate(nihiiOrg);

            samlTokenXml = sessionService.createFallbackSession(passphrase, new String(getEncryptionUtils(nihiiOrg).getSystemKeystorePassword(null)), new String(getEncryptionUtils(nihiiOrg).getSystemKeystorePassword(null)), nihiiOrg);

            SAMLTokenCache.getInstance().setSamlToken(SAML10Converter.toElement(samlTokenXml), nihiiOrg);
            if (propertyHandler != null && propertyHandler.hasProperty("care.provider.type") && propertyHandler.getProperty("care.provider.type").toUpperCase().equals(CareProviderType.PHARMACIST.toString())) {
                LOG.info("*********************** Init product Filter ******************* ");
                getLatestProductFilter(nihiiOrg);
                LOG.debug("*********************** Init latest System Services **********************");
                getLatestSystemServices(nihiiOrg);
            }
            if (this.hasValidSession(nihiiOrg)) {
                LOG.info("Fallback Session created");
            } else {
                LOG.info("Fallback Session creation failure");
            }

            LOG.info("******************* New Fallback session created ************************");
            unlockLockedFilesOnQueue();
            return samlTokenXml;

        } catch (Throwable t) {
            t.printStackTrace();
            LOG.error("Create Fallback Session Failed", t);
            Exceptionutils.errorHandler(t);
        }
        return null;
    }

    @Profiled(logFailuresSeparately = true, tag = "CommonIntegrationModule#getLatestProductFilter", logger = "org.perf4j.TimingLogger_Executor")
    public void getLatestProductFilter(final String nihiiOrg) throws IntegrationModuleException {
        // Get property data
        LOG.info("Loading latest productFilter");
        String productFilterPath;
        if (propertyHandler.hasProperty("PRODUCT_FILTER_PATH")) {
            productFilterPath = propertyHandler.getProperty("PRODUCT_FILTER_PATH");
        } else {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.get.product.filter.failed"));
        }

        String tipSystemId = null;
        if (getPropertyHandler().hasProperty("tipsystem.id")) {
            tipSystemId = getPropertyHandler().getProperty("tipsystem.id");
        } else {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.tipsystem.id"));
        }

        // Check what the current local latest version is
        XMLGregorianCalendar latestVersion = ConfigUtils.getLatestProductFilterVersion(productFilterPath);

        // Create request
        GetConfigurationRequestParameters productFilterReq = new GetConfigurationRequestParameters();
        productFilterReq.setVersion(latestVersion);
        productFilterReq.setNihiiPharmacyNumber(getRequestorIdInformation(nihiiOrg));
        String productFilterReqXml;
        try {
            productFilterReqXml = getJaxContextCentralizer().toXml(GetConfigurationRequestParameters.class, productFilterReq);// getConfigMarhsallerHelper.marsh(productFilterReq);
        } catch (GFDDPPException e) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.get.product.filter.failed"), e);
        }

        System.out.println("PRODUCTFILTER: " + productFilterReqXml);

        byte[] productFilterReqXmlSealed;

        EncryptionToken etk = getEtkHelper(nihiiOrg).getTIPSystem_ETK(tipSystemId).get(0);
        productFilterReqXmlSealed = Crypto.seal(etk, productFilterReqXml.getBytes(), nihiiOrg);

        RoutedSealedRequestType productFilterSealedReq = new RoutedSealedRequestType();
        productFilterSealedReq.setRequestParametersSealed(productFilterReqXmlSealed);

        byte[] xml = null;
        // Perform the call to TIP web service and decrypt response
        RoutedSealedResponseType routedSealedResponseType;
        try {
            LOG.debug("************* Retrieve latest version of the product filter configuration ********************* ");
            routedSealedResponseType = TipSystemServiceImpl.getInstance().getProductFilter(productFilterSealedReq, nihiiOrg);
            LOG.debug("************* Latest version retrieved of the product filter configuration ********************* ");
            xml = routedSealedResponseType.getSingleMessageSealed();

        } catch (Exception e) {

            e.printStackTrace();
            LOG.error(I18nHelper.getLabel("error.get.product.filter.failed"), e);
			// throw new
            // IntegrationModuleException(I18nHelper.getLabel("error.get.product.filter.failed"));
        }

        if (xml != null && xml.length > 0) {
            String xmlDecrypted = new String(Crypto.unseal(xml, nihiiOrg));

            ProductFilter productFilter;
            try {
                productFilter = getJaxContextCentralizer().toObject(ProductFilter.class, xmlDecrypted);
            } catch (GFDDPPException e) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.get.product.filter.failed"));
            }
            XMLGregorianCalendar currentVersion = productFilter.getVersion();

            File newFile = new File(productFilterPath + "/productfilter_v" + currentVersion.toXMLFormat().replace(':', '-') + ".xml");
            try {
                FileUtils.writeByteArrayToFile(newFile, xmlDecrypted.getBytes());
            } catch (IOException e) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.get.product.filter.failed"));
            }
            XMLGregorianCalendar newCal = ConfigUtils.getProductFilterVersionFromFile(newFile);

            if (latestVersion != null && newCal.toGregorianCalendar().after(latestVersion)) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.productfilter.version", new Object[]{newFile.getAbsolutePath()}));
            }

        } else {
            LOG.debug("Received null payload from product filter.");
        }

        ProductFilterSingleton pfs = ProductFilterSingleton.getInstance();
        pfs.setProductFilterXmlFile(ConfigUtils.getLatestProductFilterFile(productFilterPath));
        pfs.reloadCache();
        LOG.info("productFilter loaded");
    }

    @Profiled(logFailuresSeparately = true, tag = "CommonIntegrationModule#getLatestSystemServices", logger = "org.perf4j.TimingLogger_Executor")
    public void getLatestSystemServices(final String nihiiOrg) throws IntegrationModuleException {
        // Get property data
        LOG.info("Loading systemServices");
        String systemServicesPath;
        if (propertyHandler.hasProperty("SYSTEM_SERVICES_PATH")) {
            systemServicesPath = propertyHandler.getProperty("SYSTEM_SERVICES_PATH");
        } else {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.get.system.services.failed"));
        }

        String tipSystemId = null;
        if (getPropertyHandler().hasProperty("tipsystem.id")) {
            tipSystemId = getPropertyHandler().getProperty("tipsystem.id");
        } else {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.tipsystem.id"));
        }

        // Check what the current local latest version is
        XMLGregorianCalendar latestVersion = ConfigUtils.getLatestSystemServicesVersion(systemServicesPath, propertyHandler);
        // Create request
        GetConfigurationRequestParameters systemServicesReq = new GetConfigurationRequestParameters();
        systemServicesReq.setVersion(latestVersion);
        systemServicesReq.setNihiiPharmacyNumber(getRequestorIdInformation(nihiiOrg));
        // MarshallerHelper<Object, GetConfigurationRequestParameters> getConfigMarhsallerHelper = new MarshallerHelper<Object, GetConfigurationRequestParameters>(Object.class, GetConfigurationRequestParameters.class);
        String systemServicesReqXml;
        try {
            systemServicesReqXml = getJaxContextCentralizer().toXml(GetConfigurationRequestParameters.class, systemServicesReq);// getConfigMarhsallerHelper.marsh(systemServicesReq);
        } catch (GFDDPPException e) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.get.system.services.failed"), e);
        }

        System.out.println("CM UNSEALED OUTGOING MESSAGE: " + systemServicesReqXml);

        byte[] systemServicesReqXmlSealed;

        EncryptionToken etk = getEtkHelper(nihiiOrg).getTIPSystem_ETK(tipSystemId).get(0);
        systemServicesReqXmlSealed = Crypto.seal(etk, systemServicesReqXml.getBytes(), nihiiOrg);

        RoutedSealedRequestType systemServicesSealedReq = new RoutedSealedRequestType();
        systemServicesSealedReq.setRequestParametersSealed(systemServicesReqXmlSealed);

        // Perform the call to TIP web service and decrypt response
        RoutedSealedResponseType routedSealedResponseType;

		// StringWriter wr = new StringWriter();
        // javax.xml.bind.JAXB.marshal(systemServicesSealedReq, wr);
        // System.out.println("CM OUTGOING MESSAGE: " + wr.toString());
        byte[] xml = null;
        try {
            LOG.debug("************* Retrieve latest version of the system services configuration ********************* ");
            routedSealedResponseType = TipSystemServiceImpl.getInstance().getSystemServices(systemServicesSealedReq, nihiiOrg);
            LOG.debug("************* Latest version of the system services configuration retrieved ********************* ");
            xml = routedSealedResponseType.getSingleMessageSealed();
        } catch (Exception e) {
            e.printStackTrace();
			// throw new
            // IntegrationModuleException(I18nHelper.getLabel("error.get.system.services.failed"));
        }

        if (xml != null && xml.length > 0) {
            // String xmlDecrypted = new String(unseal(xml));
            byte[] xmlDecrypted = Crypto.unseal(xml, nihiiOrg);
            // Get version of result
            XMLGregorianCalendar currentVersion = null;

            SystemServices systemServices;
            try {
                systemServices = getJaxContextCentralizer().toObject(SystemServices.class, xmlDecrypted);
                currentVersion = systemServices.getVersion();
            } catch (GFDDPPException e) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.get.system.services.failed"));
            }

            File newFile = new File(systemServicesPath + "/systemservices_v" + currentVersion.toXMLFormat().replace(':', '-') + ".xml");
            try {
                FileUtils.writeByteArrayToFile(newFile, xmlDecrypted);
            } catch (IOException e) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.get.system.services.failed"));
            }
            XMLGregorianCalendar newCal = ConfigUtils.getSystemServicesVersionFromFile(newFile, propertyHandler);

            if (latestVersion != null && newCal.toGregorianCalendar().after(latestVersion)) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.systemconfiguration.version", new Object[]{newFile.getAbsolutePath()}));
            }

        } else {
            LOG.debug("Received null payload from system services.");
        }

        SystemServicesUtils ssu = SystemServicesUtils.getInstance();
        ssu.setSystemServicesXmlFile(ConfigUtils.getLatestSystemServicesFile(systemServicesPath, propertyHandler));
        ssu.reloadCache();
        LOG.info("SystemService loaded");
    }

    @Override
    public void loadSession(String token, final String nihiiOrg) throws IntegrationModuleException {
        LOG.info("Loading session");
        try {

            validateSystemCertificate(nihiiOrg);

            LOG.debug("************* Loading session .... ************");
            String systemKeystorePath = propertyHandler.getProperty(EncryptionUtils.PROP_KEYSTORE_FILE);
            sessionService.loadSession(token, systemKeystorePath, getEncryptionUtils(nihiiOrg).getSystemKeystorePassword(null), EncryptionUtils.AUTHENTICATION_ALIAS, getEncryptionUtils().get(nihiiOrg).getSystemKeystorePassword(null), nihiiOrg);
            this.setNiss(STSHelper.getNiss(SAML10Converter.toElement(token)));
            ConfigFactory.getConfigValidator(new ArrayList<String>()).setProperty("user.inss", getNiss());
            LOG.debug(" ******************* Session Loaded  **************");
        } catch (Throwable t) {
            t.printStackTrace();
            Exceptionutils.errorHandler(t);
        }
        LOG.info("Session loaded");
    }

    @Override
    public void setSystemKeystoreProperties(String systemKeystorePassword, String systemKeystorePath, String systemKeystoreDirectory, String systemKeystoreNIHIIPHARMACYCBE, final String nihiiOrg) {
        LOG.info("Setting key store : path " + systemKeystorePath + " directory : " + systemKeystoreDirectory + " Nihii : " + systemKeystoreNIHIIPHARMACYCBE);

        getEncryptionUtils(nihiiOrg).setSystemKeystorePassword(systemKeystorePassword);
        getEncryptionUtils(nihiiOrg).setSystemKeystorePath(systemKeystorePath);
        getEncryptionUtils(nihiiOrg).setSystemKeystoreDirectory(systemKeystoreDirectory);
        getEncryptionUtils(nihiiOrg).setSystemKeystoreRiziv(systemKeystoreNIHIIPHARMACYCBE);
        LOG.info("Setting key store - completed");
    }


    public KeyStore getSystemKeystore(String systemKeystorePassword, String systemKeystorePath, String systemKeystoreDirectory, String systemKeystoreNIHIIPHARMACYCBE, final String nihiiOrg) throws IntegrationModuleException {
        setSystemKeystoreProperties(systemKeystorePassword, systemKeystorePath, systemKeystoreDirectory, systemKeystoreNIHIIPHARMACYCBE, nihiiOrg);
        return getEncryptionUtils(nihiiOrg).getKeyStore();
    }

    @Override
    public void setOldSystemKeystoreProperties(String systemKeystorePassword, String systemKeystorePath, String systemKeystoreDirectory, String systemKeystoreNIHIIPHARMACYCBE, final String nihiiOrg) {
        if (getEncryptionUtils(nihiiOrg) == null) {
            setEncryptionUtils( new EncryptionUtils(propertyHandler, nihiiOrg),nihiiOrg);
        }

        getEncryptionUtils(nihiiOrg).setOldSystemKeystorePassword(systemKeystorePassword);
        getEncryptionUtils(nihiiOrg).setOldSystemKeystorePath(systemKeystorePath);
        getEncryptionUtils(nihiiOrg).setOldSystemKeystoreDirectory(systemKeystoreDirectory);
        getEncryptionUtils(nihiiOrg).setOldSystemKeystoreRiziv(systemKeystoreNIHIIPHARMACYCBE);
    }

    public KeyStore getOldSystemKeystore(String systemKeystorePassword, String systemKeystorePath, String systemKeystoreDirectory, String systemKeystoreNIHIIPHARMACYCBE, final String nihiiOrg) throws IntegrationModuleException {
        setOldSystemKeystoreProperties(systemKeystorePassword, systemKeystorePath, systemKeystoreDirectory, systemKeystoreNIHIIPHARMACYCBE, nihiiOrg);
        return getEncryptionUtils(nihiiOrg).getOldKeyStore();
    }

    @Override
    public String getRequestorIdInformation(final String nihiiOrg) throws IntegrationModuleException {
        String nihii = null;
        if (propertyHandler.hasProperty("requestid")) {
            nihii = propertyHandler.getProperty("requestid");
        } else {
            if (!samlTokenCache.hasValidSession(nihiiOrg)) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.invalid.session"));
            }
            try {
                Element token = samlTokenCache.getSamlToken(nihiiOrg);
                nihii = STSHelper.getNihii(token);
            } catch (Exception e) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.invalid.session"));
            }
        }

        if (nihii == null || nihii.length() == 0) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.saml.nihii.not.found"));
        }
        return nihii;
    }

    @Override
    public String getRequestorTypeInformation(final String nihiiOrg) throws IntegrationModuleException {
        if (!samlTokenCache.hasValidSession(nihiiOrg)) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.invalid.session"));
        }
        String type;
        if (propertyHandler.hasProperty("requesttype")) {
            type = propertyHandler.getProperty("requesttype");
        } else {
            if (!samlTokenCache.hasValidSession(nihiiOrg)) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.invalid.session"));
            }
            try {
                Element token = samlTokenCache.getSamlToken(nihiiOrg);
                type = STSHelper.getType(token);
            } catch (Exception e) {
                throw new IntegrationModuleException(I18nHelper.getLabel("error.invalid.session"));
            }
        }
        try {
            PharmacyIdType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.validation.saml.type.not.found"), e);
        }
        return type;
    }

    @Override
    public void assertValidSession(final String nihiiOrg) throws IntegrationModuleException {
        if (!hasValidSession(nihiiOrg)) {
            throw new IntegrationModuleException(I18nHelper.getLabel("error.invalid.session"));
        }
    }

    @Override
    public String getNiss() {
        return niss;
    }

    public void setNiss(String niss) {
        this.niss = niss;
    }

    @Override
    public PropertyHandler getPropertyHandler() {
        return propertyHandler;
    }

    public void setPropertyHandler(PropertyHandler propertyHandler) {
        this.propertyHandler = propertyHandler;
    }

    @Override
    public EncryptionUtils getEncryptionUtils(final String nihiiOrg) {
        if(encryptionUtils.get(nihiiOrg)==null){
            setEncryptionUtils(new EncryptionUtils(propertyHandler,nihiiOrg),nihiiOrg);
        }
        return encryptionUtils.get(nihiiOrg);

    }

    public void setEncryptionUtils(EncryptionUtils encryptionUtils, final String nihiiOrg) {
        this.encryptionUtils.put(nihiiOrg,encryptionUtils);
    }

    public ETKHelper getEtkHelper(final String nihiiOrg) {
        if(etkHelper.get(nihiiOrg)==null)
        {
            etkHelper.put(nihiiOrg,new ETKHelper(propertyHandler,getEncryptionUtils(nihiiOrg)));
        }
        return etkHelper.get(nihiiOrg);
    }

    public JaxContextCentralizer getJaxContextCentralizer() {
        return this.jaxContextCentralizer;
    }

    @Override
    public boolean hasValidSession(final String nihiiOrg) {
        return samlTokenCache.hasValidSession(nihiiOrg);
    }

    @Override
    public void setSystemProperty(String key, String value) {
        System.setProperty(key, value);
        LOG.info("System property: " + key + " is set with value: " + value);
    }

    public Map<String, EncryptionUtils> getEncryptionUtils() {
        return encryptionUtils;
    }


}

