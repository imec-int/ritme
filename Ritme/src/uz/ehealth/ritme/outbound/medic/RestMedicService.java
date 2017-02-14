package uz.ehealth.ritme.outbound.medic;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.ConfigHelper;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.outbound.internalauthentication.InternalAuthenticationService;
import uz.ehealth.ritme.plugins.PluginManager;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by bdcuyp0 on 18-8-2016.
 */
public class RestMedicService implements MedicService {

    private static final Logger LOG = LoggerFactory.getLogger(RestMedicService.class);

    private String url;


    public RestMedicService() {
        Properties properties = new Properties();
        try {
            properties.load(RestMedicService.class.getResourceAsStream(ConfigHelper.getConfigLocation("ritme.properties")));
            url = properties.getProperty("ritme.rest.medic.url");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }


    @Override
    public MedicData getData(final String medic, final String user, final String nihiiOrg) {
        MedicData[] results = this.query(medic, null, null, null, null, null, null, null, null, null, user, nihiiOrg);
        return results.length == 0 ? null : results[0];
    }

    @Override
    public MedicData[] query(final String ssin, final String name, final String firstName, final String zipCode, final String city, final String profession, final String nihiiPers, final String qualification, final String email, final String user, final String nihiiOrg) {
        return this.query(null, ssin, name, firstName, zipCode, city, profession, nihiiPers, qualification, email, user, nihiiOrg);
    }

    @Override
    public MedicData getLastInvolvedMedic(final String ssin, final String user, final String nihiiOrg) {
        InternalAuthenticationService internalAuthenticationService = PluginManager.get("ritme.rest.medic.authentication", InternalAuthenticationService.class);

        HttpClient httpClient = internalAuthenticationService.getHttpClient(user);


        String uri = url + "/getLastInvolvedMedic" + "?";
        if (ssin != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("ssin=" + ssin);
        }
        if (nihiiOrg != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

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
            string = "[]";

        } else {
            try {
                string = get.getResponseBodyAsString();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                string = "";
            } finally {
                get.releaseConnection();
            }

        }
        LOG.info(string);
        final JsonMedicData medicData;
        if (StringUtils.isEmpty(string)) {
            medicData = null;
        } else {
            medicData = JSONTools.unmarshal(string, JsonMedicData.class);
        }
        return medicData;
    }


    private MedicData[] query(final String userId, final String ssin, final String name, final String firstName, final String zipCode, final String city, final String profession, final String nihiiPers, final String qualification, final String email, final String user, final String nihiiOrg) {

        InternalAuthenticationService internalAuthenticationService = PluginManager.get("ritme.rest.medic.authentication", InternalAuthenticationService.class);

        HttpClient httpClient = internalAuthenticationService.getHttpClient(user);


        String uri = url + "/query" + "?";
        if (userId != null) {
            uri += ("userId=" + userId);
        }
        if (nihiiOrg != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("nihiiOrg=" + nihiiOrg);
        }
        if (ssin != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("ssin=" + ssin);
        }
        if (name != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("name=" + name);
        }
        if (firstName != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("firstName=" + firstName);
        }
        if (zipCode != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("zipCode=" + zipCode);
        }
        if (city != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("city=" + city);
        }
        if (profession != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("profession=" + profession);
        }
        if (email != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("email=" + email);
        }
        if (nihiiPers != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("nihiiPers=" + nihiiPers);
        }
        if (qualification != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("qualification=" + qualification);
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
            string = "[]";

        } else {
            try {
                string = get.getResponseBodyAsString();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                string = "[]";
            } finally {
                get.releaseConnection();
            }

        }
        LOG.info(string);
        final JsonMedicData[] medicDatas = JSONTools.unmarshal(string, JsonMedicData[].class);
        return medicDatas;
    }
}
