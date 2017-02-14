package uz.emv.sam.v1.db.tables.xml.zip;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.emv.sam.v1.db.tables.xml.InputStreamProvider;
import uz.emv.sam.v1.db.tables.xml.InputStreamProviderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by bdcuyp0 on 15-6-2016.
 */
public class ZipInputStreamProviderFactory implements InputStreamProviderFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ZipInputStreamProviderFactory.class);
    private final byte[] zip;
    private final String suffix;

    public ZipInputStreamProviderFactory(final InputStream stream, final String suffix) {
        this.zip = copyToByteArray(stream);
        //LOG.info(new String(zip));
        this.suffix = suffix;
    }

    private byte[] copyToByteArray(final InputStream stream) {
        try {
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public InputStreamProvider getInputStreamProvider(@NotNull final String samTableName) {
        final Pair<Map<String, Object>, byte[]> metaDataAndBytes = findBytes(samTableName, zip, suffix);
        if (metaDataAndBytes == null) {
            throw new RuntimeException("Could not find table " + samTableName + " in zipped inputstream with suffix " + suffix);

        }
        return new ByteArrayInputStreamProvider(metaDataAndBytes);
    }

    @Nullable
    private Pair<Map<String, Object>, byte[]> findBytes(@NotNull final String samTableName, final byte[] zip, final String suffix) {

        final List<Pair<Map<String, Object>, byte[]>> partialMatches = new ArrayList<Pair<Map<String, Object>, byte[]>>();
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zip));

        ZipEntry entry = null;
        Pair<Map<String, Object>, byte[]> selected = null;
        try {
            entry = zipInputStream.getNextEntry();
            while (entry != null) {
                Map<String, Object> metaData = new HashMap<String, Object>();
                String version = parseVersion(entry);
                metaData.put("version", version);
                metaData.put("name", samTableName);
                if (suffix == null || entry.getName().endsWith(suffix)) {
                    if (entry.getName().startsWith(samTableName + "#")) {
                        selected = Pair.of(metaData, IOUtils.toByteArray(zipInputStream));
                        break;
                    } else if (entry.getName().startsWith(samTableName)) {
                        partialMatches.add(Pair.of(metaData, IOUtils.toByteArray(zipInputStream)));
                    }
                }
                entry = zipInputStream.getNextEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(zipInputStream);
        }

        if (selected == null) {
            if (partialMatches.isEmpty()) {
                throw new RuntimeException("Could not find table: " + samTableName + " in zipinputstream");
            } else if (partialMatches.size() > 1) {
                throw new RuntimeException("Could not determine table for: " + samTableName + " in zipinputstream, " + partialMatches.size() + " partial matches");
            } else {
                selected = partialMatches.get(0);
            }
        }

        return selected;
    }

    @NotNull
    private String parseVersion(final ZipEntry entry) {
        LOG.debug(entry.getName());
        final int hekje = entry.getName().indexOf("#");
        int start = hekje == -1 ? 0 : (hekje + 1);//conditie is overbodig maar wel duidelijker: wat te doen als niet gevonden.
        final int underscore = entry.getName().indexOf("_", start);
        int stop = underscore == -1 ? start : underscore;
        return entry.getName().substring(start, stop);
    }
}
