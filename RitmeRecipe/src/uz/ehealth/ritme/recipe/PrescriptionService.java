package uz.ehealth.ritme.recipe;

import uz.ehealth.ritme.model.MedicatieVoorschriftItem;

import java.net.URI;
import java.util.List;

/**
 * Created by bdcuyp0 on 24-3-2016.
 */
public interface PrescriptionService {
    URI createPrescription(String remoteUser, String patientSsin, final String nihiiOrg, List<MedicatieVoorschriftItem> items);

    List<MedicatieVoorschriftItem> sortAndCreatePrescriptions(String remoteUser, String patientSsin, String nihiiOrg, List<MedicatieVoorschriftItem> items);

    void sendNotification(String bericht, List<MedicatieVoorschriftItem> items, String patientSsin, String receiverNihii, String nihiiOrg, String remoteUser);

    List<URI> listOpenPrescriptions(String patientSsin, String prescriberNihii, String nihiiOrg, String remoteUser);

    void revokePrescription(String reason, String ssinPatient, String rid, String nihiiOrg, String remoteUser);
}
