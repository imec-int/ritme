package uz.emv.sam.v1.db.tables.xml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uz.emv.sam.v1.db.tables.Table;
import uz.emv.sam.v1.db.tables.TableFactory;
import uz.emv.sam.v1.domain.SAMTools;

/**
 * User: simbre1
 * Date: 27/09/13
 */
public class XmlTableFactory implements TableFactory {
    private final NameTranslation names;
    private final InputStreamProviderFactory inputStreamProviderFactory;
    private final Table nameTranslationTable;


    public XmlTableFactory(final InputStreamProviderFactory inputStreamProviderFactory) {
        this.inputStreamProviderFactory = inputStreamProviderFactory;

        nameTranslationTable = createTable(NameTranslation.TABLE_NAME);
        if (nameTranslationTable == null) {
            throw new RuntimeException("Missing NameTranslation table: "+ NameTranslation.TABLE_NAME);
        }
        names = new NameTranslation(nameTranslationTable);
        nameTranslationTable.cleanUp();
    }

    @Override
    @Nullable
    public Table getTable(@NotNull final String name) {
        return createTranslatingTable(SAMTools.toSamTableName(SAMTools.removeBaseTablePrefix(name)));
    }

    @Override
    public String getVersion() {
        return nameTranslationTable.getVersion();
    }

    @Nullable
    public Table createTable(@NotNull final String samTableName){
        try {
            InputStreamProvider inputStreamProvider = inputStreamProviderFactory.getInputStreamProvider(samTableName);
            return new XmlStaxTable(inputStreamProvider);
        } catch (Exception e) {
            throw new RuntimeException("Error creating table: "+ samTableName, e);
        }

    }

    @Nullable
    public NameTranslatingTable createTranslatingTable(@NotNull final String samTableName){
        try {
            return new NameTranslatingTable(names, new XmlStaxTable(inputStreamProviderFactory.getInputStreamProvider(samTableName)));
        } catch (Exception e) {
            throw new RuntimeException("Error creating NameTranslatingTable: "+samTableName, e);
        }
    }


}
