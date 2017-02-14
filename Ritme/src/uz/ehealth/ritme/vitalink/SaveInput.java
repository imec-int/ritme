package uz.ehealth.ritme.vitalink;

import com.fasterxml.jackson.annotation.JsonProperty;
import uz.ehealth.ritme.model.MedicatieSchemaItem;

import java.util.List;

/**
 * Created by bdcuyp0 on 13-1-2016.
 */
public class SaveInput {
    public List<MedicatieSchemaItem> getItems() {
        return items;
    }

    private final List<MedicatieSchemaItem> items;

    public SaveInput(@JsonProperty("items") List<MedicatieSchemaItem> items) {
        this.items = items;
    }
}
