package uz.emv.sam.v1.db.tables;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: simbre1
 * Date: 27/09/13
 */
public interface TableFactory {
    @Nullable
    public Table getTable(@NotNull String tableName);

    String getVersion();
}
