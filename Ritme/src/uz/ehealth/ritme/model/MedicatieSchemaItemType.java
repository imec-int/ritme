package uz.ehealth.ritme.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdcuyp0 on 28-9-2015.
 */
public enum MedicatieSchemaItemType {
    ACUTE("acute"),
    CHRONIC("chronic"),
    ONESHOT("oneshot");

    public String getType() {
        return type;
    }

    private final String type;

    MedicatieSchemaItemType(String type) {
        this.type = type;
    }

    // Reverse-lookup map for getting a day from an abbreviation
    private static final Map<String, MedicatieSchemaItemType> LOOKUP = new HashMap<String, MedicatieSchemaItemType>();

    static {
        for (MedicatieSchemaItemType d : MedicatieSchemaItemType.values()) {
            LOOKUP.put(d.getType(), d);
        }
    }

    public static MedicatieSchemaItemType getByType(String type) {
        return LOOKUP.get(type);
    }

}
