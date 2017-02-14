package uz.ehealth.ritme.outbound.formularium;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.ConfigHelper;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.outbound.internalauthentication.InternalAuthenticationService;
import uz.ehealth.ritme.outbound.medic.InternalMedicService;
import uz.ehealth.ritme.plugins.PluginManager;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by bdcuyp0 on 18-8-2016.
 */
public class RestFormulariumService implements FormulariumService {

    private static final Logger LOG = LoggerFactory.getLogger(RestFormulariumService.class);

    private String url;

    public RestFormulariumService() {
        Properties properties = new Properties();
        try {
            properties.load(InternalMedicService.class.getResourceAsStream(ConfigHelper.getConfigLocation("ritme.properties")));
            url = properties.getProperty("ritme.rest.formularium.url");

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }


    }

    @Override
    public FormulariumCode getFormulariumCodeForCnk(final Integer cnk, final String user, final String orgNIHII) {
        InternalAuthenticationService internalAuthenticationService = PluginManager.get("ritme.rest.formularium.authentication", InternalAuthenticationService.class);
        HttpClient httpClient = internalAuthenticationService.getHttpClient(user);
        String uri = url + "?nihiiOrg=" + orgNIHII + "&cnk=" + cnk;
        GetMethod get = new GetMethod(uri);
        String string = null;

        try {
            httpClient.executeMethod(get);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        if (get.getStatusCode() != 200) {
            LOG.error("HTTP status code: {}-{}", get.getStatusCode(), get.getStatusText());

        } else {
            try {
                string = get.getResponseBodyAsString();
                return JSONTools.unmarshal(string, JsonFormulariumCode.class);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);

            } finally {
                get.releaseConnection();
            }

        }
        return null;
    }
}
