package uz.emv.sam.v1.service;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * User: simbre1
 * Date: 21/11/13
 */
public interface ObjectRepository {
    String getVersion();

    @NotNull
    public Map<Class<?>, List<Object>> getObjects();

    @NotNull
    public <T> List<T> getObjects(@NotNull Class<T> clazz);

    public void persist() throws Exception;

    public void persist(@NotNull Object o) throws Exception;
}
