package uz.emv.sam.v1.service;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.domain.*;
import uz.emv.sam.v1.service.loader.DefaultObjectLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * User: simbre1
 * Date: 27/09/13
 */
public class SamObjectRepository extends DefaultObjectRepository {

    public SamObjectRepository(@NotNull final DefaultObjectLoader objectLoader, final ObjectPersistor objectPersistor) {
        super(objectLoader, objectPersistor);
    }

    @NotNull
    public List<AdministrationForm> getAdministrationFormByName(@NotNull final String name){
        List<AdministrationForm> result = new ArrayList<AdministrationForm>();
        for (AdministrationForm administrationForm : getObjects(AdministrationForm.class)) {
            if (name.equals(administrationForm.getName())) {
                result.add(administrationForm);
            }
        }
        return result;
    }

    @NotNull
    public List<AMP> getAMPByName(@NotNull final String name){
        List<AMP> result = new ArrayList<AMP>();
        for (AMP amp : getObjects(AMP.class)) {
            if (name.equals(amp.getName())) {
                result.add(amp);
            }
        }
        return result;

    }

    @NotNull
    public List<AMP> getAMPById(@NotNull final Long id) {
        List<AMP> result = new ArrayList<AMP>();
        for (AMP amp : getObjects(AMP.class)) {
            if (id.equals(amp.getAmpId())) {
                result.add(amp);
            }
        }
        return result;
    }

    @NotNull
    public List<AMPComb> getAMPCombByParentName(@NotNull final String parentName){
        List<AMPComb> result = new ArrayList<AMPComb>();
        for (AMPComb ampComb : getObjects(AMPComb.class)) {
            if (parentName.equals(ampComb.getParent().getName())) {
                result.add(ampComb);
            }
        }
        return result;
    }

    @NotNull
    public List<AMPIntermediatePackage> getAMPIntermediatePackageByAMPName(@NotNull final String ampName){
        List<AMPIntermediatePackage> result = new ArrayList<AMPIntermediatePackage>();
        for (AMPIntermediatePackage ampIntermediatePackage : getObjects(AMPIntermediatePackage.class)) {
            if (ampName.equals(ampIntermediatePackage.getAMP().getName())) {
                result.add(ampIntermediatePackage);
            }
        }
        return result;

    }

    @NotNull
    public List<AMPIntPckComb> getAMPIntPckCombByyAMPParentName(@NotNull final String ampParentName){
        List<AMPIntPckComb> result = new ArrayList<AMPIntPckComb>();
        for (AMPIntPckComb ampIntPckComb : getObjects(AMPIntPckComb.class)) {
            if (ampParentName.equals(ampIntPckComb.getParent().getAMP().getName())) {
                result.add(ampIntPckComb);
            }
        }
        return result;

    }

    @NotNull
    public List<AMPP> getAMPPByName(@NotNull final String name){
        List<AMPP> result = new ArrayList<AMPP>();
        for (AMPP ampp : getObjects(AMPP.class)) {
            if (name.equals(ampp.getName())) {
                result.add(ampp);
            }
        }
        return result;
    }

    @NotNull
    public List<Application> getApplicationByName(@NotNull final String name){
        List<Application> result = new ArrayList<Application>();
        for (Application application : getObjects(Application.class)) {
            if (name.equals(application.getName())) {
                result.add(application);
            }
        }
        return result;
    }

    @NotNull
    public List<ATC> getATCByName(@NotNull final String name){
        List<ATC> result = new ArrayList<ATC>();
        for (ATC atc : getObjects(ATC.class)) {
            if (name.equals(atc.getName())) {
                result.add(atc);
            }
        }
        return result;
    }
    @NotNull
    public List<ATC> getATCByAtcCv(@NotNull final String atcCv){
        List<ATC> result = new ArrayList<ATC>();
        for (ATC atc : getObjects(ATC.class)) {
            if (atcCv.equals(atc.getAtcCv())) {
                result.add(atc);
            }
        }
        return result;
    }

    @NotNull
    public List<ATM> getATMByName(@NotNull final String name){
        List<ATM> result = new ArrayList<ATM>();
        for (ATM atm : getObjects(ATM.class)) {
            if (name.equals(atm.getName())) {
                result.add(atm);
            }
        }
        return result;
    }

    @NotNull
    public List<Company> getCompanyByName(@NotNull final String name){
        List<Company> result = new ArrayList<Company>();
        for (Company company : getObjects(Company.class)) {
            if (name.equals(company.getName())) {
                result.add(company);
            }
        }
        return result;
    }

    @NotNull
    public List<InnerPackage> getInnerPackageByName(@NotNull final String name){
        List<InnerPackage> result = new ArrayList<InnerPackage>();
        for (InnerPackage innerPackage : getObjects(InnerPackage.class)) {
            if (name.equals(innerPackage.getName())) {
                result.add(innerPackage);
            }
        }
        return result;
    }

