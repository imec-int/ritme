package uz.ehealth.ritme.vitalink.logic;

import be.fgov.ehealth.standards.kmehr.schema.v1.KmehrmessageType;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import uz.ehealth.ritme.model.MedicatieSchemaItem;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bdcuyp0 on 18-10-2016.
 */
public class TestKmehrmessageType {
    @Test
    public void testEanProduct() {
        String xml = "<kmehrmessage xmlns=\"http://www.ehealth.fgov.be/standards/kmehr/schema/v1\"\n" +
                "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "              xsi:schemaLocation=\"http://www.ehealth.fgov.be/standards/kmehr/schema/v1 kmehr_elements.xsd\">\n" +
                "    <header>\n" +
                "        <standard>\n" +
                "            <cd S=\"CD-STANDARD\" SV=\"1.4\">20120401</cd>\n" +
                "        </standard>\n" +
                "        <id S=\"ID-KMEHR\" SV=\"1.0\">99934051.20160601145134690</id>\n" +
                "        <id S=\"LOCAL\" SV=\"1.0\" SL=\"ID-MANUEEL\">1234567890abcdef</id>\n" +
                "        <date>2016-01-01</date>\n" +
                "        <time>16:01:01</time>\n" +
                "        <sender>\n" +
                "            <hcparty>\n" +
                "                <id S=\"ID-HCPARTY\" SV=\"1.0\">17892144001</id>\n" +
                "                <cd S=\"CD-HCPARTY\" SV=\"1.2\">persphysician</cd>\n" +
                "                <firstname>He/Se Persphysician Firstname</firstname>\n" +
                "                <familyname>He/Se Persphysician Familyname</familyname>\n" +
                "            </hcparty>\n" +
                "            <hcparty>\n" +
                "                <id S=\"LOCAL\" SV=\"2.4\">8596a68a-e632-4664-b217-6ff75eefd898</id>\n" +
                "                <cd S=\"CD-HCPARTY\" SV=\"1.2\">application</cd>\n" +
                "                <name>He/App Validatie Lab Tools</name>\n" +
                "            </hcparty>\n" +
                "        </sender>\n" +
                "        <recipient>\n" +
                "            <hcparty>\n" +
                "                <cd S=\"CD-HCPARTY\" SV=\"1.2\">application</cd>\n" +
                "                <name>Vitalink</name>\n" +
                "            </hcparty>\n" +
                "        </recipient>\n" +
                "    </header>\n" +
                "    <folder>\n" +
                "        <id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "        <patient>\n" +
                "            <id S=\"ID-PATIENT\" SV=\"1.0\">72071135503</id>\n" +
                "            <firstname>Fo/Pa Firstname</firstname>\n" +
                "            <familyname>Fo/Pa Familyname</familyname>\n" +
                "            <sex>\n" +
                "                <cd S=\"CD-SEX\" SV=\"1.0\">male</cd>\n" +
                "            </sex>\n" +
                "        </patient>\n" +
                "        <transaction>\n" +
                "            <id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "            <cd S=\"CD-TRANSACTION\" SV=\"1.4\">medicationschemeelement</cd>\n" +
                "            <date>2016-06-06</date>\n" +
                "            <time>16:06:06</time>\n" +
                "            <author>\n" +
                "                <hcparty>\n" +
                "                    <id S=\"ID-HCPARTY\" SV=\"1.0\">17892144001</id>\n" +
                "                    <cd S=\"CD-HCPARTY\" SV=\"1.3\">persphysician</cd>\n" +
                "                    <firstname>Tr/Au Persphysician Familyname</firstname>\n" +
                "                    <familyname>Ta/Au Persphysician Familyname</familyname>\n" +
                "                </hcparty>\n" +
                "                <hcparty>\n" +
                "                    <cd S=\"CD-HCPARTY\" SV=\"1.3\">application</cd>\n" +
                "                    <name>Tr/App Validatie Lab Tools</name>\n" +
                "                </hcparty>\n" +
                "            </author>\n" +
                "            <iscomplete>true</iscomplete>\n" +
                "            <isvalidated>true</isvalidated>\n" +
                "            <item>\n" +
                "                <id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "                <cd S=\"CD-ITEM\" SV=\"1.4\">healthcareelement</cd>\n" +
                "                <content>\n" +
                "                    <cd S=\"CD-ITEM-MS\" SV=\"1.0\">adaptationflag</cd>\n" +
                "                    <cd S=\"CD-MS-ADAPTATION\" SV=\"1.0\">medication</cd>\n" +
                "                </content>\n" +
                "            </item>\n" +
                "            <item>\n" +
                "                <id S=\"ID-KMEHR\" SV=\"1.0\">2</id>\n" +
                "                <cd S=\"CD-ITEM\" SV=\"1.4\">medication</cd>\n" +
                "                <content>\n" +
                "                    <cd SV=\"1.0\" S=\"CD-EAN\">3400936158832</cd>\n" +
                "                </content>\n" +
                "                <text L=\"nl\">Dafalgan Paracetamol 1 g</text>\n" +
                "                <beginmoment>\n" +
                "                    <date>2016-01-01</date>\n" +
                "                </beginmoment>\n" +
                "                <endmoment>\n" +
                "                    <date>2020-12-31</date>\n" +
                "                </endmoment>\n" +
                "                <temporality>\n" +
                "                    <cd S=\"CD-TEMPORALITY\" SV=\"1.0\">acute</cd>\n" +
                "                </temporality>\n" +
                "                <frequency>\n" +
                "                    <periodicity>\n" +
                "                        <cd S=\"CD-PERIODICITY\" SV=\"1.0\">D</cd>\n" +
                "                    </periodicity>\n" +
                "                </frequency>\n" +
                "                <regimen>\n" +
                "                    <daytime>\n" +
                "                        <dayperiod>\n" +
                "                            <cd S=\"CD-DAYPERIOD\" SV=\"1.0\">duringbreakfast</cd>\n" +
                "                        </dayperiod>\n" +
                "                    </daytime>\n" +
                "                    <quantity>\n" +
                "                        <decimal>2</decimal>\n" +
                "                    </quantity>\n" +
                "                    <daytime>\n" +
                "                        <dayperiod>\n" +
                "                            <cd S=\"CD-DAYPERIOD\" SV=\"1.0\">duringlunch</cd>\n" +
                "                        </dayperiod>\n" +
                "                    </daytime>\n" +
                "                    <quantity>\n" +
                "                        <decimal>2</decimal>\n" +
                "                    </quantity>\n" +
                "                    <daytime>\n" +
                "                        <dayperiod>\n" +
                "                            <cd S=\"CD-DAYPERIOD\" SV=\"1.0\">duringdinner</cd>\n" +
                "                        </dayperiod>\n" +
                "                    </daytime>\n" +
                "                    <quantity>\n" +
                "                        <decimal>2</decimal>\n" +
                "                    </quantity>\n" +
                "                </regimen>\n" +
                "                <instructionforpatient L=\"nl\">907 - EAN 3400936158832 - Dafalgan Paracetamol 1 g</instructionforpatient>\n" +
                "            </item>\n" +
                "        </transaction>\n" +
                "    </folder>\n" +
                "</kmehrmessage>\n";

        try {
            JAXBContext jc = JAXBContext.newInstance("be.fgov.ehealth.standards.kmehr.schema.v1");
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            JAXBElement<KmehrmessageType> feed = unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), KmehrmessageType.class);

