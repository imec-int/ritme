package uz.ehealth.ritme.addressbook;

import be.business.connector.common.CommonIntegrationModuleImpl;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.utils.EncryptionUtils;
import be.business.connector.core.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.be.fgov.ehealth.addressbook.protocol.v1.SearchOrganizationsResponseType;
import uz.ehealth.ritme.model.OrganisationType;
import uz.ehealth.ritme.outbound.organisation.OrganisationData;
import uz.ehealth.ritme.outbound.organisation.OrganisationService;
import uz.ehealth.ritme.plugins.PluginManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdcuyp0 on 24-3-2016.
 */
public class AddressBookOrganisationService implements OrganisationService {

    public static final Logger LOG = LoggerFactory.getLogger(AddressBookOrganisationService.class);
    private static AddressBookIntegrationModuleImpl module = null;
    private static CommonIntegrationModuleImpl commonModule = null;

    static {
        try {
            System.setProperty("config", "/be.ehealth.technicalconnector.properties");
            module = initModule();
            commonModule = getCommonModule(null, null, null, null, null, null);
        } catch (IntegrationModuleException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }


    private static CommonIntegrationModuleImpl getCommonModule(String path, String pathKeystorePropertiesFile, String systemKeystorePassword, String systemKeystorePath, String systemKeystoreDirectory, String systemKeystoreRizivKBO)
            throws IntegrationModuleException, IOException {

        CommonIntegrationModuleImpl module = initCommonModule();

        String[] listProperties = null;

        if (System.getProperty("systemKeystore") != null) {
            LOG.debug("Loading system keystore properties from {}", System.getProperty("systemKeystore"));
            String systemKeystore = new String(loadFile(System.getProperty("systemKeystore")),
                    "UTF-8");
            listProperties = systemKeystore.split(";");

            for (int i = 0; i < listProperties.length; i++) {
                if ("null".equals(listProperties[i])) {
                    listProperties[i] = null;
                }
            }
            module.setSystemKeystoreProperties(listProperties[0], listProperties[1], listProperties[2], listProperties[3], "");
        } else {
            createSystemKeystorePropertiesFile(pathKeystorePropertiesFile, systemKeystorePassword, systemKeystorePath, systemKeystoreDirectory, systemKeystoreRizivKBO);
            module.setSystemKeystoreProperties(systemKeystorePassword, systemKeystorePath, systemKeystoreDirectory, systemKeystoreRizivKBO, "");
        }
        return module;
    }


    private static CommonIntegrationModuleImpl getFallbackModule(String path, String pathKeystorePropertiesFile,
                                                                 String niss, String passphrase, String systemKeystorePassword, String systemKeystorePath, String systemKeystoreDirectory, String systemKeystoreRizivKBO, final String nihiiOrg) throws IntegrationModuleException,
            IOException {

        CommonIntegrationModuleImpl module = initCommonModule();

        createSystemKeystorePropertiesFile(pathKeystorePropertiesFile, systemKeystorePassword, systemKeystorePath, systemKeystoreDirectory, systemKeystoreRizivKBO);

        module.setSystemKeystoreProperties(systemKeystorePassword, systemKeystorePath, systemKeystoreDirectory, systemKeystoreRizivKBO, nihiiOrg);

        String session = module.createFallbackSession(niss, passphrase, nihiiOrg);

        if (path != null) {
            File f = new File(path);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(session.getBytes());
            fos.close();
            LOG.debug("Session saved in {}", path);
        }

        return module;
    }

    //test only
    private static void createSystemKeystorePropertiesFile(String pathKeystorePropertiesFile, String systemKeystorePassword, String systemKeystorePath, String systemKeystoreDirectory, String systemKeystoreRizivKBO) throws IOException {
        if (pathKeystorePropertiesFile != null) {
            File f = new File(pathKeystorePropertiesFile);
            String line = systemKeystorePassword + ";" + systemKeystorePath + ";" + systemKeystoreDirectory + ";" + systemKeystoreRizivKBO;
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(line.getBytes("UTF-8"));
            fos.close();
            LOG.debug("Keystore properties saved in {}", pathKeystorePropertiesFile);
        }
    }

    private static AddressBookIntegrationModuleImpl initModule()
            throws IntegrationModuleException, IOException {
        AddressBookIntegrationModuleImpl module = null;
        if ("true".equals(System.getProperty("mock"))) {
            LOG.debug("Using a mock module");
            module = new AddressBookIntegrationModuleImpl();
        } else {
            if (System.getProperty("config") != null) {
                module = new AddressBookIntegrationModuleImpl(getCommonModule());
            } else {
                module = new AddressBookIntegrationModuleImpl();
            }
        }
        return module;
    }

    private static CommonIntegrationModuleImpl initCommonModule()
            throws IntegrationModuleException {

        CommonIntegrationModuleImpl module = null;

        if ("true".equals(System.getProperty("mock"))) {
            //module = new CommonIntegrationModuleMock();
        } else {
            if (System.getProperty("config") != null) {
                module = new CommonIntegrationModuleImpl(System.getProperty("config"));
            } else {
                module = new CommonIntegrationModuleImpl();
            }
        }

        return module;
    }

    private static CommonIntegrationModuleImpl getCommonModule()
            throws IntegrationModuleException, IOException {
        return getCommonModule(null, null, null, null, null, null);
    }

    /**
     * Load file.
     *
     * @param path the path
     * @return the byte[]
     */
    private static byte[] loadFile(String path) {
        File f = new File(path);
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            LOG.debug(e.getMessage(), e);
        }
        if (stream == null) {
            throw new RuntimeException("Invalid path : " + path);
        }
        return IOUtils.getBytes(stream);
    }


