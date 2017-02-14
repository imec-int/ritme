package uz.ehealth.ritme.kmehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEM;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;

import java.util.List;

/**
 * Created by bdcuyp0 on 29-9-2015.
 */
public class KmehrTools {
    public static String findIDHCPARTY(final List<IDHCPARTY> ids, final String s) {
        for (IDHCPARTY id : ids) {
            if (id.getS().value().equals(s)) {
                return id.getValue();
            }
        }
        return null;

    }

    public static String findIDPATIENT(final List<IDPATIENT> ids, final String s) {
        for (IDPATIENT id : ids) {
            if (id.getS().value().equals(s)) {
                return id.getValue();
            }
        }
        return null;

    }

    public static String findCDCONTENT(final List<CDCONTENT> cds, final String s) {
        for (CDCONTENT cd : cds) {
            if (cd.getS().value().equals(s)) {
                return cd.getValue();
            }
        }
        return null;
    }

    public static String findCDITEM(final List<CDITEM> cds, final String s) {
        for (CDITEM cd : cds) {
            if (cd.getS().value().equals(s)) {
                return cd.getValue();
            }
        }
        return null;
    }

    public static String findCDTRANSACTION(final List<CDTRANSACTION> cds, final String s) {
        for (CDTRANSACTION cd : cds) {
            if (cd.getS().value().equals(s)) {
                return cd.getValue();
            }
        }
        return null;
    }

    public static String findIDKMEHR(final List<IDKMEHR> ids, final String s) {
        for (IDKMEHR cd : ids) {
            if (cd.getS().value().equals(s)) {
                return cd.getValue();
            }
        }
        return null;
    }

    public static HcpartyType selectOrg(final List<HcpartyType> hcparties) {
        for (HcpartyType hcparty : hcparties) {
            final String cdhcparty = KmehrTools.findCDHCPARTY(hcparty.getCds(), "CD-HCPARTY");
            if (cdhcparty != null && cdhcparty.startsWith("org")) {
                return hcparty;

            }
        }
        return null;
    }

    public static HcpartyType selectOrgOrElsePerson(final List<HcpartyType> hcparties) {
        for (HcpartyType hcparty : hcparties) {
            final String cdhcparty = KmehrTools.findCDHCPARTY(hcparty.getCds(), "CD-HCPARTY");
            if (cdhcparty != null && cdhcparty.startsWith("org")) {
                return hcparty;

            }
        }
        if (!hcparties.isEmpty()) {
            return selectPerson(hcparties);
        } else {
            return null;
        }
    }

    public static HcpartyType selectPerson(final List<HcpartyType> hcparties) {
        for (HcpartyType hcparty : hcparties) {
            final String cdhcparty = KmehrTools.findCDHCPARTY(hcparty.getCds(), "CD-HCPARTY");
            if (cdhcparty != null && cdhcparty.startsWith("pers")) {
                return hcparty;

            }
        }
        return null;

    }

    public static String findCDHCPARTY(final List<CDHCPARTY> cds, final String s) {
        for (CDHCPARTY cd : cds) {
            if (cd.getS().value().equals(s)) {
                return cd.getValue();
            }
        }
        return null;
    }
}
