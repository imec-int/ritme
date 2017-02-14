package uz.ehealth.ritme.vitalink;

import org.jetbrains.annotations.NotNull;
import uz.ehealth.ritme.model.MedicatieSchema;
import uz.ehealth.ritme.model.MedicatieSchemaItem;
import uz.ehealth.ritme.model.MedicatieSchemaItemStatus;
import uz.ehealth.ritme.outbound.hospital.HospitalData;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.patient.PatientData;

import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * Created by bdcuyp0 on 25-3-2016.
 */
public interface MedicationSchemeService extends VitalinkService {

    @NotNull
    List<byte[]> retrieveActualMedicationSchemeAsXml(MedicData userData, String nihiiOrg, String subjectSsin, final Date endDateAfter, final List<MedicatieSchemaItemStatus> excludeStatus) throws Exception;

    @NotNull
    MedicatieSchema retrieveActualMedicationSchemeAsMedicatieSchemaItems(final MedicData userData, String nihiiOrg, String subjectSsin, final Date endDateAfter, final List<MedicatieSchemaItemStatus> excludeStatus) throws Exception;

    @NotNull
    MedicatieSchema retrieveActualMedicationSchemeVersion(final MedicData medicData, String nihiiOrg, String subjectSsin, Date endDateAfter, List<MedicatieSchemaItemStatus> excludeStatus) throws Exception;

    URI saveMedicatieSchemaItem(final MedicData userData, String nihiiOrg, final HospitalData hospitalData, List<MedicatieSchemaItem> medicatieSchemaItems, final PatientData patientData, String schemaVersie) throws Exception;
}
