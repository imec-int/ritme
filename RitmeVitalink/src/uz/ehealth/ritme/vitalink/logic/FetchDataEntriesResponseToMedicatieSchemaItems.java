package uz.ehealth.ritme.vitalink.logic;

import be.fgov.ehealth.standards.kmehr.schema.v1.KmehrmessageType;
import be.smals.safe.connector.domain.DataEntry;
import be.smals.safe.connector.domain.Node;
import be.smals.safe.connector.domain.protocol.FetchDataEntriesResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.MedicatieSchemaItem;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bdcuyp0 on 29-9-2015.
 */
public class FetchDataEntriesResponseToMedicatieSchemaItems implements F1<Pair<FetchDataEntriesResponse, Map<String, String>>, List<MedicatieSchemaItem>> {

    private static final Logger LOG = LoggerFactory.getLogger(FetchDataEntriesResponseToMedicatieSchemaItems.class);

    @Override
    public List<MedicatieSchemaItem> invoke(final Pair<FetchDataEntriesResponse, Map<String, String>> responseAndMetaData) {
        List<MedicatieSchemaItem> result = new ArrayList<MedicatieSchemaItem>();
        try {
            // Process the nodes of the subject, the nodes (e.g. medication-scheme) contain the data entries and pagination information.
            for (Node node : responseAndMetaData.getLeft().getNodes()) {
                // Process each returned node
                LOG.debug("Node: '{}'", node.getName());
                node.getVersion();
                JAXBContext jc = JAXBContext.newInstance("be.fgov.ehealth.standards.kmehr.schema.v1");
                // Verify the data entries
                for (DataEntry dataEntry : node.getDataEntries()) {


                    Unmarshaller unmarshaller = jc.createUnmarshaller();
                    ByteArrayInputStream xml = new ByteArrayInputStream(dataEntry.getBusinessData());
                    JAXBElement<KmehrmessageType> feed = unmarshaller.unmarshal(new StreamSource(xml), KmehrmessageType.class);
                    xml.close();
                    final Map<String, String> meta = dataEntry.getMetadata();
                    // Retrieve the URI of the data entry
                    meta.put("uri", "/Vitalink/" + node.getVersion() + dataEntry.getDataEntryURI());
                    meta.put("source", "Vitalink");
                    for (Map.Entry<String, String> entry : responseAndMetaData.getRight().entrySet()) {
                        meta.put(entry.getKey(), entry.getValue());
                    }


                    result.add(new KmehrMessageTypeToMedicatieSchemaItems().invoke(Pair.of(meta, feed.getValue())));
                }

            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return result;
    }
}
