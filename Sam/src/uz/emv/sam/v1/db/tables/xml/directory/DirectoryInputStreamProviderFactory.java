package uz.emv.sam.v1.db.tables.xml.directory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uz.emv.sam.v1.db.tables.xml.InputStreamProvider;
import uz.emv.sam.v1.db.tables.xml.InputStreamProviderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bdcuyp0 on 15-6-2016.
 */
public class DirectoryInputStreamProviderFactory implements InputStreamProviderFactory {
    private final File basePath;
    private final String suffix;

    public DirectoryInputStreamProviderFactory(@NotNull File basePath, @Nullable String suffix) {
        this.basePath = basePath;
        this.suffix = suffix;
    }

    @Override
    @NotNull
    public InputStreamProvider getInputStreamProvider(@NotNull final String samTableName) {
        final File xmlFile = findXmlFile(samTableName, basePath, suffix);
        if (xmlFile == null) {
            throw new RuntimeException("Could not find table " + samTableName + " in " + basePath.getAbsolutePath() + " with suffix " + suffix);

        }
        return new FileInputStreamProvider(xmlFile);
    }

    @Nullable
    private File findXmlFile(@NotNull final String samTableName, final File basePath, final String suffix) {
        File[] files = basePath.listFiles();
        final List<File> partialMatches = new ArrayList<File>();
        if (files != null) {
            for (File file : files) {
                if (suffix == null || file.getName().endsWith(suffix)) {
                    if (file.getName().startsWith(samTableName + "#")) {
                        return file;
                    } else if (file.getName().startsWith(samTableName)) {
                        partialMatches.add(file);
                    }
                }
            }
        }

        if (partialMatches.isEmpty()) {
            throw new RuntimeException("Could not find table: " + samTableName + " in " + basePath.getAbsolutePath());
        } else if (partialMatches.size() > 1) {
            throw new RuntimeException("Could not determine table for: " + samTableName + " in " + basePath.getAbsolutePath()
                    + ", " + partialMatches.size() + " partial matches");
        } else {
            return partialMatches.get(0);
        }
    }
}
