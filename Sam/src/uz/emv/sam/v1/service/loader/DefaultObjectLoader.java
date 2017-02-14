package uz.emv.sam.v1.service.loader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.emv.sam.v1.db.tables.Table;
import uz.emv.sam.v1.db.tables.TableFactory;
import uz.emv.sam.v1.service.loader.fielddescriptors.ReferencingFieldDescriptor;
import uz.emv.sam.v1.service.loader.fielddescriptors.SimpleFieldDescriptor;
import uz.emv.sam.v1.service.loader.key.Key;
import uz.emv.sam.v1.service.loader.key.KeyMap;

import java.lang.reflect.Method;
import java.util.*;

/**
 * User: simbre1
 * Date: 14/10/13
 */
public class DefaultObjectLoader implements ObjectLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultObjectLoader.class);

    private final Class<?>[] classes;
    private final TableFactory tableFactory;

    public DefaultObjectLoader(@NotNull final Class<?>[] classes,
                               @NotNull final TableFactory tableFactory){
        this.classes = classes;
        this.tableFactory = tableFactory;
    }

    @Override
    @NotNull
    public Map<Class<?>, List<Object>> loadObjects(){
        final Map<Class<?>, CreationResult> creationResults = new HashMap<Class<?>, CreationResult>();
        try{
            for(Class<?> clazz : classes){
                final ClassDescriptor cd = new ClassDescriptor(clazz);
                final Table table = tableFactory.getTable(cd.getBaseTableName());
                if(table != null){
                    creationResults.put(clazz, createObjects(cd, table));
                    table.cleanUp();
                }
            }
            resolveObjectDependencies(creationResults);

            return createResult(creationResults);
        }catch(Exception e){
            LOGGER.error("Could not load objects", e);
            return Collections.emptyMap();
        }
    }

    @Override
    public String getVersion() {
        return tableFactory.getVersion();
    }

    @NotNull
    private Map<Class<?>, List<Object>> createResult(@NotNull final Map<Class<?>, CreationResult> creationResults){
        final Map<Class<?>, List<Object>> result = new HashMap<Class<?>, List<Object>>();

        for(Map.Entry<Class<?>,CreationResult> e : creationResults.entrySet()){
            Collection<Object> collection = e.getValue().getInstances().values();
            result.put(e.getKey(), new ArrayList<Object>(new TreeSet<Object>(collection)));
        }

        return result;
    }

    @NotNull
    private Object instantiateClass(@NotNull final ClassDescriptor cd) throws IllegalAccessException, InstantiationException {
        return cd.getClazz().newInstance();
    }

    @NotNull
    private CreationResult createObjects(@NotNull final ClassDescriptor cd, @NotNull final Table table){
        final Map<KeyMap, Object> instances = new TreeMap<KeyMap, Object>();
        final Map<ReferencingFieldDescriptor, List<ObjectDependency>> dependencies
                = new TreeMap<ReferencingFieldDescriptor, List<ObjectDependency>>();

        for(Map<String, Object> row : table){
            try{
                final Object instance = instantiateClass(cd);

                if(cd.getPrimaryKey() != null && cd.getUniqueKeys().isEmpty()){
                    final KeyMap keyMap = new KeyMap(cd.getPrimaryKey(), row);
                    instances.put(keyMap, instance);
                }else if(!cd.getUniqueKeys().isEmpty()){
                    for (Key key : cd.getUniqueKeys()) {
                        final KeyMap keyMap = new KeyMap(key, row);
                        if (!keyMap.isNull()) {
                            instances.put(keyMap, instance);
                        }
                    }
                }

                for(SimpleFieldDescriptor sfd : cd.getSimpleFieldDescriptors()){
                    sfd.invokeSetter(instance, row);
                }

                for(ReferencingFieldDescriptor rfd : cd.getReferencingFieldDescriptors()){
                    List<ObjectDependency> fkeyDeps = dependencies.get(rfd);
                    if(fkeyDeps == null){
                        fkeyDeps = new LinkedList<ObjectDependency>();
                        dependencies.put(rfd, fkeyDeps);
                    }
                    fkeyDeps.add(new ObjectDependency(instance, rfd.createKeyMap(row)));
                }
            }catch(Exception e){
                LOGGER.error("Could not create object of type {} from row: {}",
                        cd.getClazz().getName(),
                        row.toString(),
                        e);
            }
        }

        return new CreationResult(instances, dependencies);
    }

    private void resolveObjectDependencies(@NotNull final Map<Class<?>, CreationResult> creationResults){
        for (Map.Entry<Class<?>, CreationResult> me : creationResults.entrySet()) {
            for (Map.Entry<ReferencingFieldDescriptor, List<ObjectDependency>> fod : me.getValue().getDependencies().entrySet()) {
                final ReferencingFieldDescriptor fkey = fod.getKey();

                for(ObjectDependency od : fod.getValue()){
                    resolveObjectDependency(creationResults, fkey, od);
                }
            }
        }
        // separate iteration because adding some collection types invokes the compare method which could rely on a dependency
        for(Map.Entry<Class<?>, CreationResult> me : creationResults.entrySet()){
            for(Map.Entry<ReferencingFieldDescriptor, List<ObjectDependency>> fod : me.getValue().getDependencies().entrySet()){
                final ReferencingFieldDescriptor fkey = fod.getKey();

                for(ObjectDependency od : fod.getValue()){
                    addToSupplierCollection(creationResults, fkey, od);
                }
            }
        }
    }

    private void resolveObjectDependency(@NotNull final Map<Class<?>, CreationResult> creationResults,
                                         @NotNull final ReferencingFieldDescriptor fkey,
                                         @NotNull final ObjectDependency od){
        final Method setter = fkey.getSetter();
        if(setter != null){
            final Object supplierObject = getObject(creationResults, setter.getParameterTypes()[0], od.getForeignKeyMap());
            final Object dependentObject = od.getDependentObject();

            try {
                setter.invoke(dependentObject, supplierObject);
            } catch (Exception e) {
                LOGGER.error("Could not resolve dependency: {}.{} \"{}\" \"{}\"",
                        setter.getDeclaringClass().getName(),
                        setter.getName(),
                        dependentObject,
                        supplierObject,
                        e);
            }
        }
    }

    //adding to a collection through reflection, no type information present
    @SuppressWarnings("unchecked")
    private void addToSupplierCollection(@NotNull final Map<Class<?>, CreationResult> creationResults,
                                         @NotNull final ReferencingFieldDescriptor fkey,
                                         @NotNull final ObjectDependency od){
        final Method getCollection = fkey.getCollectionGetter();
        if(getCollection == null){
            return;
        }

        final Method setCollection = fkey.getCollectionSetter();

        // to reduce memory footprint, (try to) change the Set implementation according to its size:
        // == 0: Collections.emptySet
        // == 1: Collections.singleTon
        // > 1: TreeSet
        final Method setter = fkey.getSetter();
        if(setter != null){
            final Object supplierObject = getObject(creationResults, setter.getParameterTypes()[0], od.getForeignKeyMap());
            if(supplierObject != null){
                try {
                    final Set collection = (Set)getCollection.invoke(supplierObject);
                    if (collection == null || (collection.isEmpty() && setCollection != null)) {
                        if (setCollection == null) {
                            LOGGER.warn("Null Set: {}.{}: \"{}\"",
                                    supplierObject.getClass().getSimpleName(),
                                    getCollection.getName(),
                                    supplierObject);
                        } else {
                            setCollection.invoke(supplierObject, Collections.singleton(od.getDependentObject()));
                        }
                    } else if(collection.contains(od.getDependentObject())){
                        LOGGER.warn("Duplicate: {}.{}: {} add \"{}\"",
                                supplierObject.getClass().getSimpleName(),
                                getCollection.getName(),
                                supplierObject,
                                od.getDependentObject());
                    } else if (collection.size() < 8 && setCollection != null) {
                        Set set = new ArraySet(collection.toArray(), od.getDependentObject());
                        setCollection.invoke(supplierObject, set);
                    } else if (collection.size() == 8 && setCollection != null) {
                        Set set = new TreeSet(collection);
                        set.add(od.getDependentObject());
                        setCollection.invoke(supplierObject, set);
                    } else {
                        collection.add(od.getDependentObject());
                    }
                } catch (Exception e) {
                    LOGGER.error("Could not add object \"{}\" to collection \"{}\"",
                            od.getDependentObject(),
                            supplierObject,
                            e);
                }
            }
        }
    }

    private static <T> Set<T> arraySet(T[] initialElements, T extraElement) {
        return new ArraySet<T>(initialElements, extraElement);
    }

    private static class ArraySet<T> extends AbstractSet<T> {
        private final T[] elements;

        ArraySet(T[] initialElements, T extraElement) {
            for (T element : initialElements) {
                if (eq(element, extraElement)) {
                    // extraElement reeds in set -> gewoon initiele set kopieren
                    elements = Arrays.copyOf(initialElements, initialElements.length);
                    return;
                }
            }

            elements = Arrays.copyOf(initialElements, initialElements.length + 1);
            elements[initialElements.length] = extraElement;
        }

        @Override
        @NotNull
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private int idx = 0;

                @Override
                public boolean hasNext() {
                    return idx < elements.length;
                }

                @Override
                public T next() {
                    if (hasNext()) {
                        return elements[idx++];
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public int size() {
            return elements.length;
        }
    }

    /**
     * Returns true if the specified arguments are equal, or both null.
     */
    private static boolean eq(Object o1, Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }

    @Nullable
    private Object getObject(@NotNull final Map<Class<?>, CreationResult> creationResults,
                             @NotNull final Class<?> clazz,
                             @NotNull final KeyMap keyMap){
        final CreationResult creationResult = creationResults.get(clazz);
        if(creationResult != null){
            return creationResult.getInstances().get(keyMap);
        }
        return null;
    }

    private static class CreationResult{
        private final Map<KeyMap, Object> instances;
        private final Map<ReferencingFieldDescriptor, List<ObjectDependency>> dependencies;

        public CreationResult(@NotNull final Map<KeyMap, Object> instances,
                              @NotNull final Map<ReferencingFieldDescriptor, List<ObjectDependency>> depencencies){
            this.instances = instances;
            this.dependencies = depencencies;
        }

        @NotNull
        public Map<KeyMap, Object> getInstances(){
            return instances;
        }

        @NotNull
        public Map<ReferencingFieldDescriptor, List<ObjectDependency>> getDependencies(){
            return dependencies;
        }
    }
}
