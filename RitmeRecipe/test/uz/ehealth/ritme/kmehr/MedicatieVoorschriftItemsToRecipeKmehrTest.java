package uz.ehealth.ritme.kmehr;

import org.junit.Test;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.outbound.hospital.HospitalData;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.patient.PatientData;
import uz.ehealth.ritme.recipe.CreatePrescriptionInput;

import java.util.Date;

/**
 * Created by bdcuyp0 on 26-1-2017.
 */
public class MedicatieVoorschriftItemsToRecipeKmehrTest {

    @Test
    public void testMZHIMP19771() {
        String prescription = "{\"items\":[{\"prescriptionDate\":1485126000000,\"prescriberSSIN\":\"55110715393\",\"prescriberNihiiOrg\":\"71071207\",\"prescriptionType\":\"P1\",\"quantity\":1,\"medicatieSchemaItem\":{\"registrationDate\":1485126000000,\"patientSSIN\":\"29082010260\",\"medicSSIN\":\"55110715393\",\"medicNIHII\":\"10479562730\",\"orgNIHII\":\"71071207\",\"intendedMedication\":{\"medicationId\":\"1799121\",\"medicationIdType\":\"CNK\",\"medicationDescription\":\"DAFALGAN FORTE DROOG COMP 50 X 1000 MG\"},\"regimenItems\":[],\"posology\":\"1  tabl 4 keer per dag \",\"instructionForPatient\":\"PO\\n\",\"patientOrigin\":false,\"active\":true,\"validated\":true,\"suspensions\":[]},\"source\":\"Recip-e\"},{\"prescriptionDate\":1485126000000,\"prescriberSSIN\":\"55110715393\",\"prescriberNihiiOrg\":\"71071207\",\"prescriptionType\":\"P1\",\"quantity\":1,\"medicatieSchemaItem\":{\"registrationDate\":1485126000000,\"patientSSIN\":\"29082010260\",\"medicSSIN\":\"55110715393\",\"medicNIHII\":\"10479562730\",\"orgNIHII\":\"71071207\",\"intendedMedication\":{\"medicationId\":\"2601599\",\"medicationIdType\":\"CNK\",\"medicationDescription\":\"PLAVIX COMP 84 X 75 MG\"},\"periodicity\":\"D\",\"regimenItems\":[],\"posology\":\"1  tabl 1 keer per dag \",\"instructionForPatient\":\"PO\\n\",\"patientOrigin\":false,\"active\":true,\"validated\":true,\"suspensions\":[]},\"source\":\"Recip-e\"},{\"prescriptionDate\":1485126000000,\"prescriberSSIN\":\"55110715393\",\"prescriberNihiiOrg\":\"71071207\",\"prescriptionType\":\"P1\",\"quantity\":1,\"medicatieSchemaItem\":{\"registrationDate\":1485126000000,\"patientSSIN\":\"29082010260\",\"medicSSIN\":\"55110715393\",\"medicNIHII\":\"10479562730\",\"orgNIHII\":\"71071207\",\"intendedMedication\":{\"medicationId\":\"1610187\",\"medicationIdType\":\"CNK\",\"medicationDescription\":\"SYMBICORT TURBOHALER 1 X 120 DOSES 4,5 MCG/160MCG\"},\"periodicity\":\"D\",\"regimenItems\":[],\"posology\":\"1  inhaler 1 keer per dag \",\"instructionForPatient\":\"Inhal\\n\",\"patientOrigin\":false,\"active\":true,\"validated\":true,\"suspensions\":[]},\"source\":\"Recip-e\"},{\"prescriptionDate\":1485126000000,\"prescriberSSIN\":\"55110715393\",\"prescriberNihiiOrg\":\"71071207\",\"prescriptionType\":\"P0\",\"quantity\":1,\"medicatieSchemaItem\":{\"registrationDate\":1485126000000,\"patientSSIN\":\"29082010260\",\"medicSSIN\":\"55110715393\",\"medicNIHII\":\"10479562730\",\"orgNIHII\":\"71071207\",\"intendedMedication\":{\"medicationId\":\"1695691\",\"medicationIdType\":\"CNK\",\"medicationDescription\":\"ZOLPIDEM EG COMP FILMCOAT 30 X 10MG\"},\"periodicity\":\"D\",\"regimenItems\":[],\"posology\":\"1  tabl 1 keer per dag \",\"instructionForPatient\":\"PO\\n\",\"patientOrigin\":false,\"active\":true,\"validated\":true,\"suspensions\":[]},\"source\":\"Recip-e\"}]}";
        CreatePrescriptionInput input = JSONTools.unmarshal(prescription, CreatePrescriptionInput.class);


        String result = new MedicatieVoorschriftItemsToRecipeKmehr(new MedicData() {
            @Override
            public String getFirstName() {
                return "TestFirstName";
            }

            @Override
            public String getName() {
                return "TestName";
            }

            @Override
            public String[] getOrgNihii() {
                return new String[]{"12345678"};
            }

            @Override
            public String[] getNihii() {
                return new String[]{"12345678901"};
            }

            @Override
            public String getSsin() {
                return "1234567890";
            }

            @Override
            public String getRole() {
                return "persTEST";
            }
        }, new HospitalData() {
            @Override
            public String getTelefoon() {
                return "TestTelefoon";
            }

            @Override
            public String getFax() {
                return "TestFax";
            }

            @Override
            public String getEmail() {
                return "TestEmail";
            }

            @Override
            public String getWebsite() {
                return "TestWebSite";
            }

            @Override
            public String getStraat() {
                return "TestStraat";
            }

            @Override
            public String getHuisNr() {
                return "TestHuisNr";
            }

            @Override
            public String getBusNr() {
                return "TestBusNr";
            }

            @Override
            public String getGemeente() {
                return "TestGemeente";
            }

            @Override
            public String getPostNummer() {
                return "TestPostNummer";
            }

            @Override
            public String getDistrictOfStaat() {
                return "TestStaat";
            }

            @Override
            public String getLand() {
                return "TestLand";
            }

            @Override
            public String getName() {
                return "TestNaam";
            }

            @Override
            public String getNihii() {
                return "12345678";
            }
        }, new PatientData() {
            @Override
            public Sex getSex() {
                return Sex.UNKNOWN;
            }

            @Override
            public String getSSIN() {
                return "1234567890";
            }

            @Override
            public String getName() {
                return "TestNaam";
            }

            @Override
            public String getFirstName() {
                return "TestFirstName";
            }

            @Override
            public Date getBirthDate() {
                return new Date(0);
            }
        }).invoke(input.getItems());

        System.out.println(result);


    }


}
