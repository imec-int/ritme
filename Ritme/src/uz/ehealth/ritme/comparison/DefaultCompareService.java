package uz.ehealth.ritme.comparison;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.Years;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.model.MedicatieSchemaItem;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.model.RegimenItem;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.medic.MedicService;
import uz.ehealth.ritme.outbound.organisation.OrganisationData;
import uz.ehealth.ritme.outbound.organisation.OrganisationService;
import uz.ehealth.ritme.outbound.patient.PatientData;
import uz.ehealth.ritme.outbound.patient.PatientService;
import uz.ehealth.ritme.plugins.PluginManager;
import uz.emv.sam.v1.domain.ATC;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by bdcuyp0 on 21-10-2015.
 */
public class DefaultCompareService implements CompareService {


    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(DefaultCompareService.class);
    private static final Pattern DOUBLE_QUOTES = Pattern.compile("\"", Pattern.LITERAL);
    private static final Pattern NEW_LINE = Pattern.compile("\n", Pattern.LITERAL);


    private static final Map<Pair<MedicationIdType, Integer>, Integer> INDEXES = new HashMap<Pair<MedicationIdType, Integer>, Integer>();

    static {
        INDEXES.put(MedicationCreatorFactory.AMPP_KEY, 8);
        INDEXES.put(MedicationCreatorFactory.VMPP_KEY, 7);
        INDEXES.put(MedicationCreatorFactory.AMP_KEY, 6);
        INDEXES.put(MedicationCreatorFactory.VMP_KEY, 5);
        INDEXES.put(MedicationCreatorFactory.ATM_KEY, 4);
        INDEXES.put(MedicationCreatorFactory.VTM_KEY, 3);
        INDEXES.put(MedicationCreatorFactory.ATC5_KEY, 2);
        INDEXES.put(MedicationCreatorFactory.ATC4_KEY, 1);
        INDEXES.put(MedicationCreatorFactory.ATC3_KEY, 0);
    }

    //neem recursief de wortel van 36, trek er 1 af en deel door 10
    private static final double GROEN = 0;
    private static final double GEEL = 0.00575897342418854341253690929632;
    private static final double ORANJE = 0.01184960459738218599287558065995;
    private static final double ROOD = 0.02510334048590738236780437177798;
    private static final double PAARS = 0.05650845800732873165844854991587;
    private static final double BLAUW = 0.14494897427831780981972840747059;
    private static final double CYAAN = 0.5;

    private static final double[][] SCORE_MATRIX = new double[][]
            {
                    {GROEN, CYAAN, CYAAN, CYAAN, CYAAN, CYAAN, CYAAN, CYAAN, CYAAN},
                    {BLAUW, GROEN, CYAAN, CYAAN, CYAAN, CYAAN, CYAAN, CYAAN, CYAAN},
                    {PAARS + BLAUW, PAARS, GROEN, CYAAN, CYAAN, CYAAN, CYAAN, CYAAN, CYAAN},
                    {ROOD + PAARS + BLAUW, ROOD + PAARS, ROOD, GROEN, CYAAN, CYAAN, CYAAN, CYAAN, CYAAN},
                    {GEEL + ROOD + PAARS + BLAUW, GEEL + ROOD + PAARS, GEEL + ROOD, GEEL, GROEN, CYAAN, CYAAN, CYAAN, CYAAN},
                    {ORANJE + ROOD + PAARS + BLAUW, ORANJE + ROOD + PAARS, ORANJE + ROOD, ORANJE, CYAAN, GROEN, CYAAN, CYAAN, CYAAN},
                    {GEEL + ORANJE + ROOD + PAARS + BLAUW, GEEL + ORANJE + ROOD + PAARS, GEEL + ORANJE + ROOD, GEEL + ORANJE, ORANJE, GROEN, GROEN, CYAAN, CYAAN},
                    {GROEN + ORANJE + ROOD + PAARS + BLAUW, GROEN + ORANJE + ROOD + PAARS, GROEN + ORANJE + ROOD, GROEN + ORANJE, CYAAN, GEEL, CYAAN, GROEN, CYAAN},
                    {GROEN + GEEL + ORANJE + ROOD + PAARS + BLAUW, GROEN + GEEL + ORANJE + ROOD + PAARS, GROEN + GEEL + ORANJE + ROOD, GROEN + GEEL + ORANJE, GROEN + ORANJE, GEEL + GROEN, GROEN, GEEL, GROEN}

            };


