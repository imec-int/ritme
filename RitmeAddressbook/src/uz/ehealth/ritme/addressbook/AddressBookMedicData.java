package uz.ehealth.ritme.addressbook;

import uz.ehealth.ritme.be.fgov.ehealth.aa.complextype.v1.HealthCareProfessionalType;
import uz.ehealth.ritme.be.fgov.ehealth.aa.complextype.v1.ProfessionTypeV3;
import uz.ehealth.ritme.outbound.medic.MedicData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdcuyp0 on 6-6-2016.
 */
public class AddressBookMedicData implements MedicData {

    private final HealthCareProfessionalType healthCareProfessionalType;

    AddressBookMedicData(HealthCareProfessionalType healthCareProfessionalType) {
        this.healthCareProfessionalType = healthCareProfessionalType;
    }

    @Override
    public String getFirstName() {
        return healthCareProfessionalType.getFirstName();
    }

    @Override
    public String getName() {
        return healthCareProfessionalType.getLastName();
    }

    @Override
    public String[] getOrgNihii() {
        return new String[0];
    }

    @Override
    public String[] getNihii() {
        List<String> nihii = new ArrayList<String>();
        for (ProfessionTypeV3 professionTypeV3 : healthCareProfessionalType.getProfession()) {
            nihii.add(professionTypeV3.getNIHII());
        }
        return nihii.toArray(new String[nihii.size()]);
    }

    @Override
    public String getSsin() {
        return healthCareProfessionalType.getSSIN();
    }

    @Override
    public String getRole() {
        //tijdelijke oplossing
        boolean voorschrijver = false;
        for (ProfessionTypeV3 professionTypeV3 : healthCareProfessionalType.getProfession()) {
            if(professionTypeV3.getNIHII().startsWith("1") || professionTypeV3.getNIHII().startsWith("3")){
                voorschrijver = true;
            }
        }
        return voorschrijver?"persphysician":"persnurse";
    }

}
