package uz.ehealth.ritme.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by bdcuyp0 on 28-9-2015.
 */
public class MedicatieSchemaItem {

    private final PeriodicityType periodicity;
    private final String instructionForPatient;
    private final String instructionForOverdosing;
    private final String instructionForReimbursement;
    private final String transactionReason;
    private final String uri;
    private final String source;
    private String kmehrId;
    private MedicatieSchemaItemType type;
    private Date registrationDate;
    private String patientSSIN;
    private String medicSSIN;
    private String medicNIHII;
    private String orgNIHII;
    private Medication intendedMedication;
    private Medication deliveredMedication;
    private DrugRouteType drugRoute;
    private String medicationUse;
    private Date startDate;
    private Date stopDate;
    private String beginCondition;
    private String endCondition;
    private RegimenItem[] regimenItems;
    private String posology;
    private boolean patientOrigin;
    private boolean active;
    private boolean validated;
    private Suspension[] suspensions;

    public MedicatieSchemaItem(
            @JsonProperty("uri") final String uri,
            @JsonProperty("source") final String source,
            @JsonProperty("type") final MedicatieSchemaItemType type,
            @JsonProperty("registrationDate") final Date registrationDate,
            @JsonProperty("patientSSIN") final String patientSSIN,
            @JsonProperty("medicSSIN") final String medicSSIN,
            @JsonProperty("medicNIHII") final String medicNIHII,
            @JsonProperty("orgNIHII") final String orgNIHII,
            @JsonProperty("intendedMedication") final Medication intendedMedication,
            @JsonProperty("deliveredMedication") final Medication deliveredMedication,
            @JsonProperty("drugRoute") final DrugRouteType drugRoute,
            @JsonProperty("medicationUse") final String medicationUse,
            @JsonProperty("startDate") final Date startDate,
            @JsonProperty("stopDate") final Date stopDate,
            @JsonProperty("beginCondition") final String beginCondition,
            @JsonProperty("endCondition") final String endCondition,
            @JsonProperty("periodicity") final PeriodicityType periodicity,
            @JsonProperty("regimenItems") final RegimenItem[] regimenItems,
            @JsonProperty("posology") final String posology,
            @JsonProperty("instructionForPatient") String instructionForPatient,
            @JsonProperty("instructionForOverdosing") String instructionForOverdosing,
            @JsonProperty("instructionForReimbursement") String instructionForReimbursement,
            @JsonProperty("transactionReason") String transactionReason,
            @JsonProperty("patientOrigin") final boolean patientOrigin,
            @JsonProperty("active") final boolean active,
            @JsonProperty("validated") final boolean validated,
            @JsonProperty("suspensions") final Suspension[] suspensions) {
        this.type = type;
        this.registrationDate = registrationDate;
        this.patientSSIN = patientSSIN;
        this.medicSSIN = medicSSIN;
        this.medicNIHII = medicNIHII;
        this.orgNIHII = orgNIHII;
        this.intendedMedication = intendedMedication;
        this.deliveredMedication = deliveredMedication;
        this.drugRoute = drugRoute;
        this.medicationUse = medicationUse;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.beginCondition = beginCondition;
        this.endCondition = endCondition;
        this.periodicity = periodicity;
        this.regimenItems = regimenItems;
        this.posology = posology;
        this.instructionForPatient = instructionForPatient;
        this.instructionForOverdosing = instructionForOverdosing;
        this.instructionForReimbursement = instructionForReimbursement;
        this.transactionReason = transactionReason;
        this.patientOrigin = patientOrigin;
        this.active = active;
        this.validated = validated;
        this.uri = uri;
        this.source = source;
        this.suspensions = suspensions;
    }

    public Suspension[] getSuspensions() {
        return suspensions;
    }

    public PeriodicityType getPeriodicity() {
        return periodicity;
    }

    public String getInstructionForPatient() {
        return instructionForPatient;
    }

    public String getInstructionForOverdosing() {
        return instructionForOverdosing;
    }

    public String getInstructionForReimbursement() {
        return instructionForReimbursement;
    }

    public MedicatieSchemaItemType getType() {
        return type;

    }

    public Date getRegistrationDate() {

        return registrationDate;
    }

    public String getPatientSSIN() {
        return patientSSIN;
    }

    public String getMedicSSIN() {
        return medicSSIN;
    }

    public String getMedicNIHII() {
        return medicNIHII;
    }

    public String getOrgNIHII() {
        return orgNIHII;
    }

    public Medication getIntendedMedication() {
        return intendedMedication;
    }

    public Medication getDeliveredMedication() {
        return deliveredMedication;
    }

    public String getMedicationUse() {
        return medicationUse;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public String getBeginCondition() {
        return beginCondition;
    }

    public String getEndCondition() {
        return endCondition;
    }

    public String getPosology() {
        return posology;
    }

    public boolean isPatientOrigin() {
        return patientOrigin;
    }

    public Boolean isActive() {
        return active;
    }

    public DrugRouteType getDrugRoute() {
        return drugRoute;
    }

    public RegimenItem[] getRegimenItems() {
        return regimenItems;
    }

    public boolean isValidated() {
        return validated;
    }

    public String getTransactionReason() {
        return transactionReason;
    }

    public String getSource() {
        return source;
    }

    public String getUri() {
        return uri;
    }
}


