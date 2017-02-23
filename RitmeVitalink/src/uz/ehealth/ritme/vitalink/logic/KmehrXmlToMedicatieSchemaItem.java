package uz.ehealth.ritme.vitalink.logic;

import be.fgov.ehealth.standards.kmehr.schema.v1.KmehrmessageType;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.MedicatieSchemaItem;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by bdcuyp0 on 16-2-2017.
 */
public class KmehrXmlToMedicatieSchemaItem implements F1<Pair<byte[], Map<String, String>>, MedicatieSchemaItem> {
    static final JAXBContext JC;

    private static final Logger LOG = LoggerFactory.getLogger(FetchDataEntriesResponseToMedicatieSchemaItems.class);

    static {
        JAXBContext jc = null;
        try {
            jc = JAXBContext.newInstance("be.fgov.ehealth.standards.kmehr.schema.v1");
        } catch (JAXBException e) {
            LOG.error(e.getMessage(), e);
        }
        JC = jc;
    }

    public static MedicatieSchemaItem transformBusinessDataToMedicationSchemeItem(@NotNull final byte[] businessData, final Map<String, String> metadata) throws JAXBException, IOException {
        Unmarshaller unmarshaller = JC.createUnmarshaller();
        ByteArrayInputStream xml = new ByteArrayInputStream(businessData);
        JAXBElement<KmehrmessageType> feed = unmarshaller.unmarshal(new StreamSource(xml), KmehrmessageType.class);
        xml.close();
        return new KmehrMessageTypeToMedicatieSchemaItems().invoke(Pair.of(metadata, feed.getValue()));
    }

    @Override
    public MedicatieSchemaItem invoke(final Pair<byte[], Map<String, String>> source) {
        try {
            return transformBusinessDataToMedicationSchemeItem(source.getLeft(), source.getRight());
        } catch (JAXBException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
