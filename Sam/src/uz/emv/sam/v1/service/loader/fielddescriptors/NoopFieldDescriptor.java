package uz.emv.sam.v1.service.loader.fielddescriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uz.emv.sam.v1.service.loader.ClassDescriptorFactory;
import uz.emv.sam.v1.service.loader.key.ForeignKey;
import uz.emv.sam.v1.service.loader.key.Key;
import uz.emv.sam.v1.service.loader.key.KeyMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * User: simbre1
 * Date: 13/11/13
 */
public class NoopFieldDescriptor extends AbstractFieldDescriptor implements SimpleFieldDescriptor, ReferencingFieldDescriptor {

    public NoopFieldDescriptor(@NotNull final Field field){
        super(field);
    }

    @Override
    @Nullable
    public Key getKey() {
        return null;
    }

    @Override
    @Nullable
    public Key getReferencedKey() {
        return null;
    }

    @Override
    public ForeignKey getForeignKey() {
        return null;
    }

    @Override
    @Nullable
    public Method getCollectionGetter() {
        return null;
    }

    @Override
    @Nullable
    public Method getCollectionSetter() {
        return null;
    }

    @Override
    @Nullable
    public KeyMap createKeyMap(@NotNull final Map<String, Object> row) {
        return null;
    }

    @Override
    public void invokeSetter(@NotNull final Object instance, @NotNull final Map<String, Object> row) {
        //noop
    }

    @Override
    public void fillMap(@NotNull final ClassDescriptorFactory classDescriptorFactory,
                        @NotNull final Object instance,
                        @NotNull final Map<String, Object> map) {
        //noop
    }

    @Override
    @NotNull
    public Collection<String> getColumns() {
        return Collections.emptyList();
    }
}