    @Override
    public ComparisonResult compare(final ComparisonInput input) {
        final List<MedicatieSchemaItem> left = input.getLeft();
        final List<MedicatieSchemaItem> right = input.getRight();
        List<ComparisonPair> pairs = new ArrayList<ComparisonPair>();
        List<Integer> used = new ArrayList<Integer>();

        for (int li = 0; li < left.size(); li++) {
            MedicatieSchemaItem itemLeft = left.get(li);
            List<ComparisonPair> scores = new ArrayList<ComparisonPair>();
            for (int ri = 0; ri < right.size(); ri++) {
                MedicatieSchemaItem itemRight = right.get(ri);
                scores.add(scorePair(li, itemLeft, ri, itemRight));

            }
            try {
                ComparisonPair max = Collections.max(scores, new Comparator<ComparisonPair>() {
                    @Override
                    public int compare(final ComparisonPair o1, final ComparisonPair o2) {
                        return o1.getScore().compareTo(o2.getScore());
                    }
                });
                if (max.getScore() > 0) {
                    int index = scores.indexOf(max);
                    pairs.add(max);
                    used.add(index);
                } else {
                    pairs.add(new ComparisonPair(li, null, 0d, Collections.<String>emptyList()));
                }


            } catch (NoSuchElementException e) {
                pairs.add(new ComparisonPair(li, null, 0d, Collections.<String>emptyList()));
            }


        }
        for (int ri = 0; ri < right.size(); ri++) {
            if (!used.contains(ri)) {
                pairs.add(new ComparisonPair(null, ri, 0d, Collections.<String>emptyList()));
            }

        }

        return new ComparisonResult(pairs);

    }


    public ComparisonPair scorePair(final int li, final MedicatieSchemaItem itemLeft, final int ri, final MedicatieSchemaItem itemRight) {
        Pair<Double, List<String>> medicatieScoreAndAttributes = scoreMedicatie(itemLeft, itemRight);
        Pair<Double, List<String>> posologyScoreAndAttributes = scorePosology(itemLeft, itemRight);
        final List<String> attributes = new ArrayList<String>();
        if (medicatieScoreAndAttributes.getLeft() != 0) {
            attributes.addAll(medicatieScoreAndAttributes.getRight());
            attributes.addAll(posologyScoreAndAttributes.getRight());
        }
        return new ComparisonPair(li, ri, medicatieScoreAndAttributes.getLeft() - posologyScoreAndAttributes.getLeft(), attributes);
    }

    private Pair<Double, List<String>> scorePosology(final MedicatieSchemaItem itemLeft, final MedicatieSchemaItem itemRight) {

        final List<String> attributes = new ArrayList<String>();
        boolean periodicityResult = ObjectUtils.equals(itemLeft.getPeriodicity(), itemRight.getPeriodicity());
        if (!periodicityResult) {
            attributes.add("/periodicity");
        }
        boolean drugRouteResult = ObjectUtils.equals(itemLeft.getDrugRoute(), itemRight.getDrugRoute());
        if (!drugRouteResult) {
            attributes.add("/drugRoute");
        }
        boolean posologyResult = ObjectUtils.equals(itemLeft.getPosology(), itemRight.getPosology());
        if (!posologyResult) {
            attributes.add("/posology");
        }
        final RegimenItem[] regimenItemsLeft = itemLeft.getRegimenItems();
        final RegimenItem[] regimenItemsRight = itemRight.getRegimenItems();
        //could be refined with a distance algorithm (each regimenItem is a vector, compare distances between vectors)...
        boolean regimensResult = equals(regimenItemsLeft, regimenItemsRight);
        if (!regimensResult) {
            attributes.add("/regimenItems");
        }
        if (drugRouteResult && posologyResult && regimensResult && periodicityResult) {
            return Pair.of(0d, Collections.<String>emptyList());
        }
        return Pair.of(0.0005, attributes);
    }

