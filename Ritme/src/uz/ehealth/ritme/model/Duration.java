package uz.ehealth.ritme.model;

import java.math.BigDecimal;

/**
 * Created by bdcuyp0 on 10-5-2016.
 */
public class Duration {
    private BigDecimal decimal;
    private TimeUnitType timeUnit;

    public Duration(final BigDecimal decimal, final TimeUnitType timeUnit) {
        this.decimal = decimal;
        this.timeUnit = timeUnit;
    }

    public BigDecimal getDecimal() {
        return decimal;
    }

    public TimeUnitType getTimeUnit() {
        return timeUnit;
    }
}
