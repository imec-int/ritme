package uz.emv.sam.v1.service.loader.key;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a key instance.
 * User: simbre1
 * Date: 24/09/13
 */
public class KeyMap implements Comparable<KeyMap>{

    private final Key key;
    private final Map<String, Object> map;

    public KeyMap(@NotNull final Key key, @NotNull final Map<String, Object> map){
        this.key = key;
        Collection<String> keys = key.getColumns();
        Map<String, Object> result = new HashMap<String, Object>();
        for (String otherKey : keys) {
            if (map.containsKey(otherKey)) {
                result.put(otherKey, map.get(otherKey));
            }
        }
        this.map = Collections.unmodifiableMap(result);
    }

    @NotNull
    public Key getKey(){
        return key;
    }

    @Nullable
    public Object get(@NotNull final String name){
        return map.get(name);
    }

    public boolean isNull(){
        for(String column : key.getColumns()){
            if(map.get(column) != null){
                return false;
            }
        }
        return true;
    }

    public boolean isComplete(){
        for(String column : key.getColumns()){
            if(map.get(column) == null){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }

        KeyMap a = (KeyMap)o;
        return key.equals(a.key)
                && map.equals(a.map);
    }

    @Override
    public int hashCode(){
        return map.hashCode();
    }

    @Override
    public String toString(){
        return map.toString();
    }

    @Override
    public int compareTo(@NotNull final KeyMap a) {
        int c = key.compareTo(a.key);
        if(c != 0){
            return c;
        }

        for(String column : key.getColumns()){
            c = compare(get(column), a.get(column));
            if(c != 0){
                return c;
            }
        }

        return 0;
    }

    @SuppressWarnings("unchecked")
    private static int compare(@Nullable final Object a, @Nullable final Object b){
        //noinspection ObjectEquality
        if(b == a){
            return 0;
        }
        if(a == null){
            return -1;
        }
        if(b == null){
            return 1;
        }
        if(a.equals(b)){
            return 0;
        }
        if(!a.getClass().equals(b.getClass())){
            return a.getClass().getName().compareTo(b.getClass().getName());
        }
        if(a instanceof Comparable){
            return ((Comparable) a).compareTo(b);
        }
        return String.valueOf(a).compareTo(String.valueOf(b));
    }
}
