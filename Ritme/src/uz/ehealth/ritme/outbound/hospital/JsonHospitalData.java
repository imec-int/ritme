package uz.ehealth.ritme.outbound.hospital;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bdcuyp0 on 18-8-2016.
 */
public class JsonHospitalData implements HospitalData {

    private final String telefoon;
    private final String fax;
    private final String email;
    private final String website;
    private final String straat;
    private final String huisNr;
    private final String busNr;
    private final String gemeente;
    private final String postNummer;
    private final String districtOfStaat;
    private final String land;
    private final String name;
    private final String nihii;

    public JsonHospitalData(
            @JsonProperty("telefoon") String telefoon,
            @JsonProperty("fax") String fax,
            @JsonProperty("email") String email,
            @JsonProperty("website") String website,
            @JsonProperty("straat") String straat,
            @JsonProperty("huisNr") String huisNr,
            @JsonProperty("busNr") String busNr,
            @JsonProperty("gemeente") String gemeente,
            @JsonProperty("postNummer") String postNummer,
            @JsonProperty("districtOfStaat") String districtOfStaat,
            @JsonProperty("land") String land,
            @JsonProperty("name") String name,
            @JsonProperty("nihii") String nihii

    ) {
        this.telefoon = telefoon;
        this.fax = fax;
        this.email = email;
        this.website = website;
        this.straat = straat;
        this.huisNr = huisNr;
        this.busNr = busNr;
        this.gemeente = gemeente;
        this.postNummer = postNummer;
        this.districtOfStaat = districtOfStaat;
        this.land = land;
        this.name = name;
        this.nihii = nihii;
    }

    @Override
    public String getTelefoon() {
        return telefoon;
    }

    @Override
    public String getFax() {
        return fax;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getWebsite() {
        return website;
    }

    @Override
    public String getStraat() {
        return straat;
    }

    @Override
    public String getHuisNr() {
        return huisNr;
    }

    @Override
    public String getBusNr() {
        return busNr;
    }

    @Override
    public String getGemeente() {
        return gemeente;
    }

    @Override
    public String getPostNummer() {
        return postNummer;
    }

    @Override
    public String getDistrictOfStaat() {
        return districtOfStaat;
    }

    @Override
    public String getLand() {
        return land;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNihii() {
        return nihii;
    }
}
