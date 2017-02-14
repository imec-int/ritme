package uz.ehealth.ritme.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.ConfigHelper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by bdcuyp0 on 24-6-2015.
 */

public class PluginManager {
    private static final Properties PROPERTIES;
    private static final Logger LOG = LoggerFactory.getLogger(PluginManager.class);
    private static final Map<String, Object> CACHE = new HashMap<String, Object>();

    static {
        PROPERTIES = new Properties();
        try {
            PROPERTIES.load(PluginManager.class.getResourceAsStream(ConfigHelper.getConfigLocation("ritme-plugins.properties")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T get(String property, Class<T> clazz) {
        Object retVal = CACHE.get(property);
        if (retVal == null) {
            String resolvedProperty = PROPERTIES.getProperty(property);
            try {
                retVal = clazz.getClass().forName(resolvedProperty).newInstance();
                CACHE.put(property, retVal);
            } catch (InstantiationException e) {
                LOG.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                LOG.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                LOG.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return (T) retVal;
    }

    public static <T, C extends T> C get(String property, Class<T> clazz, Object... objects) {
        String resolvedProperty = PROPERTIES.getProperty(property);
        T instance = null;
        try {
            final Constructor<?>[] constructors = clazz.forName(resolvedProperty).getConstructors();
            for (Constructor constructor : constructors) {
                try {
                    instance = (C) constructor.newInstance(objects);
                    return (C) instance;
                } catch (InvocationTargetException e) {
                    //do nothing
                } catch (InstantiationException e) {
                    //do nothing
                } catch (IllegalAccessException e) {
                    //do nothing
                }
            }


        } catch (ClassNotFoundException e) {
            //do nothing
        }
        return (C) instance;
    }
}
