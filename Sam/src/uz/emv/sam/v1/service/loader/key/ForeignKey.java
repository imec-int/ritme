package uz.emv.sam.v1.service.loader.key;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

/**
 * User: simbre1
 * Date: 28/11/13
 */
public class ForeignKey {
    private final Map<String, String> map;
    private final Key key;
    private final Key referencedKey;

    public ForeignKey(@NotNull final Map<String, String> keyRefKeyMap){
        map = new TreeMap<String, String>(keyRefKeyMap);
        key = new Key(keyRefKeyMap.keySet());
        referencedKey = new Key(keyRefKeyMap.values());
    }

    @NotNull
    public Key getKey(){
        return key;
    }

    @NotNull
    public Key getReferencedKey(){
        return referencedKey;
    }

    @NotNull
    public Map<String, String> getMap(){
        return map;
    }
}
