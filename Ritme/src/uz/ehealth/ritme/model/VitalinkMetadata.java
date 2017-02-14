package uz.ehealth.ritme.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Date: 19-4-2016.
 */
public class VitalinkMetadata {
    private final List<VitalinkMetadataEntry> metadataEntries;

    public VitalinkMetadata(@JsonProperty("metadata") List<VitalinkMetadataEntry> metadataEntries) {
        this.metadataEntries = metadataEntries;
    }

    public List<VitalinkMetadataEntry> getMetadataEntries() {
        return metadataEntries;
    }
}
