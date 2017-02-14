package uz.ehealth.ritme.outbound.formularium;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bdcuyp0 on 18-8-2016.
 */
public class JsonFormulariumCode implements FormulariumCode {
    private final boolean within;
    private final boolean substitutable;

    public JsonFormulariumCode(
            @JsonProperty("within") boolean within,
            @JsonProperty("substitutable") boolean substitutable
    ) {
        this.within = within;
        this.substitutable = substitutable;
    }

    @Override
    public boolean isWithin() {
        return within;
    }

    @Override
    public boolean isSubstitutable() {
        return substitutable;
    }
}
