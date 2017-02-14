package uz.ehealth.ritme.kmehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.*;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.id.v1.*;
import be.fgov.ehealth.standards.kmehr.schema.v1.*;
import be.fgov.ehealth.standards.kmehr.schema.v1.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.F1;
import uz.ehealth.ritme.core.ObjectTools;
import uz.ehealth.ritme.model.*;
import uz.ehealth.ritme.model.PeriodicityType;
import uz.ehealth.ritme.outbound.hospital.HospitalData;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.patient.PatientData;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by bdcuyp0 on 8-1-2016.
 */
public class MedicatieSchemaItemsToVitalinkKmehr implements F1<List<MedicatieSchemaItem>, String> {

    private final MedicData medicData;
    private final HospitalData hospitalData;
    private final PatientData patientData;


    public MedicatieSchemaItemsToVitalinkKmehr(MedicData medicData, HospitalData hospitalData, PatientData patientData) {
        this.medicData = medicData;
        this.hospitalData = hospitalData;
        this.patientData = patientData;

    }

    public static final Logger LOGGER = LoggerFactory.getLogger(MedicatieSchemaItemsToVitalinkKmehr.class);

    @Override
    public String invoke(final List<MedicatieSchemaItem> source) {
        if (!source.isEmpty()) {


            JAXBContext jc = null;
            try {
                jc = JAXBContext.newInstance("be.fgov.ehealth.standards.kmehr.schema.v1");
                Marshaller marshaller = jc.createMarshaller();
                KmehrmessageType kmehrmessageType = new ObjectFactory().createKmehrmessageType();
                kmehrmessageType.setHeader(createHeader(medicData, hospitalData));
                kmehrmessageType.getFolders().add(createFolderType(source, medicData, hospitalData, patientData));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(new JAXBElement<KmehrmessageType>(new QName("http://www.ehealth.fgov.be/standards/kmehr/schema/v1", "kmehrmessage"), KmehrmessageType.class, kmehrmessageType), baos);
                String result = new String(baos.toByteArray());
                LOGGER.debug("Outgoing XML: {}", result);
                return result;
            } catch (JAXBException e) {
                LOGGER.error(e.getMessage(), e);
            }
            return null;
        } else {
            return null;
        }


    }

    private static FolderType createFolderType(List<MedicatieSchemaItem> source, final MedicData medicData, final HospitalData hospitalData, final PatientData patientData) {
        FolderType folderType = new FolderType();
        folderType.getIds().add(createIDKMEHR(String.valueOf(1)));
        folderType.setPatient(createPersonType(patientData));
        List<MedicatieSchemaItem> normalItems = new ArrayList<MedicatieSchemaItem>();
        List<MedicatieSchemaItem> itemsWithSuspension = new ArrayList<MedicatieSchemaItem>();
        List<MedicatieSchemaItem> unchangedItems = new ArrayList<MedicatieSchemaItem>();
        for (MedicatieSchemaItem item : source) {
            if (item.getSuspensions() != null && item.getSuspensions().length != 0) {
                unchangedItems.add(item);
                itemsWithSuspension.add(item);
            } else {
                normalItems.add(item);
            }
        }
        int counter = 1;
        if (!unchangedItems.isEmpty()) {
            folderType.getTransactions().add(createTransactionType(counter++, unchangedItems, medicData, TransactionCodeType.ELEMENT_UNCHANGED, hospitalData, null));
        }
        if (!normalItems.isEmpty()) {
            folderType.getTransactions().add(createTransactionType(counter++, normalItems, medicData, TransactionCodeType.ELEMENT_FULL, hospitalData, null));
        }
        if (!itemsWithSuspension.isEmpty()) {
            for (MedicatieSchemaItem item : itemsWithSuspension) {
                for (Suspension suspension : item.getSuspensions()) {
                    folderType.getTransactions().add(createTransactionType(counter++, itemsWithSuspension, medicData, TransactionCodeType.TREATMENT_SUSPENSION, hospitalData, suspension));
                }

            }
        }
        return folderType;
    }

