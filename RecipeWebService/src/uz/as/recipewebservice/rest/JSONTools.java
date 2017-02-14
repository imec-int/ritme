package uz.as.recipewebservice.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by bdcuyp0 on 16-11-2015.
 */
public class JSONTools {
    private static final ObjectMapper INSTANCE;

    static {

        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        INSTANCE = mapper;

    }

    public static String marshal(Object annotatedObject) {
        try {
            return INSTANCE.writeValueAsString(annotatedObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <A> A unmarshal(String message, Class<A> clazz) {
        try {
            return INSTANCE.readValue(message, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