    private Pair<Double, List<String>> scoreMedicatie(final MedicatieSchemaItem schemaItemLeft, final MedicatieSchemaItem schemaItemRight) {
        Medication medicationLeft = schemaItemLeft.getIntendedMedication();
        Medication medicationRight = schemaItemRight.getIntendedMedication();
        List<String> attributes = Collections.singletonList("/intendedMedication");
        Pair<MedicationIdType, Integer> leftKey = calculateKey(medicationLeft);
        Pair<MedicationIdType, Integer> rightKey = calculateKey(medicationRight);


        if (equals(medicationLeft, medicationRight)) {
            return Pair.of(1d, Collections.<String>emptyList());
        } else if (equals(medicationLeft, medicationRight, MedicationCreatorFactory.AMP_MEDICATION_CREATOR)) {
            return Pair.of(1 - (SCORE_MATRIX[INDEXES.get(leftKey)][INDEXES.get(MedicationCreatorFactory.AMP_KEY)] + SCORE_MATRIX[INDEXES.get(rightKey)][INDEXES.get(MedicationCreatorFactory.AMP_KEY)]), attributes);
        } else if (equals(medicationLeft, medicationRight, MedicationCreatorFactory.VMPP_MEDICATION_CREATOR)) {
            return Pair.of(1 - (SCORE_MATRIX[INDEXES.get(leftKey)][INDEXES.get(MedicationCreatorFactory.VMPP_KEY)] + SCORE_MATRIX[INDEXES.get(rightKey)][INDEXES.get(MedicationCreatorFactory.VMPP_KEY)]), attributes);
        } else if (equals(medicationLeft, medicationRight, MedicationCreatorFactory.VMP_MEDICATION_CREATOR)) {
            return Pair.of(1 - (SCORE_MATRIX[INDEXES.get(leftKey)][INDEXES.get(MedicationCreatorFactory.VMP_KEY)] + SCORE_MATRIX[INDEXES.get(rightKey)][INDEXES.get(MedicationCreatorFactory.VMP_KEY)]), attributes);
        } else if (equals(medicationLeft, medicationRight, MedicationCreatorFactory.ATM_MEDICATION_CREATOR)) {
            return Pair.of(1 - (SCORE_MATRIX[INDEXES.get(leftKey)][INDEXES.get(MedicationCreatorFactory.ATM_KEY)] + SCORE_MATRIX[INDEXES.get(rightKey)][INDEXES.get(MedicationCreatorFactory.ATM_KEY)]), attributes);
        } else if (equals(medicationLeft, medicationRight, MedicationCreatorFactory.VTM_MEDICATION_CREATOR)) {
            return Pair.of(1 - (SCORE_MATRIX[INDEXES.get(leftKey)][INDEXES.get(MedicationCreatorFactory.VTM_KEY)] + SCORE_MATRIX[INDEXES.get(rightKey)][INDEXES.get(MedicationCreatorFactory.VTM_KEY)]), attributes);
        } else if (equals(medicationLeft, medicationRight, MedicationCreatorFactory.ATC5_MEDICATION_CREATOR)) {
            return Pair.of(1 - (SCORE_MATRIX[INDEXES.get(leftKey)][INDEXES.get(MedicationCreatorFactory.ATC5_KEY)] + SCORE_MATRIX[INDEXES.get(rightKey)][INDEXES.get(MedicationCreatorFactory.ATC5_KEY)]), attributes);
        } else if (equals(medicationLeft, medicationRight, MedicationCreatorFactory.ATC4_MEDICATION_CREATOR)) {
            return Pair.of(1 - (SCORE_MATRIX[INDEXES.get(leftKey)][INDEXES.get(MedicationCreatorFactory.ATC4_KEY)] + SCORE_MATRIX[INDEXES.get(rightKey)][INDEXES.get(MedicationCreatorFactory.ATC4_KEY)]), attributes);
        } else if (equals(medicationLeft, medicationRight, MedicationCreatorFactory.ATC3_MEDICATION_CREATOR)) {
            return Pair.of(1 - (SCORE_MATRIX[INDEXES.get(leftKey)][INDEXES.get(MedicationCreatorFactory.ATC3_KEY)] + SCORE_MATRIX[INDEXES.get(rightKey)][INDEXES.get(MedicationCreatorFactory.ATC3_KEY)]), attributes);
        }
        return Pair.of(0d, Collections.<String>emptyList());
    }

    private Pair<MedicationIdType, Integer> calculateKey(final Medication medication) {

        switch (medication.getMedicationIdType()) {

            case EAN:
                break;
            case CNK:
                return Pair.of(medication.getMedicationIdType(), 0);
            case INN:
                return Pair.of(medication.getMedicationIdType(), 0);
            case MAG:
                break;
            case AMP:
                return Pair.of(medication.getMedicationIdType(), 0);
            case VMPP:
                return Pair.of(medication.getMedicationIdType(), 0);
            case ATM:
                return Pair.of(medication.getMedicationIdType(), 0);
            case VTM:
                return Pair.of(medication.getMedicationIdType(), 0);
            case ATC:
                final List<ATC> atcs = MedicationCreatorFactory.SAM_SERVICE.getATCByAtcCv(medication.getMedicationId());
                if (!atcs.isEmpty()) {
                    ATC atc = atcs.get(0);
                    return Pair.of(medication.getMedicationIdType(), ATCMedicationCreator.getLevel(atc.getAtcCv()));
                }
                break;

        }
        return null;
    }


