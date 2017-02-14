/*
 * 
 */
package be.business.connector.core.module;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import be.business.connector.core.utils.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.extras.DOMConfigurator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.perf4j.aop.Profiled;

import be.apb.gfddpp.common.utils.JaxContextCentralizer;
import be.business.connector.core.ehealth.services.KgssService;
import be.business.connector.core.ehealth.services.KgssServiceImpl;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.technical.connector.utils.Crypto;
import be.ehealth.technicalconnector.service.kgss.domain.KeyResult;
import be.fgov.ehealth.etee.crypto.decrypt.DataUnsealer;
import be.fgov.ehealth.etee.crypto.decrypt.UnsealedData;
import be.fgov.ehealth.etee.crypto.encrypt.DataSealer;
import be.fgov.ehealth.etee.crypto.encrypt.EncryptionToken;
import be.fgov.ehealth.etee.crypto.status.CryptoResult;
import be.fgov.ehealth.etee.crypto.status.NotificationError;
import be.fgov.ehealth.etee.crypto.status.NotificationWarning;
import be.fgov.ehealth.etee.kgss._1_0.protocol.GetKeyRequestContent;
import be.fgov.ehealth.etee.kgss._1_0.protocol.GetKeyResponseContent;


public abstract class AbstractIntegrationModule {
	private final static Logger LOG = Logger.getLogger(AbstractIntegrationModule.class);

	public static long TIME_KGSS_CALL = 0L;
	public static final String EHEALTH_SUCCESS_CODE_100 = "100";
	public static final String EHEALTH_SUCCESS_CODE_200 = "200";
	public static final String EHEALTH_SUCCESS_CODE_300 = "300";
	public static final String EHEALTH_SUCCESS_CODE_400 = "400";
	public static final String EHEALTH_SUCCESS_CODE_500 = "500";

	private DataSealer dataSealer = null;
	protected DataUnsealer dataUnsealer = null;
	private Map<String,DataSealer> oldDataSealer = new HashMap<String, DataSealer>();
	private Map<String,DataUnsealer> oldDataUnsealer = new HashMap<String,DataUnsealer>();

	private Map<String,EncryptionUtils> encryptionUtils = new HashMap<String,EncryptionUtils>();

	private PropertyHandler propertyHandler;
	protected JaxContextCentralizer jaxContextCentralizer;

	private Map<String,ETKHelper> etkHelper = new HashMap<String, ETKHelper>();
	private Map<String,Key> symmKey = new HashMap<String,Key>();

	private CacheManager cacheManager;
	private Cache kgssCache;
	private Cache etkCache;

	private KgssService kgssService = KgssServiceImpl.getInstance();


	public void setJaxContextCentralizer(JaxContextCentralizer jaxContextCentralizer) {
		this.jaxContextCentralizer = jaxContextCentralizer;
	}


	public AbstractIntegrationModule() throws IntegrationModuleException {
		super();
		this.propertyHandler = PropertyHandler.getInstance();
		init();
	}

	protected void init() throws IntegrationModuleException {
		try {
			LOG.info("Init abstractIntegrationModule!");
			if (propertyHandler != null) {
				String log4jXmlPath = propertyHandler.getProperty("LOG4J", "log4j.xml");
				File file = new File(log4jXmlPath);
				if (file.exists()) {
					LogManager.resetConfiguration();
					DOMConfigurator.configure(file.getAbsolutePath());
					LOG.info("Loading log4j config from " + file.getAbsolutePath());
				}
			}
			
			jaxContextCentralizer = JaxContextCentralizer.getInstance();
			jaxContextCentralizer.addContext(GetKeyRequestContent.class);
			jaxContextCentralizer.addContext(GetKeyResponseContent.class);
			
			Security.addProvider(new BouncyCastleProvider());

			
			if(this.propertyHandler!=null) {
				MessageDumper.getInstance().init(this.propertyHandler);
			}
			else{
				LOG.debug("system property -Dconfig not set!!!");
			}
			
			// When running in DOTNET, the current context class loader must be overriden to avoid class not found exceptions!!!
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			System.setProperty("javax.xml.soap.SOAPFactory", "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl");

			// Extra debug information
			if (LOG.isDebugEnabled()) {
				LOG.debug("Curdir : " + new File(".").getCanonicalPath());
				LOG.debug("Support P12 keystores : " + KeyStore.getInstance("PKCS12"));
			}

			initCaching();
			LOG.info("End Init abstractIntegrationModule!");
		} catch (Throwable t) {
			LOG.error("Exception in init abstractIntegrationModule: ", t);
			Exceptionutils.errorHandler(t);
		}
	}

