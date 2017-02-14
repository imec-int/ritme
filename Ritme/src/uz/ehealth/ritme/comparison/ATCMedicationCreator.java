package uz.ehealth.ritme.comparison;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.sam.SamService;
import uz.emv.sam.v1.domain.AMP;
import uz.emv.sam.v1.domain.ATC;

import java.util.*;

/**
 * Created by bdcuyp0 on 2-11-2015.
 */
public class ATCMedicationCreator implements F1<Medication, Medication> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ATCMedicationCreator.class);
    private final SamService samService;
    private final AMPMedicationCreator ampMedicationCreator;
    private static final Map<Integer, Integer> LEVEL_TO_LENGTH = new HashMap<Integer, Integer>();
    private static final Map<Integer, Integer> LENGTH_TO_LEVEL = new HashMap<Integer, Integer>();

    static {
        LEVEL_TO_LENGTH.put(1, 1);
        LEVEL_TO_LENGTH.put(2, 3);
        LEVEL_TO_LENGTH.put(3, 4);
        LEVEL_TO_LENGTH.put(4, 5);
        LEVEL_TO_LENGTH.put(5, 7);
        LENGTH_TO_LEVEL.put(1, 1);
        LENGTH_TO_LEVEL.put(3, 2);
        LENGTH_TO_LEVEL.put(4, 3);
        LENGTH_TO_LEVEL.put(5, 4);
        LENGTH_TO_LEVEL.put(7, 5);
    }

    private final int level;

    public ATCMedicationCreator(final SamService samService, final int level) {
        this.samService = samService;
        this.ampMedicationCreator = new AMPMedicationCreator(samService);
        this.level = level;
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
                Medication medication = ampMedicationCreator.invoke(source);
                //noinspection TailRecursion
                return invoke(medication);

            }



            case MAG:
                break;
            case AMP: {
                List<AMP> amps = samService.getAMPById(Long.valueOf(source.getMedicationId()));
                Set<ATC> atcs = new HashSet<ATC>();
                for (AMP amp : amps) {
                    atcs.add(amp.getATC());
                }
                if (atcs.size() > 1) {
                    LOGGER.warn("more than one AMP for CNK: {}", source.getMedicationId());
                }
                if (!atcs.isEmpty()) {
                    ATC atc = atcs.toArray(new ATC[atcs.size()])[0];
                    if (atc != null && atc.getAtcCv() != null) {
                        if (atc.getAtcCv().length() < LEVEL_TO_LENGTH.get(level)) {
                            // kan niet omzetten naar een ATC-level dat dieper in de boom zit (vb. ATC2 naar ATC3)
                            break;
                        }
                        while (atc.getAtcCv().length() > LEVEL_TO_LENGTH.get(level)) {
                            atc = atc.getParent();
                        }
                        return new Medication(atc.getAtcCv(), MedicationIdType.ATC, atc.getName(), null);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            case INN:
                break;
            case VMPP:
                break;
            case ATM:
                break;
            case VTM:
                break;
            case ATC:
                List<ATC> atcs = samService.getATCByAtcCv(source.getMedicationId());
                if (!atcs.isEmpty()) {
                    ATC atc = atcs.get(0);
                    if (atc.getAtcCv().length() < LEVEL_TO_LENGTH.get(level)) {
                        // kan niet omzetten naar een ATC-level dat dieper in de boom zit (vb. ATC2 naar ATC3)
                        break;
                    }
                    while (atc.getAtcCv().length() > LEVEL_TO_LENGTH.get(level)) {
                        atc = atc.getParent();
                    }
                    return new Medication(atc.getAtcCv(), MedicationIdType.ATC, atc.getName(), null);
                }
                return source;
        }
        return null;
    }

    public static Integer getLevel(final String atcCv) {
        return LENGTH_TO_LEVEL.get(atcCv.length());
    }
}
