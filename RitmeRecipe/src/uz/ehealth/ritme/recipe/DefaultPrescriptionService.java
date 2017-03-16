package uz.ehealth.ritme.recipe;

import be.business.connector.common.CommonIntegrationModuleImpl;
import be.business.connector.core.exceptions.IntegrationModuleException;
import be.business.connector.core.utils.EncryptionUtils;
import be.business.connector.core.utils.IOUtils;
import be.business.connector.recipe.prescriber.PrescriberIntegrationModuleImpl;
import be.business.connector.recipe.prescriber.mock.PrescriberIntegrationModuleMock;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import uz.ehealth.ritme.kmehr.MedicatieVoorschriftItemsToRecipeKmehr;
import uz.ehealth.ritme.model.MedicatieVoorschriftItem;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.model.PrescriptionType;
import uz.ehealth.ritme.outbound.hospital.HospitalData;
import uz.ehealth.ritme.outbound.hospital.HospitalService;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.medic.MedicService;
import uz.ehealth.ritme.outbound.metrics.DefaultMetrics;
import uz.ehealth.ritme.outbound.metrics.ElapsedTime;
import uz.ehealth.ritme.outbound.metrics.Metrics;
import uz.ehealth.ritme.outbound.patient.PatientData;
import uz.ehealth.ritme.outbound.patient.PatientService;
import uz.ehealth.ritme.plugins.PluginManager;
import uz.ehealth.ritme.sam.SamService;
import uz.emv.sam.v1.domain.AMPP;
import uz.emv.sam.v1.domain.ATC;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by bdcuyp0 on 24-3-2016.
 */
public class DefaultPrescriptionService implements PrescriptionService {

    public static final Logger LOG = LoggerFactory.getLogger(DefaultPrescriptionService.class);
    private static final SamService SAM_SERVICE = PluginManager.get("ritme.sam", SamService.class);
    private static PrescriberIntegrationModuleImpl module = null;
    private static CommonIntegrationModuleImpl commonModule = null;
    private static final Metrics METRICS = PluginManager.get("uz.ritme.outbound.metrics", Metrics.class, DefaultMetrics.class);