    private boolean equals(final RegimenItem[] regimenItemsLeft, final RegimenItem[] regimenItemsRight) {
        boolean result = true;
        if (regimenItemsLeft != null && regimenItemsRight != null) {
            try {
                for (int i = 0; i < regimenItemsLeft.length; i++) {

                    final RegimenItem regimenItemLeft = regimenItemsLeft[i];
                    final RegimenItem regimenItemRight = regimenItemsRight[i];
                    result = equals(regimenItemLeft, regimenItemRight);

                }
            } catch (Exception e) {
                result = false;
            }

        } else if (regimenItemsLeft == null && regimenItemsRight == null) {
            // do nothing
        } else {
            result = false;
        }
        return result;
    }

    private boolean equals(final RegimenItem regimenItemLeft, final RegimenItem regimenItemRight) {
        boolean result = true;
        if (regimenItemLeft != null && regimenItemRight != null) {
            result = ObjectUtils.equals(regimenItemLeft.getAdministrationUnit(), regimenItemRight.getAdministrationUnit());
            result = result && ObjectUtils.equals(regimenItemLeft.getDate(), regimenItemRight.getDate());
            result = result && ObjectUtils.equals(regimenItemLeft.getDayPeriod(), regimenItemRight.getDayPeriod());
            result = result && ObjectUtils.equals(regimenItemLeft.getWeekDay(), regimenItemRight.getWeekDay());
            result = result && ObjectUtils.equals(regimenItemLeft.getNumber(), regimenItemRight.getNumber());
            boolean tempResult;
            if (regimenItemLeft.getQuantity() == null || regimenItemRight.getQuantity() == null) {
                tempResult = ObjectUtils.equals(regimenItemLeft.getQuantity(), regimenItemRight.getQuantity());
            } else {
                tempResult = (regimenItemLeft.getQuantity().compareTo(regimenItemRight.getQuantity()) == 0);
            }
            result = result && tempResult;
            result = result && ObjectUtils.equals(regimenItemLeft.getTime(), regimenItemRight.getTime());

        }
        return result;
    }

    private boolean equals(final Medication intendedMedicationLeft, final Medication intendedMedicationRight) {
        boolean result = true;
        if (intendedMedicationLeft != null && intendedMedicationRight != null) {
            if (intendedMedicationLeft.getMedicationIdType() == MedicationIdType.MAG && intendedMedicationRight.getMedicationIdType() == MedicationIdType.MAG) {
                result = ObjectUtils.equals(intendedMedicationLeft.getMagistralText(), intendedMedicationRight.getMagistralText());
            }
            result = result && ObjectUtils.equals(intendedMedicationLeft.getMedicationId(), intendedMedicationRight.getMedicationId());
            result = result && ObjectUtils.equals(intendedMedicationLeft.getMedicationIdType(), intendedMedicationRight.getMedicationIdType());
        } else if (intendedMedicationLeft == null && intendedMedicationRight == null) {
            // do nothing
        } else {
            result = false;
        }
        return result;
    }

    private boolean equals(Medication itemLeft, Medication itemRight, final F1<Medication, Medication> converter) {
        boolean result = true;
        if (itemLeft != null && itemRight != null) {
            itemLeft = converter.invoke(itemLeft);
            itemRight = converter.invoke(itemRight);
            if (itemLeft != null && itemRight != null) {
                result = ObjectUtils.equals(itemLeft.getMedicationId(), itemRight.getMedicationId());
                result = result && ObjectUtils.equals(itemLeft.getMedicationIdType(), itemRight.getMedicationIdType());
            } else {
                result = false;
            }
        } else if (itemLeft == null && itemRight == null) {
            // do nothing
        } else {
            result = false;
        }
        return result;
    }


