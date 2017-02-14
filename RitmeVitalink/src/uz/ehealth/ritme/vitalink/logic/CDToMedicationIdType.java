package uz.ehealth.ritme.vitalink.logic;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNK;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDINNCLUSTER;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.model.MedicationIdType;

/**
 * Created by bdcuyp0 on 13-11-2015.
 */
public class CDToMedicationIdType implements F1<Object, MedicationIdType> {
    @Override
    public MedicationIdType invoke(final Object source) {
        if (source instanceof CDDRUGCNK) {
            CDDRUGCNK cddrugcnk = (CDDRUGCNK) source;
            try {
                Integer.parseInt(cddrugcnk.getValue());
                return MedicationIdType.CNK;
            } catch (NumberFormatException e) {
                return null;
            }

        } else if (source instanceof CDINNCLUSTER) {
            CDINNCLUSTER cdinncluster = (CDINNCLUSTER) source;
            try {
                Integer.parseInt(cdinncluster.getValue());
                return MedicationIdType.INN;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (source instanceof CDCONTENT) {

            if (CDCONTENTschemes.CD_EAN.equals(((CDCONTENT) source).getS())) {
                return MedicationIdType.EAN;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
