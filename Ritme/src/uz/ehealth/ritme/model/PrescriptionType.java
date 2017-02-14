package uz.ehealth.ritme.model;

/**
 * Created by bdcuyp0 on 24-3-2016.
 */

import java.util.HashMap;
import java.util.Map;

public enum PrescriptionType {
    P0("P0", "Pharmaceutical prescription"),
    P1("P1", "Pharmaceutical prescription that necessitates information on the patient's insurability"),
    P2("P2", "Pharmaceutical prescription that necessitates attestation information");

    // Reverse-lookup map for getting a day from an abbreviation
    private static final Map<String, PrescriptionType> LOOKUP = new HashMap<String, PrescriptionType>();

    private final String cd;
    private final String description;

    PrescriptionType(String cd, String description) {
        this.cd = cd;
        this.description = description;
    }

    static {
        for (PrescriptionType d : PrescriptionType.values()) {
            LOOKUP.put(d.getCd(), d);
        }
    }

    public static PrescriptionType getByCd(String cd) {
        return LOOKUP.get(cd);
    }


    public String getCd() {
        return cd;
    }

    public String getDescription() {
        return description;
    }
}

