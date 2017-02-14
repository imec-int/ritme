package uz.ehealth.ritme.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdcuyp0 on 28-9-2015.
 */
public enum WeekDayType {
    MONDAY("monday", "maandag"),
    TUESDAY("tuesday", "dinsdag"),
    WEDNESDAY("wednesday", "woensdag"),
    THURSDAY("thursday", "donderdag"),
    FRIDAY("friday", "vrijdag"),
    SATURDAY("saturday", "zaterdag"),
    SUNDAY("sunday", "zondag");

    private final String cd;

    // Reverse-lookup map for getting a day from an abbreviation
    private static final Map<String, WeekDayType> LOOKUP = new HashMap<String, WeekDayType>();
    private final String ned;

    WeekDayType(String cd, String ned) {
        this.cd = cd;
        this.ned = ned;
    }


    static {
        for (WeekDayType d : WeekDayType.values()) {
            LOOKUP.put(d.getCd(), d);
        }
    }

    public static WeekDayType getByCd(String cd) {
        return LOOKUP.get(cd);
    }


    public String getCd() {
        return cd;
    }

    public String getNed() {
        return this.ned;
    }
}
