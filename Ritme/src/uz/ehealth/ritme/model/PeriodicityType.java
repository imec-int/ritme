package uz.ehealth.ritme.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdcuyp0 on 30-9-2015.
 */
public enum PeriodicityType {
    UH("UH", "Per half uur"),
    U("U", "Per uur"),
    UT("UT", "Per 2 uur"),
    UD("UD", "Per 3 uur"),
    UV("UV", "Per 4 uur"),
    UQ("UQ", "Per 5 uur"),
    UZ("UZ", "Per 6 uur"),
    US("US", "Per 7 uur"),
    UA("UA", "Per 8 uur"),
    UN("UN", "Per 9 uur"),
    UX("UX", "Per 10 uur"),
    UE("UE", "Per 11 uur"),
    UW("UW", "Per 12 uur"),
    D("D", "Per dag"),
    DT("DT", "Per 2 dagen"),
    O1("O1", "Elke 2 dagen"),
    DD("DD", "Per 3 dagen"),
    DV("DV", "Per 4 dagen"),
    DQ("DQ", "Per 5 dagen"),
    DZ("DZ", "Per 6 dagen"),
    W("W", "Per 7 dagen"),
    DA("DA", "Per 8 dagen"),
    DN("DN", "Per 9 dagen"),
    DX("DX", "Per 10 dagen"),
    DE("DE", "Per 11 dagen"),
    DW("DW", "Per 12 dagen"),
    WT("WT", "Per 2 weken"),
    WD("WD", "Per 3 weken"),
    WV("WV", "Per 4 weken"),
    M("M", "Per maand"),
    WQ("WQ", "Per 5 weken"),
    WZ("WZ", "Per 6 weken"),
    WS("WS", "Per 7 weken"),
    WA("WA", "Per 8 weken"),
    MT("MT", "Per 2 maanden"),
    WN("WN", "Per 9 weken"),
    WX("WX", "Per 10 weken"),
    WE("WE", "Per 11 weken"),
    WW("WW", "Per 12 weken"),
    MD("MD", "Per 3 maanden"),
    MV("MV", "Per 4 maanden"),
    MQ("MQ", "Per 5 maanden"),
    WP("WP", "Per 24 weken"),
    JH2("JH2", "Per half year"),
    MZ2("MZ2", "Per 6 maanden"),
    MS("MS", "Per 7 maanden"),
    MA("MA", "Per 8 maanden"),
    MN("MN", "Per 9 maanden"),
    MX("MX", "Per 10 maanden"),
    ME("ME", "Per 11 maanden"),
    J("J", "Per jaar"),
    MC("MC", "Per 18 maanden"),
    JT("JT", "Per 2 jaar"),
    JD("JD", "Per 3 jaar"),
    JV("JV", "Per 4 jaar"),
    JQ("JQ", "Per 5 jaar"),
    JZ("JZ", "Per 6 jaar"),
    ONDEMAND("ondemand", "Op vraag");

    // Reverse-lookup map for getting a day from an abbreviation
    private static final Map<String, PeriodicityType> LOOKUP = new HashMap<String, PeriodicityType>();

    private final String cd;
    private final String description;

    PeriodicityType(String cd, String description) {
        this.cd = cd;
        this.description = description;
    }

    static {
        for (PeriodicityType d : PeriodicityType.values()) {
            LOOKUP.put(d.getCd(), d);
        }
    }

    public static PeriodicityType getByCd(String cd) {
        return LOOKUP.get(cd);
    }


    public String getCd() {
        return cd;
    }

    public String getDescription() {
        return description;
    }
}