    private static TransactionType createTransactionType(final int counter, List<MedicatieSchemaItem> source, final MedicData medicData, final TransactionCodeType transactionCode, final HospitalData hospitalData, final Suspension suspension) {
        /*
        <transaction>
			<id S="ID-KMEHR" SV="1.0">1</id>
			<cd S="CD-TRANSACTION" SV="1.4">medicationschemeelement</cd>
			<date>2012-06-01</date>
			<time>14:35:47</time>
			<author>
				<hcparty>
					<id S="ID-HCPARTY" SV="1.0">10009311123</id>
					<id SV="1.0" S="INSS">81062205558</id>
					<cd S="CD-HCPARTY" SV="1.3">persphysician</cd>
					<firstname>Jelle</firstname>
					<familyname>Gacoms</familyname>
				</hcparty>
			</author>
			<iscomplete>true</iscomplete>
			<isvalidated>true</isvalidated>

			<item>
				<id SV="1.0" S="ID-KMEHR">1</id>
				<cd SV="1.4" S="CD-ITEM">healthcareelement</cd>
				<content>
					<cd SV="1.0" S="CD-ITEM-MS">adaptationflag</cd>
					<cd SV="1.0" S="CD-MS-ADAPTATION">medication</cd>
					<cd SV="1.0" S="CD-MS-ADAPTATION">posology</cd>
				</content>
			</item>
         */
        TransactionType transactionType = new TransactionType();
        transactionType.getIds().add(createIDKMEHR(String.valueOf(counter)));
        final CDTRANSACTION cdtransaction = new CDTRANSACTION();
        cdtransaction.setS(CDTRANSACTIONschemes.CD_TRANSACTION);
        cdtransaction.setSV("1.4");
        cdtransaction.setValue(transactionCode.getTransaction());

        transactionType.getCds().add(cdtransaction);
        transactionType.setDate(createCalendar(new Date()));
        transactionType.setTime(createCalendar(new Date()));
        transactionType.setIscomplete(true);
        boolean validated = true;
        for (MedicatieSchemaItem item : source) {
            validated = validated && item.isValidated();
        }
        transactionType.setIsvalidated(validated);
        transactionType.setAuthor(createAuthor(medicData, hospitalData));

        // start ADAPTATION
        transactionType.getHeadingsAndItemsAndTexts().add(createAdaptationItem(transactionCode));
        // end ADAPTATION

        // start MEDICATION
        ItemType medicatieItem = createMedicationItem(source, suspension);
        transactionType.getHeadingsAndItemsAndTexts().add(medicatieItem);
        // end MEDICATION

        // start ORIGIN
        if (transactionCode != TransactionCodeType.ELEMENT_UNCHANGED) {
            ItemType originItem = createOriginItem(source);
            transactionType.getHeadingsAndItemsAndTexts().add(originItem);
        }
        // end ORIGIN

        // start MEDICATIONUSE
        if (transactionCode != TransactionCodeType.ELEMENT_UNCHANGED) {
            ItemType medicationUse = createMedicationUseItem(source);
            if (medicationUse != null) {
                transactionType.getHeadingsAndItemsAndTexts().add(medicationUse);
            }
        }
        // end MEDICATIONUSE
        ItemType transactionReason = createTransactionReasonItem(source, suspension, transactionCode);
        if (transactionReason != null) {

                transactionType.getHeadingsAndItemsAndTexts().add(transactionReason);
        }


        if (transactionCode != TransactionCodeType.ELEMENT_UNCHANGED) {
            ItemType beginCondition = createBeginCondition(source);
            if (beginCondition != null) {
                transactionType.getHeadingsAndItemsAndTexts().add(beginCondition);
            }
        }
        if (transactionCode != TransactionCodeType.ELEMENT_UNCHANGED) {
            ItemType endCondition = createEndCondition(source);
            if (endCondition != null) {
                transactionType.getHeadingsAndItemsAndTexts().add(endCondition);
            }
        }


        return transactionType;
    }

    private static ItemType createTransactionReasonItem(final List<MedicatieSchemaItem> source, final Suspension suspension, final TransactionCodeType transactionCodeType) {
        Set<String> transactionReasons = new HashSet<String>();
        if (transactionCodeType != TransactionCodeType.TREATMENT_SUSPENSION) {
            for (MedicatieSchemaItem item : source) {
                if (item.getTransactionReason() != null) {
                    transactionReasons.add(item.getTransactionReason());
                }
            }
        } else {
            if (suspension.getTransactionReason() != null) {
                transactionReasons.add(suspension.getTransactionReason());
            }
        }
        if (!transactionReasons.isEmpty()) {
            ItemType itemType = new ItemType();
            itemType.getIds().add(createIDKMEHR(String.valueOf(7)));
            itemType.getCds().add(createCDITEM(CDITEMschemes.CD_ITEM, "transactionreason"));
            final ContentType contentType1 = new ContentType();
            String transactionReason = StringUtils.join(transactionReasons, " ");
            final TextType textType = new TextType();
            textType.setValue(transactionReason);
            textType.setL("nl");
            contentType1.getTexts().add(textType);
            itemType.getContents().add(contentType1);
            return itemType;
        }
        return null;

    }


    private static ItemType createMedicationUseItem(final List<MedicatieSchemaItem> source) {
        /*
        <item>
        <id SV="1.0" S="ID-KMEHR">4</id>
        <cd SV="1.4" S="CD-ITEM">healthcareelement</cd>
        <content>
        <cd SV="1.0" S="CD-ITEM-MS">medicationuse</cd>
        </content>
        <content>
        <text L="nl">Voor maagpijn</text>
        </content>
        </item>
        */
        Set<String> medicationUses = new HashSet<String>();
        for (MedicatieSchemaItem item : source) {
            if (item.getMedicationUse() != null) {
                medicationUses.add(item.getMedicationUse());
            }
        }
        if (!medicationUses.isEmpty()) {
            ItemType itemType = new ItemType();
            itemType.getIds().add(createIDKMEHR(String.valueOf(4)));
            itemType.getCds().add(createCDITEMhealthcareelement());
            final ContentType contentType = new ContentType();
            contentType.getCds().add(createCDCONTENT(CDCONTENTschemes.CD_ITEM_MS, "medicationuse"));
            itemType.getContents().add(contentType);
            final ContentType contentType1 = new ContentType();
            String medicationUse = StringUtils.join(medicationUses, " ");
            final TextType textType = new TextType();
            textType.setValue(medicationUse);
            textType.setL("nl");
            contentType1.getTexts().add(textType);
            itemType.getContents().add(contentType1);
            return itemType;
        }
        return null;
    }

