package uz.ehealth.ritme.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Date: 19-4-2016.
 */
public class VitalinkMetadataEntry {
    private final String key;

    private final String value;

    public VitalinkMetadataEntry(@JsonProperty("key") String key,
                                 @JsonProperty("value") String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
