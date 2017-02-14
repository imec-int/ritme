package uz.ehealth.ritme.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by bdcuyp0 on 14-3-2016.
 */

public class ContextSpecificClassLoader extends ClassLoader {
    private static final Properties PROPERTIES;
    private static final Logger LOG = LoggerFactory.getLogger(ContextSpecificClassLoader.class);

    static {
        PROPERTIES = new Properties();
        try {
            PROPERTIES.load(ContextSpecificClassLoader.class.getResourceAsStream("/ritme-classloader.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final String context;
    private final String classPackage;

    public ContextSpecificClassLoader(final String context, final String classPackage) {
        this.context = context;
        this.classPackage = classPackage;
    }

    /**
     * Override the loadClass method and delegate the responsibility to load the
     * class to it's super class. If the parent class loader is not able to load
     * the class then this class loader shall try to load the class by itself.
     */
    @Override
    public Class<?> loadClass(final String name) {

        Class<?> clazz = null;
        if (name.startsWith(classPackage)) {
            try {
                final byte[] clazzByte = loadClassByte(name);
                clazz = defineClass(name, clazzByte, 0, clazzByte.length);
                resolveClass(clazz);
            } catch (final IOException e) {
                //this means the given class is not found the custom location
                LOG.error(e.getMessage(), e);
            }

        } else {

            try {
                clazz = super.loadClass(name);
            } catch (final ClassNotFoundException e) {
                //This means the parent class is not able to find the class.
                //not in <JAVA_HOME>/lib, <JAVA_HOME>/lib/ext, -cp, -classpath
                //try to load the class using our own custom class loader
                LOG.error(e.getMessage(), e);
            }

        }

        return clazz;
    }

    /**
     * This method shall take the class name and load the class file.
     *
     * @param name
     * @return
     * @throws IOException
     */
    private byte[] loadClassByte(final String name) throws IOException {

        byte[] data = null;

        String jarFile = PROPERTIES.getProperty(context + "." + classPackage);
        // convert the give class name to file format like
        // /home/thosan/java/clazzez/sanju/org/corejava/util/ClassDoesNotExistInClassPath

        InputStream stream = new FileInputStream(jarFile);

        ZipInputStream zipStream = new ZipInputStream(stream);
        ZipEntry entry = zipStream.getNextEntry();

        while (entry != null) {

            if (name.endsWith(entry.getName())) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[2048];

                try {

                    int len = 0;
                    while ((len = zipStream.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                } finally {
                    // we must always close the output file
                    baos.close();
                }

                data = baos.toByteArray();
                return data;
            }

            entry = zipStream.getNextEntry();

        }

        return null;

    }

}