            final Map<String, String> meta = new HashMap<String, String>();
            // Retrieve the URI of the data entry
            meta.put("uri", "/Vitalink/15/subject/28022229535/medication-scheme/92893/2");
            meta.put("source", "Vitalink");


            final MedicatieSchemaItem item = new KmehrMessageTypeToMedicatieSchemaItems().invoke(Pair.of(meta, feed.getValue()));
            assertNotNull(item.getIntendedMedication());
            assertEquals(3, item.getRegimenItems().length);
        } catch (Exception e) {
            fail(e.getCause().getMessage());
        }
    }

    @Test
    public void test2ContentTagsWaarvan1GeenMedicatieBevat() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<kmehrmessage xmlns=\"http://www.ehealth.fgov.be/standards/kmehr/schema/v1\" xmlns:ns2=\"http://www.w3.org/2001/04/xmlenc#\" xmlns:ns3=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
                "\t<header>\n" +
                "\t\t<standard>\n" +
                "\t\t\t<cd S=\"CD-STANDARD\" SV=\"1.4\">20120401</cd>\n" +
                "\t\t</standard>\n" +
                "\t\t<id S=\"ID-KMEHR\" SV=\"1.0\">13299391004.20160601190808534</id>\n" +
                "\t\t<id S=\"LOCAL\" SV=\"1.0\" SL=\"ID-CARECONNECT\">6ac5d2b3-0573-4f7c-a6bd-bf43504dd764</id>\n" +
                "\t\t<date>2016-06-01</date>\n" +
                "\t\t<time>19:08:08</time>\n" +
                "\t\t<sender>\n" +
                "\t\t\t<hcparty>\n" +
                "\t\t\t\t<id S=\"ID-HCPARTY\" SV=\"1.0\">13299391004</id>\n" +
                "\t\t\t\t<cd S=\"CD-HCPARTY\" SV=\"1.2\">persphysician</cd>\n" +
                "\t\t\t\t<firstname>Frank John</firstname>\n" +
                "\t\t\t\t<familyname>Glorieux</familyname>\n" +
                "\t\t\t</hcparty>\n" +
                "\t\t\t<hcparty>\n" +
                "\t\t\t\t<id S=\"LOCAL\" SV=\"2.5\">8596a68a-e632-4664-b217-6ff75eefd898</id>\n" +
                "\t\t\t\t<cd S=\"CD-HCPARTY\" SV=\"1.2\">application</cd>\n" +
                "\t\t\t\t<name>CareConnect</name>\n" +
                "\t\t\t</hcparty>\n" +
                "\t\t</sender>\n" +
                "\t\t<recipient>\n" +
                "\t\t\t<hcparty>\n" +
                "\t\t\t\t<cd S=\"CD-HCPARTY\" SV=\"1.2\">application</cd>\n" +
                "\t\t\t\t<name>Vitalink</name>\n" +
                "\t\t\t</hcparty>\n" +
                "\t\t</recipient>\n" +
                "\t</header>\n" +
                "\t<folder>\n" +
                "\t\t<id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "\t\t<patient>\n" +
                "\t\t\t<id S=\"ID-PATIENT\" SV=\"1.0\">28022229535</id>\n" +
                "\t\t\t<firstname>Roger Joseph</firstname>\n" +
                "\t\t\t<familyname>Deceuninck</familyname>\n" +
                "\t\t\t<birthdate>\n" +
                "\t\t\t\t<date>1928-02-22</date>\n" +
                "\t\t\t</birthdate>\n" +
                "\t\t\t<sex>\n" +
                "\t\t\t\t<cd S=\"CD-SEX\" SV=\"1.0\">male</cd>\n" +
                "\t\t\t</sex>\n" +
                "\t\t</patient>\n" +
                "\t\t<transaction>\n" +
                "\t\t\t<id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "\t\t\t<cd S=\"CD-TRANSACTION\" SV=\"1.4\">medicationschemeelement</cd>\n" +
                "\t\t\t<date>2016-06-01</date>\n" +
                "\t\t\t<time>19:08:08</time>\n" +
                "\t\t\t<author>\n" +
                "\t\t\t\t<hcparty>\n" +
                "\t\t\t\t\t<id S=\"ID-HCPARTY\" SV=\"1.0\">13299391004</id>\n" +
                "\t\t\t\t\t<cd S=\"CD-HCPARTY\" SV=\"1.2\">persphysician</cd>\n" +
                "\t\t\t\t\t<firstname>Frank John</firstname>\n" +
                "\t\t\t\t\t<familyname>Glorieux</familyname>\n" +
                "\t\t\t\t</hcparty>\n" +
                "\t\t\t</author>\n" +
                "\t\t\t<iscomplete>true</iscomplete>\n" +
                "\t\t\t<isvalidated>true</isvalidated>\n" +
                "\t\t\t<item>\n" +
                "\t\t\t\t<id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "\t\t\t\t<cd S=\"CD-ITEM\" SV=\"1.4\">healthcareelement</cd>\n" +
                "\t\t\t\t<content>\n" +
                "\t\t\t\t\t<cd S=\"CD-ITEM-MS\" SV=\"1.0\">adaptationflag</cd>\n" +
                "\t\t\t\t\t<cd S=\"CD-MS-ADAPTATION\" SV=\"1.0\">medication</cd>\n" +
                "\t\t\t\t</content>\n" +
                "\t\t\t</item>\n" +
                "\t\t\t<item>\n" +
                "\t\t\t\t<id S=\"ID-KMEHR\" SV=\"1.0\">2</id>\n" +
                "\t\t\t\t<cd S=\"CD-ITEM\" SV=\"1.4\">medication</cd>\n" +
                "\t\t\t\t<content>\n" +
                "\t\t\t\t\t<medicinalproduct>\n" +
                "\t\t\t\t\t\t<intendedcd S=\"CD-DRUG-CNK\" SV=\"juni 2013\">2388684</intendedcd>\n" +
                "\t\t\t\t\t\t<intendedname>Finasteride Sandoz tab 100x 5mg</intendedname>\n" +
                "\t\t\t\t\t</medicinalproduct>\n" +
                "\t\t\t\t</content>\n" +
                "\t\t\t\t<content>\n" +
                "\t\t\t\t\t<cd S=\"CD-ATC\" SV=\"juni 2013\">G04CB01</cd>\n" +
                "\t\t\t\t</content>\n" +
                "\t\t\t\t<beginmoment>\n" +
                "\t\t\t\t\t<date>2016-06-01</date>\n" +
                "\t\t\t\t</beginmoment>\n" +
                "\t\t\t\t<temporality>\n" +
                "\t\t\t\t\t<cd S=\"CD-TEMPORALITY\" SV=\"1.0\">chronic</cd>\n" +
                "\t\t\t\t</temporality>\n" +
                "\t\t\t\t<frequency>\n" +
                "\t\t\t\t\t<periodicity>\n" +
                "\t\t\t\t\t\t<cd S=\"CD-PERIODICITY\" SV=\"1.0\">D</cd>\n" +
                "\t\t\t\t\t</periodicity>\n" +
                "\t\t\t\t</frequency>\n" +
                "\t\t\t\t<regimen>\n" +
                "\t\t\t\t\t<daytime>\n" +
                "\t\t\t\t\t\t<dayperiod>\n" +
                "\t\t\t\t\t\t\t<cd S=\"CD-DAYPERIOD\" SV=\"1.1\">beforebreakfast</cd>\n" +
                "\t\t\t\t\t\t</dayperiod>\n" +
                "\t\t\t\t\t</daytime>\n" +
                "\t\t\t\t\t<quantity>\n" +
                "\t\t\t\t\t\t<decimal>1.00</decimal>\n" +
                "\t\t\t\t\t\t<unit>\n" +
                "\t\t\t\t\t\t\t<cd S=\"CD-ADMINISTRATIONUNIT\" SV=\"1.2\">00005</cd>\n" +
                "\t\t\t\t\t\t</unit>\n" +
                "\t\t\t\t\t</quantity>\n" +
                "\t\t\t\t</regimen>\n" +
                "\t\t\t</item>\n" +
                "\t\t</transaction>\n" +
                "\t</folder>\n" +
                "</kmehrmessage>\n";
        try {
            JAXBContext jc = JAXBContext.newInstance("be.fgov.ehealth.standards.kmehr.schema.v1");
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            JAXBElement<KmehrmessageType> feed = unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), KmehrmessageType.class);

            final Map<String, String> meta = new HashMap<String, String>();
            // Retrieve the URI of the data entry
            meta.put("uri", "/Vitalink/15/subject/28022229535/medication-scheme/92893/2");
            meta.put("source", "Vitalink");


            final MedicatieSchemaItem item = new KmehrMessageTypeToMedicatieSchemaItems().invoke(Pair.of(meta, feed.getValue()));
            assertEquals(1, item.getRegimenItems().length);
        } catch (Exception e) {
            fail(e.getCause().getMessage());
        }
    }

    @Test
    public void testEndCondition() {
        String xml = "<kmehrmessage xmlns=\"http://www.ehealth.fgov.be/standards/kmehr/schema/v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.ehealth.fgov.be/standards/kmehr/schema/v1 kmehr_elements.xsd\">\n" +
                "    <header>\n" +
                "        <standard>\n" +
                "            <cd S=\"CD-STANDARD\" SV=\"1.4\">20120401</cd>\n" +
                "        </standard>\n" +
                "        <id S=\"ID-KMEHR\" SV=\"1.0\">99934051.20160513144859564</id>\n" +
                "        <date>2016-11-30</date>\n" +
                "        <time>14:48:59</time>\n" +
                "        <sender>\n" +
                "            <hcparty>\n" +
                "                <id S=\"ID-HCPARTY\" SV=\"1.0\">99934051</id>\n" +
                "                <id S=\"INSS\" SV=\"1.0\">73082511743</id>\n" +
                "                <cd S=\"CD-HCPARTY\" SV=\"1.3\">perspharmacist</cd>\n" +
                "                <firstname>BOB</firstname>\n" +
                "                <familyname>WALBERS</familyname>\n" +
                "            </hcparty>\n" +
                "            <hcparty>\n" +
                "                <cd S=\"CD-HCPARTY\" SV=\"1.3\">application</cd>\n" +
                "                <name>Lab</name>\n" +
                "            </hcparty>\n" +
                "        </sender>\n" +
                "        <recipient>\n" +
                "            <hcparty>\n" +
                "                <cd S=\"CD-HCPARTY\" SV=\"1.3\">application</cd>\n" +
                "                <name>VITALINK</name>\n" +
                "            </hcparty>\n" +
                "        </recipient>\n" +
                "    </header>\n" +
                "    <folder>\n" +
                "        <id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "        <patient>\n" +
                "            <id S=\"ID-PATIENT\" SV=\"1.0\">72071135503</id>\n" +
                "            <firstname>Fo/Pa Firstname</firstname>\n" +
                "            <familyname>Fo/Pa Familyname</familyname>\n" +
                "            <sex>\n" +
                "                <cd S=\"CD-SEX\" SV=\"1.0\">male</cd>\n" +
                "            </sex>\n" +
                "        </patient>\n" +
                "        <transaction>\n" +
                "            <id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "            <cd S=\"CD-TRANSACTION\" SV=\"1.4\">medicationschemeelement</cd>\n" +
                "            <date>2016-06-06</date>\n" +
                "            <time>16:06:06</time>\n" +
                "            <author>\n" +
                "                <hcparty>\n" +
                "                    <id S=\"ID-HCPARTY\" SV=\"1.0\">17892144001</id>\n" +
                "                    <cd S=\"CD-HCPARTY\" SV=\"1.3\">persphysician</cd>\n" +
                "                    <firstname>Tr/Au Persphysician Familyname</firstname>\n" +
                "                    <familyname>Ta/Au Persphysician Familyname</familyname>\n" +
                "                </hcparty>\n" +
                "                <hcparty>\n" +
                "                    <cd S=\"CD-HCPARTY\" SV=\"1.3\">application</cd>\n" +
                "                    <name>Tr/App Validatie Lab Tools</name>\n" +
                "                </hcparty>\n" +
                "            </author>\n" +
                "            <iscomplete>true</iscomplete>\n" +
                "            <isvalidated>true</isvalidated>\n" +
                "            <item>\n" +
                "                <id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "                <cd S=\"CD-ITEM\" SV=\"1.4\">healthcareelement</cd>\n" +
                "                <content>\n" +
                "                    <cd S=\"CD-ITEM-MS\" SV=\"1.0\">adaptationflag</cd>\n" +
                "                    <cd S=\"CD-MS-ADAPTATION\" SV=\"1.0\">medication</cd>\n" +
                "                </content>\n" +
                "            </item>\n" +
                "            <item>\n" +
                "                <id S=\"ID-KMEHR\" SV=\"1.0\">2</id>\n" +
                "                <cd S=\"CD-ITEM\" SV=\"1.4\">medication</cd>\n" +
                "                <content>\n" +
                "                    <medicinalproduct>\n" +
                "                        <intendedcd S=\"CD-DRUG-CNK\" SV=\"1.0\">0117028</intendedcd>\n" +
                "                        <intendedname>KEFZOL AMP INJ 3X1G</intendedname>\n" +
                "                    </medicinalproduct>\n" +
                "                </content>\n" +
                "                <beginmoment>\n" +
                "                    <date>2017-01-01</date>\n" +
                "                </beginmoment>\n" +
                "                <temporality>\n" +
                "                    <cd S=\"CD-TEMPORALITY\" SV=\"1.0\">acute</cd>\n" +
                "                </temporality>\n" +
                "                <frequency>\n" +
                "                    <periodicity>\n" +
                "                        <cd S=\"CD-PERIODICITY\" SV=\"1.0\">UW</cd>\n" +
                "                    </periodicity>\n" +
                "                </frequency>\n" +
                "                <route>\n" +
                "                    <cd S=\"CD-DRUG-ROUTE\" SV=\"2.0\">00013</cd>\n" +
                "                </route>\n" +
                "                <instructionforpatient L=\"nl\">904</instructionforpatient>\n" +
                "            </item>\n" +
                "            <item>\n" +
                "                <id S=\"ID-KMEHR\" SV=\"1.0\">3</id>\n" +
                "                <cd S=\"CD-ITEM\" SV=\"1.4\">healthcareelement</cd>\n" +
                "                <content>\n" +
                "                    <cd S=\"CD-ITEM-MS\" SV=\"1.0\">endcondition</cd>\n" +
                "                </content>\n" +
                "                <content>\n" +
                "                    <text L=\"nl\">op=stop</text>\n" +
                "                </content>\n" +
                "            </item>\n" +
                "        </transaction>\n" +
                "    </folder>\n" +
                "</kmehrmessage>";
        try {
            JAXBContext jc = JAXBContext.newInstance("be.fgov.ehealth.standards.kmehr.schema.v1");
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            JAXBElement<KmehrmessageType> feed = unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), KmehrmessageType.class);

            final Map<String, String> meta = new HashMap<String, String>();
            // Retrieve the URI of the data entry
            meta.put("uri", "/Vitalink/15/subject/28022229535/medication-scheme/92893/2");
            meta.put("source", "Vitalink");


            final MedicatieSchemaItem item = new KmehrMessageTypeToMedicatieSchemaItems().invoke(Pair.of(meta, feed.getValue()));
            assertEquals(true, item.getEndCondition().contains("op=stop"));
        } catch (Exception e) {
            fail(e.getCause().getMessage());
        }
    }

    @Test
    public void testOnceAYear() {

        String xml = "<kmehrmessage xmlns=\"http://www.ehealth.fgov.be/standards/kmehr/schema/v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.ehealth.fgov.be/standards/kmehr/schema/v1 kmehr_elements.xsd\">\n" +
                "    <header>\n" +
                "        <standard>\n" +
                "            <cd S=\"CD-STANDARD\" SV=\"1.4\">20120401</cd>\n" +
                "        </standard>\n" +
                "        <id S=\"ID-KMEHR\" SV=\"1.0\">99934051.20160513144859582</id>\n" +
                "        <date>2016-11-30</date>\n" +
                "        <time>14:48:59</time>\n" +
                "        <sender>\n" +
                "            <hcparty>\n" +
                "                <id S=\"ID-HCPARTY\" SV=\"1.0\">99934051</id>\n" +
                "                <id S=\"INSS\" SV=\"1.0\">73082511743</id>\n" +
                "                <cd S=\"CD-HCPARTY\" SV=\"1.3\">perspharmacist</cd>\n" +
                "                <firstname>BOB</firstname>\n" +
                "                <familyname>WALBERS</familyname>\n" +
                "            </hcparty>\n" +
                "            <hcparty>\n" +
                "                <cd S=\"CD-HCPARTY\" SV=\"1.3\">application</cd>\n" +
                "                <name>Lab</name>\n" +
                "            </hcparty>\n" +
                "        </sender>\n" +
                "        <recipient>\n" +
                "            <hcparty>\n" +
                "                <cd S=\"CD-HCPARTY\" SV=\"1.3\">application</cd>\n" +
                "                <name>VITALINK</name>\n" +
                "            </hcparty>\n" +
                "        </recipient>\n" +
                "    </header>\n" +
                "    <folder>\n" +
                "        <id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "        <patient>\n" +
                "            <id S=\"ID-PATIENT\" SV=\"1.0\">72071135503</id>\n" +
                "            <firstname>Fo/Pa Firstname</firstname>\n" +
                "            <familyname>Brackez</familyname>\n" +
                "            <sex>\n" +
                "                <cd S=\"CD-SEX\" SV=\"1.0\">male</cd>\n" +
                "            </sex>\n" +
                "        </patient>\n" +
                "        <transaction>\n" +
                "            <id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "            <cd S=\"CD-TRANSACTION\" SV=\"1.4\">medicationschemeelement</cd>\n" +
                "            <date>2016-06-06</date>\n" +
                "            <time>16:06:06</time>\n" +
                "            <author>\n" +
                "                <hcparty>\n" +
                "                    <id S=\"ID-HCPARTY\" SV=\"1.0\">17892144001</id>\n" +
                "                    <cd S=\"CD-HCPARTY\" SV=\"1.3\">persphysician</cd>\n" +
                "                    <firstname>Tr/Au Persphysician Familyname</firstname>\n" +
                "                    <familyname>Ta/Au Persphysician Familyname</familyname>\n" +
                "                </hcparty>\n" +
                "                <hcparty>\n" +
                "                    <cd S=\"CD-HCPARTY\" SV=\"1.3\">application</cd>\n" +
                "                    <name>Tr/App Validatie Lab Tools</name>\n" +
                "                </hcparty>\n" +
                "            </author>\n" +
                "            <iscomplete>true</iscomplete>\n" +
                "            <isvalidated>true</isvalidated>\n" +
                "            <item>\n" +
                "                <id S=\"ID-KMEHR\" SV=\"1.0\">1</id>\n" +
                "                <cd S=\"CD-ITEM\" SV=\"1.4\">healthcareelement</cd>\n" +
                "                <content>\n" +
                "                    <cd S=\"CD-ITEM-MS\" SV=\"1.0\">adaptationflag</cd>\n" +
                "                    <cd S=\"CD-MS-ADAPTATION\" SV=\"1.0\">medication</cd>\n" +
                "                </content>\n" +
                "            </item>\n" +
                "            <item>\n" +
                "                <id S=\"ID-KMEHR\" SV=\"1.0\">2</id>\n" +
                "                <cd S=\"CD-ITEM\" SV=\"1.4\">medication</cd>\n" +
                "                <content>\n" +
                "                    <medicinalproduct>\n" +
                "                        <intendedcd S=\"CD-DRUG-CNK\" SV=\"1.0\">8050809</intendedcd>\n" +
                "                        <intendedname>influenzavaccin (inj.)</intendedname>\n" +
                "                    </medicinalproduct>\n" +
                "                </content>\n" +
                "                <beginmoment>\n" +
                "                    <date>2010-01-01</date>\n" +
                "                </beginmoment>\n" +
                "                <temporality>\n" +
                "                    <cd S=\"CD-TEMPORALITY\" SV=\"1.0\">chronic</cd>\n" +
                "                </temporality>\n" +
                "                <frequency>\n" +
                "                    <periodicity>\n" +
                "                        <cd S=\"CD-PERIODICITY\" SV=\"1.0\">J</cd>\n" +
                "                    </periodicity>\n" +
                "                </frequency>\n" +
                "                <regimen>\n" +
                "                    <date>2016-11-15</date>\n" +
                "                    <daytime>\n" +
                "                        <dayperiod>\n" +
                "                            <cd S=\"CD-DAYPERIOD\" SV=\"1.0\">morning</cd>\n" +
                "                        </dayperiod>\n" +
                "                    </daytime>\n" +
                "                    <quantity>\n" +
                "                        <decimal>1</decimal>\n" +
                "                        <unit>\n" +
                "                            <cd S=\"CD-ADMINISTRATIONUNIT\" SV=\"1.0\">00006</cd>\n" +
                "                        </unit>\n" +
                "                    </quantity>\n" +
                "                </regimen>\n" +
                "                <route>\n" +
                "                    <cd S=\"CD-DRUG-ROUTE\" SV=\"2.0\">00068</cd>\n" +
                "                </route>\n" +
                "                <instructionforpatient L=\"nl\">Jaarlijks op +/- 15 november</instructionforpatient>\n" +
                "            </item>\n" +
                "        </transaction>\n" +
                "    </folder>\n" +
                "</kmehrmessage>";
        try {
            JAXBContext jc = JAXBContext.newInstance("be.fgov.ehealth.standards.kmehr.schema.v1");
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            JAXBElement<KmehrmessageType> feed = unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), KmehrmessageType.class);

            final Map<String, String> meta = new HashMap<String, String>();
            // Retrieve the URI of the data entry
            meta.put("uri", "/Vitalink/15/subject/28022229535/medication-scheme/92893/2");
            meta.put("source", "Vitalink");


            final MedicatieSchemaItem item = new KmehrMessageTypeToMedicatieSchemaItems().invoke(Pair.of(meta, feed.getValue()));
            assertEquals(new Date(1479164400000L), item.getRegimenItems()[0].getDate());
        } catch (Exception e) {
            fail(e.getCause().getMessage());
        }
    }

}