    private static ItemType createBeginCondition(final List<MedicatieSchemaItem> source) {
        /*
        <item>
				<id SV="1.0" S="ID-KMEHR">6</id>
				<cd SV="1.4" S="CD-ITEM">healthcareelement</cd>
				<content>
					<cd SV="1.0" S="CD-ITEM-MS">begincondition</cd>
				</content>
				<content>
					<text L="nl">medicijn innemen bij migraine aanval</text>
				</content>
			</item>
        */
        ItemType itemType = new ItemType();
        itemType.getIds().add(createIDKMEHR(String.valueOf(5)));
        itemType.getCds().add(createCDITEMhealthcareelement());
        final ContentType contentType = new ContentType();
        contentType.getCds().add(createCDCONTENT(CDCONTENTschemes.CD_ITEM_MS, "begincondition"));
        itemType.getContents().add(contentType);
        final ContentType contentType1 = new ContentType();
        Set<String> beginConditions = new HashSet<String>();
        for (MedicatieSchemaItem item : source) {
            final String beginCondition = item.getBeginCondition();
            if (!StringUtils.isEmpty(beginCondition)) {
                beginConditions.add(beginCondition);
            }
        }
        if (beginConditions.isEmpty()) {
            return null;
        }
        for (String beginCondition : beginConditions) {
            final TextType textType = new TextType();
            textType.setValue(beginCondition);
            textType.setL("nl");
            contentType1.getTexts().add(textType);
        }
        itemType.getContents().add(contentType1);
        return itemType;
    }

    private static ItemType createEndCondition(final List<MedicatieSchemaItem> source) {
        /*
<item>
				<id SV="1.0" S="ID-KMEHR">7</id>
				<cd SV="1.4" S="CD-ITEM">healthcareelement</cd>
				<content>
					<cd SV="1.0" S="CD-ITEM-MS">endcondition</cd>
				</content>
				<content>
					<text L="nl">innemen tot doosje leeg is</text>
				</content>
			</item>
        */
        ItemType itemType = new ItemType();
        itemType.getIds().add(createIDKMEHR(String.valueOf(6)));
        itemType.getCds().add(createCDITEMhealthcareelement());
        final ContentType contentType = new ContentType();
        contentType.getCds().add(createCDCONTENT(CDCONTENTschemes.CD_ITEM_MS, "endcondition"));
        itemType.getContents().add(contentType);
        final ContentType contentType1 = new ContentType();
        Set<String> endConditions = new HashSet<String>();
        for (MedicatieSchemaItem item : source) {
            final String endCondition = item.getEndCondition();
            if (!StringUtils.isEmpty(endCondition)) {
                endConditions.add(endCondition);
            }
        }
        if (endConditions.isEmpty()) {
            return null;
        }
        for (String endCondition : endConditions) {
            final TextType textType = new TextType();
            textType.setValue(endCondition);
            textType.setL("nl");
            contentType1.getTexts().add(textType);
        }
        itemType.getContents().add(contentType1);
        return itemType;
    }


    private static CDITEM createCDITEMhealthcareelement() {
        return createCDITEM(CDITEMschemes.CD_ITEM, "healthcareelement");
    }

    private static ItemType createOriginItem(final List<MedicatieSchemaItem> source) {
        /*
        <item>
        <id SV="1.0" S="ID-KMEHR">1</id>
        <cd SV="1.4" S="CD-ITEM">healthcareelement</cd>
        <content>
        <cd SV="1.0" S="CD-ITEM-MS">origin</cd>
        <cd SV="1.0" S="CD-MS-ORIGIN">recorded</cd>
        <!-- values: "recorded" or "regularprocess"-->
        </content>
        </item>
        */
        ItemType itemType = new ItemType();
        itemType.getIds().add(createIDKMEHR(String.valueOf(3)));
        itemType.getCds().add(createCDITEMhealthcareelement());
        final ContentType contentType = new ContentType();
        contentType.getCds().add(createCDCONTENT(CDCONTENTschemes.CD_ITEM_MS, "origin"));
        boolean isPatientOrigin = false;
        for (MedicatieSchemaItem item : source) {
            isPatientOrigin = isPatientOrigin || item.isPatientOrigin();
        }
        contentType.getCds().add(createCDCONTENT(CDCONTENTschemes.CD_MS_ORIGIN, isPatientOrigin ? "recorded" : "regularprocess"));
        itemType.getContents().add(contentType);
        return itemType;
    }


