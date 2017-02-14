package uz.ehealth.ritme.kmehr;

import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;

import java.util.Date;
import java.util.List;

/**
 * Created by bdcuyp0 on 30-9-2015.
 */
public class KmehrMessageInfo {
    private final List<TransactionType> suspensions;
    private String patientSSIN;
    private String orgNIHII;
    private Date registrationDate;
    private TransactionType medicationSchemeElement;
    private String source;
    private String uri;

    public String getPatientSSIN() {
        return patientSSIN;
    }

    public String getOrgNIHII() {
        return orgNIHII;
    }

    public TransactionType getMedicationSchemeElement() {
        return medicationSchemeElement;
    }

    public KmehrMessageInfo(final String uri, final String source, final String patientSSIN, final String orgNIHII, final Date registrationDate, final TransactionType medicationSchemeElement, final List<TransactionType> suspensions) {
        this.patientSSIN = patientSSIN;
        this.orgNIHII = orgNIHII;
        this.registrationDate = registrationDate;
        this.medicationSchemeElement = medicationSchemeElement;
        this.source = source;
        this.uri = uri;
        this.suspensions = suspensions;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public String getUri() {
        return uri;
    }

    public String getSource() {
        return source;
    }

    public List<TransactionType> getSuspensions() {
        return suspensions;
    }
}
