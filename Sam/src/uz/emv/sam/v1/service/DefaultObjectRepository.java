package uz.emv.sam.v1.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.emv.sam.v1.service.loader.ObjectLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: simbre1
 * Date: 18/10/13
 */
public class DefaultObjectRepository implements ObjectRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultObjectRepository.class);

    private final Map<Class<?>, List<Object>> objects;
    private final ObjectPersistor objectPersistor;
    private final String version;

    public DefaultObjectRepository(@NotNull final ObjectLoader objectLoader,
                                   @NotNull final ObjectPersistor objectPersistor) {
        objects = objectLoader.loadObjects();
        version = objectLoader.getVersion();
        this.objectPersistor = objectPersistor;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    @NotNull
    public Map<Class<?>, List<Object>> getObjects(){
        return objects;
    }

    @Override
    @NotNull
    public <T> List<T> getObjects(@NotNull Class<T> clazz){
        @SuppressWarnings("unchecked") List<T> list = (List<T>)objects.get(clazz);
        return list == null ? Collections.<T>emptyList() : list;
    }

    @Override
    public void persist() throws Exception {
        objectPersistor.persist(getObjects());
    }

    @Override
    public void persist(@NotNull final Object o) throws Exception {
        objectPersistor.persist(o);
    }
}
