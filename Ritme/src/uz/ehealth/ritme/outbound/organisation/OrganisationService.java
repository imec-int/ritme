package uz.ehealth.ritme.outbound.organisation;

import uz.ehealth.ritme.model.OrganisationType;

/**
 * Created by bdcuyp0 on 7-6-2016.
 */
public interface OrganisationService {
    public OrganisationData[] query(final String ehp, final String nihiiToSearch, final String cbe, OrganisationType type, String naam, String city, final String zipCode, final String email, final String user, final String nihiiOrg);
}
