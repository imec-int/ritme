package uz.ehealth.ritme.outbound.patient;

import java.util.Date;

/**
 * Created by bdcuyp0 on 14-1-2016.
 */
public interface PatientData {
    public enum Sex {
        MALE, FEMALE, OTHER, UNKNOWN
    }

    public Sex getSex();

    public String getSSIN();

    public String getName();

    public String getFirstName();

    public Date getBirthDate();
}
