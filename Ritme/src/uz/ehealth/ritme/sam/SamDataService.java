package uz.ehealth.ritme.sam;

import java.io.InputStream;

/**
 * Created by bdcuyp0 on 13-6-2016.
 */
public interface SamDataService {
    String getActualSamDataVersion(String user, String nihiiOrg);

    public InputStream getActualSamDataAsZipStream(String user, String nihiiOrg);
}
