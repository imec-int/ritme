package uz.ehealth.ritme.sam;

import org.jetbrains.annotations.NotNull;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.emv.sam.v1.domain.*;

import java.util.List;
import java.util.Set;

/**
 * Created by bdcuyp0 on 24-9-2015.
 */
public interface SamService {
    void update(String user, String nihiiOrg);

    @NotNull
    List<AMP> getAMPById(@NotNull Long id);

    @NotNull
    List<VMP> getVMPByCnk(Integer cnk);

    @NotNull
    List<AMPP> getAMPPByCnk(Integer cnk);

    @NotNull
    List<VTM> getVTMByName(String name);

    @NotNull
    List<VMPP> getVMPPByVMPName(String vmpName);

    @NotNull
    List<VMPComb> getVMPCombByParentName(String parentName);

    @NotNull
    List<VMP> getVMPByName(String name);

    @NotNull
    List<VirtualIngredientStrength> getVirtualIngredientStrengthByVTMName(String vtmName);

    @NotNull
    List<VirtualIngredient> getVirtualIngredientByVTMName(String vtmName);

    @NotNull
    List<TreatmentDurationCategory> getTreatmentDurationCategoryByName(String name);

    @NotNull
    List<Substance> getSubstanceByName(String name);

    @NotNull
    List<RouteOfAdministration> getRouteOfAdministrationByName(String name);

    @NotNull
    List<PharmaceuticalForm> getPharmaceuticalFormByName(String name);

    @NotNull
    List<InnerPackage> getInnerPackageByName(String name);

    @NotNull
    List<Company> getCompanyByName(String name);

    @NotNull
    List<ATM> getATMByName(String name);

    @NotNull
    List<ATC> getATCByAtcCv(String atcCv);

    @NotNull
    List<ATC> getATCByName(String name);

    @NotNull
    List<Application> getApplicationByName(String name);

    @NotNull
    List<AMPP> getAMPPByName(String name);

    @NotNull
    List<AMPIntPckComb> getAMPIntPckCombByyAMPParentName(String ampParentName);

    @NotNull
    List<AMPIntermediatePackage> getAMPIntermediatePackageByAMPName(String ampName);

    @NotNull
    List<AMPComb> getAMPCombByParentName(String parentName);

    @NotNull
    List<AMP> getAMPByName(String name);

    @NotNull
    List<AdministrationForm> getAdministrationFormByName(String name);

    @NotNull
    List<ATM> getATMById(Long medicationId);

    @NotNull
    List<VMP> getVMPById(Integer integer);

    Set<ATC> getATCForMedication(MedicationIdType type, String id);

    Set<AMPP> getAMPPsForMedication(Medication medication);
}