    private static ItemType createMedicationItem(final List<MedicatieSchemaItem> source, final Suspension suspension) {
        ItemType medicatieItem = new ItemType();
        final ItemType.Regimen regimen = new ItemType.Regimen();

        String oriMedId = null;
        Set<String> instructionsForPatient = new HashSet<String>();
        Set<String> instructionsForOverdosing = new HashSet<String>();
        Set<String> instructionsForReimbursement = new HashSet<String>();
        Set<String> posologies = new HashSet<String>();
        MedicationIdType oriMedType = null;
        for (int i = 0; i < source.size(); i++) {
            MedicatieSchemaItem item = source.get(i);
            final String medId = source.get(i).getIntendedMedication().getMedicationId();
            final MedicationIdType medType = source.get(i).getIntendedMedication().getMedicationIdType();

            if (i == 0) {
                oriMedId = medId;
                oriMedType = medType;
                medicatieItem.getIds().add(createIDKMEHR(String.valueOf(i + 2)));
                medicatieItem.getCds().add(createCDITEM(CDITEMschemes.CD_ITEM, "medication"));

                final ContentType medicatieContent = createMedicationContent(item);

                medicatieItem.getContents().add(medicatieContent);
                if (item.getIntendedMedication().getMedicationIdType() == MedicationIdType.MAG) {
                    final TextType omschrijving = new TextType();
                    omschrijving.setL("nl");
                    omschrijving.setValue(item.getIntendedMedication().getMedicationDescription());
                    medicatieItem.getTexts().add(omschrijving);
                }
                if (suspension == null) {
                    if (item.getStartDate() != null) {
                        medicatieItem.setBeginmoment(createMomentType(item.getStartDate()));
                    }
                    if (item.getStopDate() != null) {
                        medicatieItem.setEndmoment(createMomentType(item.getStopDate()));
                    }
                } else {
                    if (suspension.getStartDate() != null) {
                        medicatieItem.setBeginmoment(createMomentType(suspension.getStartDate()));
                    }
                    if (suspension.getStopDate() != null) {
                        medicatieItem.setEndmoment(createMomentType(suspension.getStopDate()));
                    }

                }
                if (suspension != null) {
                    medicatieItem.setLifecycle(createLifecycleType(suspension));
                }
                if (item.getDrugRoute() != null) {
                    medicatieItem.setRoute(createRoute(item.getDrugRoute()));
                }
                if (item.getPeriodicity() != null) {
                    medicatieItem.setFrequency(createFrequencyType(item.getPeriodicity()));
                }
                if (item.getType() != null) {
                    final MedicatieSchemaItemType type = item.getType();
                    final TemporalityType temporalityType = new TemporalityType();
                    final CDTEMPORALITY cdtemporality = new CDTEMPORALITY();
                    cdtemporality.setValue(CDTEMPORALITYvalues.valueOf(type.name()));
                    cdtemporality.setS("CD-TEMPORALITY");
                    cdtemporality.setSV("1.0");
                    temporalityType.setCd(cdtemporality);
                    //S="CD-TEMPORALITY" SV="1.0"
                    medicatieItem.setTemporality(temporalityType);
                }

            }

            if (!StringUtils.isEmpty(item.getInstructionForPatient())) {
                instructionsForPatient.add(item.getInstructionForPatient());
            }
            if (!StringUtils.isEmpty(item.getInstructionForOverdosing())) {
                instructionsForOverdosing.add(item.getInstructionForOverdosing());
            }
            if (!StringUtils.isEmpty(item.getInstructionForReimbursement())) {
                instructionsForReimbursement.add(item.getInstructionForReimbursement());
            }
            if (!StringUtils.isEmpty(item.getPosology())) {
                posologies.add(item.getPosology());
            }


            //check of het nog altijd over hetzelfde item gaat
            if (!ObjectTools.equals(medId, oriMedId) && medType.equals(oriMedType)) {
                throw new RuntimeException("Assumptie: een kmehr gaat maar over 1 medicatie: dit klopt niet in dit geval!");
            }

            for (RegimenItem regimenItem : item.getRegimenItems()) {
                if (regimenItem.getNumber() != null) {
                    regimen.getDaynumbersAndQuantitiesAndDaytimes().add(new JAXBElement<BigInteger>(new QName("http://www.ehealth.fgov.be/standards/kmehr/schema/v1", "daynumber"), BigInteger.class, regimenItem.getNumber()));
                }
                if (regimenItem.getDate() != null) {
                    regimen.getDaynumbersAndQuantitiesAndDaytimes().add(new JAXBElement<Calendar>(new QName("http://www.ehealth.fgov.be/standards/kmehr/schema/v1", "date"), Calendar.class, createCalendar(regimenItem.getDate())));
                }
                if (regimenItem.getWeekDay() != null) {
                    WeekdayType weekdayType = new WeekdayType();
                    final CDWEEKDAY cdweekday = new CDWEEKDAY();
                    cdweekday.setS("CD-WEEKDAY");
                    cdweekday.setSV("1.0");
                    cdweekday.setValue(CDWEEKDAYvalues.valueOf(regimenItem.getWeekDay().name()));
                    weekdayType.setCd(cdweekday);
                    //SV="1.0" S="CD-WEEKDAY"

                    regimen.getDaynumbersAndQuantitiesAndDaytimes().add(new JAXBElement<WeekdayType>(new QName("http://www.ehealth.fgov.be/standards/kmehr/schema/v1", "weekday"), WeekdayType.class, weekdayType));
                }
                if (regimenItem.getTime() != null) {
                    ItemType.Regimen.Daytime daytime = new ItemType.Regimen.Daytime();
                    daytime.setTime(createTimeCalendar(regimenItem.getTime()));
                    regimen.getDaynumbersAndQuantitiesAndDaytimes().add(new JAXBElement<ItemType.Regimen.Daytime>(new QName("http://www.ehealth.fgov.be/standards/kmehr/schema/v1", "daytime"), ItemType.Regimen.Daytime.class, daytime));
                }
                if (regimenItem.getDayPeriod() != null) {
                    //<cd SV="1.1" S="CD-DAYPERIOD">thehourofsleep</cd>
                    ItemType.Regimen.Daytime daytime = new ItemType.Regimen.Daytime();
                    final DayperiodType dayperiodType = new DayperiodType();
                    final CDDAYPERIOD cddayperiod = new CDDAYPERIOD();
                    cddayperiod.setValue(CDDAYPERIODvalues.valueOf(regimenItem.getDayPeriod().name()));
                    cddayperiod.setS("CD-DAYPERIOD");
                    cddayperiod.setSV("1.1");
                    dayperiodType.setCd(cddayperiod);
                    daytime.setDayperiod(dayperiodType);
                    regimen.getDaynumbersAndQuantitiesAndDaytimes().add(new JAXBElement<ItemType.Regimen.Daytime>(new QName("http://www.ehealth.fgov.be/standards/kmehr/schema/v1", "daytime"), ItemType.Regimen.Daytime.class, daytime));
                }
                if (regimenItem.getQuantity() != null) {
                    QuantityType quantityType = new QuantityType();
                    quantityType.setDecimal(regimenItem.getQuantity());
                    if (regimenItem.getAdministrationUnit() != null) {
                        final UnitType unitType = new UnitType();
                        final CDUNIT cdunit = new CDUNIT();
                        cdunit.setS(CDUNITschemes.CD_ADMINISTRATIONUNIT);
                        cdunit.setSV("1.0");
                        cdunit.setValue(regimenItem.getAdministrationUnit().getCd());
                        unitType.setCd(cdunit);
                        quantityType.setUnit(unitType);
                    }
                    regimen.getDaynumbersAndQuantitiesAndDaytimes().add(new JAXBElement<QuantityType>(new QName("http://www.ehealth.fgov.be/standards/kmehr/schema/v1", "quantity"), QuantityType.class, quantityType));
                }

            }
        }


        if (!instructionsForPatient.isEmpty()) {
            final TextType instructionforpatient = new TextType();
            instructionforpatient.setValue(StringUtils.join(instructionsForPatient.toArray(), " "));
            instructionforpatient.setL("nl");
            medicatieItem.setInstructionforpatient(instructionforpatient);
        }
        if (!instructionsForOverdosing.isEmpty()) {
            final TextType instructionforoverdosing = new TextType();
            instructionforoverdosing.setValue(StringUtils.join(instructionsForOverdosing.toArray(), " "));
            instructionforoverdosing.setL("nl");
            medicatieItem.setInstructionforoverdosing(instructionforoverdosing);
        }
        if (!instructionsForReimbursement.isEmpty()) {
            final TextType instructionforreimbursement = new TextType();
            instructionforreimbursement.setValue(StringUtils.join(instructionsForReimbursement.toArray(), " "));
            instructionforreimbursement.setL("nl");
            medicatieItem.setInstructionforreimbursement(instructionforreimbursement);
        }
        if (!posologies.isEmpty()) {
            final ItemType.Posology posology = new ItemType.Posology();
            final TextType textType = new TextType();
            textType.setValue(StringUtils.join(posologies.toArray(), " "));
            textType.setL("nl");
            posology.setText(textType);
            medicatieItem.setPosology(posology);
        }
        if (!regimen.getDaynumbersAndQuantitiesAndDaytimes().isEmpty()) {
            medicatieItem.setRegimen(regimen);
        }
        return medicatieItem;
    }

