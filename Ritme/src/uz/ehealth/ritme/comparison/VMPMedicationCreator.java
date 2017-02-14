package uz.ehealth.ritme.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.sam.SamService;
import uz.emv.sam.v1.domain.AMP;
import uz.emv.sam.v1.domain.VMP;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bdcuyp0 on 2-11-2015.
 */
public class VMPMedicationCreator implements F1<Medication, Medication> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VMPMedicationCreator.class);
    private final SamService samService;
    private final AMPMedicationCreator ampMedicationCreator;


    public VMPMedicationCreator(final SamService samService) {
        this.samService = samService;
        this.ampMedicationCreator = new AMPMedicationCreator(samService);
    }

    @Override
    public Medication invoke(final Medication source) {
        if (source == null) {
            return null;
        }
        if (source.getMedicationId() == null) {
            return null;
        }
        switch (source.getMedicationIdType()) {

            case EAN:
                break;
            case CNK: {
                Medication ampMedicatie = ampMedicationCreator.invoke(source);
                //noinspection TailRecursion
                return invoke(ampMedicatie);
            }

            case MAG:
                break;
            case AMP: {
                List<AMP> amps = samService.getAMPById(Long.valueOf(source.getMedicationId()));
                Set<VMP> vmps = new HashSet<VMP>();
                for (AMP amp : amps) {
                    vmps.add(amp.getVMP());
                }
                if (vmps.size() > 1) {
                    LOGGER.warn("more than one AMP for CNK: {}", source.getMedicationId());
                }
                if (vmps.isEmpty()) {
                    return null;
                }
                VMP vmp = vmps.toArray(new VMP[vmps.size()])[0];
                return new Medication(String.valueOf(vmp.getVmpId()), MedicationIdType.INN, vmp.getName(), null);
            }

            case INN:
                return source;
            case VMPP:
                break;
            case ATM:
                break;
            case VTM:
                break;
            case ATC:
                break;
        }
        return null;
    }

}
