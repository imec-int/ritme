package uz.ehealth.ritme.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdcuyp0 on 29-9-2015.
 */
public enum DrugRouteType {
    AURIC("00001", "auric.", "In het oor ", 1),
    DERM("00002", "derm.", "Op de huid ", 1),
    DERM_RECT("00003", "derm./rect.", "", 0),
    DERM_VAG("00004", "derm./vag.", "", 0),
    E_DUR("00005", "e.dur.", "Inspuiting buiten het hersenvlies ", 2),
    E_DUR_I_THEC_LOKAAL("00006", "e.dur./i.thec./lokaal.", "", 0),
    E_DUR_LOKAAL("00007", "e.dur./lokaal.", "", 0),
    E_TRACH("00008", "e.trach.", "In de luchtpijp ", 2),
    ENDOCERV("00009", "endocerv.", "Inbrengen in de baarmoederhals ", 2),
    I_ARTER("00010", "i.arter.", "Inspuiting in een slagader ", 2),
    I_CAVERN("00011", "i.cavern.", "Inspuiting in de penis ", 2),
    I_DERM("00012", "i.derm.", "Inspuiting in de huid ", 2),
    IM("00013", "i.m.", "Inspuiting in een spier ", 1),
    IM_I_THEC_E_DUR_SC("00014", "i.m./i.thec./e.dur./s.c.", "", 0),
    IM_IV("00015", "i.m./i.v.", "", 0),
    IM_IV_INF("00016", "i.m./i.v./inf.", "", 0),
    IM_IV_INF_E_DUR_SC("00017", "i.m./i.v./inf./e.dur./s.c.", "", 0),
    IM_IV_INF_I_ARTER_I_THEC("00018", "i.m./i.v./inf./i.arter./i.thec.", "", 0),
    IM_IV_INF_I_ARTER_LOKAAL_SC("00019", "i.m./i.v./inf./i.arter./lokaal/s.c.", "", 0),
    IM_IV_INF_I_THEC("00020", "i.m./i.v./inf./i.thec.", "", 0),
    IM_IV_INF_RECT("00021", "i.m./i.v./inf./rect.", "", 0),
    IM_IV_INHAL("00023", "i.m./i.v./inhal.", "", 0),
    IM_IV_INF_SC("00022", "i.m./i.v./inf./s.c.", "", 0),
    IM_IV_LOKAAL("00024", " i.m./i.v./lokaal", "", 0),
    IM_IV_OR("00025", "i.m./i.v./or.", "", 0),
    IM_IV_RECT("00026", "i.m./i.v./rect.", "", 0),
    IM_IV_SC("00027", "i.m./i.v./s.c.", "", 0),
    IM_INF("00028", "i.m./inf.", "", 0),
    IM_INF_LOKAAL("00029", "i.m./inf./lokaal.", "", 0),
    IM_LOKAAL("00030", "i.m./lokaal.", "", 0),
    IM_LOKAAL_RECT("00031", "i.m./lokaal/rect.", "", 0),
    IM_SC("00032", "i.m./s.c.", "", 0),
    I_THEC("00033", "i.thec.", "Inspuiting in het ruggenmerg ", 2),
    I_UT("00034", "i.ut.", "Inbrengen in de baarmoeder ", 2),
    IV("00035", "i.v.", "Inspuiting in een ader ", 1),
    IV_E_DUR_LOKAAL("00036", "i.v./e.dur./lokaal.", "", 0),
    IV_I_ARTER_IVES("00037", "i.v./i.arter./i.ves.", "", 0),
    IV_PERF("00038", "i.v./perf.", "", 0),
    IV_PERF_P_DUR("00039", "i.v./perf./p.dur.", "", 0),
    IV_PERF_I_ARTER("00040", "i.v./perf./i.artér.", "", 0),
    IV_PERF_I_THEC_SC("00041", "i.v./perf./i.théc./s.c.", "", 0),
    IV_PERF_INHAL("00042", "i.v./perf./inhal.", "", 0),
    IV_PERF_SC("00043", "i.v./perf./s.c.", "", 0),
    IV_SC("00044", "i.v./s.c.", "", 0),
    IVES("00045", "i.vés.", "In de urineblaas ", 2),
    PERF("00046", "perf.", "Infuus ", 1),
    PERF_SC("00048", "perf./s.c.", "", 0),
    PERF_IVES("00047", "perf./i.vés.", "", 0),
    INHAL("00049", "inhal.", "Inhalatie ", 1),
    INHAL_NAS_LOCAL("00050", "inhal./nas./local", "", 0),
    INJ("00051", "inj.", "Inspuiting ", 2),
    LEVRE("00052", "lèvre", " Op de lippen", 1),
    LOCAL("00053", "local", " Lokaal", 1),
    MUC("00054", "muc.", "Op het slijmvlies ", 2),
    NAS("00055", "nas.", "In de neus ", 1),
    OPHT("00056", "opht.", "In de ogen ", 1),
    OPHT_AURIC("00057", "opht./auric.", "", 0),
    OPHT_AURIC_NAS("00058", "opht./auric./nas.", "", 0),
    OPHT_AURIC_NAS_LOCAL("00059", "opht./auric./nas./local", "", 0),
    OR("00060", "or.", "Innemen ", 1),
    OR_BUCCO_PHAR_DERM("00061", "or./bucco-phar./derm.", "", 0),
    OR_RECT("00062", "or./rect.", "", 0),
    OR_SL("00063", "or./s.l.", "", 0),
    BUCCO_PHAR("00064", "bucco-phar.", "In de keel ", 1),
    BUCCO_PHAR_DERM("00065", "bucco-phar./derm.", "", 0),
    BUCCO_PHAR_NAS("00066", "bucco-phar./nas.", "In de keel en in de neus ", 2),
    RECT("00067", "rect.", "Anaal opsteken ", 1),
    SC("00068", "s.c.", "Onderhuidse inspuiting ", 1),
    SC_I_DERM("00069", "s.c./i.derm.", "", 0),
    SL("00070", "s.l.", "Onder de tong ", 1),
    DENTS("00071", "dents.", "Op de tanden ", 2),
    URETHR("00072", "urethr.", "In de urineleider ", 2),
    VAG("00073", "vag.", "Vaginaal ", 1);
    private final String cd;
    private final String name;
    private final String description;
    private final int priority;

    DrugRouteType(String cd, String name, String description, int priority) {
        this.cd = cd;
        this.name = name;
        this.description = description;
        this.priority = priority;
    }


    // Reverse-lookup map for getting a day from an abbreviation
    private static final Map<String, DrugRouteType> LOOKUP = new HashMap<String, DrugRouteType>();

    public String getCd() {
        return cd;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    static {
        for (DrugRouteType d : DrugRouteType.values()) {
            LOOKUP.put(d.getCd(), d);
        }
    }

    public static DrugRouteType getByCd(String cd) {
        return LOOKUP.get(cd);
    }
}
