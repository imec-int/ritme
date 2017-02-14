package uz.emv.sam.v1.db.tables.xml.directory;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.db.tables.xml.InputStreamProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bdcuyp0 on 15-6-2016.
 */
public class FileInputStreamProvider implements InputStreamProvider {
    private final File xmlFile;
    private final List<InputStream> inputStreams = new LinkedList<InputStream>();

    public FileInputStreamProvider(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    @Override
    @NotNull
    public InputStream getInputStream() throws IOException {
        final InputStream in = new FileInputStream(xmlFile);
        inputStreams.add(in);
        return in;
    }

    @Override
    public void cleanUp() {
        for (InputStream is : inputStreams) {
            try {
                is.close();
            } catch (Exception ignored) {
            }
        }
        inputStreams.clear();
    }

    @Override
    public String getName() {
        return xmlFile.getName();
    }

    @Override
    public String getVersion() {
        return parseVersion(xmlFile);
    }

    @NotNull
    private String parseVersion(final File file) {
        int start = file.getName().indexOf("#");
        int stop = file.getName().indexOf("_", start);
        return file.getName().substring(start + 1, stop);
    }

}
