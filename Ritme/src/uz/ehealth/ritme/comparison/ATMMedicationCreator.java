package uz.ehealth.ritme.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.sam.SamService;
import uz.emv.sam.v1.domain.AMP;
import uz.emv.sam.v1.domain.ATM;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bdcuyp0 on 2-11-2015.
 */
class ATMMedicationCreator implements F1<Medication, Medication> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ATMMedicationCreator.class);
    private final SamService samService;
    private AMPMedicationCreator ampMedicationCreator;

    public ATMMedicationCreator(final SamService samService) {
        this.samService = samService;
        ampMedicationCreator = new AMPMedicationCreator(samService);
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
                Medication amp = ampMedicationCreator.invoke(source);
                //noinspection TailRecursion
                return this.invoke(amp);
            }

            case MAG:
                break;
            case AMP: {
                List<AMP> amps = samService.getAMPById(Long.valueOf(source.getMedicationId()));
                if (amps.size() > 1) {
                    LOGGER.warn("more than one AMP for CNK: {}", source.getMedicationId());
                }
                Set<ATM> atms = new HashSet<ATM>();
                for (AMP amp : amps) {
                    atms.add(amp.getATM());
                }
                if (atms.size() > 1) {
                    LOGGER.warn("more than one ATM for CNK: {}", source.getMedicationId());
                }
                if (atms.isEmpty()) {
                    LOGGER.warn("no ATM for CNK: {}", source.getMedicationId());
                    return null;
                }
                ATM atm = atms.toArray(new ATM[atms.size()])[0];
                return new Medication(String.valueOf(atm.getAtmId()), MedicationIdType.ATM, atm.getName(), null);
            }
            case INN:
                break;
            case VMPP:
                break;
            case ATM:
                return source;
            case VTM:
                break;
            case ATC:
                break;
        }
        return null;
    }
}
