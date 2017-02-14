package uz.ehealth.ritme.vitalink;

import org.jetbrains.annotations.Nullable;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.vitalink.logic.FetchDataEntriesResponseToXmls;

import java.util.List;

/**
 * Date: 1-3-2016.
 */
public class DefaultSumehrService extends DefaultVitalinkService implements SumehrService {
    private static final FetchDataEntriesResponseToXmls FETCH_DATA_ENTRIES_RESPONSE_TO_XMLS = new FetchDataEntriesResponseToXmls();

    public DefaultSumehrService() {
        super("sumehr", "Lege sumehr");
    }

    @Nullable
    @Override
    public List<byte[]> retrieveSumehrAsXml(MedicData userData, String nihiiOrg, String subjectSsin) throws Exception {
        return getVitalinkNode(userData, nihiiOrg, subjectSsin, FETCH_DATA_ENTRIES_RESPONSE_TO_XMLS, byte[].class);
    }
}