    private static LifecycleType createLifecycleType(final Suspension suspension) {
        LifecycleType lifecycleType = new LifecycleType();
        final CDLIFECYCLE value = new CDLIFECYCLE();
        value.setS("CD-LIFECYCLE");
        value.setSV("1.3");
        value.setValue(suspension.getStopDate() != null ? CDLIFECYCLEvalues.SUSPENDED : CDLIFECYCLEvalues.STOPPED);
        lifecycleType.setCd(value);
        return lifecycleType;
    }

    private static ContentType createMedicationContent(final MedicatieSchemaItem item) {
        final ContentType medicatieContent = new ContentType();
        switch (item.getIntendedMedication().getMedicationIdType()) {
            case MAG:
                medicatieContent.setCompoundprescription(createCompoundPrescription(item.getIntendedMedication()));
                break;
            case EAN:
                break;
            case CNK:
                medicatieContent.setMedicinalproduct(createMedicinalProduct(item.getIntendedMedication(), item.getDeliveredMedication()));
                break;
            case INN:
                medicatieContent.setSubstanceproduct(createSubstanceproduct(item.getIntendedMedication(), item.getDeliveredMedication()));
                break;
            case AMP:
                break;
            case VMPP:
                break;
            case ATM:
                break;
            case VTM:
                break;
            case ATC:
                break;
            default:
                break;

        }

        return medicatieContent;
    }

