package uz.emv.sam.v1.db.tables.xml;

/**
 * Created by bdcuyp0 on 15-6-2016.
 */
public interface InputStreamProviderFactory {
    public InputStreamProvider getInputStreamProvider(String samTableName);
}
