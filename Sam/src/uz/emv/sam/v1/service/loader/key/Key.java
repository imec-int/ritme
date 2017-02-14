package uz.emv.sam.v1.service.loader.key;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * User: simbre1
 * Date: 19/09/13
 */
public final class Key implements Comparable<Key>{
    private final SortedSet<String> columns = new TreeSet<String>();

    public Key(@NotNull final String column){
        columns.add(column);
    }

    public Key(@NotNull final String[] columns){
        this.columns.addAll(Arrays.asList(columns));
    }

    public Key(@NotNull final Collection<String> columns){
        this.columns.addAll(columns);
    }

    @NotNull
    public Set<String> getColumns(){
        return columns;
    }

    public boolean isKeyPart(@NotNull final String name){
        for(String s : getColumns()){
            if(s.equals(name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }

        Key a = (Key)o;
        return columns.equals(a.getColumns());
    }

    @Override
    public int hashCode(){
        return columns.hashCode();
    }

    @Override
    public int compareTo(@NotNull final Key key) {
        return compare(columns, key.columns);
    }

    private static <T extends Comparable<T>> int compare(@NotNull final SortedSet<T> a, @NotNull final SortedSet<T> b){
        int c = a.size() - b.size();
        if(c != 0){
            return c;
        }

        Iterator<T> ia = a.iterator();
        Iterator<T> ib = b.iterator();
        while(c == 0 && ia.hasNext()){
            c = ia.next().compareTo(ib.next());
        }
        return c;
    }
}