	private void initCaching() {
		LOG.info("INIT CACHE MANAGER");
		URL url = getClass().getResource("/cache/config/ehcache.xml");
		cacheManager = CacheManager.newInstance(url);

		LOG.info("DOES KGSS CACHE EXIST?");
		kgssCache = cacheManager.getCache("KGSS");
		if (kgssCache == null) {
			LOG.info("NEW KGSS CACHE");
			kgssCache = new Cache("KGSS", 0, false, false, 0, 0);
			cacheManager.addCache(kgssCache);
		}

		LOG.info("DOES ETK CACHE EXIST?");
		etkCache = cacheManager.getCache("ETK");
		if (etkCache == null) {
			LOG.info("NEW ETK CACHE");
			etkCache = new Cache("ETK", 0, false, false, 0, 0);
			cacheManager.addCache(etkCache);
		}
	}

	public void initEncryption(PropertyHandler propHandler) throws IntegrationModuleException {
		try {

			LOG.info("Init the encryption!");
			propertyHandler = propHandler;

			// if (hasPersonalEtk()) { //only for care providers
			// LOG.info("Init the encryption - care provider has a personal ETK");
			// encrUtils.verifyDecryption(etkHelper.getSystemETK().get(0));
			// }
		} catch (Throwable t) {
			LOG.error("Exception occured when initializing the encryption util: ", t);
			Exceptionutils.errorHandler(t, "error.initialization");
		}
	}

	/**
	 * Checks for personal etk. Default implementation returns true always. Override and set to false for patients.
	 * 
	 * @return whether the user has an etk.
	 */
	protected boolean hasPersonalEtk() {
		return true;
	}

	@Profiled(logFailuresSeparately = true, tag = "0.AbstractIntegrationModule#sealRequest", logger = "org.perf4j.TimingLogger_Common")
	protected synchronized byte[] sealRequest(EncryptionToken paramEncryptionToken, byte[] paramArrayOfByte, final String nihiiOrg) throws IntegrationModuleException {
		return Crypto.seal(paramEncryptionToken, paramArrayOfByte, nihiiOrg);
	}



	/**
	 * Unseal request.
	 * 
	 * @param message
	 *            the message
	 * @param nihiiOrg
	 * @return the byte[]
	 * @throws IntegrationModuleException
	 *             the integration module exception
	 */
	@Profiled(logFailuresSeparately = true, tag = "0.AbstractIntegrationModule#unsealRequest", logger = "org.perf4j.TimingLogger_Common")
	protected byte[] unsealRequest(byte[] message, final String nihiiOrg) throws IntegrationModuleException {
		return Crypto.unseal(message, nihiiOrg);
	}


	/**
	 * Unseal.
	 * 
	 * @param message
	 *            the message
	 * @param nihiiOrg
	 * @return the byte[]
	 * @throws IntegrationModuleException
	 *             the integration module exception
	 */
	protected byte[] unsealNotif(byte[] message, final String nihiiOrg) throws IntegrationModuleException {
		byte[] unsealedData = null;
		
			unsealedData = Crypto.unseal(message, nihiiOrg);

		return unsealedData;
	}

