package uz.ehealth.ritme.vitalink;

import be.ehealth.technicalconnector.session.SessionItem;
import be.smals.safe.connector.domain.*;
import be.smals.safe.connector.domain.Error;
import be.smals.safe.connector.domain.protocol.FetchDataEntriesRequest;
import be.smals.safe.connector.domain.protocol.FetchDataEntriesResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.VitalinkMetadata;
import uz.ehealth.ritme.model.VitalinkMetadataEntry;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.medic.MedicService;
import uz.ehealth.ritme.plugins.PluginManager;

import java.util.*;

/**
 * Date: 8-4-2016.
 */
public class DefaultVitalinkService extends RitmeVitalink implements VitalinkService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultVitalinkService.class);

    private final String node;
    private final String emptyError;

    protected DefaultVitalinkService(String node, String emptyError){
        this.node = node;
        this.emptyError = emptyError;
    }

    @NotNull
    @Override
    public List<VitalinkMetadata> getMetadataAsJSON(MedicData userData, String nihiiOrg, String subjectSsin) throws Exception {
        return getVitalinkNode(userData, nihiiOrg, subjectSsin, new F1<FetchDataEntriesResponse, List<VitalinkMetadata>>() {
            @Override
            public List<VitalinkMetadata> invoke(FetchDataEntriesResponse response) {
                List<VitalinkMetadata> result = new ArrayList<VitalinkMetadata>();
                // Process the nodes of the subject, the nodes (e.g. medication-scheme) contain the data entries and pagination information.
                for (Node node : response.getNodes()) {

                    // Verify the data entries
                    for (DataEntry dataEntry : node.getDataEntries()) {
                        // Retrieve the URI of the data entry

                        List<VitalinkMetadataEntry> metadataEntries = new ArrayList<VitalinkMetadataEntry>();
                        for (Map.Entry<String, String> metaDataMapEntry : dataEntry.getMetadata().entrySet()) {
                            metadataEntries.add(new VitalinkMetadataEntry(metaDataMapEntry.getKey(), metaDataMapEntry.getValue()));
                        }

                        result.add(new VitalinkMetadata(metadataEntries));
                    }
                }

                return result;
            }
        }, VitalinkMetadata.class, false);
    }

    public static String getMetaDataRole(final MedicData medicData) {
        if (medicData.getRole().startsWith("pers")) {
            return medicData.getRole().substring("pers".length());
        } else {
            return medicData.getRole();
        }
    }


    @NotNull
    protected <T, C extends F1<FetchDataEntriesResponse, List<T>>> List<T> getVitalinkNode(MedicData userData, String nihiiOrg, String subjectSsin, final C converter, final Class<T> item) throws Exception {
        return getVitalinkNode(userData, nihiiOrg, subjectSsin, converter, item, true);
    }

    @NotNull
    public <T, C extends F1<FetchDataEntriesResponse, List<T>>> List<T> getVitalinkNode(MedicData userData, String nihiiOrg, String subjectSsin, final C converter, final Class<T> item, boolean includeBusinessData) throws Exception {
        /*******************************
         * Initialize Session Management
         *******************************/
        // Initialize a valid Session (see SessionManagementExample for details).
        // Under normal usage this only needs to be done once (at the beginning)
        // 1. Define the SSIN of the subject to retrieve
        SessionItem sessionItem = initializeSessionManagementForOrganisation(nihiiOrg);
        final List<T> items = new ArrayList<T>();
        PaginationInfo paginationInfo = new PaginationInfo("/subject/" + subjectSsin + "/" + node, 0, 100, 10);

        while (paginationInfo != null && paginationInfo.hasNextPage()) {

            final Pair<PaginationInfo, List<T>> result = getPage(subjectSsin, sessionItem, userData, paginationInfo.getNextPagination(), item, converter, nihiiOrg, node, includeBusinessData);
            paginationInfo = result.getKey();
            items.addAll(result.getValue());
        }

        if (items.isEmpty()) {
            LOG.info("No items found for {}", subjectSsin);
            throw new RuntimeException(new Exception("404", new Exception(emptyError)));
        }

        return items;
    }

    private synchronized <T, C extends F1<FetchDataEntriesResponse, List<T>>> Pair<PaginationInfo, List<T>> getPage(final String subjectSsin, final SessionItem sessionItem, final MedicData medicData, final Pagination pagination, final Class<T> item, final C converter, final String nihiiOrg, final String node, boolean includeBusinessData) {
        /*******************************
         * Build Request
         *******************************/
        // Create the fetch request
        FetchDataEntriesRequest request = new FetchDataEntriesRequest(subjectSsin, includeBusinessData);
        request.setPagination(pagination);

        PersonInformation pInfo = new PersonInformation(medicData.getFirstName(), medicData.getName(), medicData.getSsin());
        pInfo.setRole(getMetaDataRole(medicData));
        pInfo.setNihii(medicData.getNihii().length > 0 ? medicData.getNihii()[0] : null);
        request.setPersonInformation(pInfo);

        //other options are vaccination or sumehr
        //request.setPagination(new Pagination("/subject/" + subjectID + "/vaccination", 1));
        //request.setPagination(new Pagination("/subject/" + subjectID + "/sumehr", 1));

        /*******************************
         * Send Request to Vitalink
         *******************************/
        final FetchDataEntriesResponse response = getVitalink().fetchDataEntries(request, sessionItem);

        /*******************************
         * Verify response
         *******************************/
        // A unique message ID to track the request/response. Use this as a reference if you contact the Vitalink helpdesk.
        //Assert.assertNotNull(response.getServerMessageID());

        LOG.info("PATIENT={},ARTS={},ZIEKENHUIS={},STATUS={},NODE={}", subjectSsin, medicData.getSsin(), nihiiOrg, response.getStatus().getCode(), node);

        // Response should be 200, indicating that everything is OK
        // If the response is not 200, an error has occured.
        if (response.getStatus().getCode() != 200 && response.getStatus().getCode() != 202) {
            // See the status code documentation in the cookbook for more information on the possible errors.
            // The code and message describe to problem that has occured.
            int errCode = response.getStatus().getCode();
            String errMessage = response.getStatus().getMessage();
            // The software should interpret the error and take action accordingly
            LOG.error("Error while fetching data entries for subject, code: '{}', message: '{}'", errCode, errMessage);

            if (response.getStatus().getErrors() != null) {
                // Retrieve the errors for each data entry
                for (Error error : response.getStatus().getErrors()) {
                    // Each error will have a reference to the data entry for which the error has occured.
                    String subErrReference = error.getReference();
                    // The code and message describe to problem that has occured.
                    int subErrCode = error.getCode();
                    String subErrMessage = error.getMessage();
                    // The software should interpret the error and take action accordingly
                    LOG.error("Error while fetching data entry '{}', code: '{}', message: '{}'", subErrReference, subErrCode, subErrMessage);
                }
            }
            throw new RuntimeException(new Exception(Integer.toString(errCode), new Exception(errMessage)));
        } else {
            List<Node> nodes = response.getNodes();
            PaginationInfo paginationInfo = null;
            if (!nodes.isEmpty()) {
                paginationInfo = nodes.get(0).getPagination();
            }

            // Retrieve the latest version for the given subjectID
            int version = response.getVersion();
            // Retrieve the time on which the last change has occured
            Calendar lastUpdated = response.getLastUpdated();
            // Process the data (e.g. update local database if the returned version is newer)
            LOG.info("Subject '{}' is at version '{}' and has been updated on '{}'", subjectSsin, version, lastUpdated.getTime());
            return Pair.of(paginationInfo, converter.invoke(response));
        }
    }

    @NotNull
    public static MedicData getMedicData(final String remoteUser, final String nihiiOrg) {
        MedicService medicService = PluginManager.get("ritme.outbound.medic", MedicService.class);
        MedicData medicData = medicService.getData(remoteUser, remoteUser, nihiiOrg);
        if (!Arrays.asList(medicData.getOrgNihii()).contains(nihiiOrg)) {
            throw new RuntimeException("403", new Exception("U mag het ziekenhuiscertificaat niet gebruiken"));
        }
        return medicData;
    }
}
