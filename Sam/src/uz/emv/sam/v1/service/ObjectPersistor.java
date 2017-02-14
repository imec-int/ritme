package uz.emv.sam.v1.service;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created by bdcuyp0 on 22-4-2016.
 */
public interface ObjectPersistor {
    void persist(@NotNull Map<Class<?>, List<Object>> objects) throws Exception;

    void persist(@NotNull Object o) throws Exception;
}
