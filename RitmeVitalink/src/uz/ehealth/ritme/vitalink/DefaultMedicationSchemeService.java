package uz.ehealth.ritme.vitalink;

import be.ehealth.technicalconnector.session.SessionItem;
import be.fgov.ehealth.standards.kmehr.schema.v1.KmehrmessageType;
import be.smals.safe.connector.domain.DataEntry;
import be.smals.safe.connector.domain.Error;
import be.smals.safe.connector.domain.PersonInformation;
import be.smals.safe.connector.domain.protocol.FetchDataEntriesResponse;
import be.smals.safe.connector.domain.protocol.StoreDataEntriesRequest;
import be.smals.safe.connector.domain.protocol.StoreDataEntriesResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.kmehr.MedicatieSchemaItemsToVitalinkKmehr;
import uz.ehealth.ritme.model.*;
import uz.ehealth.ritme.outbound.hospital.HospitalData;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.patient.PatientData;
import uz.ehealth.ritme.plugins.PluginManager;
import uz.ehealth.ritme.sam.SamService;
import uz.ehealth.ritme.vitalink.logic.*;
import uz.emv.sam.v1.domain.AMP;
import uz.emv.sam.v1.domain.AMPIntermediatePackage;
import uz.emv.sam.v1.domain.AMPP;

import java.net.URI;
import java.util.*;