	protected byte[] unsealNotifOld(byte[] message, final String nihiiOrg) {
		byte[] unsealedData = null;

		CryptoResult<UnsealedData> result = getOldDataUnsealer(nihiiOrg).unseal(message);
		// decryption operation succeeded and there are no errors or failures
		if (result != null && result.hasData()) {

			if (result.hasErrors()) { // 3.A.A. There are no errors or failures
				for (NotificationError error : result.getErrors()
                                        ) {
					LOG.error(error.name());
				}
				for (NotificationWarning warning : result.getWarnings()) {
					LOG.error(warning.name());
				}
                                if (result.getFatal() != null) {
                                    LOG.error(result.getFatal().getErrorMessage());
                                }
			}

			// Get the unsealed data
			InputStream unsealedDataStream = result.getData().getContent();
			unsealedData = IOUtils.getBytes(unsealedDataStream);

			return unsealedData;
		}
		return null;
	}

	protected byte[] unsealNotiffeed(byte[] message, final String nihiiOrg) throws IntegrationModuleException {
		byte[] unsealedNotification = null;
		boolean calledUnsealNotifOld = false;
		try {
			LOG.debug("Start unseal notification: " + Arrays.toString(message));
			unsealedNotification = Crypto.unseal(message, nihiiOrg);
			if (unsealedNotification != null) {
				return unsealedNotification;
			} 
			if(getOldDataUnsealer(nihiiOrg) != null){
				LOG.debug("Unseal notification was null. Start unseal notification with old keystore: " + Arrays.toString(message));
				calledUnsealNotifOld = true;
				unsealedNotification = unsealNotifOld(message,nihiiOrg );
				if (unsealedNotification != null) {
					return unsealNotifOld(message, nihiiOrg);
				}
			}else{
				LOG.debug("OldDataUnsealer is null.");
			}
		} catch (Throwable t) {
			LOG.error("Exception occured with unsealing notification: ", t);
			if(calledUnsealNotifOld){
				Exceptionutils.errorHandler(t, "error.data.unseal");
			}else{
				try {
					LOG.debug("Exception occured with unsealing notification. Trying to unseal notification with old keystore: " + Arrays.toString(message));
					unsealedNotification = unsealNotifOld(message,nihiiOrg );
				} catch (Throwable te) {
					Exceptionutils.errorHandler(te, "error.data.unseal");
				}
			}
		}
		if(unsealedNotification == null){
				throw new IntegrationModuleException(I18nHelper.getLabel("error.data.unseal"));
		}
		return unsealedNotification;
	}

	@Profiled(logFailuresSeparately = true, tag = "0.AbstractIntegrationModule#unsealPrescriptionForUnknown", logger = "org.perf4j.TimingLogger_Common")
	protected byte[] unsealPrescriptionForUnknown(KeyResult key, byte[] protectedMessage, final String nihiiOrg) throws IntegrationModuleException {
		return Crypto.unsealForUnknown(key, protectedMessage, nihiiOrg);
	}


	protected KeyResult getKeyFromKgss(String keyId, final String nihiiOrg) throws IntegrationModuleException {
		return getKeyFromKgss(keyId, null,nihiiOrg );
	}

	@Profiled(logFailuresSeparately = true, tag = "0.AbstractIntegrationModule#getKeyFromKgss", logger = "org.perf4j.TimingLogger_Common")
	public KeyResult getKeyFromKgss(String keyId, byte[] myEtk, final String nihiiOrg) throws IntegrationModuleException {
		KeyResult keyResult = null;
		try {
			// For test, when a sim key is specified in the config
			if (getPropertyHandler().hasProperty("test_kgss_key")) {
				String part1 = propertyHandler.getProperty("test_kgss_key").split(";")[0];
				String part2 = propertyHandler.getProperty("test_kgss_key").split(";")[1];
				// LOG.info("KGSS key retrieved from configuration. Key Id = part1);
				byte[] keyResponse = Base64.decode(part2);
				return new KeyResult(new SecretKeySpec(keyResponse, "AES"), part1);
			}

			keyResult = kgssService.retrieveKeyFromKgss(keyId.getBytes(), myEtk, etkHelper.get(nihiiOrg).getKGSS_ETK().get(0).getEncoded());

		} catch (Throwable t) {
			LOG.error("Exception in getKeyFromKgss abstractIntegrationModule: ", t);
			Exceptionutils.errorHandler(t);
		}
		return keyResult;
	}



