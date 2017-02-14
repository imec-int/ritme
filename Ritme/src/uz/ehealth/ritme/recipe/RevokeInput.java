package uz.ehealth.ritme.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bdcuyp0 on 31-1-2017.
 */
public class RevokeInput {
    public String getReason() {
        return reason;
    }

    private final String reason;

    public RevokeInput(@JsonProperty("reason") String reason) {
        this.reason = reason;
    }
}
