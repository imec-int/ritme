package uz.ehealth.ritme.outbound.organisation;

import com.fasterxml.jackson.annotation.JsonProperty;
import uz.ehealth.ritme.model.IdType;
import uz.ehealth.ritme.model.OrganisationType;

/**
 * Created by bdcuyp0 on 7-6-2016.
 */
public class JsonOrganisationData implements OrganisationData {

    private final String name;
    private final String id;
    private final IdType idType;
    private final OrganisationType organisationType;

    public JsonOrganisationData(
            @JsonProperty("name") String name,
            @JsonProperty("id") String id,
            @JsonProperty("idType") String idType,
            @JsonProperty("organisationType") String organisationType) {
        this.name = name;
        this.id = id;
        this.idType = IdType.valueOf(idType);
        this.organisationType = OrganisationType.valueOf(organisationType);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public IdType getIdType() {
        return idType;
    }

    @Override
    public OrganisationType getOrganisationType() {
        return organisationType;
    }
}
