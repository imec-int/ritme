package uz.emv.sam.v1.db.tables.xml;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.service.ObjectPersistor;

import java.util.List;
import java.util.Map;

/**
 * Created by bdcuyp0 on 22-4-2016.
 */
public class XMLObjectPersistor implements ObjectPersistor {
    @Override
    public void persist(@NotNull final Map<Class<?>, List<Object>> objects) throws Exception {
    }

    @Override
    public void persist(@NotNull final Object o) throws Exception {
    }
}
