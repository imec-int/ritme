package uz.ehealth.ritme.core;

/**
 * Created by bdcuyp0 on 29-1-2016.
 */
public class ObjectTools {

    private ObjectTools() {
    }

    /**
     * Vergelijkt twee objecten maar houdt rekening met <code>null</code> waarden.
     * Indien de twee objecten <code>null</code> zijn, worden ze gelijk geacht.
     * Een <code>null</code> waarde wordt verschillend geacht van een niet-<code>null</code> waarde.
     *
     * @param o1 een object, of <code>null</code>
     * @param o2 een object, of <code>null</code>
     * @return zijn de gegeven argumenten gelijk of alletwee null ?
     * @see #equals(Object, Object, boolean)
     */
    public static boolean equals(Object o1, Object o2) {
        return equals(o1, o2, true);
    }

    /**
     * Gelijkaardig aan {@link #equals(Object, Object)} maar dan met de mogelijkheid om te bepalen
     * of twee null-objecten al dan niet gelijk beschouwd moeten worden.
     *
     * @param o1                een object, of <code>null</code>
     * @param o2                een object, of <code>null</code>
     * @param nullIsEqualToNull zijn twee null waarden aan mekaar gelijk?
     * @return zijn de gegeven argumenten gelijk of - indien de vlag gezet is - alletwee null ?
     */
    public static boolean equals(Object o1, Object o2, boolean nullIsEqualToNull) {
        if (o1 == null && o2 == null) {
            return nullIsEqualToNull;
        }

        //noinspection ObjectEquality
        if (o1 == o2) {
            return true;
        }

        if (o1 == null || o2 == null) {
            return false;
        }

        return o1.equals(o2);
    }
}
