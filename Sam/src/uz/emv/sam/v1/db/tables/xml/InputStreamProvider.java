package uz.emv.sam.v1.db.tables.xml;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bdcuyp0 on 15-6-2016.
 */
public interface InputStreamProvider {

    @NotNull
    InputStream getInputStream() throws IOException;

    void cleanUp();

    String getName();

    String getVersion();
}
