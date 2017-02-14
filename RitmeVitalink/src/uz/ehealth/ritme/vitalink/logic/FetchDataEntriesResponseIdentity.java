package uz.ehealth.ritme.vitalink.logic;

import be.smals.safe.connector.domain.protocol.FetchDataEntriesResponse;
import uz.ehealth.ritme.core.F1;

import java.util.Collections;
import java.util.List;


/**
 * Created by bdcuyp0 on 20-5-2016.
 */
public class FetchDataEntriesResponseIdentity implements F1<FetchDataEntriesResponse, List<FetchDataEntriesResponse>> {
    @Override
    public List<FetchDataEntriesResponse> invoke(final FetchDataEntriesResponse source) {
        return Collections.singletonList(source);
    }
}