public class DefaultMedicationSchemeService extends DefaultVitalinkService implements MedicationSchemeService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMedicationSchemeService.class);
    private final SamService samService;
    private static final FetchDataEntriesResponseToXmls FETCH_DATA_ENTRIES_RESPONSE_TO_XMLS;
    private static final FetchDataEntriesResponseToKmehrmessageTypes FETCH_DATA_ENTRIES_RESPONSE_TO_KMEHRMESSAGE_TYPES;
    private static final FetchDataEntriesResponseToMedicatieSchemaItems FETCH_DATA_ENTRIES_RESPONSE_TO_MEDICATIE_SCHEMA_ITEMS;
    private static final FetchDataEntriesResponseIdentity FETCH_DATA_ENTRIES_IDENTITY;
    private static final KmehrXmlToMedicatieSchemaItem KMEHR_XML_TO_MEDICATIE_SCHEMA_ITEM;

    static {
        LOG.debug("Starting class init");
        FETCH_DATA_ENTRIES_RESPONSE_TO_XMLS = new FetchDataEntriesResponseToXmls();
        LOG.debug("FETCH_DATA_ENTRIES_RESPONSE_TO_XMLS loaded");
        FETCH_DATA_ENTRIES_RESPONSE_TO_KMEHRMESSAGE_TYPES = new FetchDataEntriesResponseToKmehrmessageTypes();
        LOG.debug("FETCH_DATA_ENTRIES_RESPONSE_TO_KMEHRMESSAGE_TYPES loaded");
        FETCH_DATA_ENTRIES_RESPONSE_TO_MEDICATIE_SCHEMA_ITEMS = new FetchDataEntriesResponseToMedicatieSchemaItems();
        LOG.debug("FETCH_DATA_ENTRIES_RESPONSE_TO_MEDICATIE_SCHEMA_ITEMS loaded");
        FETCH_DATA_ENTRIES_IDENTITY = new FetchDataEntriesResponseIdentity();
        LOG.debug("KMEHR_XML_TO_MEDICATIE_SCHEMA_ITEM loaded");
        KMEHR_XML_TO_MEDICATIE_SCHEMA_ITEM = new KmehrXmlToMedicatieSchemaItem();
        LOG.debug("Ending class init");
    }

    public DefaultMedicationSchemeService() {
        super("medication-scheme", "Leeg medicatieschema");
        samService = PluginManager.get("ritme.sam", SamService.class);
        LOG.debug("SAM Service loaded");
    }


    @Override
    @NotNull
    public List<byte[]> retrieveActualMedicationSchemeAsXml(MedicData medicData, String nihiiOrg, String subjectSsin, final Date endDateAfter, final List<MedicatieSchemaItemStatus> excludeStatus) throws Exception {
        List<FetchDataEntriesResponse> responses = getVitalinkNode(medicData, nihiiOrg, subjectSsin, FETCH_DATA_ENTRIES_IDENTITY, FetchDataEntriesResponse.class);
        final List<byte[]> selected = new ArrayList<byte[]>();
        for (FetchDataEntriesResponse response : responses) {
            final List<byte[]> kmehrMessageTypes = FETCH_DATA_ENTRIES_RESPONSE_TO_XMLS.invoke(response);
            final List<MedicatieSchemaItem> medicatieSchemaItems = FETCH_DATA_ENTRIES_RESPONSE_TO_MEDICATIE_SCHEMA_ITEMS.invoke(Pair.of(response, nihiiOrg));
            List<Integer> indexes = getFiltered(medicatieSchemaItems, endDateAfter, excludeStatus);
            for (Integer index : indexes) {
                selected.add(kmehrMessageTypes.get(index));
            }
        }
        return selected;
    }

    @NotNull
    public List<KmehrmessageType> retrieveActualMedicationSchemeAsKmehrmessageType(final MedicData medicData, String nihiiOrg, String subjectSsin, final Date endDateAfter, final List<MedicatieSchemaItemStatus> excludeStatus) throws Exception {
        List<FetchDataEntriesResponse> responses = getVitalinkNode(medicData, nihiiOrg, subjectSsin, FETCH_DATA_ENTRIES_IDENTITY, FetchDataEntriesResponse.class, true);
        final List<KmehrmessageType> selected = new ArrayList<KmehrmessageType>();
        for (FetchDataEntriesResponse response : responses) {
            final List<KmehrmessageType> kmehrMessageTypes = FETCH_DATA_ENTRIES_RESPONSE_TO_KMEHRMESSAGE_TYPES.invoke(response);
            final List<MedicatieSchemaItem> medicatieSchemaItems = FETCH_DATA_ENTRIES_RESPONSE_TO_MEDICATIE_SCHEMA_ITEMS.invoke(Pair.of(response, nihiiOrg));
            List<Integer> indexes = getFiltered(medicatieSchemaItems, endDateAfter, excludeStatus);
            for (Integer index : indexes) {
                selected.add(kmehrMessageTypes.get(index));
            }
        }
        return selected;
    }

    private List<Integer> getFiltered(final List<MedicatieSchemaItem> medicatieSchemaItems, final Date endDateAfter, final List<MedicatieSchemaItemStatus> excludeStatus) {
        List<Integer> selected = new ArrayList<Integer>();
        for (int i = 0; i < medicatieSchemaItems.size(); i++) {
            selected.add(i);
        }
        selected = filterEndDate(medicatieSchemaItems, endDateAfter, selected);
        selected = filterStopStatus(medicatieSchemaItems, excludeStatus, selected);
        selected = filterSuspendedStatus(medicatieSchemaItems, excludeStatus, selected);
        return selected;
    }

    private List<Integer> filterSuspendedStatus(final List<MedicatieSchemaItem> medicatieSchemaItems, final List<MedicatieSchemaItemStatus> excludeStatus, final List<Integer> input) {
        List<Integer> selected = new ArrayList<Integer>();
        if (excludeStatus.contains(MedicatieSchemaItemStatus.SUSPENDED)) {
            for (Integer index : input) {
                MedicatieSchemaItem medicatieSchemaItem = medicatieSchemaItems.get(index);
                if (medicatieSchemaItem.getSuspensions() == null || medicatieSchemaItem.getSuspensions().length == 0) {
                    selected.add(index);
                } else {

                    boolean suspended = false;
                    for (Suspension suspension : medicatieSchemaItem.getSuspensions()) {
                        if (suspension.getStopDate() != null && suspension.getStopDate().after(new Date())) {
                            suspended = true;
                        }
                    }
                    if (!suspended) {
                        selected.add(index);
                    }

                }
            }
            return selected;
        } else {
            return input;
        }
    }

    private List<Integer> filterStopStatus(final List<MedicatieSchemaItem> medicatieSchemaItems, final List<MedicatieSchemaItemStatus> excludeStatus, final List<Integer> input) {
        if (excludeStatus.contains(MedicatieSchemaItemStatus.STOPPED)) {
            List<Integer> selected = new ArrayList<Integer>();
            for (Integer index : input) {
                MedicatieSchemaItem medicatieSchemaItem = medicatieSchemaItems.get(index);
                if (medicatieSchemaItem.getSuspensions() == null || medicatieSchemaItem.getSuspensions().length == 0) {
                    selected.add(index);
                } else {

                    boolean stopped = false;
                    for (Suspension suspension : medicatieSchemaItem.getSuspensions()) {
                        if (suspension.getStopDate() == null) {
                            stopped = true;
                        }
                    }
                    if (!stopped) {
                        selected.add(index);
                    }

                }
            }
            return selected;
        } else {
            return input;
        }


    }

    private List<Integer> filterEndDate(final List<MedicatieSchemaItem> medicatieSchemaItems, final Date endDateAfter, final List<Integer> input) {
        if (endDateAfter != null) {
            List<Integer> selected = new ArrayList<Integer>();

            for (Integer i : input) {

                MedicatieSchemaItem medicatieSchemaItem = medicatieSchemaItems.get(i);
                if (medicatieSchemaItem.getStopDate() == null || medicatieSchemaItem.getStopDate().after(endDateAfter)) {

                    selected.add(i);
                }
            }

            return selected;
        } else {
            return input;
        }
    }

    @Override
    @NotNull
    public MedicatieSchema retrieveActualMedicationSchemeAsMedicatieSchemaItems(final MedicData medicData, String nihiiOrg, String subjectSsin, final Date endDateAfter, final List<MedicatieSchemaItemStatus> excludeStatus) throws Exception {
        List<FetchDataEntriesResponse> responses = getVitalinkNode(medicData, nihiiOrg, subjectSsin, FETCH_DATA_ENTRIES_IDENTITY, FetchDataEntriesResponse.class, true);
        final List<MedicatieSchemaItem> selected = new ArrayList<MedicatieSchemaItem>();
        Calendar lastUpdated = null;
        int version = 0;
        Integer nodeVersion = null;
        for (FetchDataEntriesResponse response : responses) {
            lastUpdated = (lastUpdated == null || response.getLastUpdated().compareTo(lastUpdated) > 0) ? response.getLastUpdated() : lastUpdated;
            version = response.getVersion() > version ? response.getVersion() : version;
            nodeVersion = response.getNodes() != null && !response.getNodes().isEmpty() ? response.getNodes().get(0).getVersion() : null;
            final List<MedicatieSchemaItem> medicatieSchemaItems = FETCH_DATA_ENTRIES_RESPONSE_TO_MEDICATIE_SCHEMA_ITEMS.invoke(Pair.of(response, nihiiOrg));
            List<Integer> indexes = getFiltered(medicatieSchemaItems, endDateAfter, excludeStatus);

            for (Integer index : indexes) {
                selected.add(medicatieSchemaItems.get(index));
            }
        }
        return new MedicatieSchema(version, nodeVersion, lastUpdated == null ? null : lastUpdated.getTime(), selected);
    }

    @Override
    @NotNull
    public MedicatieSchema retrieveActualMedicationSchemeVersion(final MedicData medicData, String nihiiOrg, String subjectSsin, final Date endDateAfter, final List<MedicatieSchemaItemStatus> excludeStatus) throws Exception {
        List<FetchDataEntriesResponse> responses = getVitalinkNode(medicData, nihiiOrg, subjectSsin, FETCH_DATA_ENTRIES_IDENTITY, FetchDataEntriesResponse.class, false);
        Calendar lastUpdated = null;
        int version = 0;
        Integer nodeVersion = null;
        for (FetchDataEntriesResponse response : responses) {
            lastUpdated = (lastUpdated == null || response.getLastUpdated().compareTo(lastUpdated) > 0) ? response.getLastUpdated() : lastUpdated;
            version = response.getVersion() > version ? response.getVersion() : version;
            nodeVersion = response.getNodes() != null && !response.getNodes().isEmpty() ? response.getNodes().get(0).getVersion() : null;

        }
        return new MedicatieSchema(version, nodeVersion, lastUpdated == null ? null : lastUpdated.getTime(), Collections.<MedicatieSchemaItem>emptyList());
    }


    @NotNull
    @Override
    public List<MedicatieSchemaItem> saveMedicatieSchemaItems(final MedicData medicData, String nihiiOrg, final HospitalData hospitalData, List<MedicatieSchemaItem> medicatieSchemaItems, final PatientData patientData, String schemaVersie) throws Exception {
        List<MedicatieSchemaItem> returnItems = new ArrayList<MedicatieSchemaItem>();

        List<MedicatieSchemaItem> cnkOfInn = new ArrayList<MedicatieSchemaItem>();

        Boolean active = null;

        for (MedicatieSchemaItem item : medicatieSchemaItems) {
            if (active == null) {
                active = item.isActive();
            } else {
                if (!active.equals(item.isActive())) {
                    throw new RuntimeException("400", new Exception("active and ended items should not be mixed"));
                }
            }

            if (item.getIntendedMedication().getMedicationIdType() == MedicationIdType.AMP) {
                final Set<AMPP> ampps = getAMPPsForAMP(item.getIntendedMedication());
                AMPP selected = null;
                if (!ampps.isEmpty()) {
                    selected = getSmallestPackage(ampps);
                    MedicatieSchemaItem cnkItem = new MedicatieSchemaItem(
                            item.getUri(),
                            item.getSource(),
                            item.getType(),
                            item.getRegistrationDate(),
                            item.getPatientSSIN(),
                            item.getMedicSSIN(),
                            item.getMedicNIHII(),
                            item.getOrgNIHII(),
                            new Medication(String.valueOf(selected.getAmppId()), MedicationIdType.CNK, selected.getName(), null),
                            item.getDeliveredMedication(),
                            item.getDrugRoute(),
                            item.getMedicationUse(),
                            item.getStartDate(),
                            item.getStopDate(),
                            item.getBeginCondition(),
                            item.getEndCondition(),
                            item.getPeriodicity(),
                            item.getRegimenItems(),
                            item.getPosology(),
                            item.getInstructionForPatient(),
                            item.getInstructionForOverdosing(),
                            item.getInstructionForReimbursement(),
                            item.getTransactionReason(),
                            item.isPatientOrigin(),
                            item.isActive(),
                            item.isValidated(), item.getSuspensions());
                    cnkOfInn.add(cnkItem);
                }

            } else {
                cnkOfInn.add(item);
            }
        }

        Map<Medication, List<MedicatieSchemaItem>> perMedicatie = new HashMap<Medication, List<MedicatieSchemaItem>>();


        for (MedicatieSchemaItem item : cnkOfInn) {

            Medication currentMedication = item.getDeliveredMedication() != null ? item.getDeliveredMedication() : item.getIntendedMedication();

            List<MedicatieSchemaItem> items = perMedicatie.get(currentMedication);
            if (items == null) {
                items = new ArrayList<MedicatieSchemaItem>();

                perMedicatie.put(currentMedication, items);
            }

            items.add(item);
        }


        /*******************************
         * Initialize Session Management
         *******************************/
        // Initialize a valid Session (see SessionManagementExample for details).
        // Under normal usage this only needs to be done once (at the beginning)
        SessionItem sessionItem = initializeSessionManagementForOrganisation(nihiiOrg);
        PersonInformation pInfo = new PersonInformation(medicData.getFirstName(), medicData.getName(), medicData.getSsin());
        pInfo.setRole(getMetaDataRole(medicData));
        pInfo.setNihii(medicData.getNihii().length > 0 ? medicData.getNihii()[0] : null);


        /*******************************
         * Define the values used in this example
         *******************************/

        Integer nodeVersion = 0;
        StoreDataEntriesResponse response = null;


        for (Map.Entry<Medication, List<MedicatieSchemaItem>> medicatieItems : perMedicatie.entrySet()) {

            String vitalinkMedicatieNr = null;
            String vitalinkMedicatieVersie = null;
            boolean isValidated = true;
            for (MedicatieSchemaItem item : medicatieItems.getValue()) {
                isValidated = isValidated && item.isValidated();
                if ("Vitalink".equals(item.getSource()) && item.getUri() != null) {
                    final String[] split = item.getUri().split("/");
                    if (vitalinkMedicatieNr == null || vitalinkMedicatieNr.equals(split[6])) {
                        vitalinkMedicatieNr = split[6];
                        if (vitalinkMedicatieVersie == null || Integer.valueOf(vitalinkMedicatieVersie) < Integer.valueOf(split[7])) {
                            vitalinkMedicatieVersie = split[7];
                        }
                    }
                }
            }


            // 2. Build a medication scheme document in KMEHR format
            byte[] kmehrMedicationScheme = new MedicatieSchemaItemsToVitalinkKmehr(medicData, hospitalData, patientData).invoke(medicatieItems.getValue()).getBytes();


            active = (active == null) ? Boolean.TRUE : active;
            // 3. Define the required metadata
            Map<String, String> metadata = new HashMap<String, String>();
            metadata.put("languageCode", "nl-BE");
            metadata.put("availabilityStatus", active ? "active" : "ended");
            metadata.put("formatCode", "KMEHR_20120401");
            metadata.put("mimeType", "text/xml");
            metadata.put("encryptionFlag", "encrypted");
            metadata.put("validationStatus", isValidated ? "validated" : "toBeValidated");

            // 4. Define your own reference to identify the data entry during the store operation.
            // This reference / correlationID is not saved, but is sent back in the response as a reference.
            String reference = UUID.randomUUID().toString();

            // 5. Define the nodeVersion for the dataEntry (this is required for the medication-scheme node normal flow)
            // the nodeVersion should be taken from the retrieved dataEntry
            nodeVersion = Integer.valueOf(schemaVersie);


            // 1. Create the URI
            // The SSIN of the subject for which the data entry must be saved.
            // Build the Vitalink URI of the Data Entry to be saved.
            String uri = "/subject/" + patientData.getSSIN() + "/medication-scheme";
            if (vitalinkMedicatieNr == null) {
                uri += "/new";
            } else {
                uri += "/" + vitalinkMedicatieNr + "/new/" + (Integer.valueOf(vitalinkMedicatieVersie));
            }

            LOG.info("tried URI {}", "/Vitalink/" + nodeVersion + uri);


            /*******************************
             * Build Request
             *******************************/
            // Create the store data entries operation request
            StoreDataEntriesRequest request = new StoreDataEntriesRequest(patientData.getSSIN());
            // Create the Data Entry with URI, reference and payload (non-encrypted XML as byte array) and add the metadata
            DataEntry dataEntry = new DataEntry(uri, kmehrMedicationScheme, reference, nodeVersion).withMetadata(metadata);
            // Add the complete Data Entry (URI, payload, reference, metadata) to the request
            request.getDataEntries().add(dataEntry);

            pInfo.setRole(getMetaDataRole(medicData));
            request.setPersonInformation(pInfo);

            /*******************************
             * Send Request to Vitalink
             *******************************/
            response = getVitalink().storeDataEntries(request, sessionItem);

            /*******************************
             * Verify response
             *******************************/
            // A unique message ID to track the request/response. Use this as a reference if you contact the Vitalink helpdesk.
            LOG.info("PATIENT={},ARTS={},ZIEKENHUIS={},STATUS={}", patientData.getSSIN(), medicData.getSsin(), nihiiOrg, response.getStatus().getCode());


            // Response should be 200, indicating that everything is OK
            // If the response is not 200, an error has occurred.
            if (response.getStatus().getCode() != 200) {
                // See the status code documentation in the cookbook for more information on the possible errors.
                // Most errors will be related to the Data Entry not being OK, in this example the focus is on those errors.

                // The code and message describe to problem that has occurred.
                int errCode = response.getStatus().getCode();
                String errMessage = response.getStatus().getMessage();
                // The software should interpret the error and take action accordingly
                LOG.error("Error while saving data entries for subject, code: '{}', message: '{}'", errCode, errMessage);

                if (response.getStatus().getErrors() != null) {
                    // Retrieve the errors for each data entry
                    for (Error error : response.getStatus().getErrors()) {
                        // Each error will have a reference to the data entry for which the error has occurred.
                        String subErrReference = error.getReference();
                        // The code and message describe to problem that has occurred.
                        int subErrCode = error.getCode();
                        String subErrMessage = error.getMessage();
                        // The software should interpret the error and take action accordingly
                        LOG.error("Error while saving data entry '{}', code: '{}', message: '{}'", subErrReference, subErrCode, subErrMessage);
                    }
                }


                throw new RuntimeException(new Exception(Integer.toString(errCode), new Exception(errMessage)));
            }


            // The store returns URI's for each data entry that is saved
            // The client software should save this URI as it is needed for further communication with Vitalink regarding this data entry.
            for (DataEntry savedDataEntry : response.getDataEntries()) {
                URI result = null;
                // retrieve the reference and the URI for the saved data entry
                String ref = savedDataEntry.getReference();
                String dataEntryURI = savedDataEntry.getDataEntryURI();
                // The software should process (save) this data
                LOG.info("Data Entry saved! Reference: '{}', URI: '{}'", ref, dataEntryURI);
                nodeVersion = savedDataEntry.getNodeVersion();
                try {
                    result = URI.create("/Vitalink/" + nodeVersion + dataEntryURI);
                    LOG.debug("result URI {}", result);
                    /*
                    Map<String,String> itemMetadata = new HashMap<String, String>();
                    itemMetadata.put("uri", result.toString());
                    itemMetadata.put("nihiiOrg", nihiiOrg);
                    itemMetadata.put("source", "Vitalink");
                    */
                    //converteer businessdata naar json
                    //zet de URI correct
                    for (MedicatieSchemaItem item : medicatieItems.getValue()) {
                        MedicatieSchemaItem updatedItem = new MedicatieSchemaItem(result.toString(), "Vitalink", item.getType(), item.getRegistrationDate(), item.getPatientSSIN(), item.getMedicSSIN(), item.getMedicNIHII(), item.getOrgNIHII(), item.getIntendedMedication(), item.getDeliveredMedication(), item.getDrugRoute(), item.getMedicationUse(), item.getStartDate(), item.getStopDate(), item.getBeginCondition(), item.getEndCondition(), item.getPeriodicity(), item.getRegimenItems(), item.getPosology(), item.getInstructionForPatient(), item.getInstructionForOverdosing(), item.getInstructionForReimbursement(), item.getTransactionReason(), item.isPatientOrigin(), item.isActive(), item.isValidated(), item.getSuspensions());
                        //MedicatieSchemaItem item = KMEHR_XML_TO_MEDICATIE_SCHEMA_ITEM.invoke(Pair.of(savedDataEntry.getBusinessData(),itemMetadata));
                        //voeg toe aan returnItems
                        returnItems.add(updatedItem);
                    }

                } catch (IllegalArgumentException e) {
                    //throw new RuntimeException("500", e);
                }


            }
        }

        //voor het tegelijk saven van verschillende medicaties moeten de businessdata van de saved items aangepast worden met de nieuwe URIs
        //als er maar 1 gesaved wordt, dan wordt alleen de URI als return value gebruikt.
        return returnItems;

    }

    @Override
    public URI saveMedicatieSchemaItem(final MedicData medicData, String nihiiOrg, final HospitalData hospitalData, List<MedicatieSchemaItem> medicatieSchemaItems, final PatientData patientData, String schemaVersie) throws Exception {
        List<MedicatieSchemaItem> items = saveMedicatieSchemaItems(medicData, nihiiOrg, hospitalData, medicatieSchemaItems, patientData, schemaVersie);
        if (items.isEmpty()) {
            throw new RuntimeException("404");
        }
        return URI.create(items.get(0).getUri());
    }

    public AMPP getSmallestPackage(final Set<AMPP> ampps) {
        Integer smallest = null;
        AMPP pack = null;
        for (AMPP ampp : ampps) {
            if (smallest == null || smallest.compareTo(ampp.getContentMultiplier()) > 0) {
                smallest = ampp.getContentMultiplier();
                pack = ampp;
            }

        }
        return pack;
    }

    public Set<AMPP> getAMPPsForAMP(final Medication intendedMedication) {
        Set<AMPP> result = new HashSet<AMPP>();
        if (intendedMedication.getMedicationIdType() == MedicationIdType.AMP) {
            final List<AMP> amps = samService.getAMPById(Long.valueOf(intendedMedication.getMedicationId()));
            for (AMP amp : amps) {
                final Set<AMPIntermediatePackage> ampips = amp.getAMPIntermediatePackages();
                for (AMPIntermediatePackage ampip : ampips) {
                    result.addAll(ampip.getAMPPs());
                }
            }
        }
        return result;
    }
}