    @NotNull
    public List<PharmaceuticalForm> getPharmaceuticalFormByName(@NotNull final String name){
        List<PharmaceuticalForm> result = new ArrayList<PharmaceuticalForm>();
        for (PharmaceuticalForm pharmaceuticalForm : getObjects(PharmaceuticalForm.class)) {
            if (name.equals(pharmaceuticalForm.getName())) {
                result.add(pharmaceuticalForm);
            }
        }
        return result;
    }

    @NotNull
    public List<RouteOfAdministration> getRouteOfAdministrationByName(@NotNull final String name){
        List<RouteOfAdministration> result = new ArrayList<RouteOfAdministration>();
        for (RouteOfAdministration routeOfAdministration : getObjects(RouteOfAdministration.class)) {
            if (name.equals(routeOfAdministration.getName())) {
                result.add(routeOfAdministration);
            }
        }
        return result;
    }

    @NotNull
    public List<Substance> getSubstanceByName(@NotNull final String name){
        List<Substance> result = new ArrayList<Substance>();
        for (Substance substance : getObjects(Substance.class)) {
            if (name.equals(substance.getName())) {
                result.add(substance);
            }
        }
        return result;
    }

    @NotNull
    public List<TreatmentDurationCategory> getTreatmentDurationCategoryByName(@NotNull final String name){
        List<TreatmentDurationCategory> result = new ArrayList<TreatmentDurationCategory>();
        for (TreatmentDurationCategory treatmentDurationCategory : getObjects(TreatmentDurationCategory.class)) {
            if (name.equals(treatmentDurationCategory.getName())) {
                result.add(treatmentDurationCategory);
            }
        }
        return result;
    }

    @NotNull
    public List<VirtualIngredient> getVirtualIngredientByVTMName(@NotNull final String vtmName){
        List<VirtualIngredient> result = new ArrayList<VirtualIngredient>();
        for (VirtualIngredient administrationForm : getObjects(VirtualIngredient.class)) {
            if (vtmName.equals(administrationForm.getVTM().getName())) {
                result.add(administrationForm);
            }
        }
        return result;
    }

    @NotNull
    public List<VirtualIngredientStrength> getVirtualIngredientStrengthByVTMName(@NotNull final String vtmName){
        List<VirtualIngredientStrength> result = new ArrayList<VirtualIngredientStrength>();
        for (VirtualIngredientStrength virtualIngredientStrength : getObjects(VirtualIngredientStrength.class)) {
            if (vtmName.equals(virtualIngredientStrength.getVirtualIngredient().getVTM().getName())) {
                result.add(virtualIngredientStrength);
            }
        }
        return result;

    }

    @NotNull
    public List<VMP> getVMPByName(@NotNull final String name){
        List<VMP> result = new ArrayList<VMP>();
        for (VMP vmp : getObjects(VMP.class)) {
            if (name.equals(vmp.getName())) {
                result.add(vmp);
            }
        }
        return result;
    }

    @NotNull
    public List<VMPComb> getVMPCombByParentName(@NotNull final String parentName){
        List<VMPComb> result = new ArrayList<VMPComb>();
        for (VMPComb administrationForm : getObjects(VMPComb.class)) {
            if (parentName.equals(administrationForm.getParent().getName())) {
                result.add(administrationForm);
            }
        }
        return result;
    }

    @NotNull
    public List<VMPP> getVMPPByVMPName(@NotNull final String vmpName){
        List<VMPP> result = new ArrayList<VMPP>();
        for (VMPP vmpp : getObjects(VMPP.class)) {
            if (vmpName.equals(vmpp.getVMP().getName())) {
                result.add(vmpp);
            }
        }
        return result;
    }

    @NotNull
    public List<VTM> getVTMByName(@NotNull final String name){
        List<VTM> result = new ArrayList<VTM>();
        for (VTM vtm : getObjects(VTM.class)) {
            if (name.equals(vtm.getName())) {
                result.add(vtm);
            }
        }
        return result;
    }

    @NotNull
    public List<AMPP> getAMPPByCnk(final int cnk){
        List<AMPP> result = new ArrayList<AMPP>();
        for (AMPP ampp : getObjects(AMPP.class)) {
            if (cnk == ampp.getAmppId()) {
                result.add(ampp);
            }
        }
        return result;
    }

    @NotNull
    public List<VMP> getVMPByCnk(final int cnk){
        List<VMP> result = new ArrayList<VMP>();
        for (VMP vmp : getObjects(VMP.class)) {
            if (cnk == vmp.getVmpId()) {
                result.add(vmp);
            }
        }
        return result;
    }


    @NotNull
    public List<ATM> getATMById(final long atmId) {
        List<ATM> result = new ArrayList<ATM>();
        for (ATM atm : getObjects(ATM.class)) {
            if (atmId == atm.getAtmId()) {
                result.add(atm);
            }
        }
        return result;
    }

    @NotNull
    public List<VMP> getVMPById(final int vmpId) {
        List<VMP> result = new ArrayList<VMP>();
        for (VMP vmp : getObjects(VMP.class)) {
            if (vmpId == vmp.getVmpId()) {
                result.add(vmp);
            }
        }
        return result;
    }


}
