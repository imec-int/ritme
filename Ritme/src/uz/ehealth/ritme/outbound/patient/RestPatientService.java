package uz.ehealth.ritme.outbound.patient;

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
public class RestPatientService implements PatientService {

    private static final Logger LOG = LoggerFactory.getLogger(RestPatientService.class);

    private String url;


    public RestPatientService() {
        Properties properties = new Properties();
        try {
            properties.load(RestMedicService.class.getResourceAsStream(ConfigHelper.getConfigLocation("ritme.properties")));
            url = properties.getProperty("ritme.rest.patient.url");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public PatientData getData(final String ssin, final String user, final String nihiiOrg) {
        InternalAuthenticationService internalAuthenticationService = PluginManager.get("ritme.rest.patient.authentication", InternalAuthenticationService.class);

        HttpClient httpClient = internalAuthenticationService.getHttpClient(user);


        String uri = url + "?";
        if (nihiiOrg != null) {
            uri += ("nihiiOrg=" + nihiiOrg);
        }

        if (ssin != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }
            uri += ("ssin=" + ssin);
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
        final JsonPatientData data = JSONTools.unmarshal(string, JsonPatientData.class);
        return data;

    }
}
