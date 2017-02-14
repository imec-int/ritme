package uz.ehealth.ritme.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.sam.SamService;
import uz.emv.sam.v1.domain.AMP;
import uz.emv.sam.v1.domain.AMPP;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bdcuyp0 on 2-11-2015.
 */
class AMPMedicationCreator implements F1<Medication, Medication> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AMPMedicationCreator.class);
    private final SamService samService;

    public AMPMedicationCreator(final SamService samService) {
        this.samService = samService;
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
            case CNK:
                List<AMPP> medicaties = samService.getAMPPByCnk(Integer.parseInt(source.getMedicationId()));
                Set<AMP> amps = new HashSet<AMP>();
                for (AMPP ampp : medicaties) {
                    amps.add(ampp.getAMPIntermediatePackage().getAMP());
                }
                if (amps.size() > 1) {
                    LOGGER.warn("more than one AMP for CNK: {}", source.getMedicationId());
                }
                if (amps.isEmpty()) {
                    return null;
                }
                AMP amp = amps.toArray(new AMP[amps.size()])[0];
                return new Medication(String.valueOf(amp.getAmpId()), MedicationIdType.AMP, amp.getName(), null);
            case INN:
                break;
            case MAG:
                break;
            case AMP:
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
