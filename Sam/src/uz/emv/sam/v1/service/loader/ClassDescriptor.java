package uz.emv.sam.v1.service.loader;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uz.emv.sam.v1.domain.SAMTools;
import uz.emv.sam.v1.service.loader.fielddescriptors.*;
import uz.emv.sam.v1.service.loader.key.Key;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * User: simbre1
 * Date: 18/09/13
 */
public class ClassDescriptor {
    private final Class<?> clazz;

    private String databaseName;

    @Override
    public String toString() {
        return "ClassDescriptor{" +
                "clazz=" + clazz +
                ", databaseName='" + databaseName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", baseTableName='" + baseTableName + '\'' +
                ", columns=" + columns +
                ", primaryKey=" + primaryKey +
                ", uniqueKeys=" + uniqueKeys +
                ", simpleFieldDescriptors=" + simpleFieldDescriptors +
                ", referencingFieldDescriptors=" + referencingFieldDescriptors +
                '}';
    }

    private String tableName;
    private String baseTableName;
    private final Set<String> columns = new TreeSet<String>();

    private Key primaryKey = null;
    private final Set<Key> uniqueKeys = new TreeSet<Key>();

    private final Set<SimpleFieldDescriptor> simpleFieldDescriptors = new HashSet<SimpleFieldDescriptor>();
    private final Set<ReferencingFieldDescriptor> referencingFieldDescriptors = new HashSet<ReferencingFieldDescriptor>();

    public ClassDescriptor(@NotNull final Class<?> clazz){
        this.clazz = clazz;
        parseAnnotation();
    }

    @NotNull
    public Class<?> getClazz(){
        return clazz;
    }

    @Nullable
    public String getDatabaseName(){
        return databaseName;
    }

    @NotNull
    public String getTableName(){
        return tableName;
    }

    @NotNull
    public String getBaseTableName(){
        return baseTableName;
    }

    @Nullable
    public Key getPrimaryKey(){
        return primaryKey;
    }

    @NotNull
    public Set<Key> getUniqueKeys(){
        return uniqueKeys;
    }

    @NotNull
    public Collection<SimpleFieldDescriptor> getSimpleFieldDescriptors(){
        return simpleFieldDescriptors;
    }

    @NotNull
    public Collection<ReferencingFieldDescriptor> getReferencingFieldDescriptors(){
        return referencingFieldDescriptors;
    }

    @NotNull
    public Collection<String> getColumns(){
        return columns;
    }

    @NotNull
    public Collection<FieldDescriptor> getFieldDescriptors(){
        final List<FieldDescriptor> fieldDescriptors = new ArrayList<FieldDescriptor>();
        fieldDescriptors.addAll(simpleFieldDescriptors);
        fieldDescriptors.addAll(referencingFieldDescriptors);
        return fieldDescriptors;
    }

    private void parseAnnotation(){
        if(!clazz.isAnnotationPresent(Table.class)){
            throw new RuntimeException("Missing @Table annotation: "+ clazz.getName());
        }

        handleTableAnnotation(clazz.getAnnotation(Table.class));

        for(Field field : clazz.getDeclaredFields()){
            handleField(field);
        }

        for(FieldDescriptor fd : getFieldDescriptors()){
            columns.addAll(fd.getColumns());
        }
    }

    private void handleTableAnnotation(@NotNull final Table table){
        tableName = table.name();
        baseTableName = SAMTools.toBaseS9TableName(tableName);
        databaseName = table.catalog();

        for(UniqueConstraint unique : table.uniqueConstraints()){
            uniqueKeys.add(new Key(unique.columnNames()));
        }
    }

    private void handleField(@NotNull final Field field){
        if(field.isAnnotationPresent(ManyToOne.class)){
            referencingFieldDescriptors.add(createManyToOneFieldDescriptor(field));
        } else if (field.isAnnotationPresent(Column.class)) {
            simpleFieldDescriptors.add(createColumnFieldDescriptor(field));
        }

        if(field.isAnnotationPresent(Id.class)){
            final String columnName = getColumnName(field);
            if(columnName != null){
                primaryKey = new Key(columnName);
            }else{
                throw new RuntimeException("Missing @Column for @Id: "
                        + field.getType().getName() +"."+ field.getName());
            }
        }
    }

    @NotNull
    private ReferencingFieldDescriptor createManyToOneFieldDescriptor(@NotNull final Field field){
        return new ManyToOneFieldDescriptor(field);
    }


    @NotNull
    private SimpleFieldDescriptor createColumnFieldDescriptor(@NotNull final Field field){
        return new ColumnFieldDescriptor(field);
    }

    @Nullable
    private String getColumnName(@NotNull final Field field){
        if(!field.isAnnotationPresent(Column.class)){
            return null;
        }

        final Column column = field.getAnnotation(Column.class);
        return !StringUtils.isEmpty(column.name()) ? column.name() : field.getName();
    }

    @NotNull
    public Map<String, Object> toMap(@NotNull final ClassDescriptorFactory classDescriptorFactory,
                                     @NotNull final Object o){
        if(!o.getClass().equals(clazz)){
            throw new RuntimeException("Can not map "+ o.getClass().getName() +" to "+ clazz.getName());
        }

        final Map<String, Object> map = new TreeMap<String, Object>();
        for(FieldDescriptor fieldDescriptor : getFieldDescriptors()){
            fieldDescriptor.fillMap(classDescriptorFactory, o, map);
        }

        return map;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }

        ClassDescriptor a = (ClassDescriptor)o;
        return clazz == a.clazz;
    }
    @Override
    public int hashCode(){
        return clazz.hashCode();
    }
}
