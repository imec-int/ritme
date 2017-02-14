package uz.ehealth.ritme.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bdcuyp0 on 28-9-2015.
 */
public class Medication {
    public Medication(
            @JsonProperty("medicationId") final String medicationId,
            @JsonProperty("medicationIdType") final uz.ehealth.ritme.model.MedicationIdType medicationIdType,
            @JsonProperty("medicationDescription") final String medicationDescription,
            @JsonProperty("magistralText") final String magistralText) {
        this.medicationId = medicationId;
        this.medicationIdType = medicationIdType;
        this.medicationDescription = medicationDescription;
        this.magistralText = magistralText;
    }

    private String medicationId;
    private MedicationIdType medicationIdType;

    public MedicationIdType getMedicationIdType() {
        return medicationIdType;
    }

    public String getMedicationDescription() {
        return medicationDescription;
    }

    public String getMedicationId() {

        return medicationId;
    }

    private String medicationDescription;

    public String getMagistralText() {
        return magistralText;
    }

    private String magistralText;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Medication that = (Medication) o;

        if (medicationId != null ? !medicationId.equals(that.medicationId) : that.medicationId != null) {
            return false;
        }
        if (medicationIdType != that.medicationIdType) {
            return false;
        }
        if (!medicationDescription.equals(that.medicationDescription)) {
            return false;
        }
        if (magistralText != null ? !magistralText.equals(that.magistralText) : that.magistralText != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = medicationId != null ? medicationId.hashCode() : 0;
        result = 31 * result + medicationIdType.hashCode();
        result = 31 * result + medicationDescription.hashCode();
        result = 31 * result + (magistralText != null ? magistralText.hashCode() : 0);
        return result;
    }
}
