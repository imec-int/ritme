package uz.ehealth.ritme.outbound.medic;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.ConfigHelper;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.outbound.internalauthentication.InternalAuthenticationService;
import uz.ehealth.ritme.plugins.PluginManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by bdcuyp0 on 30-6-2016.
 */
public class InternalMedicService implements MedicService {

    private static final Logger LOG = LoggerFactory.getLogger(InternalMedicService.class);

    private Map<String, MedicData[]> ssinCache = new HashMap<String, MedicData[]>();
    private Map<String, MedicData[]> nihiiCache = new HashMap<String, MedicData[]>();

    private String url;


    public InternalMedicService() {
        Properties properties = new Properties();
        try {
            properties.load(InternalMedicService.class.getResourceAsStream(ConfigHelper.getConfigLocation("ritme.properties")));
            url = properties.getProperty("ritme.internal.medic.url");

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }


    }


    @Override
    public MedicData getData(final String medic, final String user, final String nihiiOrg) {
        return PluginManager.get("ritme.outbound.medic", MedicService.class).getData(medic, user, nihiiOrg);
    }

    @Override
    public MedicData[] query(final String ssin, final String name, final String firstName, final String zipCode, final String city, final String profession, final String nihiiPers, final String qualification, final String email, final String user, final String nihiiOrg) {

        if (ssin != null && ssinCache.get(ssin) != null && zipCode == null && city == null && profession == null && nihiiPers == null && qualification == null && email == null) {
            return ssinCache.get(ssin);

        }

        if (nihiiPers != null && nihiiCache.get(nihiiPers) != null && zipCode == null && city == null && profession == null && ssin == null && qualification == null && email == null) {
            return nihiiCache.get(nihiiPers);

        }


        InternalAuthenticationService internalAuthenticationService = PluginManager.get("ritme.outbound.internalauthentication.jersey", InternalAuthenticationService.class);

        HttpClient httpClient = internalAuthenticationService.getHttpClient(user);


        String uri = url + "/addressbookwebservice/api/v1/caretaker/JSON/" + nihiiOrg + "/addressbook/caretaker?";
        if (ssin != null) {
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
        String string = "[]";

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
        for (MedicData medicData : medicDatas) {
            ssinCache.put(medicData.getSsin(), new MedicData[]{medicData});
            for (String nihii : medicData.getNihii()) {
                nihiiCache.put(nihii, new MedicData[]{medicData});
            }
        }
        return medicDatas;

    }

    @Override
    public MedicData getLastInvolvedMedic(final String patientSSIN, final String user, final String nihiiOrg) {
        return PluginManager.get("ritme.outbound.medic", MedicService.class).getLastInvolvedMedic(patientSSIN, user, nihiiOrg);
    }
}
