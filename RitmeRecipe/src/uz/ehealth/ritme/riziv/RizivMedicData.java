package uz.ehealth.ritme.riziv;

import uz.ehealth.ritme.outbound.medic.MedicData;

/**
 * Created by bdcuyp0 on 24-5-2016.
 */

public class RizivMedicData implements MedicData {

    private final RizivSilverPage rizivSilverPage;

    public RizivMedicData(RizivSilverPage rizivSilverPage) {
        this.rizivSilverPage = rizivSilverPage;
    }


    @Override
    public String getFirstName() {
        return (rizivSilverPage.getFirstName1() + " " + rizivSilverPage.getFirstName2()).trim();
    }

    @Override
    public String getName() {
        return rizivSilverPage.getLastName();
    }

    @Override
    public String[] getOrgNihii() {
        return new String[0];
    }

    @Override
    public String[] getNihii() {
        return new String[]{rizivSilverPage.getNihdi()};
    }

    @Override
    public String getSsin() {
        return null;
    }

    @Override
    public String getRole() {
        //todo: to be completed
        if ("10".equals(rizivSilverPage.getProfessionCode())) {
            return "persphysician";
        } else if ("20".equals(rizivSilverPage.getProfessionCode())) {
            return "perspharmacist";
        }
        return null;
    }

}
