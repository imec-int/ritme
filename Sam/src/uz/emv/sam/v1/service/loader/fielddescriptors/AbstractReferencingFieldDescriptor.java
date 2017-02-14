package uz.emv.sam.v1.service.loader.fielddescriptors;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.service.loader.key.Key;
import uz.emv.sam.v1.service.loader.key.KeyMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * User: simbre1
 * Date: 14/11/13
 */
public abstract class AbstractReferencingFieldDescriptor extends AbstractFieldDescriptor implements ReferencingFieldDescriptor {

    public AbstractReferencingFieldDescriptor(@NotNull Field field) {
        super(field);
    }

    @Override
    @NotNull
    public Key getKey(){
        return getForeignKey().getKey();
    }

    @Override
    @NotNull
    public Key getReferencedKey(){
        return getForeignKey().getReferencedKey();
    }

    @Override
    @NotNull
    public KeyMap createKeyMap(@NotNull final Map<String, Object> row){
        Map<String, Object> map = new HashMap<String, Object>();
        for(Map.Entry<String, String> e : getForeignKey().getMap().entrySet()){
            map.put(e.getValue(), row.get(e.getKey()));
        }
        return new KeyMap(getReferencedKey(), map);
    }
}
