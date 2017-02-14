package uz.ehealth.ritme.outbound.medic;

/**
 * Created by bdcuyp0 on 24-6-2015.
 */
public interface MedicService {
    public MedicData getData(String medic, final String user, final String nihiiOrg);

    public MedicData[] query(final String ssin, String naam, final String firstName, final String zipCode, String city, String profession, final String nihiiPers, String qualification, final String eMail, final String user, final String nihiiOrg);

    public MedicData getLastInvolvedMedic(String patientSSIN, final String user, final String nihiiOrg);
}
