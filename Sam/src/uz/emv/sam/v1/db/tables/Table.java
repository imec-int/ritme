package uz.emv.sam.v1.db.tables;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * User: simbre1
 * Date: 27/09/13
 */
public interface Table extends Iterable<Map<String, Object>>{
    @NotNull
    public List<String> getColumns();
    public void cleanUp();

    String getVersion();
}
