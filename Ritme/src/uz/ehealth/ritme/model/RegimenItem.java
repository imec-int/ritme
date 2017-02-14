package uz.ehealth.ritme.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by bdcuyp0 on 28-9-2015.
 */
public class RegimenItem {
    public BigDecimal getQuantity() {
        return quantity;
    }

    public DayPeriodType getDayPeriod() {
        return dayPeriod;
    }

    public Date getTime() {
        return time;
    }

    public Date getDate() {
        return date;
    }

    public uz.ehealth.ritme.model.WeekDayType getWeekDay() {
        return weekDay;
    }

    public BigInteger getNumber() {
        return number;
    }

    public RegimenItem(
            @JsonProperty("weekDay") final uz.ehealth.ritme.model.WeekDayType weekDay,
            @JsonProperty("date") final Date date,
            @JsonProperty("number") final BigInteger number,
            @JsonProperty("dayPeriod") final DayPeriodType dayPeriod,
            @JsonProperty("time") final Date time,
            @JsonProperty("quantity") final BigDecimal quantity,
            @JsonProperty("administrationUnit") final AdministrationUnitType administrationUnit) {
        this.administrationUnit = administrationUnit;
        this.quantity = quantity;
        this.dayPeriod = dayPeriod;
        this.time = time;
        this.date = date;
        this.weekDay = weekDay;
        this.number = number;

    }

    public AdministrationUnitType getAdministrationUnit() {
        return administrationUnit;
    }

    private AdministrationUnitType administrationUnit;
    private BigDecimal quantity;
    private DayPeriodType dayPeriod;
    private Date time;
    private Date date;
    private WeekDayType weekDay;
    private BigInteger number;

}
