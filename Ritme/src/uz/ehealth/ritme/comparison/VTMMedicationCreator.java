package uz.ehealth.ritme.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.sam.SamService;
import uz.emv.sam.v1.domain.ATM;
import uz.emv.sam.v1.domain.VMP;
import uz.emv.sam.v1.domain.VTM;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bdcuyp0 on 2-11-2015.
 */
class VTMMedicationCreator implements F1<Medication, Medication> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCompareService.class);
    private final SamService samService;
    private final VMPMedicationCreator vmpMedicationCreator;
    private final ATMMedicationCreator atmMedicationCreator;

    public VTMMedicationCreator(final SamService samService) {
        this.samService = samService;
        this.atmMedicationCreator = new ATMMedicationCreator(samService);
        this.vmpMedicationCreator = new VMPMedicationCreator(samService);
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
                Medication atm = atmMedicationCreator.invoke(source);
                //noinspection TailRecursion
                return this.invoke(atm);
            }
            case MAG:
                break;
            case AMP: {
                Medication atm = atmMedicationCreator.invoke(source);
                //noinspection TailRecursion
                return this.invoke(atm);
            }
            case INN: {
                List<VMP> vmps = samService.getVMPById(Integer.valueOf(source.getMedicationId()));
                Set<VTM> vtms = new HashSet<VTM>();
                for (VMP vmp : vmps) {
                    vtms.add(vmp.getVTM());
                }
                if (vtms.size() > 1) {
                    LOGGER.warn("more than one VTM for VMP: {}", source.getMedicationId());
                }
                if (vtms.isEmpty()) {
                    return null;
                }
                VTM vtm = vtms.toArray(new VTM[vtms.size()])[0];
                return new Medication(String.valueOf(vtm.getVtmId()), MedicationIdType.VTM, vtm.getName(), null);

            }

            case VMPP: {
                Medication vmp = vmpMedicationCreator.invoke(source);
                //noinspection TailRecursion
                return this.invoke(vmp);
            }
            case ATM: {
                List<ATM> atms = samService.getATMById(Long.valueOf(source.getMedicationId()));
                if (atms.size() > 1) {
                    LOGGER.warn("more than one ATM for CNK: {}", source.getMedicationId());
                }
                if (atms.isEmpty()) {
                    return null;
                }
                ATM atm = atms.toArray(new ATM[atms.size()])[0];
                return new Medication(String.valueOf(atm.getVTM().getVtmId()), MedicationIdType.VTM, atm.getVTM().getName(), null);
            }

            case VTM:
                return source;
            case ATC:
                break;
        }
        return null;
    }
}
