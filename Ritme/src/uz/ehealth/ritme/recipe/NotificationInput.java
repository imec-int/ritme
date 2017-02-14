package uz.ehealth.ritme.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import uz.ehealth.ritme.model.MedicatieVoorschriftItem;

import java.util.List;

/**
 * Created by bdcuyp0 on 24-5-2016.
 */
public class NotificationInput {
    private String message;
    private List<MedicatieVoorschriftItem> medicatieVoorschriftItems;

    public NotificationInput(
            @JsonProperty("message") String message,
            @JsonProperty("items") List<MedicatieVoorschriftItem> medicatieVoorschriftItems) {
        this.message = message;
        this.medicatieVoorschriftItems = medicatieVoorschriftItems;

    }

    public String getMessage() {
        return message;
    }

    public List<MedicatieVoorschriftItem> getItems() {
        return medicatieVoorschriftItems;
    }

}
