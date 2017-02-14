package uz.ehealth.ritme.riziv;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.medic.MedicService;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdcuyp0 on 24-5-2016.
 */
public class RizivMedicService implements MedicService {
    private static final Logger LOG = LoggerFactory.getLogger(RizivMedicService.class);
    private Client client;

    public RizivMedicService() {
        ClientConfig cfg = new DefaultClientConfig();
        client = Client.create(cfg);
    }

    @Override
    public MedicData getData(final String medic, final String user, final String nihiiOrg) {
        final List<MedicData> result = getRizivMedicData(null, null, null, medic);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }

    }

    @Override
    public MedicData[] query(final String ssin, final String naam, final String firstName, final String zipCode, final String city, final String profession, final String nihiiPers, final String qualification, final String eMail, final String user, final String nihiiOrg) {

        final String adaptedProfession;
        if ("persphysician".equals(profession)) {
            adaptedProfession = "10";
        } else if ("perspharmacist".equals(profession)) {
            adaptedProfession = "20";
        } else {
            adaptedProfession = null;
        }
        final List<MedicData> result = getRizivMedicData(qualification, adaptedProfession, city == null ? zipCode : city, naam == null ? nihiiPers : naam);
        return result.toArray(new MedicData[result.size()]);
    }

    @Override
    public MedicData getLastInvolvedMedic(final String patientSSIN, final String user, final String nihiiOrg) {
        return null;
    }


    private List<MedicData> getRizivMedicData(final String qualification, final String what, final String where, final String who) {


        WebResource resource = client.resource("https://www.inami.fgov.be/webprd/appl/psilverpages/api/Query");
        resource = resource.queryParam("qualification", qualification == null ? "" : qualification);

        resource = resource.queryParam("what", what == null ? "" : what);

        resource = resource.queryParam("where", where == null ? "" : where);

        resource = resource.queryParam("who", who == null ? "" : who);


        final WebResource.Builder builder = resource.accept(MediaType.APPLICATION_JSON_TYPE);


        LOG.info(resource.getURI().toString());

        ClientResponse response = builder.get(ClientResponse.class);


        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        String string = response.getEntity(String.class);
        LOG.info(string);

        RizivSilverPage[] pages = JSONTools.unmarshal(string, RizivSilverPage[].class);

        List<MedicData> medicData = new ArrayList<MedicData>();
        for (RizivSilverPage page : pages) {
            medicData.add(new RizivMedicData(page));
        }
        return medicData;
    }
}
