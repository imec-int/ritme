package uz.ehealth.ritme.comparison;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by bdcuyp0 on 30-10-2015.
 */
public class ComparisonResult {
    private List<ComparisonPair> value;

    public ComparisonResult(@JsonProperty("value") final List<ComparisonPair> value) {
        this.value = value;
    }

    public List<ComparisonPair> getValue() {
        return value;
    }
}
