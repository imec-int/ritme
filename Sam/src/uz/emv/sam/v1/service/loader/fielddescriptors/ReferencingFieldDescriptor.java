package uz.emv.sam.v1.service.loader.fielddescriptors;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.service.loader.key.ForeignKey;
import uz.emv.sam.v1.service.loader.key.Key;
import uz.emv.sam.v1.service.loader.key.KeyMap;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * User: simbre1
 * Date: 14/11/13
 */
public interface ReferencingFieldDescriptor extends FieldDescriptor {
    public Key getKey();
    public Key getReferencedKey();
    public ForeignKey getForeignKey();
    public Method getCollectionGetter();
    public Method getCollectionSetter();
    public KeyMap createKeyMap(@NotNull Map<String, Object> row);
}
