package uz.ehealth.ritme.outbound.internalauthentication;

import com.sun.jersey.api.client.filter.ClientFilter;
import org.apache.commons.httpclient.HttpClient;

/**
 * Created by bdcuyp0 on 30-6-2016.
 */
public interface InternalAuthenticationService {
    ClientFilter getFilter(String user);

    HttpClient getHttpClient(final String user);
}
