package uz.ehealth.ritme.comparison;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by bdcuyp0 on 2-11-2015.
 */
public class ComparisonPair {
    public ComparisonPair(@JsonProperty("left") final Integer left, @JsonProperty("right") final Integer right, @JsonProperty("score") final Double score, @JsonProperty("attributes") final List<String> attributes) {
        this.left = left;
        this.right = right;
        this.score = score;
        this.attributes = attributes;
    }

    private Integer left;

    public Integer getLeft() {
        return left;
    }

    public Integer getRight() {
        return right;
    }

    public Double getScore() {
        return score;
    }

    private Integer right;
    private Double score;

    public List<String> getAttributes() {
        return attributes;
    }

    private List<String> attributes;
}
