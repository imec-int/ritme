package uz.emv.sam.v1.service.loader.fielddescriptors;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * User: simbre1
 * Date: 14/11/13
 */
public interface SimpleFieldDescriptor extends FieldDescriptor{
    public void invokeSetter(@NotNull Object instance, @NotNull Map<String, Object> row);
}
