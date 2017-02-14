package uz.ehealth.ritme.model;

/**
 * Created by bdcuyp0 on 29-9-2015.
 */
public enum MedicationIdType {
    EAN("ean"),
    CNK("cnk"),
    INN("inn"),
    MAG("mag"),
    AMP("amp"),
    VMPP("vmpp"),
    ATM("atm"),
    VTM("vtm"),
    ATC("atc");

    private final String type;

    MedicationIdType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
