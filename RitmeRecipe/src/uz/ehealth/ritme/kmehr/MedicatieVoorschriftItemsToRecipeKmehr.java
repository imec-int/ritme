package uz.ehealth.ritme.kmehr;

import be.fgov.ehealth.standards.kmehr.cd.v1.*;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.id.v1.*;
import be.fgov.ehealth.standards.kmehr.schema.v1.*;
import be.fgov.ehealth.standards.kmehr.schema.v1.ObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by bdcuyp0 on 8-1-2016.
 */
public class MedicatieVoorschriftItemsToRecipeKmehr implements F1<List<MedicatieVoorschriftItem>, String> {

    private final MedicData medicData;
    private final HospitalData hospitalData;
    private final PatientData patientData;


    public MedicatieVoorschriftItemsToRecipeKmehr(MedicData medicData, HospitalData hospitalData, final PatientData patientData) {
        this.medicData = medicData;
        this.hospitalData = hospitalData;
        this.patientData = patientData;

    }

    public static final Logger LOGGER = LoggerFactory.getLogger(MedicatieVoorschriftItemsToRecipeKmehr.class);

    @Override
    public String invoke(final List<MedicatieVoorschriftItem> source) {
        if (!source.isEmpty()) {


            JAXBContext jc = null;
            try {
                jc = JAXBContext.newInstance("be.fgov.ehealth.standards.kmehr.schema.v1");
                Marshaller marshaller = jc.createMarshaller();
                Kmehrmessage kmehrmessageType = new ObjectFactory().createKmehrmessage();
                kmehrmessageType.setHeader(createHeader(medicData, hospitalData));
                kmehrmessageType.getFolders().add(createFolderType(patientData, source, medicData, hospitalData));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(new JAXBElement<Kmehrmessage>(new QName("http://www.ehealth.fgov.be/standards/kmehr/schema/v1", "kmehrmessage"), Kmehrmessage.class, kmehrmessageType), baos);
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

    private static FolderType createFolderType(final PatientData patientData, List<MedicatieVoorschriftItem> source, final MedicData medicData, final HospitalData hospitalData) {
        FolderType folderType = new FolderType();
        folderType.getIds().add(createIDKMEHR(String.valueOf(1)));
        folderType.setPatient(createPersonType(patientData));
        folderType.getTransactions().add(createTransactionType(1, source, medicData, hospitalData));
        return folderType;
    }

    private static TransactionType createTransactionType(final int counter, List<MedicatieVoorschriftItem> source, final MedicData medicData, final HospitalData hospitalData) {
        TransactionType transactionType = new TransactionType();
        transactionType.getIds().add(createIDKMEHR(String.valueOf(counter)));
        final CDTRANSACTION cdtransaction = new CDTRANSACTION();
        cdtransaction.setS(CDTRANSACTIONschemes.CD_TRANSACTION);
        cdtransaction.setSV("1.1");
        cdtransaction.setValue("pharmaceuticalprescription");

        transactionType.getCds().add(cdtransaction);
        Date transactionDate = source.get(0).getExecutionDate()==null?source.get(0).getPrescriptionDate():source.get(0).getExecutionDate();
        transactionType.setDate(createCalendar(transactionDate));
        transactionType.setTime(createCalendar(transactionDate));
        transactionType.setIscomplete(true);
        transactionType.setIsvalidated(true);
        DateTime expirationDate = null;
        for(MedicatieVoorschriftItem item :  source)
        {
            if((item.getExpirationDate()!=null && expirationDate == null)||
                
            (item.getExpirationDate()!=null && item.getExpirationDate().compareTo(expirationDate.toGregorianCalendar().getTime())<0)){
                expirationDate = new DateTime(item.getExpirationDate());
            }
            
        }
        transactionType.setExpirationdate(expirationDate);
        transactionType.setAuthor(createAuthor(medicData, hospitalData));
        
        transactionType.getHeading().add(createHeading(source));




        return transactionType;
    }

    private static HeadingType createHeading(final List<MedicatieVoorschriftItem> source) {
        HeadingType heading = new HeadingType();
        /*
        <id S="ID-KMEHR" SV="1.0">1</id>
        <cd S="CD-HEADING" SV="1.1">prescription</cd>
        */
        heading.getIds().add(createIDKMEHR(String.valueOf(1)));
        final CDHEADING e = new CDHEADING();
        e.setS(CDHEADINGschemes.CD_HEADING);
        e.setSV("1.1");
        e.setValue("prescription");
        heading.getCds().add(e);
        for (int count = 0; count < source.size(); count++)
        {
            final MedicatieVoorschriftItem item = source.get(count);
            heading.getItem().add(createMedicationItem(Collections.singletonList(item.getMedicatieSchemaItem()), BigDecimal.valueOf(item.getQuantity()), count + 1));

        }
        
        return heading;
        
    }

    private static ItemType createMedicationItem(final List<MedicatieSchemaItem> source, final BigDecimal quantity, final int count) {
        ItemType itemType = new ItemType();
        final Regimen regimen = new Regimen();


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
                itemType.getIds().add(createIDKMEHR(String.valueOf(count)));
                itemType.getCds().add(createCDITEM(CDITEMschemes.CD_ITEM, "medication"));
                final QuantityType quantityType = new QuantityType();
                quantityType.setDecimal(quantity);
                itemType.setQuantity(quantityType);

                final ContentType medicatieContent = createMedicationContent(item);

                itemType.getContents().add(medicatieContent);
                if (item.getIntendedMedication().getMedicationIdType() == MedicationIdType.MAG) {
                    final TextType omschrijving = new TextType();
                    omschrijving.setL("nl");
                    omschrijving.setValue(item.getIntendedMedication().getMedicationDescription());
                    itemType.getTexts().add(omschrijving);
                }
                if (item.getStartDate() != null) {
                    itemType.setBeginmoment(createMomentType(item.getStartDate()));
                }
                if (item.getStopDate() != null) {
                    itemType.setEndmoment(createMomentType(item.getStopDate()));
                }
                if (item.getDrugRoute() != null) {
                    itemType.setRoute(createRoute(item.getDrugRoute()));
                }
                if (item.getPeriodicity() != null) {
                    itemType.setFrequency(createFrequencyType(item.getPeriodicity()));
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
                    itemType.setTemporality(temporalityType);
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
                    regimen.getDaynumber().add(regimenItem.getNumber());
                }
                if (regimenItem.getDate() != null) {
                    regimen.getDate().add(createCalendar(regimenItem.getDate()).toString("dd-MM-YYYY"));
                }
                if (regimenItem.getWeekDay() != null) {
                    Weekday weekdayType = new Weekday();
                    final CDWEEKDAY cdweekday = new CDWEEKDAY();
                    cdweekday.setS("CD-WEEKDAY");
                    cdweekday.setSV("1.0");
                    cdweekday.setValue(CDWEEKDAYvalues.valueOf(regimenItem.getWeekDay().getCd()));
                    weekdayType.setCd(cdweekday);
                    regimen.getWeekday().add(weekdayType);
                }
                if (regimenItem.getTime() != null) {
                    Daytime daytime = new Daytime();
                    daytime.setTime(createTimeCalendar(regimenItem.getTime()));
                    regimen.getDaytime().add(daytime);
                }
                if (regimenItem.getDayPeriod() != null) {
                    //<cd SV="1.1" S="CD-DAYPERIOD">thehourofsleep</cd>
                    Daytime daytime = new Daytime();
                    final DayperiodType dayperiodType = new DayperiodType();
                    final CDDAYPERIOD cddayperiod = new CDDAYPERIOD();
                    cddayperiod.setValue(CDDAYPERIODvalues.valueOf(regimenItem.getDayPeriod().name()));
                    cddayperiod.setS("CD-DAYPERIOD");
                    cddayperiod.setSV("1.1");
                    dayperiodType.setCd(cddayperiod);
                    daytime.setDayperiod(dayperiodType);
                    regimen.getDaytime().add(daytime);
                }
                if (regimenItem.getQuantity() != null) {
                    AdministrationquantityType quantityType = new AdministrationquantityType();
                    quantityType.setDecimal(regimenItem.getQuantity());
                    if (regimenItem.getAdministrationUnit() != null) {
                        final AdministrationunitType unitType = new AdministrationunitType();
                        final CDADMINISTRATIONUNIT cdunit = new CDADMINISTRATIONUNIT();
                        cdunit.setS("CD-ADMINISTRATIONUNIT");
                        cdunit.setSV("1.0");

                        cdunit.setValue(regimenItem.getAdministrationUnit().getCd());
                        unitType.setCd(cdunit);
                        quantityType.setUnit(unitType);
                    }
                    regimen.getQuantity().add(quantityType);
                }

            }
        }


        if (!instructionsForPatient.isEmpty()) {
            final TextType instructionforpatient = new TextType();
            instructionforpatient.setValue(StringUtils.join(instructionsForPatient.toArray(), " "));
            instructionforpatient.setL("nl");
            itemType.setInstructionforpatient(instructionforpatient);
        }
        if (!instructionsForOverdosing.isEmpty()) {
            final TextType instructionforoverdosing = new TextType();
            instructionforoverdosing.setValue(StringUtils.join(instructionsForOverdosing.toArray(), " "));
            instructionforoverdosing.setL("nl");
            itemType.setInstructionforoverdosing(instructionforoverdosing);
        }
        if (!instructionsForReimbursement.isEmpty()) {
            final TextType instructionforreimbursement = new TextType();
            instructionforreimbursement.setValue(StringUtils.join(instructionsForReimbursement.toArray(), " "));
            instructionforreimbursement.setL("nl");
            itemType.setInstructionforreimbursement(instructionforreimbursement);
        }
        if (!posologies.isEmpty()) {
            final Posology posology = new Posology();
            final TextType textType = new TextType();
            textType.setValue(StringUtils.join(posologies.toArray(), " "));
            textType.setL("nl");
            posology.setText(textType);
            itemType.setPosology(posology);
        }
        if(!regimen.getDate().isEmpty()||!regimen.getDaynumber().isEmpty()||!regimen.getDaytime().isEmpty()||!regimen.getQuantity().isEmpty()||!regimen.getWeekday().isEmpty()) {
            itemType.setRegimen(regimen);
        }
        return itemType;
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

    private static MedicinalProductType createMedicinalProduct(final Medication intendedMedication, final Medication deliveredMedication) {
        MedicinalProductType medicinalproduct = new MedicinalProductType();
        medicinalproduct.getIntendedcds().add(createCDDRUGCNK(intendedMedication));
        medicinalproduct.setIntendedname(intendedMedication.getMedicationDescription());
        if (deliveredMedication != null) {
            medicinalproduct.getDeliveredcds().add(createCDDRUGCNK(deliveredMedication));
            medicinalproduct.setDeliveredname(deliveredMedication.getMedicationDescription());
        }
        return medicinalproduct;
    }

    private static Substanceproduct createSubstanceproduct(final Medication intendedMedication, final Medication deliveredMedication) {
        Substanceproduct substanceproduct = new Substanceproduct();
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
        cddrugcnk.setS(CDDRUGCNKschemes.CD_DRUG_CNK);
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
        cditem.setSV("1.1");
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
        idpatient.setS(IDPATIENTschemes.ID_PATIENT);
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
        birthDate.setDate(new DateTime(patientData.getBirthDate()));
        personType.setBirthdate(birthDate);
        return personType;
    }

    private static HeaderType createHeader(final MedicData medicData, HospitalData hospitalData) {
        HeaderType header = new HeaderType();
        header.setStandard(createStandard());
        header.getIds().add(createIDKMEHR(UUID.randomUUID().toString()));
        header.setDate(createCalendar(new Date()));
        header.setTime(createCalendar(new Date()));
        header.setSender(createSender(medicData, hospitalData));
        header.getRecipients().add(createRecipient());
        return header;
    }


    private static DateTime createCalendar(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        DateTime dateTime = new DateTime(date);
        return dateTime;
    }

    private static DateTime createTimeCalendar(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date cannot be null");
        }

        //temporary fix
        DateTime dateTime = new DateTime();
        dateTime.withZone(DateTimeZone.UTC);
        dateTime.withMillis(date.getTime());
        return dateTime;
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
        recipientType.getHcparties().add(createHcpartyRecipe());
        return recipientType;
    }

    private static HcpartyType createHcpartyRecipe() {
        HcpartyType hcpartyType = new HcpartyType();
        IDHCPARTY recipe = new IDHCPARTY();
        recipe.setS(IDHCPARTYschemes.ID_HCPARTY);
        recipe.setSV("1.0");
        recipe.setValue("RECIPE");
        hcpartyType.getIds().add(recipe);
        CDHCPARTY cdhcparty = new CDHCPARTY();
        cdhcparty.setS(CDHCPARTYschemes.CD_HCPARTY);
        cdhcparty.setSV("1.0");
        cdhcparty.setValue("orgpublichealth");
        hcpartyType.getCds().add(cdhcparty);
        hcpartyType.setName("Recip-e");
        return hcpartyType;
    }

    private static SenderType createSender(final MedicData medicData, final HospitalData hospitalData) {
        /*
        <sender>

		</sender>
         */
        SenderType senderType = new SenderType();
        //senderType.getHcparties().add(createHcpartyMedic(medicData, hospitalData.getNihii()));
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
        cdstandard.setValue("20100601");
        cdstandard.setSV("1.1");
        cdstandard.setS("CD-STANDARD");
        standardType.setCd(cdstandard);
        return standardType;
    }
}
