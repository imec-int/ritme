package uz.ehealth.ritme.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdcuyp0 on 28-9-2015.
 */
public enum AdministrationUnitType {
    ML_5("00001", "5 ml", "5 ml", "Koffielepel (5 ml)", 1),
    AMP("00002", "amp.", "amp.", "ampule", 1),
    APPLIC("00003", "applic.", "applic.", "aanbrengen", 1),
    CAPS("00004", "caps.", "caps.", "Capsule", 1),
    COMPR("00005", "compr.", "compr.", "Tablet", 1),
    DOSIS("00006", "Dosis", "dose", "Dosis", 1),
    DRUPPELS("00007", "Druppels", "gouttes", "Druppel(s)", 1),
    FLAC("00008", "flac.", "flac.", "Flesje", 2),
    IMPLANT("00009", "Implant", "implant.", "Implantaat", 2),
    INFUUS("00010", "Infuus", "perf.", "Infuus", 2),
    INHAL("00011", "inhal.", "inhal.", "Inhalatie", 1),
    INSERT("00012", "Insert", "insert", "Inbrengen", 2),
    KAUWGUM("00013", "Kauwgom", "gommes", "Kauwgom", 2),
    COMPRES("00014", "kompres(sen)", "compres(ses)", "Kompres(sen)", 2),
    LAV("00015", "lav.", "lav.", "Lavement", 2),
    ML("00016", "Ml", "ml", "ml", 1),
    OV("00017", "ov.", "ov.", "Ovule", 2),
    PAREL("00018", "parel(s)", "perle(s)", "Parel", 2),
    PAST("00019", "past.", "past.", "Pastille", 1),
    PATCH("00020", "Patch", "patch", "Pleister", 1),
    PATR("00021", "patr.", "cart.", "Patroon", 2),
    PEN("00022", "Pen", "stylo", "Pen", 2),
    PUFF("00023", "puff(s)", "puff(s)", "Puff(s)", 1),
    SPONS("00024", "Spons", "éponge", "Spons", 2),
    STYLO("00025", "Stylo", "stylo", "", null),
    SUPPO("00026", "Suppo", "suppo", "Zetpil", 1),
    TUBE("00027", "Tube", "tube", "Tube", 2),
    WIEK("00028", "Wiek", "mèche", "Wiek", 2),
    ZAK("00029", "Zak", "sac", "Zak", 2),
    ZAKJE("00030", "zakje(s)", "sachet(s)", "Zakje", 1),
    CM("cm", "Centimeter", "Centimetre", "Centimeter", null),
    DROPSPERMIN("dropsperminute", "Druppels per minuut", "Druppels per minuut", "", null),
    GR("gm", "Gram", "Gram", "Gram", null),
    IE("internationalunits", "Internationale eenheden", "Internationale eenheden", "", null),
    MCKPERH("mck/h", "Microgram per uur", "Microgram par heure", "Microgram per uur", null),
    MCKPERKGPERMIN("mck/kg/minute", "Microgram per kilogram per minuut", "Microgram per kilogram per minuut", "", null),
    MEASURE("measure", "Maat", "Maat", "", null),
    MGPERH("mg/h", "Milligram per uur", "Milligram par heure", "Milligram per uur", null),
    MLPERH("ml/h", "Milliliter per uur", "Millilitre par heure", "Milliliter per uur", null),
    TBL("tbl", "Eetlepel", "Eetlepel", "", null),
    TSP("tsp", "Koffielepel", "Koffielepel", "", null),
    UNTPERH("unt/h", "Eenheden per uur", "Eenheden per uur", "", null),
    MG("mg", "Milligram", "Milligram", "Milligram", null),
    MGPERML("mg/ml", "Milligram per milliliter", "Milligram par millilitre", "Milligram per milliliter", null);

    private final String ned;

    public String getNed() {
        return ned;
    }

    public String getFra() {
        return fra;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPriority() {
        return priority;
    }

    private final String fra;
    private final String description;
    private final Integer priority;
    private final String cd;
    private static final Map<String, AdministrationUnitType> LOOKUP = new HashMap<String, AdministrationUnitType>();

    AdministrationUnitType(String cd, String ned, String fra, String description, Integer priority) {
        this.cd = cd;
        this.ned = ned;
        this.fra = fra;
        this.description = description;
        this.priority = priority;
    }

    static {
        for (AdministrationUnitType d : AdministrationUnitType.values()) {
            LOOKUP.put(d.getCd(), d);
        }
    }

    public static AdministrationUnitType getByCd(String cd) {
        return LOOKUP.get(cd);
    }


    public String getCd() {
        return cd;
    }
}
