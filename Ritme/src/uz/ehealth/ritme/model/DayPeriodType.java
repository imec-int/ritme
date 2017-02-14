package uz.ehealth.ritme.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdcuyp0 on 28-9-2015.
 */
public enum DayPeriodType {
    AFTERBREAKFAST("afterbreakfast", "na het ontbijt"),
    AFTERDINNER("afterdinner", "na het avondmaal"),
    AFTERLUNCH("afterlunch", "na het middagmaal"),
    BEFOREBREAKFAST("beforebreakfast", "voor het ontbijt"),
    BEFOREDINNER("beforedinner", "voor het avondmaal"),
    BEFORELUNCH("beforelunch", "voor het middagmaal"),
    BETWEENBREAKFASTANDLUNCH("betweenbreakfastandlunch", "tussen ontbijt en middagmaal"),
    BETWEENDINNERANDSLEEP("betweendinnerandsleep", "tussen het avondmaal en slapengaan"),
    BETWEENLUNCHANDDINNER("betweenlunchanddinner", "tussen het middagmaal en het avondmaal"),
    MORNING("morning", "'s morgens"),
    THEHOUROFSLEEP("thehourofsleep", "slapengaan"),
    DURINGBREAKFAST("duringbreakfast", "tijdens het ontbijt"),
    DURINGLUNCH("duringlunch", "tijdens het middagmaal"),
    DURINGDINNER("duringdinner", "tijdens het avondmaal");


    private final String cd;

    // Reverse-lookup map for getting a day from an abbreviation
    private static final Map<String, DayPeriodType> LOOKUP = new HashMap<String, DayPeriodType>();
    private final String ned;

    DayPeriodType(String cd, String ned) {
        this.cd = cd;
        this.ned = ned;
    }


    static {
        for (DayPeriodType d : DayPeriodType.values()) {
            LOOKUP.put(d.getCd(), d);
        }
    }

    public static DayPeriodType getByCd(String cd) {
        return LOOKUP.get(cd);
    }


    public String getCd() {
        return cd;
    }

    public String getNed() {
        return ned;
    }
}
