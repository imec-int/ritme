package uz.ehealth.ritme.comparison;

import uz.ehealth.ritme.model.MedicatieSchemaItem;

import java.util.List;

/**
 * Created by bdcuyp0 on 21-10-2015.
 */
public interface CompareService {
    ComparisonResult compare(ComparisonInput comparisonInput);

    String createComparisonUIData(List<MedicatieSchemaItem> left1, List<MedicatieSchemaItem> right1, List<ComparisonPair> comparisonResult, String nihiiOrg, String ssin, final String user);
}
