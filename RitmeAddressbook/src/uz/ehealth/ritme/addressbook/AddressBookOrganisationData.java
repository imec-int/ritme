package uz.ehealth.ritme.addressbook;

import uz.ehealth.ritme.be.fgov.ehealth.addressbook.protocol.v1.SearchOrganizationsResponseType;
import uz.ehealth.ritme.model.IdType;
import uz.ehealth.ritme.model.OrganisationType;
import uz.ehealth.ritme.outbound.organisation.OrganisationData;

/**
 * Created by bdcuyp0 on 7-6-2016.
 */
public class AddressBookOrganisationData implements OrganisationData {
    private final SearchOrganizationsResponseType.HealthCareOrganization organisation;

    public AddressBookOrganisationData(final SearchOrganizationsResponseType.HealthCareOrganization organization) {
        this.organisation = organization;
    }

    @Override
    public String getName() {
        return organisation.getName().get(0).getValue();
    }

    @Override
    public String getId() {
        return organisation.getId().getValue();
    }

    @Override
    public IdType getIdType() {
        switch (IdType.valueOf(organisation.getId().getType())) {
            case HCI:
                return IdType.NIHII;
            case NIHII:
            case CBE:
            case EHP:
            default:
                return IdType.valueOf(organisation.getId().getType());
        }

    }

    @Override
    public OrganisationType getOrganisationType() {
        return OrganisationType.valueOf(organisation.getOrganizationTypeCode().get(0).getValue());
    }
}
