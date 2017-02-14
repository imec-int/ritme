package uz.ehealth.ritme.outbound.internalauthentication;

import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.mina.util.ExpiringMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.connector.RequestFilter;
import uz.ehealth.ritme.core.ConfigHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * Created by bdcuyp0 on 30-6-2016.
 */
public class BasicAuthenticationRestService implements InternalAuthenticationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticationRestService.class);

    private static final int TIME_TO_LIVE_SECONDS = 60 * 60;
    private static final int EXPIRATION_INTERVAL_SECONDS = 5 * 60;
    private ExpiringMap<String, HttpClient> httpClientCache = new ExpiringMap<String, HttpClient>(TIME_TO_LIVE_SECONDS, EXPIRATION_INTERVAL_SECONDS);
    private MultiThreadedHttpConnectionManager connectionManager;

    public BasicAuthenticationRestService() {
        int total = 800;
        int perHost = 400;
        Properties properties = new Properties();
        try {
            properties.load(BasicAuthenticationRestService.class.getResourceAsStream(ConfigHelper.getConfigLocation("ritme.properties")));
            total = Integer.parseInt(properties.getProperty("ritme.connections.total", "800"));
            //2*N en N is aantal concurrent users
            perHost = Integer.parseInt(properties.getProperty("ritme.connections.perHost", "400"));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setDefaultMaxConnectionsPerHost(perHost);
        params.setMaxTotalConnections(total);
        connectionManager.setParams(params);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connectionManager.shutdown();
    }


    @Override
    public ClientFilter getFilter(final String user) {
        return new HTTPBasicAuthFilter(user, getPassword());
    }

    @Override
    public HttpClient getHttpClient(final String user) {
        HttpClient httpClient = httpClientCache.get(user);
        final String pwd = getPassword();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                user,
                pwd
        );


        if (httpClient == null || !httpClient.getState().getCredentials(AuthScope.ANY).equals(credentials)) {

            httpClient = new HttpClient(connectionManager);

            httpClient.getState().setCredentials(
                    AuthScope.ANY,
                    credentials
            );

            httpClientCache.put(user, httpClient);

        }
        return httpClient;
    }

    @NotNull
    public String getPassword() {
        HttpServletRequest request = (HttpServletRequest) RequestFilter.getCurrentRequest();
        final String header = request.getHeader("Authorization");
        final String encoded = header.substring(header.indexOf("Basic ") + "Basic ".length());
        Base64 codec = new Base64();
        final String decoded = new String(codec.decode(encoded));
        return decoded.substring(decoded.indexOf(":") + 1);
    }
}
