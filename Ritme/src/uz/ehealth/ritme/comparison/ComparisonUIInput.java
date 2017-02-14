package uz.ehealth.ritme.comparison;

import com.fasterxml.jackson.annotation.JsonProperty;
import uz.ehealth.ritme.model.MedicatieSchemaItem;

import java.util.List;

/**
 * Created by bdcuyp0 on 30-10-2015.
 */

public class ComparisonUIInput {
    public List<MedicatieSchemaItem> getLeft() {
        return left;
    }

    public ComparisonUIInput(@JsonProperty("left") final List<MedicatieSchemaItem> left, @JsonProperty("right") final List<MedicatieSchemaItem> right, @JsonProperty("value") final List<ComparisonPair> value) {
        this.left = left;
        this.right = right;
        this.value = value;
    }

    private List<MedicatieSchemaItem> left;
    private List<MedicatieSchemaItem> right;

    public List<MedicatieSchemaItem> getRight() {
        return right;
    }

    private List<ComparisonPair> value;

    public List<ComparisonPair> getValue() {
        return value;
    }

}
