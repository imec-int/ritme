package uz.emv.sam.v1.service.loader;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * User: simbre1
 * Date: 21/11/13
 */
public interface ObjectLoader {
    @NotNull
    public Map<Class<?>, List<Object>> loadObjects();

    String getVersion();
}
