package uz.ehealth.ritme.substitution;

import org.jetbrains.annotations.NotNull;
import uz.ehealth.ritme.comparison.MedicationCreatorFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.outbound.formularium.FormulariumCode;
import uz.ehealth.ritme.outbound.formularium.FormulariumService;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.medic.MedicService;
import uz.ehealth.ritme.plugins.PluginManager;
import uz.ehealth.ritme.sam.SamService;
import uz.emv.sam.v1.domain.AMPP;

import java.util.*;

/**
 * Created by bdcuyp0 on 16-11-2015.
 */
public class DefaultSubstitutionService implements SubstitutionService {

    private static final SamService SAM_SERVICE = PluginManager.get("ritme.sam", SamService.class);

    @Override
    public SubstitutionResult substitute(final String user, final String orgNihii, final Medication medication, final boolean formularium, final MedicationIdType parent, final int level) {
        MedicService medicService = PluginManager.get("ritme.outbound.medic", MedicService.class);
        MedicData physician = medicService.getData(user, user, orgNihii);
        if (!Arrays.asList(physician.getOrgNihii()).contains(orgNihii)) {
            throw new RuntimeException("<html><body><p>You are not allowed to use the hospital certificate</p></body></html>");
        }
        final FormulariumService formulariumService = PluginManager.get("ritme.outbound.formularium", FormulariumService.class);


        if (medication != null) {

            // een AMPP binnen formularium moet niet gesubstitueerd worden
            if (formularium && medication.getMedicationIdType().equals(MedicationIdType.CNK)) {

                FormulariumCode code = formulariumService.getFormulariumCodeForCnk(Integer.valueOf(medication.getMedicationId()), user, orgNihii);
                if (!code.isSubstitutable() || code.isWithin()) {
                    return new SubstitutionResult(Collections.singletonList(medication));
                }
            }


            Set<AMPP> newAmpps = fromMedicationToCandidateSubstitutes(medication, medication.getMedicationIdType(), level);

            if (formularium) {
                newAmpps = filterAmppsOpFormularium(physician, formulariumService, orgNihii, newAmpps);

            }
            List<Medication> items = amppToMedication(newAmpps);

            return new SubstitutionResult(items);

        } else {
            return new SubstitutionResult(Collections.<Medication>emptyList());
        }

    }

    @NotNull
    public Set<AMPP> fromMedicationToCandidateSubstitutes(final Medication medication, final MedicationIdType type, final int level) {

        final F1<Medication, Medication> commonParentCreator = MedicationCreatorFactory.getMedicationCreator(type, level);

        Medication commonParent = null;

        if (commonParentCreator != null) {

            commonParent = commonParentCreator.invoke(medication);

        }


        Set<AMPP> ampps = SAM_SERVICE.getAMPPsForMedication(commonParent);
        return ampps;
    }

    @NotNull
    public List<Medication> amppToMedication(final Set<AMPP> ampps) {

        List<Medication> items = new ArrayList<Medication>();
        for (AMPP ampp : ampps) {
            items.add(new Medication(String.valueOf(ampp.getAmppId()), MedicationIdType.CNK, ampp.getName(), null));
        }
        return items;
    }

    @NotNull
    public Set<AMPP> filterAmppsOpFormularium(final MedicData physician, final FormulariumService formulariumService, final String orgNihii, final Set<AMPP> ampps) {
        Set<AMPP> formulariumFilteredAmpps = new HashSet<AMPP>();

        for (AMPP newAmpp : ampps) {
            FormulariumCode newCode = formulariumService.getFormulariumCodeForCnk(newAmpp.getAmppId(), physician.getSsin(), orgNihii);
            if (newCode.isWithin()) {
                formulariumFilteredAmpps.add(newAmpp);
            }
        }
        return formulariumFilteredAmpps;
    }


}
