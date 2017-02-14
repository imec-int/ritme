package uz.ehealth.ritme.vitalink.logic;

import be.smals.safe.connector.domain.DataEntry;
import be.smals.safe.connector.domain.Node;
import be.smals.safe.connector.domain.protocol.FetchDataEntriesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdcuyp0 on 29-9-2015.
 */
public class FetchDataEntriesResponseToXmls implements F1<FetchDataEntriesResponse, List<byte[]>> {

    private static final Logger LOG = LoggerFactory.getLogger(FetchDataEntriesResponseToXmls.class);

    @Override
    public List<byte[]> invoke(final FetchDataEntriesResponse response) {
        List<byte[]> result = new ArrayList<byte[]>();
        // Process the nodes of the subject, the nodes (e.g. medication-scheme) contain the data entries and pagination information.
        for (Node node : response.getNodes()) {
            // Process each returned node
            FetchDataEntriesResponseToXmls.LOG.error("Node: '{}'", node.getName());

            // Verify the data entries
            for (DataEntry dataEntry : node.getDataEntries()) {
                // Retrieve the URI of the data entry
                result.add(dataEntry.getBusinessData());
            }

        }

        return result;
    }
}
