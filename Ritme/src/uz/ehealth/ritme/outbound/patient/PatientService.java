package uz.ehealth.ritme.outbound.patient;

/**
 * Created by bdcuyp0 on 14-1-2016.
 */
public interface PatientService {
    public PatientData getData(String ssin, final String user, final String nihiiOrg);
}
