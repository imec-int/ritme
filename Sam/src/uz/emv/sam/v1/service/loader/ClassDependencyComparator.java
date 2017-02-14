package uz.emv.sam.v1.service.loader;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * User: simbre1
 * Date: 12/11/13
 */
public class ClassDependencyComparator implements Comparator<Class<?>> {
    /**
     * Compare two classes on their dependency.
     * The dependency is defined by the declared fields of a class.
     * @param a
     * @param b
     * @return <ul>
     *     <li>a equals b: 0</li>
     *     <li>a depends on b: 1</li>
     *     <li>b depends on a: -1</li>
     *     <li>a depends on b and b depends on a: a.getName().compareTo(b.getName)</li>
     *     <li>no dependencies between a and b: a.getName().compareTo(b.getName)</li>
     *     </ul>
     */
    @Override
    public int compare(Class<?> a, Class<?> b) {
        if(a.equals(b)){
            return 0;
        }

        final boolean ab = dependsOn(a, b);
        final boolean ba = dependsOn(b, a);

        if(ab && ba){
            return a.getName().compareTo(b.getName());
        }
        if(!ab && !ba){
            return a.getName().compareTo(b.getName());
        }
        if(ab){
            return 1;
        }else{
            return -1;
        }
    }

    /**
     * Check if a depends on b
     * @param a
     * @param b
     * @return true if any of a's declared fields or constructor parameters depend on b
     */
    public static boolean dependsOn(@NotNull final Class<?> a, @NotNull final Class<?> b){
        return dependsOn(a, b, new HashSet<Class<?>>());
    }

    private static boolean dependsOn(@NotNull final Class<?> a,
                                     @NotNull final Class<?> b,
                                     @NotNull final Set<Class<?>> checkedClasses){
        final Set<Class<?>> aDeps = getClassDependencies(a);

        if(aDeps.contains(b)){
            return true;
        }

        for(Class<?> aDep : aDeps){
            if(checkedClasses.add(aDep) && !ignoreClass(aDep)){
                if(dependsOn(aDep, b, checkedClasses)){
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean ignoreClass(@NotNull final Class<?> a){
        return a.isPrimitive()
                || Object.class.equals(a)
                || String.class.equals(a);

    }

    @NotNull
    private static Set<Class<?>> getClassDependencies(@NotNull final Class<?> a){
        Set<Class<?>> deps = new HashSet<Class<?>>();
        for(Field field : a.getDeclaredFields()){
            deps.add(field.getType());
        }
        return deps;
    }
}
