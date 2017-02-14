package uz.emv.sam.v1.db.tables.xml.zip;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.db.tables.xml.InputStreamProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bdcuyp0 on 15-6-2016.
 */
public class ByteArrayInputStreamProvider implements InputStreamProvider {
    private final Pair<Map<String, Object>, byte[]> bytes;
    private final List<InputStream> inputStreams = new LinkedList<InputStream>();

    public ByteArrayInputStreamProvider(Pair<Map<String, Object>, byte[]> metaDataAndBytes) {
        this.bytes = metaDataAndBytes;
    }

    @Override
    @NotNull
    public InputStream getInputStream() throws IOException {
        final InputStream in = new ByteArrayInputStream(bytes.getRight());
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
        return (String) bytes.getLeft().get("name");
    }

    @Override
    public String getVersion() {
        return (String) bytes.getLeft().get("version");
    }

}
