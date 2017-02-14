package uz.ehealth.ritme.outbound.patient;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by bdcuyp0 on 18-8-2016.
 */
public class JsonPatientData implements PatientData {
    private final Sex sex;
    private final String ssin;
    private final String name;
    private final String firstName;
    private final Date birthDate;

    public JsonPatientData(
            @JsonProperty("sex") String sex,
            @JsonProperty("ssin") String ssin,
            @JsonProperty("name") String name,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("birthDate") long birthDate

    ) {
        this.sex = Sex.valueOf(sex);
        this.ssin = ssin;
        this.name = name;
        this.firstName = firstName;
        this.birthDate = new Date(birthDate);


    }

    @Override
    public Sex getSex() {
        return sex;
    }

    @Override
    public String getSSIN() {
        return ssin;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public Date getBirthDate() {
        return birthDate;
    }
}