    @Override
    public OrganisationData[] query(String ehp, String nihiiToSearch, String cbe, final OrganisationType type, String name, String city, String zipCode, String email, final String user, final String nihiiOrg) {


        try {
            if (!commonModule.hasValidSession(nihiiOrg)) {

                EncryptionUtils encryptionUtils = PluginManager.get("ritme.addressbook.connector.encryption", EncryptionUtils.class, null, nihiiOrg);

                commonModule.setEncryptionUtils(encryptionUtils, nihiiOrg);
                commonModule.createFallbackSession(null, null, nihiiOrg);

                module.setEncryptionUtils(encryptionUtils, nihiiOrg);

            }


            // business logic checks as in https://www.ehealth.fgov.be/sites/default/files/assets/int/pdf/Addressbook/ehealth_addressbook_-_cookbook_v1.2_20160901.pdf
            // paragraph 5.3.3.1

            if(ehp !=  null){
                nihiiToSearch = null;
                cbe = null;
                name = null;
                city = null;
                zipCode = null;
                email = null;
            } else if (nihiiToSearch != null){
                ehp = null;
                cbe = null;
                name = null;
                city = null;
                zipCode = null;
                email = null;
            } else if (cbe != null){
               ehp = null;
                nihiiToSearch = null;
                name = null;
                city = null;
                zipCode = null;
                email = null;
            } else if (name != null && type != null){
                ehp = null;
                nihiiToSearch = null;
                cbe = null;
                city = null;
                zipCode = null;
                email = null;

            } else if (city != null && type != null){
                ehp = null;
                nihiiToSearch = null;
                cbe = null;
                name = null;
                zipCode = null;
                email = null;
            } else if (zipCode != null && type != null){
                ehp = null;
                nihiiToSearch = null;
                cbe = null;
                name = null;
                city = null;
                email = null;
            } else if (email != null){
                ehp = null;
                nihiiToSearch = null;
                cbe = null;
                name = null;
                city = null;
                zipCode = null;
            } else {
                throw new RuntimeException("400");
            }



            final SearchOrganizationsResponseType response = module.searchOrganisation(nihiiOrg, city, email, name, nihiiToSearch, type != null ? type.name() : null, zipCode, cbe, ehp);

            final List<SearchOrganizationsResponseType.HealthCareOrganization> organizations = response.getHealthCareOrganization();

            List<OrganisationData> organisationDataList = new ArrayList<OrganisationData>();

            for (SearchOrganizationsResponseType.HealthCareOrganization organization : organizations) {
                organisationDataList.add(new AddressBookOrganisationData(organization));
            }

            return organisationDataList.toArray(new OrganisationData[organisationDataList.size()]);


        } catch (IntegrationModuleException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("500", new Exception(e.getMessage(), e));
        }
    }
}
