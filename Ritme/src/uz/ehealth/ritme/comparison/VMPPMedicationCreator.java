package uz.ehealth.ritme.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.sam.SamService;
import uz.emv.sam.v1.domain.AMPP;
import uz.emv.sam.v1.domain.VMPP;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bdcuyp0 on 2-11-2015.
 */
class VMPPMedicationCreator implements F1<Medication, Medication> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VMPPMedicationCreator.class);
    private final SamService samService;


    VMPPMedicationCreator(SamService samService) {
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
                Set<VMPP> vmpps = new HashSet<VMPP>();
                for (AMPP ampp : medicaties) {
                    vmpps.add(ampp.getVMPP());
                }
                if (vmpps.size() > 1) {
                    LOGGER.warn("more than one VMPP for CNK: {}", source.getMedicationId());
                }
                if (vmpps.isEmpty()) {
                    return null;
                }
                VMPP vmpp = vmpps.toArray(new VMPP[vmpps.size()])[0];
                return new Medication(String.valueOf(vmpp.getId()), MedicationIdType.VMPP, vmpp.toString(), null);
            case MAG:
                break;
            case AMP:
                break;
            case INN:
                break;

            case VMPP:
                return source;
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
