package uz.ehealth.ritme.outbound.medic;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bdcuyp0 on 24-5-2016.
 */
public class JsonMedicData implements MedicData {
    private final String firstName;
    private final String name;
    private final String[] orgNihii;
    private final String[] nihii;
    private final String ssin;
    private final String role;

    public JsonMedicData(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("name") String name,
            @JsonProperty("orgNihii") String[] orgNihii,
            @JsonProperty("nihii") String[] nihii,
            @JsonProperty("ssin") String ssin,
            @JsonProperty("role") String role) {
        this.firstName = firstName;
        this.name = name;
        this.orgNihii = orgNihii;
        this.nihii = nihii;
        this.ssin = ssin;
        this.role = role;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getOrgNihii() {
        return orgNihii;
    }

    @Override
    public String[] getNihii() {
        return nihii;
    }

    @Override
    public String getSsin() {
        return ssin;
    }

    @Override
    public String getRole() {
        return role;
    }


}
