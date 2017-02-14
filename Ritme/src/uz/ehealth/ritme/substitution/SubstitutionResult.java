package uz.ehealth.ritme.substitution;

import uz.ehealth.ritme.model.Medication;

import java.util.List;

/**
 * Created by bdcuyp0 on 16-11-2015.
 */
public class SubstitutionResult {
    private final List<Medication> medicationList;

    public SubstitutionResult(final List<Medication> medicationList) {
        this.medicationList = medicationList;


    }

    public List<Medication> getMedicationList() {
        return medicationList;
    }
}
