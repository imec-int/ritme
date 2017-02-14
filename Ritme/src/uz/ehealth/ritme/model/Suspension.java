package uz.ehealth.ritme.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by bdcuyp0 on 28-4-2016.
 */
public class Suspension {
    private Date startDate;
    private Date stopDate;
    private String transactionReason;

    public Suspension(
            @JsonProperty("startDate") Date startDate,
            @JsonProperty("stopDate") Date stopDate,
            @JsonProperty("transactionReason") String transactionReason
    ) {
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.transactionReason = transactionReason;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public String getTransactionReason() {
        return transactionReason;
    }


}
