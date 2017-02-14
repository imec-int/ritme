package uz.ehealth.ritme.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by bdcuyp0 on 24-3-2016.
 */
public class MedicatieVoorschriftItem {
    private final Date prescriptionDate;
    private final Date executionDate;
    private final Date expirationDate;
    private final String prescriberSSIN;
    private final String prescriberNihiiOrg;
    private final PrescriptionType prescriptionType;
    private final Integer quantity;
    private final MedicatieSchemaItem medicatieSchemaItem;
    private final String source;
    private final String uri;

    public Date getPrescriptionDate() {
        return prescriptionDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getPrescriberSSIN() {
        return prescriberSSIN;
    }

    public String getPrescriberNihiiOrg() {
        return prescriberNihiiOrg;
    }

    public PrescriptionType getPrescriptionType() {
        return prescriptionType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public MedicatieSchemaItem getMedicatieSchemaItem() {
        return medicatieSchemaItem;
    }

    public MedicatieVoorschriftItem(
            @JsonProperty("prescriptionDate") final Date prescriptionDate,
            @JsonProperty("executionDate") final Date executionDate,
            @JsonProperty("expirationDate") final Date expirationDate,
            @JsonProperty("prescriberSSIN") final String prescriberSSIN,
            @JsonProperty("prescriberNihiiOrg") final String prescriberNihiiOrg,
            @JsonProperty("prescriptionType") final PrescriptionType prescriptionType,
            @JsonProperty("quantity") final Integer quantity,
            @JsonProperty("medicatieSchemaItem") final MedicatieSchemaItem medicatieSchemaItem,
            @JsonProperty("source") final String source,
            @JsonProperty("uri") final String uri) {
        this.prescriptionDate = prescriptionDate;
        this.executionDate = executionDate;
        this.expirationDate = expirationDate;
        this.prescriberSSIN = prescriberSSIN;
        this.prescriberNihiiOrg = prescriberNihiiOrg;
        this.prescriptionType = prescriptionType;
        this.quantity = quantity;
        this.medicatieSchemaItem = medicatieSchemaItem;
        this.source = source;
        this.uri = uri;
    }

    public String getSource() {
        return source;
    }

    public String getUri() {
        return uri;
    }

    public Date getExecutionDate() {
        return executionDate;
    }
}
