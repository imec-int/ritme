package uz.ehealth.ritme.substitution;

import uz.ehealth.ritme.model.Medication;

/**
 * Created by bdcuyp0 on 16-11-2015.
 */
public class SubstitutionInput {
    private final Medication medication;
    private final String orgNihii;

    public SubstitutionInput(Medication medication, String orgNihii) {
        this.medication = medication;
        this.orgNihii = orgNihii;
    }

    Medication getMedication() {
        return medication;
    }

    public String getOrgNihii() {
        return orgNihii;
    }
}
