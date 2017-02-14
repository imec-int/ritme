package uz.emv.sam.v1.service.loader.fielddescriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uz.emv.sam.v1.service.loader.ClassDescriptorFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * User: simbre1
 * Date: 13/11/13
 */
public interface FieldDescriptor extends Comparable<FieldDescriptor> {
    @NotNull
    public Field getField();

    @Nullable
    public Method getSetter();

    @Nullable
    public Method getGetter();

    @NotNull
    public Collection<String> getColumns();

    public void fillMap(@NotNull ClassDescriptorFactory classDescriptorFactory,
                        @NotNull Object instance,
                        @NotNull Map<String, Object> map);
}