    static {
        try {
            System.setProperty("config", System.getProperty("be.ehealth.technicalconnector.config.location", "/be.ehealth.technicalconnector.properties"));
            module = initModule();
            commonModule = getCommonModule(null, null, null, null, null, null);
        } catch (IntegrationModuleException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public URI createPrescription(final String remoteUser, final String patientSsin, final String nihiiOrg, final List<MedicatieVoorschriftItem> items) {


        PrescriptionType prescriptionType = PrescriptionType.P0;
        for(MedicatieVoorschriftItem item:items){
            PrescriptionType currentPrescriptionType = determinePrescriptionType(item);
            switch(prescriptionType){
                case P0:
                    prescriptionType = currentPrescriptionType;
                    break;
                case P1:
                    //noinspection NestedSwitchStatement
                    switch(currentPrescriptionType){
                        case P0:
                        case P1:
                            break;
                        case P2:
                            prescriptionType = currentPrescriptionType;
                            break;
                    }
                    break;
                case P2:
                    break;
            }

        }


        MedicData medicData = PluginManager.get("ritme.outbound.medic", MedicService.class).getData(remoteUser, remoteUser, nihiiOrg);
        HospitalData hospitalData = PluginManager.get("ritme.outbound.hospital", HospitalService.class).getData(nihiiOrg, remoteUser);
        PatientData patientData = PluginManager.get("ritme.outbound.patient", PatientService.class).getData(patientSsin, remoteUser, nihiiOrg);


        MedicatieVoorschriftItemsToRecipeKmehr converter = new MedicatieVoorschriftItemsToRecipeKmehr(medicData, hospitalData, patientData);

        String xml = converter.invoke(items);
        ElapsedTime createPrescriptionTime = METRICS.getElapsedTime();


        try {

            if (!commonModule.hasValidSession(nihiiOrg)) {


                EncryptionUtils encryptionUtils = PluginManager.get("ritme.recipe.connector.encryption", EncryptionUtils.class, null, nihiiOrg);

                commonModule.setEncryptionUtils(encryptionUtils, nihiiOrg);
                commonModule.createFallbackSession(null, encryptionUtils.getSystemKeystorePassword(null) == null ? null : new String(encryptionUtils.getSystemKeystorePassword(null)), nihiiOrg);

                module.setEncryptionUtils(encryptionUtils, nihiiOrg);
            }
            createPrescriptionTime.start(this.getClass(), "timing", "remoteUser", "patientSSIN", "RID/ERROR");
            String rid = module.createPrescription(
                    true, Long.valueOf(patientSsin),
                    xml.getBytes(), prescriptionType.getCd(), nihiiOrg);
            createPrescriptionTime.stop(remoteUser, patientSsin, rid);

            return new URI("recipe:" + rid);
        } catch (IntegrationModuleException e) {
            LOG.error(e.getMessage(), e);
            createPrescriptionTime.stop(remoteUser, patientSsin, e.getMessage());
            throw new RuntimeException("500", e);
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
            createPrescriptionTime.stop(remoteUser, patientSsin, e.getMessage());
            throw new RuntimeException("500", e);
        }


    }

    private PrescriptionType determinePrescriptionType(final MedicatieVoorschriftItem item) {
        if(item.getMedicatieSchemaItem().getIntendedMedication().getMedicationIdType().equals(MedicationIdType.CNK))
        {
            final List<AMPP> ampps = SAM_SERVICE.getAMPPByCnk(Integer.valueOf(item.getMedicatieSchemaItem().getIntendedMedication().getMedicationId()));
            if(!ampps.isEmpty())
            {
                AMPP ampp = ampps.get(0);
                if (StringUtils.isEmpty(ampp.getSocsecReimbCv()))
                {
                    return PrescriptionType.P0;
                }
                else{
                    return PrescriptionType.P1;
                }
            }

        }
        return item.getPrescriptionType();
    }


    @Override
    public List<MedicatieVoorschriftItem> sortAndCreatePrescriptions(final String remoteUser, final String patientSsin, final String nihiiOrg, final List<MedicatieVoorschriftItem> items) {

        List<List<MedicatieVoorschriftItem>> gesorteerdeLijst = sortPrescriptions(items);
        List<MedicatieVoorschriftItem> done = new ArrayList<MedicatieVoorschriftItem>();
        for (List<MedicatieVoorschriftItem> lijst : gesorteerdeLijst) {
            URI uri = createPrescription(remoteUser, patientSsin, nihiiOrg, lijst);
            for (int i = 0; i < lijst.size(); i++) {
                final MedicatieVoorschriftItem old = lijst.get(i);
                done.add(new MedicatieVoorschriftItem(
                        old.getPrescriptionDate(),
                        old.getExecutionDate(),
                        old.getExpirationDate(),
                        old.getPrescriberSSIN(),
                        old.getPrescriberNihiiOrg(),
                        old.getPrescriptionType(),
                        old.getQuantity(),
                        old.getMedicatieSchemaItem(),
                        old.getSource(),
                        uri.toString() + "/" + (i + 1)));
            }
        }

        return done;

    }

    @Override
    public void sendNotification(String bericht, List<MedicatieVoorschriftItem> items, String patientSsin, String receiverNihii, String nihiiOrg, String remoteUser) {

        /*
        <?xml version="1.0" encoding="UTF-8"?>
        <p:notification xmlns:p="http://services.recipe.be"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://services.recipe.be notification.xsd">
                <text>this is a notification</text>
        <kmehrmessage>[the Kmehr prescription]</kmehrmessage>
        </p:notification>
        */

        MedicData medicData = PluginManager.get("ritme.outbound.medic", MedicService.class).getData(remoteUser, remoteUser, nihiiOrg);
        HospitalData hospitalData = PluginManager.get("ritme.outbound.hospital", HospitalService.class).getData(nihiiOrg, remoteUser);
        PatientData patientData = PluginManager.get("ritme.outbound.patient", PatientService.class).getData(patientSsin, remoteUser, nihiiOrg);


        try {

            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            final DocumentBuilder documentBuilder = fac.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element root = doc.createElementNS("http://services.recipe.be", "p:notification");
            doc.appendChild(root);
            Element text = doc.createElementNS(null, "text");
            root.appendChild(text);
            Node textContents = doc.createTextNode(bericht);
            text.appendChild(textContents);


            if (items != null && !items.isEmpty()) {
                MedicatieVoorschriftItemsToRecipeKmehr converter = new MedicatieVoorschriftItemsToRecipeKmehr(medicData, hospitalData, patientData);
                String messageXml = converter.invoke(items);
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                builderFactory.setNamespaceAware(false);
                final DocumentBuilder parser = builderFactory.newDocumentBuilder();
                final Document message = parser.parse(new ByteArrayInputStream(messageXml.getBytes()));
                Node messageNode = message.getFirstChild();
                NamedNodeMap attributes = messageNode.getAttributes();
                attributes.removeNamedItem("xmlns");
                Node adoptedNode = doc.adoptNode(messageNode);
                //adoptedNode.setPrefix("km");
                root.appendChild(adoptedNode);
            }
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            writer.flush();
            String xml = writer.toString();
            LOG.debug(xml);
            try {
                if (!commonModule.hasValidSession(nihiiOrg)) {

                    EncryptionUtils encryptionUtils = PluginManager.get("ritme.recipe.connector.encryption", EncryptionUtils.class, null, nihiiOrg);

                    commonModule.setEncryptionUtils(encryptionUtils, nihiiOrg);
                    commonModule.createFallbackSession(null, encryptionUtils.getSystemKeystorePassword(null) == null ? null : new String(encryptionUtils.getSystemKeystorePassword(null)), nihiiOrg);

                    module.setEncryptionUtils(encryptionUtils, nihiiOrg);
                }
                module.sendNotification(xml.getBytes(),
                        patientSsin, Long.valueOf(receiverNihii), nihiiOrg);

            } catch (IntegrationModuleException e) {
                LOG.error(e.getMessage(), e);
                throw new RuntimeException("500", new Exception(e.getMessage(), e));
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("500", new Exception(e.getMessage(), e));
        } catch (TransformerConfigurationException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("500", new Exception(e.getMessage(), e));
        } catch (ParserConfigurationException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("500", new Exception(e.getMessage(), e));
        } catch (SAXException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("500", new Exception(e.getMessage(), e));
        } catch (TransformerException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("500", new Exception(e.getMessage(), e));
        }
        LOG.debug("Notification sent!");

    }

    @Override
    public List<URI> listOpenPrescriptions(String patientSsin, String prescriberNihii, String nihiiOrg, String remoteUser) {

        List<URI> uris = new ArrayList<URI>();

        try {
            if (!commonModule.hasValidSession(nihiiOrg)) {

                EncryptionUtils encryptionUtils = PluginManager.get("ritme.recipe.connector.encryption", EncryptionUtils.class, null, nihiiOrg);

                commonModule.setEncryptionUtils(encryptionUtils, nihiiOrg);
                commonModule.createFallbackSession(null, encryptionUtils.getSystemKeystorePassword(null) == null ? null : new String(encryptionUtils.getSystemKeystorePassword(null)), nihiiOrg);

                module.setEncryptionUtils(encryptionUtils, nihiiOrg);
            }
            final List<String> rids = module.listOpenPrescription(patientSsin, nihiiOrg, Long.valueOf(prescriberNihii));

            for (String rid : rids) {
                URI uri;
                try {
                    uri = new URI("recipe", rid, null);
                } catch (URISyntaxException e) {
                    continue;
                }
                uris.add(uri);

            }

        } catch (IntegrationModuleException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("500", new Exception(e.getMessage(), e));
        }
        return uris;
    }

    @Override
    public void revokePrescription(final String reason, final String ssinPatient, final String rid, final String nihiiOrg, final String remoteUser) {
        try {
            if (!commonModule.hasValidSession(nihiiOrg)) {

                EncryptionUtils encryptionUtils = PluginManager.get("ritme.recipe.connector.encryption", EncryptionUtils.class, null, nihiiOrg);

                commonModule.setEncryptionUtils(encryptionUtils, nihiiOrg);
                commonModule.createFallbackSession(null, encryptionUtils.getSystemKeystorePassword(null) == null ? null : new String(encryptionUtils.getSystemKeystorePassword(null)), nihiiOrg);

                module.setEncryptionUtils(encryptionUtils, nihiiOrg);
            }
            module.revokePrescription(rid, reason, nihiiOrg, ssinPatient);

        } catch (IntegrationModuleException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("500", new Exception(e.getMessage(), e));
        }

    }


    private List<List<MedicatieVoorschriftItem>> sortPrescriptions(final List<MedicatieVoorschriftItem> items) {
        List<List<MedicatieVoorschriftItem>> gesorteerdeLijst0 = sorteerOpType(items);
        List<List<MedicatieVoorschriftItem>> gesorteerdeLijst1 = new ArrayList<List<MedicatieVoorschriftItem>>();
        for(List<MedicatieVoorschriftItem> list : gesorteerdeLijst0)
        {
            gesorteerdeLijst1.addAll(sorteerOpQuantity(list));
        }
        gesorteerdeLijst0.clear();
        for(List<MedicatieVoorschriftItem> list : gesorteerdeLijst1)
        {
            gesorteerdeLijst0.addAll(sorteerOpExecutionDate(list));
        }
        gesorteerdeLijst1.clear();
        for(List<MedicatieVoorschriftItem> list : gesorteerdeLijst0)
        {
            gesorteerdeLijst1.addAll(sorteerOpInsuline(list));
        }
        gesorteerdeLijst0.clear();
        for(List<MedicatieVoorschriftItem> list : gesorteerdeLijst1)
        {
            gesorteerdeLijst0.addAll(sorteerPerX(list,10));
        }


        return gesorteerdeLijst0;
    }

    private List<List<MedicatieVoorschriftItem>> sorteerOpType(final List<MedicatieVoorschriftItem> items) {
        Map<PrescriptionType, List<MedicatieVoorschriftItem>> map = new HashMap<PrescriptionType, List<MedicatieVoorschriftItem>>();

        for (MedicatieVoorschriftItem item : items) {

            PrescriptionType type = determinePrescriptionType(item);

            if (map.get(type) == null) {
                map.put(type, new ArrayList<MedicatieVoorschriftItem>());

            }

            map.get(type).add(item);

        }
        List<List<MedicatieVoorschriftItem>> result = new ArrayList<List<MedicatieVoorschriftItem>>();
        result.addAll(map.values());
        return result;

    }


    private List<List<MedicatieVoorschriftItem>> sorteerOpExecutionDate(final List<MedicatieVoorschriftItem> items) {
        Map<DateTime, List<MedicatieVoorschriftItem>> map = new HashMap<DateTime, List<MedicatieVoorschriftItem>>();

        for (MedicatieVoorschriftItem item : items) {
            Date exDate = item.getExecutionDate() == null ? item.getPrescriptionDate() : item.getExecutionDate();
            DateTime executionDate = new DateTime().withMillis(exDate.getTime()).withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0);


            if (map.get(executionDate) == null) {
                map.put(executionDate, new ArrayList<MedicatieVoorschriftItem>());
            }

            map.get(executionDate).add(item);

        }
        List<List<MedicatieVoorschriftItem>> result = new ArrayList<List<MedicatieVoorschriftItem>>();
        result.addAll(map.values());
        return result;

    }

    private List<List<MedicatieVoorschriftItem>> sorteerOpQuantity(final List<MedicatieVoorschriftItem> items) {
        Map<Integer, List<MedicatieVoorschriftItem>> map = new HashMap<Integer, List<MedicatieVoorschriftItem>>();

        for (MedicatieVoorschriftItem item : items) {
            final Integer quantity = item.getQuantity();

            for (int i = 0; i < quantity; i++) {
                if (map.get(i) == null) {
                    map.put(i, new ArrayList<MedicatieVoorschriftItem>());
                }

                map.get(i).add(new MedicatieVoorschriftItem(
                        item.getPrescriptionDate(),
                        item.getExecutionDate(),
                        item.getExpirationDate(),
                        item.getPrescriberSSIN(),
                        item.getPrescriberNihiiOrg(),
                        item.getPrescriptionType(),
                        1,
                        item.getMedicatieSchemaItem(),
                        item.getSource(),
                        item.getUri()));
            }

        }
        List<List<MedicatieVoorschriftItem>> result = new ArrayList<List<MedicatieVoorschriftItem>>();
        result.addAll(map.values());
        return result;

    }

    //todo sorteren volgens insuline...

    private List<List<MedicatieVoorschriftItem>> sorteerOpInsuline(final List<MedicatieVoorschriftItem> items) {
        Map<Integer, List<MedicatieVoorschriftItem>> map = new HashMap<Integer, List<MedicatieVoorschriftItem>>();
        int counter = 0;


        for (MedicatieVoorschriftItem item : items) {
            int bucket = 0;
            final String id = item.getMedicatieSchemaItem().getIntendedMedication().getMedicationId();
            final MedicationIdType type = item.getMedicatieSchemaItem().getIntendedMedication().getMedicationIdType();

            Set<ATC> atcs = SAM_SERVICE.getATCForMedication(type, id);

            for (ATC atc : atcs) {
                if (atc.getAtcCv().contains("A10A")) {
                    counter++;
                    bucket = counter;
                } else {
                    bucket = 0;
                }
            }


            if (map.get(bucket) == null) {
                map.put(bucket, new ArrayList<MedicatieVoorschriftItem>());
            }
            map.get(bucket).add(item);


        }
        List<List<MedicatieVoorschriftItem>> result = new ArrayList<List<MedicatieVoorschriftItem>>();
        result.addAll(map.values());
        return result;

    }

    private List<List<MedicatieVoorschriftItem>> sorteerPerX(final List<MedicatieVoorschriftItem> items, int aantal) {
        List<List<MedicatieVoorschriftItem>> result = new ArrayList<List<MedicatieVoorschriftItem>>();
        final int step = items.size() / aantal;
        for(int x = 0; x< step+1; x++)
        {
            result.add(items.subList(x,x+aantal>items.size()?items.size():x+aantal));
        }
        return result;

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

    private static PrescriberIntegrationModuleImpl initModule()
            throws IntegrationModuleException, IOException {
        PrescriberIntegrationModuleImpl module = null;
        if ("true".equals(System.getProperty("mock"))) {
            LOG.debug("Using a mock module");
            module = new PrescriberIntegrationModuleMock();
        } else {
            if (System.getProperty("config") != null) {
                module = new PrescriberIntegrationModuleImpl(getCommonModule(), getCommonModule());
            } else {
                module = new PrescriberIntegrationModuleImpl();
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


}
