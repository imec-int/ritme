package uz.ehealth.ritme.vitalink.logic;

import be.fgov.ehealth.standards.kmehr.schema.v1.KmehrmessageType;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.model.MedicatieSchemaItem;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

/**
 * Created by bdcuyp0 on 18-10-2016.
 */

public class TestAllInData {
    private static final Pattern EOL = Pattern.compile("\\r\\n|\\n");

    @Test
    public void testKmehr() {

        CodeSource src = TestAllInData.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL url = src.getLocation();

            File root = new File(url.getFile() + "data/");
            try {

                doTest(root);
            } catch (Exception e) {
                fail();
            }


        }
    }

    public void doTest(final File directory) throws FileNotFoundException {
        File[] files = directory.listFiles();

        files = files == null ? new File[]{} : files;

        for (File file : files) {

            if (file.isDirectory()) {
                doTest(file);
            } else {
                if (file.getName().endsWith(".xml")) {
                    System.out.println(file.getAbsoluteFile());
                    FileReader fr = new FileReader(file);
                    try {
                        JAXBContext jc = JAXBContext.newInstance("be.fgov.ehealth.standards.kmehr.schema.v1");
                        Unmarshaller unmarshaller = jc.createUnmarshaller();

                        JAXBElement<KmehrmessageType> feed = unmarshaller.unmarshal(new StreamSource(fr), KmehrmessageType.class);

                        final Map<String, String> meta = new HashMap<String, String>();
                        // Retrieve the URI of the data entry
                        meta.put("uri", "/Vitalink/15/subject/28022229535/medication-scheme/92893/2");
                        meta.put("source", "Vitalink");


                        final MedicatieSchemaItem item = new KmehrMessageTypeToMedicatieSchemaItems().invoke(Pair.of(meta, feed.getValue()));

                        String json = EOL.matcher(JSONTools.marshal(item)).replaceAll("");

                        File target = new File(file.getAbsolutePath() + ".json");

                        if (!target.exists()) {
                            FileWriter writer = new FileWriter(target);
                            writer.write(json);
                            writer.flush();
                            writer.close();

                        }

                        String expectedFromFile = readFile(target);
                        final MedicatieSchemaItem expectedItem = JSONTools.unmarshal(expectedFromFile, MedicatieSchemaItem.class);
                        String expected = JSONTools.marshal(expectedItem);


                        assertEquals(expected, json);


                    } catch (Exception e) {
                        fail(e.getCause().getMessage());
                    }
                } else {
                    //do nothing
                }
            }

        }
    }

    private String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
}
