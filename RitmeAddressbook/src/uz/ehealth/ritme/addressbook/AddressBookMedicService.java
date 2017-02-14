package uz.ehealth.ritme.addressbook;

import be.business.connector.common.CommonIntegrationModuleImpl;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.utils.EncryptionUtils;
import be.business.connector.core.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.be.fgov.ehealth.aa.complextype.v1.HealthCareProfessionalType;
import uz.ehealth.ritme.be.fgov.ehealth.addressbook.protocol.v1.SearchProfessionalsResponseType;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.medic.MedicService;
import uz.ehealth.ritme.plugins.PluginManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdcuyp0 on 24-3-2016.
 */
public class AddressBookMedicService implements MedicService {

    public static final Logger LOG = LoggerFactory.getLogger(AddressBookMedicService.class);
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
        // comment
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
    public MedicData getData(final String medic, final String user, final String nihiiOrg) {
        return null;
    }

    @Override
    public MedicData[] query(String ssin, String lastName, String firstName, String zipCode, String city, String rawProfession, String nihiiPers, final String qualification, String email, final String user, final String nihiiOrg) {

        String profession;

        if (rawProfession != null && rawProfession.startsWith("pers")) {
            profession = rawProfession.substring("pers".length()).toUpperCase();
        } else {
            profession = rawProfession;
        }

        try {
            if (!commonModule.hasValidSession(nihiiOrg)) {

                EncryptionUtils encryptionUtils = PluginManager.get("ritme.addressbook.connector.encryption", EncryptionUtils.class, null, nihiiOrg);

                commonModule.setEncryptionUtils(encryptionUtils, nihiiOrg);
                commonModule.createFallbackSession(null, null, nihiiOrg);

                module.setEncryptionUtils(encryptionUtils, nihiiOrg);

            }

            // checks from https://www.ehealth.fgov.be/sites/default/files/assets/int/pdf/Addressbook/ehealth_addressbook_-_cookbook_v1.2_20160901.pdf
            // paragraph 5.3.2.1

            if(ssin != null){
                nihiiPers = null;
                firstName = null;
                lastName = null;
                city = null;
                zipCode = null;
                email = null;
            } else if (nihiiPers != null){
                ssin = null;
                firstName = null;
                lastName = null;
                city = null;
                zipCode = null;
                email = null;


            } else if (lastName != null && profession != null){
                ssin = null;
                nihiiPers = null;
                city = null;
                zipCode = null;
                email = null;

            } else if (city != null && profession != null){
               ssin = null;
                nihiiPers = null;
                firstName = null;
                lastName = null;
                zipCode = null;
                email = null;
            } else if (zipCode != null && profession != null){
                ssin = null;
                nihiiPers = null;
                firstName = null;
                lastName = null;
                city = null;
                email = null;
            }
            else if (email != null){
                ssin = null;
                nihiiPers = null;
                firstName = null;
                lastName = null;
                city = null;
                profession = null;
            } else {
                throw new RuntimeException("400");
            }



            final SearchProfessionalsResponseType response = module.searchProfessional(nihiiOrg, city, email, firstName, lastName, nihiiPers, profession, zipCode, ssin);

            final List<HealthCareProfessionalType> medics = response.getHealthCareProfessional();

            List<MedicData> medicDataList = new ArrayList<MedicData>();

            for (HealthCareProfessionalType medic : medics) {
                medicDataList.add(new AddressBookMedicData(medic));
            }

            return medicDataList.toArray(new MedicData[medicDataList.size()]);


        } catch (IntegrationModuleException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("500", new Exception(e.getMessage(), e));
        }
    }

    @Override
    public MedicData getLastInvolvedMedic(final String patientSSIN, final String user, final String nihiiOrg) {
        LOG.error("Addressbook does not implement last involved medic");
        return null;
    }
}
