package uz.ehealth.ritme.vitalink.logic;

import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.KmehrmessageType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.lang3.tuple.Pair;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.kmehr.KmehrMessageInfo;
import uz.ehealth.ritme.kmehr.KmehrTools;

import java.util.*;

/**
 * Created by bdcuyp0 on 29-9-2015.
 */
public class KmehrMessageTypeToKmehrMessageInfo implements F1<Pair<Map<String, String>, KmehrmessageType>, KmehrMessageInfo> {
    @Override
    public KmehrMessageInfo invoke(final Pair<Map<String, String>, KmehrmessageType> mapKmehrmessageTypePair) {

        if (mapKmehrmessageTypePair.getValue().getFolders().size() == 1 && mapKmehrmessageTypePair.getValue().getFolders().get(0).getTransactions() != null) {
            FolderType folder = mapKmehrmessageTypePair.getValue().getFolders().get(0);
            Calendar registrationCalendar = mapKmehrmessageTypePair.getValue().getHeader().getDate();
            Calendar registrationTimeCalendar = mapKmehrmessageTypePair.getValue().getHeader().getTime();
            registrationCalendar.add(Calendar.HOUR_OF_DAY, registrationTimeCalendar.get(Calendar.HOUR_OF_DAY));
            registrationCalendar.add(Calendar.MINUTE, registrationTimeCalendar.get(Calendar.MINUTE));
            registrationCalendar.add(Calendar.SECOND, registrationTimeCalendar.get(Calendar.SECOND));
            registrationCalendar.add(Calendar.MILLISECOND, registrationTimeCalendar.get(Calendar.MILLISECOND));
            Date registrationDate = registrationCalendar.getTime();
            String patientSSIN = KmehrTools.findIDPATIENT(folder.getPatient().getIds(), "INSS");

            final List<HcpartyType> hcparties = mapKmehrmessageTypePair.getValue().getHeader().getSender().getHcparties();
            HcpartyType org = KmehrTools.selectOrgOrElsePerson(hcparties);
            String orgNIHII = null;
            if (org != null) {
                orgNIHII = KmehrTools.findIDHCPARTY(org.getIds(), "ID-HCPARTY");
            }
            TransactionType medicationSchemeElement = null;
            List<TransactionType> suspensions = new ArrayList<TransactionType>();
            for (TransactionType transaction : mapKmehrmessageTypePair.getValue().getFolders().get(0).getTransactions()) {
                //Do not make a separate item for a treatmentsuspension
                if ("treatmentsuspension".equals(KmehrTools.findCDTRANSACTION(transaction.getCds(), "CD-TRANSACTION"))) {
                    suspensions.add(transaction);
                } else {
                    medicationSchemeElement = transaction;

                }
            }

            return new KmehrMessageInfo(mapKmehrmessageTypePair.getKey().get("uri"), mapKmehrmessageTypePair.getKey().get("source"), patientSSIN, orgNIHII, registrationDate, medicationSchemeElement, suspensions);
        } else {
            throw new IllegalArgumentException("KMEHR format invalid");
        }


    }
}
