package uz.ehealth.ritme.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.List;

/**
 * Created by bdcuyp0 on 30-1-2017.
 */
public class ListPrescriptionOutput {
    private List<URI> uris;

    public List<URI> getUris() {
        return uris;
    }

    public ListPrescriptionOutput(@JsonProperty("uri") final List<URI> uris) {
        this.uris = uris;
    }
}
