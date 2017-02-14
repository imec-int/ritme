package uz.ehealth.ritme.model;

/**
 * Created by bdcuyp0 on 10-5-2016.
 */
public enum TimeUnitType {
    A("a", "Year", "jaar"),
    MO("mo", "Month", "maand"),
    WK("wk", "Week", "week"),
    D("d", "Day", "dag"),
    HR("hr", "Hour", "uur"),
    MIN("min", "Min", "min"),
    S("s", "Second", "s"),
    MS("ms", "Millisecond", "ms"),
    US("us", "Microsecond", "µs"),
    NS("ns", "Nanosecond", "ns");

    public String getCd() {
        return cd;
    }

    public String getEng() {
        return eng;
    }

    public String getNed() {
        return ned;
    }

    private final String cd;
    private final String eng;
    private final String ned;

    TimeUnitType(final String cd, final String eng, final String ned) {
        this.cd = cd;
        this.eng = eng;
        this.ned = ned;
    }
}
