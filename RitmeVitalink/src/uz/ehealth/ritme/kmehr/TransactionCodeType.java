package uz.ehealth.ritme.kmehr;

/**
 * Created by bdcuyp0 on 21-4-2016.
 */
public enum TransactionCodeType {
    ELEMENT_UNCHANGED("medicationschemeelement", new String[]{"nochanges"}),
    ELEMENT_FULL("medicationschemeelement", new String[]{"medication", "posology"}),
    TREATMENT_SUSPENSION("treatmentsuspension", new String[]{"treatmentsuspension"});

    private final String[] adaptationFlags;
    private final String transaction;

    TransactionCodeType(String transaction, String[] adaptationFlags) {
        this.transaction = transaction;
        this.adaptationFlags = adaptationFlags;
    }

    public String[] getAdaptationFlags() {
        return adaptationFlags;
    }

    public String getTransaction() {
        return transaction;
    }
}
