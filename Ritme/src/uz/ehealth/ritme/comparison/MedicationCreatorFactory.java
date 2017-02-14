package uz.ehealth.ritme.comparison;

import org.apache.commons.lang3.tuple.Pair;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.plugins.PluginManager;
import uz.ehealth.ritme.sam.SamService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdcuyp0 on 15-7-2016.
 */
public class MedicationCreatorFactory {
    static final SamService SAM_SERVICE = PluginManager.get("ritme.sam", SamService.class);
    static final F1<Medication, Medication> ATC3_MEDICATION_CREATOR = new ATCMedicationCreator(SAM_SERVICE, 3);
    static final F1<Medication, Medication> ATC4_MEDICATION_CREATOR = new ATCMedicationCreator(SAM_SERVICE, 4);
    static final F1<Medication, Medication> ATC5_MEDICATION_CREATOR = new ATCMedicationCreator(SAM_SERVICE, 5);
    static final F1<Medication, Medication> VTM_MEDICATION_CREATOR = new VTMMedicationCreator(SAM_SERVICE);
    static final F1<Medication, Medication> ATM_MEDICATION_CREATOR = new ATMMedicationCreator(SAM_SERVICE);
    static final F1<Medication, Medication> VMP_MEDICATION_CREATOR = new VMPMedicationCreator(SAM_SERVICE);
    static final F1<Medication, Medication> VMPP_MEDICATION_CREATOR = new VMPPMedicationCreator(SAM_SERVICE);
    static final F1<Medication, Medication> AMP_MEDICATION_CREATOR = new AMPMedicationCreator(SAM_SERVICE);
    static final F1<Medication, Medication> IDENTITY_MEDICATION_CREATOR = new F1<Medication, Medication>() {
        @Override
        public Medication invoke(final Medication source) {
            return source;
        }
    };
    private static final Map<Pair<MedicationIdType, Integer>, F1<Medication, Medication>> LOOKUP = new HashMap<Pair<MedicationIdType, Integer>, F1<Medication, Medication>>();
    static final Pair<MedicationIdType, Integer> ATC3_KEY = Pair.of(MedicationIdType.ATC, 3);
    static final Pair<MedicationIdType, Integer> ATC4_KEY = Pair.of(MedicationIdType.ATC, 4);
    static final Pair<MedicationIdType, Integer> ATC5_KEY = Pair.of(MedicationIdType.ATC, 5);
    static final Pair<MedicationIdType, Integer> VTM_KEY = Pair.of(MedicationIdType.VTM, 0);
    static final Pair<MedicationIdType, Integer> ATM_KEY = Pair.of(MedicationIdType.ATM, 0);
    static final Pair<MedicationIdType, Integer> VMP_KEY = Pair.of(MedicationIdType.INN, 0);
    static final Pair<MedicationIdType, Integer> AMP_KEY = Pair.of(MedicationIdType.AMP, 0);
    static final Pair<MedicationIdType, Integer> VMPP_KEY = Pair.of(MedicationIdType.VMPP, 0);
    static final Pair<MedicationIdType, Integer> AMPP_KEY = Pair.of(MedicationIdType.CNK, 0);

    static {
        LOOKUP.put(ATC3_KEY, ATC3_MEDICATION_CREATOR);
        LOOKUP.put(ATC4_KEY, ATC4_MEDICATION_CREATOR);
        LOOKUP.put(ATC5_KEY, ATC5_MEDICATION_CREATOR);
        LOOKUP.put(VTM_KEY, VTM_MEDICATION_CREATOR);
        LOOKUP.put(ATM_KEY, ATM_MEDICATION_CREATOR);
        LOOKUP.put(VMP_KEY, VMP_MEDICATION_CREATOR);
        LOOKUP.put(AMP_KEY, AMP_MEDICATION_CREATOR);
        LOOKUP.put(VMPP_KEY, VMPP_MEDICATION_CREATOR);
        LOOKUP.put(AMPP_KEY, IDENTITY_MEDICATION_CREATOR);


    }


    public static F1<Medication, Medication> getMedicationCreator(MedicationIdType type, int level) {

        final Pair<MedicationIdType, Integer> key = Pair.of(type, level);

        return LOOKUP.get(key);

    }
}
