package uz.emv.sam.v1.service.loader.fielddescriptors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.emv.sam.v1.service.loader.ClassDescriptor;
import uz.emv.sam.v1.service.loader.ClassDescriptorFactory;
import uz.emv.sam.v1.service.loader.key.ForeignKey;

import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * User: simbre1
 * Date: 13/11/13
 */
public class ManyToOneFieldDescriptor extends AbstractReferencingFieldDescriptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManyToOneFieldDescriptor.class);

    private final ForeignKey foreignKey;
    private final Method collectionGetter;
    private final Method collectionSetter;

    public ManyToOneFieldDescriptor(@NotNull final Field field){
        super(field);

        final Pair<Method, Method> collectionGetterAndSetter = findCollectionMappedByFieldGetterAndSetter(field);
        this.collectionGetter = collectionGetterAndSetter.getLeft();
        this.collectionSetter = collectionGetterAndSetter.getRight();

        final ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        if(manyToOne != null){
            if(field.isAnnotationPresent(JoinColumns.class)){
                final JoinColumns joinColumns = field.getAnnotation(JoinColumns.class);
                final Map<String, String> columnNamesMap = new TreeMap<String, String>();

                for(JoinColumn joinColumn : joinColumns.value()){
                    final String referencedColumnName = !StringUtils.isEmpty(joinColumn.referencedColumnName()) ?
                            joinColumn.referencedColumnName()
                            : joinColumn.name();
                    columnNamesMap.put(joinColumn.name(), referencedColumnName);
                }

                foreignKey = new ForeignKey(columnNamesMap);
            }
            else if(field.isAnnotationPresent(JoinColumn.class)){
                final JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                final String referencedColumnName = !StringUtils.isEmpty(joinColumn.referencedColumnName()) ?
                            joinColumn.referencedColumnName()
                            : joinColumn.name();
                final Map<String, String> result = new HashMap<String, String>();
                result.put(joinColumn.name(), referencedColumnName);
                foreignKey = new ForeignKey(result);
            }else{
                throw new RuntimeException("Missing @JoinColumn(s) for field: "+
                        field.getDeclaringClass().getName() +"."+ field.getName());
            }
        }else{
            throw new RuntimeException("Missing @ManyToOne for field: "+
                    field.getDeclaringClass().getName() +"."+ field.getName());
        }
    }

    @Override
    @NotNull
    public ForeignKey getForeignKey() {
        return foreignKey;
    }

    @Override
    @Nullable
    public Method getCollectionGetter() {
        return collectionGetter;
    }

    @Override
    public Method getCollectionSetter() {
        return collectionSetter;
    }

    @Override
    public void fillMap(@NotNull final ClassDescriptorFactory classDescriptorFactory,
                        @NotNull final Object instance,
                        @NotNull final Map<String, Object> map) {
        final Method getter = getGetter();
        if(getter != null){
            try{
                final Object o = getter.invoke(instance);
                if(o != null){
                    final ClassDescriptor referencedCd = classDescriptorFactory.get(getField().getType());
                    final Map<String, Object> referencedMap = referencedCd.toMap(classDescriptorFactory, o);
                    for(Map.Entry<String, String> keyToRefKey : foreignKey.getMap().entrySet()){
                        final Object value = referencedMap.get(keyToRefKey.getValue());
                        map.put(keyToRefKey.getKey(), value);
                    }
                }
            } catch(Exception e) {
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
        return foreignKey.getKey().getColumns();
    }

    /**
     * Gets the getter which returns the collection mappedBy this field in a OneToMany relationship
     * @param field field referred to in a OneToMany mappedBy-attribute
     * @return getter of OneToMany-collection or null
     */
    @NotNull
    public static Pair<Method, Method> findCollectionMappedByFieldGetterAndSetter(Field field) {
        final Field collection = findOneToManyFieldMappedByThisField(field);
        if(collection != null){
            if(collection.getType().isAssignableFrom(Set.class)){
                final Method getter = AbstractFieldDescriptor.findGetter(collection);
                final Method setter = AbstractFieldDescriptor.findSetter(collection);
                return new ImmutablePair<Method, Method>(getter, setter);
            }else{
                LOGGER.error("Must be of type Set<{}>: {}.{}",
                        field.getType().getSimpleName(),
                        collection.getDeclaringClass().getSimpleName(),
                        collection.getName());
            }
        }

        return new ImmutablePair<Method, Method>(null, null);
    }

    @Nullable
    public static Field findOneToManyFieldMappedByThisField(Field thisField){
        for(Field thatField : thisField.getType().getDeclaredFields()){
            if(thatField.isAnnotationPresent(OneToMany.class)){
                final OneToMany oneToMany = thatField.getAnnotation(OneToMany.class);
                if(oneToMany.targetEntity() != void.class){
                    if(!oneToMany.targetEntity().equals(thisField.getDeclaringClass())){
                        continue;
                    }
                }else{
                    LOGGER.warn("Missing targetEntity: {}.{}",
                            thatField.getDeclaringClass().getSimpleName(),
                            thatField.getName());
                }

                if (StringUtils.isEmpty(oneToMany.mappedBy())) {
                    LOGGER.warn("Missing mappedBy: {}.{}",
                            thatField.getDeclaringClass().getSimpleName(),
                            thatField.getName());
                    return thatField;
                }

                if(oneToMany.mappedBy().equals(thisField.getName())){
                    return thatField;
                }
            }
        }

        return null;
    }
}
