package uz.emv.sam.v1.db.tables.xml;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.db.tables.Table;
import uz.emv.sam.v1.domain.SAMTools;

import java.util.*;

/**
 * User: simbre1
 * Date: 29/11/13
 */
public class NameTranslatingTable implements Table {

    private final NameTranslation names;
    private final Table table;

    public NameTranslatingTable(@NotNull final NameTranslation names, @NotNull final Table table){
        this.names = names;
        this.table = table;
    }

    @NotNull
    @Override
    public List<String> getColumns() {
        return table.getColumns();
    }

    @Override
    public void cleanUp() {
        table.cleanUp();
    }

    @Override
    public String getVersion() {
        return table.getVersion();
    }

    @Override
    public Iterator<Map<String, Object>> iterator() {
        return new TranslatingIterator(table.iterator());
    }

    private class TranslatingIterator implements Iterator<Map<String, Object>>{

        private int index = 0;
        private final Iterator<Map<String, Object>> iterator;

        public TranslatingIterator(@NotNull final Iterator<Map<String, Object>> iterator){
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Map<String, Object> next() {
            //noinspection CaughtExceptionImmediatelyRethrown
            try{
                final Map<String, Object> map = iterator.next();
                final Map<String, Object> rmap = new TreeMap<String, Object>();

                for (Map.Entry<String, Object> e : map.entrySet()) {
                    rmap.put(SAMTools.toS9Name(e.getKey()), e.getValue());
                }

                final Object nameId = rmap.get("nameId");
                if(nameId != null){
                    final Integer nameIdInt = Integer.parseInt((String) nameId);
                    rmap.put("name", names.getName(nameIdInt));
                }

                rmap.put("id", null);
                ++index;

                return rmap;
            }catch(NoSuchElementException e){
                throw e;
            }
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
