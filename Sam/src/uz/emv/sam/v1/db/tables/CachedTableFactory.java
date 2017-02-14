package uz.emv.sam.v1.db.tables;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: simbre1
 * Date: 27/09/13
 */
public class CachedTableFactory implements TableFactory {

    private final Map<String, Table> map = new TreeMap<String, Table>();

    private final TableFactory factory;

    public CachedTableFactory(@NotNull final TableFactory factory){
        if(factory instanceof CachedTableFactory){
            throw new RuntimeException("Illegal nesting of CachedTableFactories");
        }
        this.factory = factory;
    }

    @Override
    @Nullable
    public synchronized Table getTable(@NotNull final String tableName) {
        Table t = map.get(tableName);
        if(t == null){
            t = createTable(tableName);
            map.put(tableName, t);
        }
        return t;
    }

    @Override
    public String getVersion() {
        return factory.getVersion();
    }

    @Nullable
    private Table createTable(@NotNull String name){
        return factory.getTable(name);
    }

    @NotNull
    public Collection<Table> getCachedTables(){
        return map.values();
    }

    public boolean isCached(@NotNull final String tableName){
        return map.containsKey(tableName);
    }

    public void clearCache(){
        for(Table t : map.values()){
            cleanUp(t);
        }
        map.clear();
    }

    public void clearCache(@NotNull final String tableName){
        if(isCached(tableName)){
            cleanUp(map.get(tableName));
            map.put(tableName, null);
        }
    }

    private void cleanUp(@NotNull final Table t){
        t.cleanUp();
    }
}
