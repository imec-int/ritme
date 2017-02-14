package uz.ehealth.ritme.outbound.organisation;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.ConfigHelper;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.model.IdType;
import uz.ehealth.ritme.model.OrganisationType;
import uz.ehealth.ritme.outbound.internalauthentication.InternalAuthenticationService;
import uz.ehealth.ritme.plugins.PluginManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by bdcuyp0 on 30-6-2016.
 */
public class InternalOrganisationService implements OrganisationService {

    private Map<String, OrganisationData[]> nihiiCache = new HashMap<String, OrganisationData[]>();

    private static final Logger LOG = LoggerFactory.getLogger(InternalOrganisationService.class);

    private String url;


    public InternalOrganisationService() {
        Properties properties = new Properties();
        try {
            properties.load(InternalOrganisationService.class.getResourceAsStream(ConfigHelper.getConfigLocation("ritme.properties")));
            url = properties.getProperty("ritme.internal.organisation.url");

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }


    }

    @Override
    public OrganisationData[] query(final String ehp, final String nihiiToSearch, final String cbe, final OrganisationType type, final String naam, final String city, final String zipCode, final String email, final String user, final String nihiiOrg) {
        final boolean otherParametersAreNull = zipCode == null && city == null && type == null && ehp == null && cbe == null && email == null;
        if (nihiiToSearch != null && nihiiCache.get(nihiiToSearch) != null && otherParametersAreNull) {
            return nihiiCache.get(nihiiToSearch);

        }


        InternalAuthenticationService internalAuthenticationService = PluginManager.get("ritme.outbound.internalauthentication.jersey", InternalAuthenticationService.class);

        HttpClient httpClient = internalAuthenticationService.getHttpClient(user);


        String uri = url + "/addressbookwebservice/api/v1/organisation/JSON/" + nihiiOrg + "/addressbook/organisation?";
        if (ehp != null) {
            uri += ("ehp=" + ehp);
        }
        if (naam != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("name=" + naam);
        }
        if (nihiiToSearch != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("nihii=" + nihiiToSearch);
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
        if (cbe != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("cbe=" + cbe);
        }
        if (email != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("email=" + email);
        }
        if (type != null) {
            if (!uri.endsWith("?")) {
                uri += "&";
            }

            uri += ("type=" + type.name());
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
        final JsonOrganisationData[] organisationDatas = JSONTools.unmarshal(string, JsonOrganisationData[].class);
        for (OrganisationData organisationData : organisationDatas) {
            if (organisationData.getIdType().equals(IdType.NIHII)) {
                nihiiCache.put(organisationData.getId(), new OrganisationData[]{organisationData});
            }
        }

        if (nihiiToSearch != null && otherParametersAreNull && organisationDatas.length == 0) {
            nihiiCache.put(nihiiToSearch, organisationDatas);
        }
        return organisationDatas;
        /*
        ClientConfig cfg = new DefaultClientConfig();
        Client client = Client.create(cfg);
        InternalAuthenticationService internalAuthenticationService = PluginManager.get("ritme.outbound.internalauthentication.jersey", InternalAuthenticationService.class);
        client.addFilter(internalAuthenticationService.getFilter(user));
        WebResource resource = client.resource(url + "/addressbookwebservice/api/v1/organisation/JSON/" + nihiiOrg + "/addressbook/organisation");
        if (ehp != null) {
            resource = resource.queryParam("ehp", ehp);
        }
        if (nihiiToSearch != null) {
            resource = resource.queryParam("nihii", nihiiToSearch);
        }
        if (cbe != null) {
            resource = resource.queryParam("cbe", cbe);
        }
        if (type != null) {
            resource = resource.queryParam("type", type.name());
        }
        if (naam != null) {
            resource = resource.queryParam("name", naam);
        }
        if (city != null) {
            resource = resource.queryParam("city", city);
        }
        if (zipCode != null) {
            resource = resource.queryParam("zipCode", zipCode);
        }
        if (email != null) {
            resource = resource.queryParam("email", email);
        }
        final WebResource.Builder builder = resource.accept(MediaType.APPLICATION_JSON_TYPE);
        LOG.info(resource.getURI().toString());
        final ClientResponse response = builder.get(ClientResponse.class);
        final String string;
        if (response.getStatus() != 200) {
            LOG.error("HTTP status code: {}", response.getStatus());
            string = "[]";
        } else {
            string = response.getEntity(String.class);
        }
        LOG.info(string);
        return JSONTools.unmarshal(string, JsonOrganisationData[].class);
        */
    }


}
