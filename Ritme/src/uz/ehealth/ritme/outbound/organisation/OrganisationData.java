package uz.ehealth.ritme.outbound.organisation;

import uz.ehealth.ritme.model.IdType;
import uz.ehealth.ritme.model.OrganisationType;

/**
 * Created by bdcuyp0 on 7-6-2016.
 */
public interface OrganisationData {
    String getName();

    String getId();

    IdType getIdType();

    OrganisationType getOrganisationType();
}
