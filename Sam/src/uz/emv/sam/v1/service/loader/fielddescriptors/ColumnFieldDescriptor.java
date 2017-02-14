package uz.emv.sam.v1.service.loader.fielddescriptors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.emv.sam.v1.JavaTypeConverter;
import uz.emv.sam.v1.service.loader.ClassDescriptorFactory;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * User: simbre1
 * Date: 13/11/13
 */
public class ColumnFieldDescriptor extends AbstractFieldDescriptor implements SimpleFieldDescriptor{
    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnFieldDescriptor.class);

    private final String columnName;
    private final EnumType enumType;

    public ColumnFieldDescriptor(@NotNull final Field field){
        super(field);
        final Enumerated enumerated = field.getAnnotation(Enumerated.class);
        if (enumerated != null) {
            enumType = enumerated.value();
        } else {
            enumType = null;
        }
        final Column column = field.getAnnotation(Column.class);
        if (column != null && !StringUtils.isEmpty(column.name())) {
            columnName = column.name();
        }else{
            columnName = field.getName();
        }
    }

    @Override
    public void invokeSetter(@NotNull final Object instance, @NotNull final Map<String, Object> row) {
        final Method setter = getSetter();
        if(setter != null){
            if (enumType != null) {
                switch (enumType) {
                    case ORDINAL:
                        final Integer integer = (Integer) row.get(columnName);
                        if (setter.getParameterTypes()[0].isEnum()) {
                            try {
                                //noinspection unchecked
                                setter.invoke(instance, integer == null ? null : ((Class<Enum>) setter.getParameterTypes()[0]).getEnumConstants()[integer]);
                            } catch (Exception e) {
                                LOGGER.error("Could not invoke setter: {}.{}(\"{}\")",
                                        setter.getDeclaringClass().getSimpleName(),
                                        setter.getName(),
                                        integer, e);
                            }
                        } else {
                            LOGGER.error("Could not invoke setter: {}.{}(\"{}\")",
                                    setter.getDeclaringClass().getSimpleName(),
                                    setter.getName(),
                                    integer);
                        }

                        break;
                    case STRING:

                        final String value = (String) row.get(columnName);
                        if (setter.getParameterTypes()[0].isEnum()) {
                            @SuppressWarnings("unchecked")
                            final Class<Enum> type = (Class<Enum>) setter.getParameterTypes()[0];
                            try {
                                setter.invoke(instance, value == null ? null : Enum.valueOf(type, value));
                            } catch (Exception e) {
                                LOGGER.error("Could not invoke setter: {}.{}(\"{}\")",
                                        setter.getDeclaringClass().getSimpleName(),
                                        setter.getName(),
                                        value, e);
                            }
                        } else {
                            LOGGER.error("Could not invoke setter: {}.{}(\"{}\")",
                                    setter.getDeclaringClass().getSimpleName(),
                                    setter.getName(),
                                    value);
                        }

                        break;
                }
            } else {
                final Object value = row.get(columnName);
                final Class<?> type = getField().getType();
                try {
                    final Object o = JavaTypeConverter.convertValue(type, value);
                    // don't pass null to a primitive
                    // no error thrown, we assume null means no change to the default value
                    if (o != null || !type.isPrimitive()) {
                        setter.invoke(instance, o);
                    }
                } catch (Exception e) {
                    LOGGER.error("Could not invoke setter: {}.{}(\"{}\")",
                            setter.getDeclaringClass().getSimpleName(),
                            setter.getName(),
                            value,
                            e);
                }
            }
        }
    }

    @Override
    public void fillMap(@NotNull final ClassDescriptorFactory classDescriptorFactory,
                        @NotNull final Object instance,
                        @NotNull final Map<String, Object> map) {
        final Method getter = getGetter();
        if(getter != null){
            try{
                final Object o = getter.invoke(instance);
                map.put(columnName, o);
            }catch(Exception e){
                LOGGER.error("Could not invoke getter: {}.{}",
                        getter.getDeclaringClass().getSimpleName(),
                        getter.getName(),
                        e);
            }
        }
    }

    @Override
    @NotNull
    public Collection<String> getColumns() {
        return Collections.singleton(columnName);
    }

    @NotNull
    public String getColumnName(){
        return columnName;
    }
}
