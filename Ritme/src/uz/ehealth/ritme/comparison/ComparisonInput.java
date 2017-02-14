package uz.ehealth.ritme.comparison;

import com.fasterxml.jackson.annotation.JsonProperty;
import uz.ehealth.ritme.model.MedicatieSchemaItem;

import java.util.List;

/**
 * Created by bdcuyp0 on 30-10-2015.
 *
 */

public class ComparisonInput {
    public List<MedicatieSchemaItem> getLeft() {
        return left;
    }

    public ComparisonInput(@JsonProperty("left") final List<MedicatieSchemaItem> left, @JsonProperty("right") final List<MedicatieSchemaItem> right) {
        this.left = left;
        this.right = right;
    }

    private List<MedicatieSchemaItem> left;
    private List<MedicatieSchemaItem> right;

    public List<MedicatieSchemaItem> getRight() {
        return right;
    }
}
