package uz.ehealth.ritme.outbound.hospital;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.ConfigHelper;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.outbound.internalauthentication.InternalAuthenticationService;
import uz.ehealth.ritme.outbound.medic.RestMedicService;
import uz.ehealth.ritme.plugins.PluginManager;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by bdcuyp0 on 18-8-2016.
 */
public class RestHospitalService implements HospitalService {

    private static final Logger LOG = LoggerFactory.getLogger(RestMedicService.class);

    private String url;


    public RestHospitalService() {
        Properties properties = new Properties();
        try {
            properties.load(RestMedicService.class.getResourceAsStream(ConfigHelper.getConfigLocation("ritme.properties")));
            url = properties.getProperty("ritme.rest.hospital.url");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public HospitalData getData(final String nihiiOrg, final String user) {
        InternalAuthenticationService internalAuthenticationService = PluginManager.get("ritme.rest.hospital.authentication", InternalAuthenticationService.class);

        HttpClient httpClient = internalAuthenticationService.getHttpClient(user);


        String uri = url + "?";
        if (nihiiOrg != null) {
            uri += ("nihiiOrg=" + nihiiOrg);
        }


        GetMethod get = new GetMethod(uri);
        String string = null;

        try {
            httpClient.executeMethod(get);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        if (get.getStatusCode() != 200) {
            LOG.error("HTTP status code: {}-{}", get.getStatusCode(), get.getStatusText());
            string = null;

        } else {
            try {
                string = get.getResponseBodyAsString();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                string = null;
            } finally {
                get.releaseConnection();
            }

        }
        LOG.info(string);
        final JsonHospitalData data = JSONTools.unmarshal(string, JsonHospitalData.class);
        return data;
    }

}