    private static CompoundprescriptionType createCompoundPrescription(final Medication intendedMedication) {
        CompoundprescriptionType compoundprescriptionType = new CompoundprescriptionType();
        compoundprescriptionType.setL("nl");
        compoundprescriptionType.getContent().add(intendedMedication.getMagistralText());
        return compoundprescriptionType;
    }

    private static ItemType createAdaptationItem(final TransactionCodeType transactionCode) {
        ItemType itemType = new ItemType();
        itemType.getIds().add(createIDKMEHR(String.valueOf(1)));
        final CDITEM cditem = createCDITEMhealthcareelement();
        itemType.getCds().add(cditem);
        final ContentType contentType = new ContentType();
        contentType.getCds().add(createCDCONTENT(CDCONTENTschemes.CD_ITEM_MS, "adaptationflag"));
        for (String flag : transactionCode.getAdaptationFlags()) {
            contentType.getCds().add(createCDCONTENT(CDCONTENTschemes.CD_MS_ADAPTATION, flag));
        }
        itemType.getContents().add(contentType);
        return itemType;
    }

    private static FrequencyType createFrequencyType(final PeriodicityType periodicity) {
        FrequencyType frequencyType = new FrequencyType();
        final be.fgov.ehealth.standards.kmehr.schema.v1.PeriodicityType value = new be.fgov.ehealth.standards.kmehr.schema.v1.PeriodicityType();
        //<cd S="CD-PERIODICITY" SV="1.0">D</cd>
        final CDPERIODICITY value1 = new CDPERIODICITY();
        value1.setValue(periodicity.getCd());
        value1.setS("CD-PERIODICITY");
        value1.setSV("1.0");
        value.setCd(value1);
        frequencyType.setPeriodicity(value);
        return frequencyType;
    }

    private static RouteType createRoute(final DrugRouteType drugRoute) {

        final RouteType routeType = new RouteType();
        CDDRUGROUTE cddrugroute = new CDDRUGROUTE();
        cddrugroute.setValue(drugRoute.getCd());
        cddrugroute.setS("CD-DRUG-ROUTE");
        cddrugroute.setSV("2.0");
        routeType.setCd(cddrugroute);
        return routeType;
    }

    private static MomentType createMomentType(final Date startDate) {
        MomentType momentType = new MomentType();
        momentType.setDate(createCalendar(startDate));
        return momentType;
    }

    private static ContentType.Medicinalproduct createMedicinalProduct(final Medication intendedMedication, final Medication deliveredMedication) {
        ContentType.Medicinalproduct medicinalproduct = new ContentType.Medicinalproduct();
        medicinalproduct.setIntendedcd(createCDDRUGCNK(intendedMedication));
        medicinalproduct.setIntendedname(intendedMedication.getMedicationDescription());
        if (deliveredMedication != null) {
            medicinalproduct.setDeliveredcd(createCDDRUGCNK(deliveredMedication));
            medicinalproduct.setDeliveredname(deliveredMedication.getMedicationDescription());
        }
        return medicinalproduct;
    }

    private static ContentType.Substanceproduct createSubstanceproduct(final Medication intendedMedication, final Medication deliveredMedication) {
        ContentType.Substanceproduct substanceproduct = new ContentType.Substanceproduct();
        substanceproduct.setIntendedcd(createCDINNCLUSTER(intendedMedication));
        substanceproduct.setIntendedname(intendedMedication.getMedicationDescription());
        if (deliveredMedication != null) {
            substanceproduct.setDeliveredcd(createCDDRUGCNK(deliveredMedication));
            substanceproduct.setDeliveredname(deliveredMedication.getMedicationDescription());
        }
        return substanceproduct;
    }


    private static CDDRUGCNK createCDDRUGCNK(final Medication medication) {
        CDDRUGCNK cddrugcnk = new CDDRUGCNK();
        cddrugcnk.setValue(medication.getMedicationId());
        cddrugcnk.setS("CD-DRUG-CNK");
        cddrugcnk.setSV("2010-07");
        return cddrugcnk;
    }

    private static CDINNCLUSTER createCDINNCLUSTER(final Medication medication) {
        CDINNCLUSTER cdinncluster = new CDINNCLUSTER();
        cdinncluster.setValue(medication.getMedicationId());
        cdinncluster.setS("CD-INNCLUSTER");
        cdinncluster.setSV("2010-07");
        return cdinncluster;
    }


