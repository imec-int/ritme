package uz.emv.sam.v1;

/**
 * Created by bdcuyp0 on 22-4-2016.
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

    /**
     * Vergelijkt twee comparable objecten maar houdt rekening met <code>null</code>
     * waarden. Indien de twee objecten <code>null</code> zijn, worden ze gelijk
     * geacht. Een <code>null</code> waarde wordt kleiner geacht dan om het even
     * welke niet-<code>null</code> waarde.
     *
     * @param o1 een comparable object
     * @param o2 een comparable object
     * @return de waarde 0 als de twee objecten gelijk zijn; een waarde kleiner dan 0 als o1 kleiner is dan o2;
     * en een waarde groter dan 0 als o1 groter is dan o2
     */
    public static <C extends Comparable<? super C>> int compare(C o1, C o2) {
        return compare(-1, o1, o2);
    }

    /**
     * Vergelijkt twee comparable objecten maar houdt rekening met <code>null</code>
     * waarden. Indien de twee objecten <code>null</code> zijn, worden ze gelijk
     * geacht. Indien <code>extremum == BOTTOM</code> wordt een <code>null</code>
     * waarde kleiner geacht dan om het even welke niet-<code>null</code> waarde.
     * Indien <code>extremum == TOP</code> wordt een <code>null</code> waarde
     * groter geacht dan om het even welke niet-<code>null</code> waarde.
     *
     * @param extremum waar zit <code>null</code> in de orde: {@link Extremum#TOP} of {@link Extremum#BOTTOM}
     * @param o1       een comparable object
     * @param o2       een comparable object
     * @return de waarde 0 als de twee objecten gelijk zijn; een waarde kleiner dan 0 als o1 kleiner is dan o2;
     * en een waarde groter dan 0 als o1 groter is dan o2
     */
    public static <C extends Comparable<? super C>> int compare(int extremum, C o1, C o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return extremum;
        }
        if (o2 == null) {
            return -extremum;
        }
        return o1.compareTo(o2);
    }
}
