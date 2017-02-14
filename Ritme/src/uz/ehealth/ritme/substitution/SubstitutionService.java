package uz.ehealth.ritme.substitution;

import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;

/**
 * Created by bdcuyp0 on 16-11-2015.
 */
public interface SubstitutionService {
    public SubstitutionResult substitute(final String ssin, final String orgNihii, final Medication medication, final boolean formularium, final MedicationIdType parent, final int level);
}
