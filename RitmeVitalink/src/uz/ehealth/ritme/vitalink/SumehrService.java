package uz.ehealth.ritme.vitalink;

import org.jetbrains.annotations.Nullable;
import uz.ehealth.ritme.outbound.medic.MedicData;

import java.util.List;

/**
 * Date: 8-4-2016.
 */
public interface SumehrService extends VitalinkService{
    @Nullable
    List<byte[]> retrieveSumehrAsXml(MedicData userData, String nihiiOrg, String subjectSsin) throws Exception;
}
