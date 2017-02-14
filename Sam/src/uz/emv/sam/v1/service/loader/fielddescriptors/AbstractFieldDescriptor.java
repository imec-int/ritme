package uz.emv.sam.v1.service.loader.fielddescriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: simbre1
 * Date: 13/11/13
 */
public abstract class AbstractFieldDescriptor implements FieldDescriptor{
    private final Field field;
    private final Method setter;
    private final Method getter;

    public AbstractFieldDescriptor(@NotNull final Field field){
        this.field = field;
        this.setter = findSetter(field);
        this.getter = findGetter(field);
    }

    @Override
    @NotNull
    public Field getField() {
        return field;
    }

    @Override
    @Nullable
    public Method getSetter(){
        return setter;
    }

    @Override
    @Nullable
    public Method getGetter(){
        return getter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AbstractFieldDescriptor that = (AbstractFieldDescriptor) o;
        return this.field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return field.hashCode();
    }

    @Override
    public int compareTo(@NotNull final FieldDescriptor a){
        final Field af = a.getField();
        int c = field.getDeclaringClass().getName().compareTo(af.getDeclaringClass().getName());
        return c != 0 ? c : field.getName().compareTo(a.getField().getName());
    }

    @Nullable
    public static Method findSetter(@NotNull final Field field){
        final Method m = findMethodByName(field.getDeclaringClass(), "set" + field.getName());
        if(m != null && m.getParameterTypes().length == 1
                && field.getType().isAssignableFrom(m.getParameterTypes()[0])){
            return m;
        }
        return null;
    }

    @Nullable
    public static Method findGetter(@NotNull final Field field){
        final Method m = findMethodByName(field.getDeclaringClass(), "get"+ field.getName());
        if(m != null && m.getParameterTypes().length == 0
                    && m.getReturnType().isAssignableFrom(field.getType())){
            return m;
        }
        return null;
    }

    @Nullable
    public static Method findMethodByName(@NotNull final Class<?> c, @NotNull final String methodName){
        final String lcName = methodName.toLowerCase();
        for(Method m : c.getDeclaredMethods()){
            if(m.getName().toLowerCase().equals(lcName)){
                return m;
            }
        }
        return null;
    }
}
