package uz.ehealth.ritme.substitution;

import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.emv.sam.v1.domain.AMPP;

/**
 * Created by bdcuyp0 on 16-11-2015.
 */
public class AMPPToMedication implements F1<AMPP, Medication> {
    @Override
    public Medication invoke(final AMPP source) {
        return new Medication(String.valueOf(source.getAmppId()), MedicationIdType.CNK, source.getName(), null);
    }
}
