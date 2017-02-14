package uz.emv.sam.v1.service.loader;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ClassDescriptorFactory {
    private final Map<Class<?>, ClassDescriptor> cache = new HashMap<Class<?>, ClassDescriptor>();

    public ClassDescriptorFactory() {
    }

    @NotNull
    public ClassDescriptor get(@NotNull final Class<?> clazz) {
        ClassDescriptor descriptor = cache.get(clazz);
        if (descriptor == null) {
            descriptor = new ClassDescriptor(clazz);
            cache.put(clazz, descriptor);
        }
        return descriptor;
    }

    public void clear() {
        cache.clear();
    }
}