    @Override
    public String createComparisonUIData(final List<MedicatieSchemaItem> left1, final List<MedicatieSchemaItem> right1, final List<ComparisonPair> comparisonResult, final String nihiiOrg, final String ssin, final String user) {
        final List<Integer> unique1 = new ArrayList<Integer>();
        for (ComparisonPair comparisonPair : comparisonResult) {
            Integer left = comparisonPair.getLeft();
            if (left != null) {
                unique1.add(left);
            }

        }

        final List<Integer> unique2 = new ArrayList<Integer>();
        for (ComparisonPair comparisonPair : comparisonResult) {
            Integer right = comparisonPair.getRight();
            if (right != null) {
                unique2.add(right + left1.size());
            }

        }


        final List<List<Integer>> identical = new ArrayList<List<Integer>>();
        for (ComparisonPair comparisonPair : comparisonResult) {
            if (comparisonPair.getScore().equals(1d)) {
                identical.add(Arrays.asList(comparisonPair.getLeft(), comparisonPair.getRight() + left1.size()));
            }
        }


        final List<String> similar = new ArrayList<String>();

        for (ComparisonPair comparisonPair : comparisonResult) {
            if (comparisonPair.getScore() > 0 && comparisonPair.getScore() < 1) {
                Set<String> differences = new HashSet<String>();
                for (String attribute : comparisonPair.getAttributes()) {
                    Set<String> set = new HashSet<String>();
                    if ("/posology".equals(attribute)) {
                        set.add("__ATTR_DOSE__");
                        set.add("__ATTR_FREQUENCY__");
                    }
                    if ("/intendedMedication".equals(attribute)) {
                        set.add("__ATTR_NAME__");
                    }
                    if ("/regimenItems".equals(attribute)) {
                        set.add("__ATTR_DOSE__");
                        set.add("__ATTR_FREQUENCY__");
                    }
                    if ("/drugRoute".equals(attribute)) {
                        set.add("__ATTR_ROUTE__");
                    }
                    if ("/regimenItems".equals(attribute)) {
                        set.add("__ATTR_FREQUENCY__");
                    }
                    differences.addAll(set);
                }

                String jsonDifferences = "[";
                for (String s : differences) {
                    jsonDifferences = jsonDifferences + "\"" + s + "\",";
                }
                jsonDifferences = jsonDifferences.substring(0, jsonDifferences.length() - 1);
                jsonDifferences += "]";

                String item = "{ items :" + Arrays.asList(comparisonPair.getLeft(), comparisonPair.getRight() + left1.size()) + ", differences : " + jsonDifferences + " }";

                similar.add(item);

            }
        }


        final List<Object[]> list0 = getTriples(left1, 0, "list0", user, nihiiOrg);
        final List<Object[]> list1 = getTriples(right1, left1.size(), "list1", user, nihiiOrg);


        PatientData patientData = PluginManager.get("ritme.outbound.patient", PatientService.class).getData(ssin, user, nihiiOrg);


        String json = "{" +
                // patient data
                "patientFirstName: \"" + patientData.getFirstName() + "\"," +
                "patientLastName: \"" + patientData.getName() + "\"," +
                //todo improve with joda time
                "patientAge:" + Years.yearsBetween(new DateTime(patientData.getBirthDate().getTime()), new DateTime(new Date().getTime())).get(DurationFieldType.years()) + " ," +
                "patientGender: \"" + patientData.getSex() + "\"," +
                "nihiiOrg: \"" + nihiiOrg + "\"," +
                // item relationships
                "unique1:" + unique1.toString() + "," +
                "unique2:" + unique2.toString() + "," +
                "identical:" + identical.toString() + "," +
                "similar: [";

        boolean first = true;
        for (String s : similar) {
            if (first) {
                first = false;
            } else {
                json += ",";
            }
            json += s;

        }

        json += "]," +

                // item data
                "csv:" +
                "\"id,origin,recorded name,generic name,brand name,dose,route,frequency,drug classes,diagnoses,validator,organisation\\n\"+";

        for (Object[] triple : list0) {
            json += triple[1];
        }

        for (Object[] triple : list1) {
            json += triple[1];
        }

        json = json.substring(0, json.length() - 4);

        json += "\"";

        json += ", other_data: {";

        for (Object[] triple : list0) {
            if (!json.endsWith("{")) {
                json += ",";
            }
            json += triple[0];
            json += ":{ \"__ATTR_FULLDATA__\" :";
            json += triple[2];
            json += "}";

        }

        for (Object[] triple : list1) {
            if (!json.endsWith("{")) {
                json += ",";
            }
            json += triple[0];
            json += ":{ \"__ATTR_FULLDATA__\" :";
            json += triple[2];
            json += "}";

        }

        json += "} }";

        LOGGER.error(json);


        return json;
    }