	protected Key getSymmKey(final String nihiiOrg) {

		if(symmKey.get(nihiiOrg)==null)
		{
			try {
				LOG.info("Init the encryption - create symmKey for "+ nihiiOrg);
				symmKey.put(nihiiOrg, encryptionUtils.get(nihiiOrg).generateSecretKey());
			} catch (IntegrationModuleException e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		return symmKey.get(nihiiOrg);
	}

	

	public EncryptionUtils getEncryptionUtils(final String nihiiOrg) {
		if(encryptionUtils.get(nihiiOrg)==null)
		{
			encryptionUtils.put(nihiiOrg,new EncryptionUtils(propertyHandler, nihiiOrg));
		}
		return this.encryptionUtils.get(nihiiOrg);
	}

	public PropertyHandler getPropertyHandler() {
		return propertyHandler;
	}

	public void setOldDataUnsealer(DataUnsealer oldDataUnsealer, final String nihiiOrg) {
		this.oldDataUnsealer.put(nihiiOrg, oldDataUnsealer);
	}

	public DataUnsealer getOldDataUnsealer(final String nihiiOrg) {
		try {
			if (encryptionUtils.get(nihiiOrg).getOldKeyStore() != null && oldDataUnsealer.get(nihiiOrg)==null) {
                oldDataUnsealer.put(nihiiOrg, encryptionUtils.get(nihiiOrg).initOldUnSealing());

            }
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		return oldDataUnsealer.get(nihiiOrg);
	}

	public void setOldDataSealer(DataSealer oldDataSealer, final String nihiiOrg) {
		this.oldDataSealer.put(nihiiOrg, oldDataSealer);
	}

	public DataSealer getOldDataSealer(final String nihiiOrg) {
		try {
		if (encryptionUtils.get(nihiiOrg).getOldKeyStore() != null && oldDataSealer.get(nihiiOrg)==null) {
			oldDataSealer.put(nihiiOrg, encryptionUtils.get(nihiiOrg).initOldSealing());
		}
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		return oldDataSealer.get(nihiiOrg);
	}

	protected ETKHelper getEtkHelper(final String nihiiOrg) {
		if(etkHelper.get(nihiiOrg)==null) {
			LOG.info("Init the encryption - init etkHelper");
			etkHelper.put(nihiiOrg,new ETKHelper(propertyHandler, encryptionUtils.get(nihiiOrg)));
		}

		return etkHelper.get(nihiiOrg);
	}



	public void setDataSealer(DataSealer dataSealer) {
		this.dataSealer = dataSealer;
	}

	public void setDataUnsealer(DataUnsealer dataUnsealer) {
		this.dataUnsealer = dataUnsealer;
	}

	public void setEncryptionUtils(EncryptionUtils encryptionUtils, final String nihiiOrg) {
		this.encryptionUtils.put(nihiiOrg,encryptionUtils);
	}

	public void setPropertyHandler(PropertyHandler propertyHandler) {
		this.propertyHandler = propertyHandler;
	}

	public JaxContextCentralizer getJaxContextCentralizer() {
		return this.jaxContextCentralizer;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public Cache getKgssCache() {
		return kgssCache;
	}

	public void setKgssCache(Cache kgssCache) {
		this.kgssCache = kgssCache;
	}

	public Cache getEtkCache() {
		return etkCache;
	}

	public void setEtkCache(Cache etkCache) {
		this.etkCache = etkCache;
	}

}
