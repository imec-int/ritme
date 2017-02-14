package uz.ehealth.ritme.core;

import org.jetbrains.annotations.NotNull;

/**
 * Created by bdcuyp0 on 9-2-2017.
 */
public class ConfigHelper {
    @NotNull
    public static String getConfigLocation(final String classpathLocation) {
        return System.getProperty(classpathLocation + ".location", "/" + classpathLocation);
    }
}
