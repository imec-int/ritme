package uz.ehealth.ritme.vitalink.logic;

import be.fgov.ehealth.standards.kmehr.schema.v1.KmehrmessageType;
import be.smals.safe.connector.domain.DataEntry;
import be.smals.safe.connector.domain.Node;
import be.smals.safe.connector.domain.protocol.FetchDataEntriesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdcuyp0 on 29-9-2015.
 */
public class FetchDataEntriesResponseToKmehrmessageTypes implements F1<FetchDataEntriesResponse, List<KmehrmessageType>> {

    private static final Logger LOG = LoggerFactory.getLogger(FetchDataEntriesResponseToKmehrmessageTypes.class);

    @Override
    public List<KmehrmessageType> invoke(final FetchDataEntriesResponse response) {
        List<KmehrmessageType> result = new ArrayList<KmehrmessageType>();
        try {
            // Process the nodes of the subject, the nodes (e.g. medication-scheme) contain the data entries and pagination information.
            for (Node node : response.getNodes()) {
                // Process each returned node
                LOG.debug("Node: '{}'", node.getName());
                JAXBContext jc = JAXBContext.newInstance("be.fgov.ehealth.standards.kmehr.schema.v1");

                // Verify the data entries
                for (DataEntry dataEntry : node.getDataEntries()) {


                    Unmarshaller unmarshaller = jc.createUnmarshaller();
                    ByteArrayInputStream xml = new ByteArrayInputStream(dataEntry.getBusinessData());
                    JAXBElement<KmehrmessageType> feed = unmarshaller.unmarshal(new StreamSource(xml), KmehrmessageType.class);
                    xml.close();
                    // Retrieve the URI of the data entry
                    result.add(feed.getValue());
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
