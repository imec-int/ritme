package uz.ehealth.ritme.riziv;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by bdcuyp0 on 23-5-2016.
 */

public class RizivSilverPage {


    private final Integer conventionStatus;

    private final String firstName1;

    private final String firstName2;

    private final String gender;

    private final boolean isToBeConfirmed;

    private final String lastName;

    private final String nihdi;

    private final String professionCode;

    private final String professionDescription;

    private final String qualificationDescription;

    private final String qualificationInscriptionDate;
    private final String qualificationCode;

    public RizivSilverPage(
            @JsonProperty("ConventionStatus") Integer conventionStatus,
            @JsonProperty("FirstName1") String firstName1,
            @JsonProperty("FirstName2") String firstName2,
            @JsonProperty("Gender") String gender,
            @JsonProperty("IsToBeConfirmed") boolean isToBeConfirmed,
            @JsonProperty("LastName") String lastName,
            @JsonProperty("Nihdi") String nihdi,
            @JsonProperty("ProfessionCode") String professionCode,
            @JsonProperty("ProfessionDescription") String professionDescription,
            @JsonProperty("QualificationCode") String qualificationCode,
            @JsonProperty("QualificationDescription") String qualificationDescription,
            @JsonProperty("QualificationInscriptionDate") String qualificationInscriptionDate) {
        this.conventionStatus = conventionStatus;
        this.firstName1 = firstName1;
        this.firstName2 = firstName2;
        this.gender = gender;
        this.isToBeConfirmed = isToBeConfirmed;
        this.lastName = lastName;
        this.nihdi = nihdi;
        this.professionCode = professionCode;
        this.professionDescription = professionDescription;
        this.qualificationDescription = qualificationDescription;
        this.qualificationInscriptionDate = qualificationInscriptionDate;
        this.qualificationCode = qualificationCode;
    }

    public Integer getConventionStatus() {
        return conventionStatus;
    }

    public String getFirstName1() {
        return firstName1;
    }

    public String getFirstName2() {
        return firstName2;
    }

    public String getGender() {
        return gender;
    }

    public boolean isToBeConfirmed() {
        return isToBeConfirmed;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNihdi() {
        return nihdi;
    }

    public String getProfessionCode() {
        return professionCode;
    }

    public String getProfessionDescription() {
        return professionDescription;
    }

    public String getQualificationDescription() {
        return qualificationDescription;
    }

    public String getQualificationInscriptionDate() {
        return qualificationInscriptionDate;
    }

    public String getQualificationCode() {
        return qualificationCode;
    }
}
