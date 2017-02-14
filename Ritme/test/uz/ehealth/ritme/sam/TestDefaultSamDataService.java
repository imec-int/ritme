package uz.ehealth.ritme.sam;


import org.junit.Assert;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.plugins.PluginManager;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.fail;

/**
 * Created by bdcuyp0 on 13-6-2016.
 */
public class TestDefaultSamDataService {

    public static void main(String[] args) {
        new TestDefaultSamDataService().testZipFile();
    }

    public static final Logger LOG = LoggerFactory.getLogger(TestDefaultSamDataService.class);

    @Ignore
    public void testZipFile() {
        SamDataService samDataService = PluginManager.get("ritme.sam.data", SamDataService.class);
        ZipInputStream stream = new ZipInputStream(samDataService.getActualSamDataAsZipStream("user", "org"));

        ZipEntry entry = null;
        try {
            entry = stream.getNextEntry();

        } catch (IOException e) {
            fail(e.getMessage());
        }
        while (entry != null) {
            LOG.info(entry.getName());
            try {
                entry = stream.getNextEntry();

            } catch (IOException e) {
                fail(e.getMessage());
            }
        }
        Assert.assertTrue(true);

    }

}
