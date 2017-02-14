package uz.ehealth.ritme.vitalink.logic;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.kmehr.KmehrMessageInfo;
import uz.ehealth.ritme.kmehr.KmehrTools;
import uz.ehealth.ritme.model.*;
import uz.ehealth.ritme.model.PeriodicityType;
import uz.ehealth.ritme.outbound.medic.JsonMedicData;
import uz.ehealth.ritme.outbound.medic.MedicData;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by bdcuyp0 on 30-9-2015.
 */
public class KmehrMessageTypeToMedicatieSchemaItems implements F1<Pair<Map<String, String>, KmehrmessageType>, MedicatieSchemaItem> {
    private static final CDToMedicationIdType CD_TO_MEDICATION_ID_TYPE = new CDToMedicationIdType();
    private static final KmehrMessageTypeToKmehrMessageInfo KMEHR_MESSAGE_TYPE_TO_KMEHR_MESSAGE_INFO = new KmehrMessageTypeToKmehrMessageInfo();

    @Override
    public MedicatieSchemaItem invoke(final Pair<Map<String, String>, KmehrmessageType> metadataAndMessage) {
        KmehrMessageInfo messageInfo = KMEHR_MESSAGE_TYPE_TO_KMEHR_MESSAGE_INFO.invoke(metadataAndMessage);
        String uri = messageInfo.getUri();
        String source = messageInfo.getSource();
        Date registrationDate = messageInfo.getRegistrationDate();
        TransactionType transaction = messageInfo.getMedicationSchemeElement();
        MedicData medicData = getMedicData(transaction.getAuthor().getHcparties());



        String patientSSIN = messageInfo.getPatientSSIN();
        boolean validated = transaction.isIsvalidated();
        MedicatieSchemaItemType type = null;
        DrugRouteType drugRoute = null;
        Medication intendedMedication = null;
        Medication deliveredMedication = null;
        String medicationUse = null;
        Date startDate = null;
        Date stopDate = null;
        String beginCondition = null;
        String endCondition = null;
        uz.ehealth.ritme.model.PeriodicityType periodicity = null;
        String posology = null;
        boolean origin = false;
        List<RegimenItem> regimenItems = new ArrayList<RegimenItem>();
        List<Suspension> suspensions = new ArrayList<Suspension>();
        String instructionForPatient = null;
        String instructionForOverdosing = null;
        String instructionForReimbursement = null;
        String transactionReason = null;

        for (TransactionType suspensionTransaction : messageInfo.getSuspensions()) {

            Date startSuspension = null;
            Date stopSuspension = null;
            String transactionReasonSuspension = null;
            for (Object o : suspensionTransaction.getHeadingsAndItemsAndTexts()) {
                if (o instanceof ItemType) {
                    ItemType item = (ItemType) o;
                    if (item.getBeginmoment() != null) {
                        if (startSuspension == null) {
                            startSuspension = item.getBeginmoment().getDate().getTime();
                        } else {
                            throw new RuntimeException("2 startDates found...:" + uri + ":" + item.getBeginmoment().getDate().getTime());
                        }
                    }
                    if (item.getEndmoment() != null) {
                        if (stopSuspension == null) {
                            stopSuspension = item.getEndmoment().getDate().getTime();
                        } else {
                            throw new RuntimeException("2 endDate found...:" + uri + ":" + item.getRoute().getCd().getValue());
                        }
                    }
                    final List<ContentType> contents = item.getContents();
                    for (ContentType content : contents) {
                        if ("transactionreason".equals(KmehrTools.findCDITEM(item.getCds(), "CD-ITEM")) && !content.getTexts().isEmpty()) {
                            transactionReasonSuspension = content.getTexts().get(0).getValue();
                        }
                    }
                }
            }
            suspensions.add(new Suspension(startSuspension, stopSuspension, transactionReasonSuspension));
        }

        for (Object o : transaction.getHeadingsAndItemsAndTexts()) {
            boolean nextMedicationUse = false;
            boolean nextBeginCondition = false;
            boolean nextEndCondition = false;
            boolean nextOrigin = false;

            if (o instanceof ItemType) {
                ItemType item = (ItemType) o;
                if (item.getPosology() != null) {
                    if (posology == null) {
                        posology = item.getPosology().getText().getValue();
                    } else {
                        throw new RuntimeException("2 posologies found...:" + uri + ":" + item.getPosology().getText().getValue());
                    }
                }
                if (item.getTemporality() != null) {
                    if (type == null) {
                        type = MedicatieSchemaItemType.getByType(item.getTemporality().getCd().getValue().value());
                    } else {
                        throw new RuntimeException("2 medication item types found...:" + uri + ":" + item.getTemporality().getCd().getValue().value());
                    }
                }
                if (item.getRoute() != null) {
                    if (drugRoute == null) {
                        drugRoute = DrugRouteType.getByCd(item.getRoute().getCd().getValue());
                    } else {
                        throw new RuntimeException("2 drugroutes found...:" + uri + ":" + item.getRoute().getCd().getValue());
                    }
                }
                if (item.getBeginmoment() != null) {
                    if (startDate == null) {
                        startDate = item.getBeginmoment().getDate().getTime();
                    } else {
                        throw new RuntimeException("2 startDates found...:" + uri + ":" + item.getBeginmoment().getDate().getTime());
                    }
                }
                if (item.getEndmoment() != null) {
                    if (stopDate == null) {
                        stopDate = item.getEndmoment().getDate().getTime();
                    } else {
                        throw new RuntimeException("2 endDate found...:" + uri + ":" + item.getRoute().getCd().getValue());
                    }
                }
                if (item.getInstructionforpatient() != null) {
                    instructionForPatient = item.getInstructionforpatient().getValue();
                }
                if (item.getInstructionforoverdosing() != null) {
                    instructionForOverdosing = item.getInstructionforoverdosing().getValue();
                }
                if (item.getInstructionforreimbursement() != null) {
                    instructionForReimbursement = item.getInstructionforreimbursement().getValue();
                }

                if (item.getContents() != null) {
                    final List<ContentType> contents = item.getContents();
                    for (ContentType content : contents) {
                        if (content.getCds() != null) {
                            if ("medicationuse".equals(KmehrTools.findCDCONTENT(content.getCds(), "CD-ITEM-MS"))) {
                                nextMedicationUse = true;
                            }
                            if ("begincondition".equals(KmehrTools.findCDCONTENT(content.getCds(), "CD-ITEM-MS"))) {
                                nextBeginCondition = true;
                            }
                            if ("endcondition".equals(KmehrTools.findCDCONTENT(content.getCds(), "CD-ITEM-MS"))) {
                                nextEndCondition = true;
                            }
                            if ("origin".equals(KmehrTools.findCDCONTENT(content.getCds(), "CD-ITEM-MS"))) {
                                nextOrigin = true;
                            }
                        }

                        if ("transactionreason".equals(KmehrTools.findCDITEM(item.getCds(), "CD-ITEM")) && !content.getTexts().isEmpty()) {
                            transactionReason = content.getTexts().get(0).getValue();
                        }

                        if (nextMedicationUse && "healthcareelement".equals(KmehrTools.findCDITEM(item.getCds(), "CD-ITEM")) && !content.getTexts().isEmpty()) {
                            medicationUse = content.getTexts().get(0).getValue();
                            nextMedicationUse = false;
                        }

                        if (nextBeginCondition && "healthcareelement".equals(KmehrTools.findCDITEM(item.getCds(), "CD-ITEM")) && !content.getTexts().isEmpty()) {
                            beginCondition = content.getTexts().get(0).getValue();
                            nextBeginCondition = false;
                        }
                        if (nextEndCondition && "healthcareelement".equals(KmehrTools.findCDITEM(item.getCds(), "CD-ITEM")) && !content.getTexts().isEmpty()) {
                            endCondition = content.getTexts().get(0).getValue();
                            nextEndCondition = false;
                        }
                        if (nextOrigin && "healthcareelement".equals(KmehrTools.findCDITEM(item.getCds(), "CD-ITEM"))) {
                            origin = !"regularprocess".equals(KmehrTools.findCDCONTENT(content.getCds(), "CD-MS-ORIGIN"));
                            nextOrigin = false;
                        }


                        if (content.getMedicinalproduct() != null) {
                            ContentType.Medicinalproduct medicinalProduct = content.getMedicinalproduct();

                            intendedMedication = new Medication(medicinalProduct.getIntendedcd().getValue(), CD_TO_MEDICATION_ID_TYPE.invoke(medicinalProduct.getIntendedcd()), medicinalProduct.getIntendedname(), null);

                            if (medicinalProduct.getDeliveredcd() != null) {
                                deliveredMedication = new Medication(medicinalProduct.getDeliveredcd().getValue(), CD_TO_MEDICATION_ID_TYPE.invoke(medicinalProduct.getDeliveredcd()), medicinalProduct.getDeliveredname().toString(), null);//todo why is this an object?
                            }
                        } else if (content.getCompoundprescription() != null) {
                            //todo should probably be refined using the components.
                            CompoundprescriptionType compoundPrescription = content.getCompoundprescription();

                            final List<Serializable> serializables = compoundPrescription.getContent();
                            String text = "";
                            for (Serializable contentItem : serializables) {
                                text += contentItem.toString();
                            }
                            intendedMedication = new Medication(null, MedicationIdType.MAG, text, text);
                            deliveredMedication = null;
                        } else if (content.getSubstanceproduct() != null) {
                            ContentType.Substanceproduct substanceProduct = content.getSubstanceproduct();
                            intendedMedication = new Medication(substanceProduct.getIntendedcd().getValue(), CD_TO_MEDICATION_ID_TYPE.invoke(substanceProduct.getIntendedcd()), ((Node) substanceProduct.getIntendedname()).getFirstChild().getNodeValue(), null);
                            if (substanceProduct.getDeliveredcd() != null) {
                                deliveredMedication = new Medication(substanceProduct.getDeliveredcd().getValue(), CD_TO_MEDICATION_ID_TYPE.invoke(substanceProduct.getDeliveredcd()), ((Node) substanceProduct.getDeliveredname()).getFirstChild().getNodeValue(), null);//todo why is this an object?
                            }
                        } else if ("medication".equals(KmehrTools.findCDITEM(item.getCds(), "CD-ITEM"))) {
                            /*
                            <content>
" +
                "                    <cd SV=\"1.0\" S=\"CD-EAN\">3400936158832</cd>\n" +
                "                </content>\n" +
                "                <text L=\"nl\">Dafalgan Paracetamol 1 g</text>\n"
                             */
                            for (CDCONTENT cdcontent : content.getCds()) {
                                if (cdcontent.getS().equals(CDCONTENTschemes.CD_EAN)) {
                                    intendedMedication = new Medication(cdcontent.getValue(), CD_TO_MEDICATION_ID_TYPE.invoke(cdcontent), item.getTexts().get(0).getValue(), null);
                                }
                            }

                        }

                        if (item.getFrequency() != null && isMedicationContent(content)) {
                            FrequencyType frequency = item.getFrequency();
                            if (frequency.getPeriodicity() != null) {
                                String value = frequency.getPeriodicity().getCd().getValue();
                                periodicity = PeriodicityType.getByCd(value);

                            }
                        }


                        if (item.getRegimen() != null && isMedicationContent(content)) {
                            ItemType.Regimen regimen = item.getRegimen();
                            WeekDayType weekday = null;
                            Date date = null;
                            BigInteger dayNumber = null;
                            DayPeriodType dayPeriodType = null;
                            Date time = null;
                            BigDecimal quantity = null;
                            AdministrationUnitType administrationUnitType = null;

                            for (JAXBElement element : regimen.getDaynumbersAndQuantitiesAndDaytimes()) {
                                if (element.getDeclaredType().equals((Class) Calendar.class)) {
                                    Calendar dateType = (Calendar) element.getValue();
                                    date = dateType.getTime();

                                }

                                if (element.getDeclaredType().equals((Class) WeekdayType.class)) {
                                    WeekdayType weekdayType = (WeekdayType) element.getValue();
                                    weekday = WeekDayType.getByCd(weekdayType.getCd().getValue().value());

                                }
                                if (element.getDeclaredType().equals((Class) DateType.class)) {
                                    DateType dateType = (DateType) element.getValue();
                                    date = dateType.getDate().getTime();

                                }
                                if (element.getDeclaredType().equals((Class) BigInteger.class)) {
                                    dayNumber = (BigInteger) element.getValue();


                                }
                                if (element.getDeclaredType().equals((Class) ItemType.Regimen.Daytime.class)) {
                                    ItemType.Regimen.Daytime daytime = (ItemType.Regimen.Daytime) element.getValue();
                                    DayperiodType dayperiodType = daytime.getDayperiod();
                                    if (dayperiodType != null) {
                                        dayPeriodType = DayPeriodType.getByCd(dayperiodType.getCd().getValue().value());
                                    }
                                    if (daytime.getTime() != null) {
                                        time = daytime.getTime().getTime();
                                    }
                                }
                                if (element.getDeclaredType().equals((Class) AdministrationquantityType.class)) {
                                    AdministrationquantityType quantityType = (AdministrationquantityType) element.getValue();
                                    if (quantityType != null) {
                                        quantity = quantityType.getDecimal();
                                        AdministrationunitType administrationunitType = quantityType.getUnit();
                                        if (administrationunitType != null) {
                                            administrationUnitType = AdministrationUnitType.getByCd(administrationunitType.getCd().getValue());
                                        }
                                    }
                                    /* quantity is last element, so we can safely create the regimenItem here */
                                    RegimenItem regimenItem = new RegimenItem(weekday, date, dayNumber, dayPeriodType, time, quantity, administrationUnitType);
                                    regimenItems.add(regimenItem);
                                    /* reset stuff */
                                    weekday = null;
                                    date = null;
                                    dayNumber = null;
                                    dayPeriodType = null;
                                    time = null;
                                    quantity = null;
                                    administrationUnitType = null;
                                }

                            }
                        }
                    }

                }
            }
        }


        return new MedicatieSchemaItem(
                uri,
                source,
                type,
                registrationDate,
                patientSSIN,
                medicData.getSsin(),
                medicData.getNihii().length == 0 ? null : medicData.getNihii()[0],
                medicData.getOrgNihii().length == 0 ? null : medicData.getOrgNihii()[0],
                intendedMedication,
                deliveredMedication,
                drugRoute,
                medicationUse,
                startDate,
                stopDate,
                beginCondition,
                endCondition,
                periodicity,
                regimenItems.toArray(new RegimenItem[regimenItems.size()]),
                posology,
                instructionForPatient,
                instructionForOverdosing,
                instructionForReimbursement,
                transactionReason,
                origin,
                true,
                validated,
                suspensions.toArray(new Suspension[suspensions.size()]));
    }

    @NotNull
    public MedicData getMedicData(final List<HcpartyType> hcparties) {
        HcpartyType person = KmehrTools.selectPerson(hcparties);
        HcpartyType org = KmehrTools.selectOrg(hcparties);
        return new JsonMedicData(person != null ? person.getFirstname() : null, person != null ? person.getFamilyname() : null, org != null ? new String[]{KmehrTools.findIDHCPARTY(org.getIds(), "ID-HCPARTY")} : new String[0], person != null ? new String[]{KmehrTools.findIDHCPARTY(person.getIds(), "ID-HCPARTY")} : new String[0], person != null ? KmehrTools.findIDHCPARTY(person.getIds(), "INSS") : null, person != null ? KmehrTools.findCDHCPARTY(person.getCds(), "CD-HCPARTY") : null);
    }

    public boolean isMedicationContent(final ContentType content) {
        return !(content.getMedicinalproduct() == null && content.getSubstanceproduct() == null && content.getCompoundprescription() == null) || !StringUtils.isEmpty(KmehrTools.findCDCONTENT(content.getCds(), CDCONTENTschemes.CD_EAN.value()));
    }
}
