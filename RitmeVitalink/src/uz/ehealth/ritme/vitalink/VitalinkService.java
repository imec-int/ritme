package uz.ehealth.ritme.vitalink;

import org.jetbrains.annotations.NotNull;
import uz.ehealth.ritme.model.VitalinkMetadata;
import uz.ehealth.ritme.outbound.medic.MedicData;

import java.util.List;

/**
 * Date: 30-5-2016.
 */
public interface VitalinkService {

    @NotNull
    List<VitalinkMetadata> getMetadataAsJSON(MedicData medicData, String nihiiOrg, String subjectSsin) throws Exception;

}
