package uz.ehealth.ritme.vitalink.logic;

import be.smals.safe.connector.domain.DataEntry;
import be.smals.safe.connector.domain.Node;
import be.smals.safe.connector.domain.protocol.FetchDataEntriesResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.MedicatieSchemaItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bdcuyp0 on 29-9-2015.
 */
public class FetchDataEntriesResponseToMedicatieSchemaItems implements F1<Pair<FetchDataEntriesResponse, String>, List<MedicatieSchemaItem>> {

    private static final Logger LOG = LoggerFactory.getLogger(FetchDataEntriesResponseToMedicatieSchemaItems.class);

    @Override
    public List<MedicatieSchemaItem> invoke(final Pair<FetchDataEntriesResponse, String> responseAndNihiiOrg) {
        List<MedicatieSchemaItem> result = new ArrayList<MedicatieSchemaItem>();
        try {
            // Process the nodes of the subject, the nodes (e.g. medication-scheme) contain the data entries and pagination information.
            for (Node node : responseAndNihiiOrg.getLeft().getNodes()) {
                // Process each returned node
                LOG.debug("Node: '{}'", node.getName());
                // Verify the data entries
                for (DataEntry dataEntry : node.getDataEntries()) {
                    Map<String, String> metadata = new HashMap<String, String>();
                    metadata.putAll(dataEntry.getMetadata());
                    // Retrieve the URI of the data entry
                    metadata.put("uri", "/Vitalink/" + node.getVersion() + dataEntry.getDataEntryURI());
                    metadata.put("source", "Vitalink");
                    metadata.put("nihiiOrg", responseAndNihiiOrg.getRight());
                    final MedicatieSchemaItem item = KmehrXmlToMedicatieSchemaItem.transformBusinessDataToMedicationSchemeItem(dataEntry.getBusinessData(), metadata);
                    result.add(item);
                }

            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return result;
    }

}