    private static List<Object[]> getTriples(final List<MedicatieSchemaItem> right1, int counter, final String listName, final String user, final String nihiiOrg) {
        final List<Object[]> list1 = new ArrayList<Object[]>();
        for (MedicatieSchemaItem item : right1) {
            final Object[] result = new Object[]{counter, getString(item, listName, counter, user, nihiiOrg), JSONTools.marshal(item)};
            list1.add(result);
            counter++;
        }
        return list1;
    }

    public static String getString(final MedicatieSchemaItem source, final String listId, final int counter, final String user, final String nihiiOrg) {
        MedicData[] medicData = PluginManager.get("ritme.internal.caretaker", MedicService.class).query(source.getMedicSSIN(), null, null, null, null, null, null, null, null, user, nihiiOrg);
        if (medicData.length == 0) {
            //fallback naar NIHII
            medicData = PluginManager.get("ritme.internal.caretaker", MedicService.class).query(null, null, null, null, null, null, source.getMedicNIHII(), null, null, user, nihiiOrg);
        }
        String validator = "";
        if (medicData.length != 0) {
            validator = medicData[0].getName() + ", " + medicData[0].getFirstName();
        }
        final OrganisationData[] organisationData = PluginManager.get("ritme.internal.organisation", OrganisationService.class).query(null, source.getOrgNIHII(), null, null, null, null, null, null, user, nihiiOrg);
        String organisation = "";
        if (organisationData.length != 0) {
            organisation = organisationData[0].getName();
        }
        String atc2CodeName = "";
        if (source.getIntendedMedication() != null) {
            final Set<ATC> result = MedicationCreatorFactory.SAM_SERVICE.getATCForMedication(source.getIntendedMedication().getMedicationIdType(), source.getIntendedMedication().getMedicationId());
            final Set<ATC> atc2 = new HashSet<ATC>();
            for (ATC anyAtcCode : result) {
                ATC temp = anyAtcCode;
                //zolang er een parent is en een parent van een parent, dan zitten we niet op niveau twee.
                while (temp.getParent() != null && temp.getParent().getParent() != null) {
                    temp = temp.getParent();
                }
                atc2.add(temp);
            }

            for (ATC item : atc2) {
                atc2CodeName += (item.getName() + ",");
            }
            if (atc2CodeName.endsWith(",")) {
                atc2CodeName = atc2CodeName.substring(0, atc2CodeName.length() - 1);
            }
        }
        String dose = "";
        if (source.getRegimenItems().length != 0) {
            for (RegimenItem item : source.getRegimenItems()) {
                if (!dose.isEmpty()) {
                    dose += " ";
                }
                dose += (item.getQuantity() + " " + (item.getAdministrationUnit() != null ? item.getAdministrationUnit().getNed() : "[]"));
            }
        } else {
            dose += source.getPosology();
        }
        String result = "\"" + counter + "," + listId + ",";
        result += "\\\"" + (source.getIntendedMedication() != null ? NEW_LINE.matcher(DOUBLE_QUOTES.matcher(source.getIntendedMedication().getMedicationDescription()).replaceAll("")).replaceAll(" ") : "[null]") + "\\\",";
        result += "\\\"" + (source.getIntendedMedication() != null ? NEW_LINE.matcher(DOUBLE_QUOTES.matcher(source.getIntendedMedication().getMedicationDescription()).replaceAll("")).replaceAll(" ") : "[null]") + "\\\",";
        result += "\\\"" + (source.getIntendedMedication() != null ? NEW_LINE.matcher(DOUBLE_QUOTES.matcher(source.getIntendedMedication().getMedicationDescription()).replaceAll("")).replaceAll(" ") : "[null]") + "\\\",";
        result += "\\\"" + (!dose.isEmpty() ? NEW_LINE.matcher(DOUBLE_QUOTES.matcher(dose).replaceAll("")).replaceAll(" ") : "?") + "\\\",";
        result += "\\\"" + (source.getDrugRoute() != null ? source.getDrugRoute().getName() : "?") + "\\\",";
        result += "\\\"" + (source.getPeriodicity() != null ? source.getPeriodicity().getDescription() : "?") + "\\\",";
        result += "\\\"" + (atc2CodeName.isEmpty() ? "?" : atc2CodeName) + "\\\",";
        result += "\\\"" + " \\\",";
        result += "\\\"" + (validator.isEmpty() ? "?" : validator) + "\\\",";
        result += "\\\"" + (organisation.isEmpty() ? "?" : organisation) + "\\\"";
        result += "\\n\"+";
        return result;
    }


}
