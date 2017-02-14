package uz.emv.sam.v1.service.xml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uz.emv.sam.v1.db.tables.xml.XmlTableFactory;
import uz.emv.sam.v1.db.tables.xml.directory.DirectoryInputStreamProviderFactory;
import uz.emv.sam.v1.db.tables.xml.zip.ZipInputStreamProviderFactory;
import uz.emv.sam.v1.domain.SAMTools;
import uz.emv.sam.v1.service.ObjectPersistor;
import uz.emv.sam.v1.service.SamObjectRepository;
import uz.emv.sam.v1.service.loader.DefaultObjectLoader;

import java.io.File;
import java.io.InputStream;

/**
 * Created by bdcuyp0 on 7-10-2015.
 */
public class SamObjectRepositoryFactory {
    @NotNull
    public static SamObjectRepository createRepositoryFromXml(@NotNull final File basePath,
                                                              @Nullable final String suffix,
                                                              @NotNull final ObjectPersistor objectPersistor) {
        return new SamObjectRepository(
                new DefaultObjectLoader(
                        SAMTools.SAM_CLASSES,
                        new XmlTableFactory(
                                new DirectoryInputStreamProviderFactory(basePath, suffix))),
                objectPersistor);
    }

    @NotNull
    public static SamObjectRepository createRepositoryFromXmlZipInputStream(@NotNull final InputStream stream,
                                                                            @Nullable final String suffix,
                                                                            @NotNull final ObjectPersistor objectPersistor) {
        return new SamObjectRepository(
                new DefaultObjectLoader(
                        SAMTools.SAM_CLASSES,
                        new XmlTableFactory(
                                new ZipInputStreamProviderFactory(stream, suffix))),
                objectPersistor);
    }
}