    private static CDITEM createCDITEM(final CDITEMschemes cdItem, final String type) {
        final CDITEM cditem = new CDITEM();
        cditem.setS(cdItem);
        cditem.setSV("1.4");
        cditem.setValue(type);
        return cditem;
    }


    private static CDCONTENT createCDCONTENT(final CDCONTENTschemes cdItemMs, final String adaptationflag) {
        final CDCONTENT cdcontent = new CDCONTENT();
        cdcontent.setS(cdItemMs);
        cdcontent.setSV("1.0");
        cdcontent.setValue(adaptationflag);
        return cdcontent;
    }


    private static IDKMEHR createIDKMEHR(final String value) {
        final IDKMEHR idkmehr = new IDKMEHR();
        idkmehr.setS(IDKMEHRschemes.ID_KMEHR);
        idkmehr.setSV("1.0");
        idkmehr.setValue(value);
        return idkmehr;
    }

    private static AuthorType createAuthor(final MedicData medicData, final HospitalData hospitalData) {
        AuthorType author = new AuthorType();
        author.getHcparties().add(createHcpartyMedic(medicData, hospitalData));
        return author;
    }

    private static PersonType createPersonType(final PatientData patientData) {
        PersonType personType = new PersonType();
        IDPATIENT idpatient = new IDPATIENT();
        idpatient.setS(IDPATIENTschemes.INSS);
        idpatient.setSV("1.0");
        idpatient.setValue(patientData.getSSIN());
        personType.getIds().add(idpatient);
        final SexType sexType = new SexType();
        final CDSEX cdsex = new CDSEX();
        CDSEXvalues cdsexvalue = null;
        switch (patientData.getSex()) {

            case MALE:
                cdsexvalue = CDSEXvalues.MALE;
                break;
            case FEMALE:
                cdsexvalue = CDSEXvalues.FEMALE;
                break;
            case OTHER:
                cdsexvalue = CDSEXvalues.CHANGED;
                break;
            case UNKNOWN:
                cdsexvalue = CDSEXvalues.UNKNOWN;
                break;
        }
        cdsex.setValue(cdsexvalue);
        cdsex.setS("CD-SEX");
        cdsex.setSV("1.0");
        sexType.setCd(cdsex);
        personType.setSex(sexType);
        personType.setFamilyname(patientData.getName());
        personType.getFirstnames().add(patientData.getFirstName());
        DateType birthDate = new DateType();
        birthDate.setDate(createCalendar(patientData.getBirthDate()));
        personType.setBirthdate(birthDate);
        return personType;
    }

    private static HeaderType createHeader(final MedicData medicData, final HospitalData hospitalData) {
        HeaderType header = new HeaderType();
        header.setStandard(createStandard());
        header.getIds().add(createIDKMEHR(UUID.randomUUID().toString()));
        header.setDate(createCalendar(new Date()));
        header.setTime(createCalendar(new Date()));
        header.setSender(createSender(medicData, hospitalData));
        header.getRecipients().add(createRecipient());
        return header;
    }


    private static Calendar createCalendar(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    private static Calendar createTimeCalendar(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }

        //temporary fix
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);
        return calendar;
    }

    private static RecipientType createRecipient() {
        /*
        <recipient>
        <hcparty>
        <cd S="CD-HCPARTY" SV="1.3">application</cd>
        <name>VITALINK</name>
        </hcparty>
        </recipient>
        */
        RecipientType recipientType = new RecipientType();
        recipientType.getHcparties().add(createHcpartyVitalink());
        return recipientType;
    }

    private static HcpartyType createHcpartyVitalink() {
        HcpartyType hcpartyType = new HcpartyType();
        CDHCPARTY vitalink = new CDHCPARTY();
        vitalink.setS(CDHCPARTYschemes.CD_APPLICATION);
        vitalink.setSV("1.3");
        vitalink.setValue("application");
        hcpartyType.setName("VITALINK");
        hcpartyType.getCds().add(vitalink);
        return hcpartyType;
    }

    private static SenderType createSender(final MedicData medicData, final HospitalData hospitalData) {
        /*
        <sender>

		</sender>
         */
        SenderType senderType = new SenderType();
        senderType.getHcparties().add(createHcpartyMedic(medicData, hospitalData));
        senderType.getHcparties().add(createHcpartyOrg(hospitalData));
        return senderType;
    }

    private static HcpartyType createHcpartyOrg(final HospitalData hospitalData) {
        HcpartyType hcpartyType = new HcpartyType();
        hcpartyType.getIds().add(createNihii(hospitalData.getNihii()));
        CDHCPARTY org = new CDHCPARTY();
        org.setS(CDHCPARTYschemes.CD_HCPARTY);
        org.setSV("1.3");
        org.setValue("orghospital");
        hcpartyType.getCds().add(org);
        hcpartyType.setName(hospitalData.getName());
        return hcpartyType;
    }

    private static IDHCPARTY createNihii(final String nihii) {
        IDHCPARTY id = new IDHCPARTY();
        /*should be NIHII */
        id.setS(IDHCPARTYschemes.ID_HCPARTY);
        id.setSV("1.0");
        id.setValue(nihii);
        return id;
    }

    private static HcpartyType createHcpartyMedic(final MedicData medicData, final HospitalData hospitalData) {
        /*
        <hcparty>
        <id S="ID-HCPARTY" SV="1.0">10009311123</id>
        <id SV="1.0" S="INSS">81062205558</id>
        <cd S="CD-HCPARTY" SV="1.3">persphysician</cd>
        <firstname>Jelle</firstname>
        <familyname>Gacoms</familyname>
        </hcparty>
        */
        HcpartyType hcpartyType = new HcpartyType();
        hcpartyType.getIds().add(createRijksregister(medicData));
        hcpartyType.getIds().add(createRizivNr(medicData));
        hcpartyType.getCds().add(createMedicalProfession(medicData));
        hcpartyType.getAddresses().add(createAddressType(medicData, hospitalData));
        hcpartyType.getTelecoms().add(createPhoneNumber(medicData, hospitalData));
        hcpartyType.getTelecoms().add(createEmail(medicData, hospitalData));
        hcpartyType.setFamilyname(medicData.getName());
        hcpartyType.setFirstname(medicData.getFirstName());
        return hcpartyType;
    }

    private static TelecomType createPhoneNumber(final MedicData medicData, final HospitalData hospitalData) {
        if (hospitalData.getTelefoon() != null) {
            TelecomType telecomType = new TelecomType();
            final CDTELECOM cd = new CDTELECOM();
            cd.setS(CDTELECOMschemes.CD_TELECOM);
            cd.setSV("1.0");
            cd.setValue(CDTELECOMvalues.PHONE.value());
            telecomType.getCds().add(cd);
            telecomType.setTelecomnumber(hospitalData.getTelefoon());
            return telecomType;
        } else {
            return null;
        }
    }

    private static TelecomType createEmail(final MedicData medicData, final HospitalData hospitalData) {
        if (hospitalData.getEmail() != null) {
            TelecomType telecomType = new TelecomType();
            final CDTELECOM cd = new CDTELECOM();
            cd.setS(CDTELECOMschemes.CD_TELECOM);
            cd.setSV("1.0");
            cd.setValue(CDTELECOMvalues.EMAIL.value());
            telecomType.getCds().add(cd);
            telecomType.setTelecomnumber(hospitalData.getEmail());
            return telecomType;
        } else {
            return null;
        }
    }

    private static AddressType createAddressType(final MedicData medicData, final HospitalData hospitalData) {
        if (hospitalData.getPostNummer() != null) {
            AddressType addressType = new AddressType();
            final CDADDRESS cd = new CDADDRESS();
            cd.setS(CDADDRESSschemes.CD_ADDRESS);
            cd.setSV("1.0");
            cd.setValue(CDADDRESSvalues.WORK.value());
            addressType.getCds().add(cd);
            final CountryType countryType = new CountryType();
            final CDCOUNTRY cdcountry = new CDCOUNTRY();
            cdcountry.setS(CDCOUNTRYschemes.CD_COUNTRY);
            cdcountry.setSV("1.0");
            //to set translation from
            cdcountry.setValue("be");
            countryType.setCd(cdcountry);
            addressType.setCountry(countryType);
            addressType.setCity(hospitalData.getGemeente());
            addressType.setHousenumber(hospitalData.getHuisNr());
            addressType.setStreet(hospitalData.getStraat());
            addressType.setZip(hospitalData.getPostNummer());
            addressType.setPostboxnumber(hospitalData.getBusNr());
            return addressType;
        } else {
            return null;
        }
    }

    private static IDHCPARTY createRizivNr(final MedicData medicData) {
        if (medicData.getNihii().length > 0) {
            //<id S="ID-HCPARTY" SV="1.0">14675011004</id>
            IDHCPARTY id = new IDHCPARTY();
            id.setS(IDHCPARTYschemes.ID_HCPARTY);
            id.setSV("1.0");
            id.setValue(medicData.getNihii()[0]);
            return id;
        } else {
            return null;
        }
    }

    private static CDHCPARTY createMedicalProfession(MedicData medicData) {
        CDHCPARTY profession = new CDHCPARTY();
        profession.setS(CDHCPARTYschemes.CD_HCPARTY);
        profession.setSV("1.3");
        profession.setValue(medicData.getRole());
        //toevoegen aan medicData indien nodig.
        return profession;
    }

    private static IDHCPARTY createRijksregister(final MedicData medicData) {
        /*
        <id SV="1.0" S="INSS">81062205558</id>
        */
        IDHCPARTY id = new IDHCPARTY();
        id.setS(IDHCPARTYschemes.INSS);
        id.setSV("1.0");
        id.setValue(medicData.getSsin());
        return id;
    }


    private static StandardType createStandard() {
        StandardType standardType = new StandardType();
        CDSTANDARD cdstandard = new CDSTANDARD();
        cdstandard.setValue("20120401");
        cdstandard.setSV("1.4");
        cdstandard.setS("CD-STANDARD");
        standardType.setCd(cdstandard);
        return standardType;
    }
}
