package uz.emv.sam.v1.service.loader;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.service.loader.key.KeyMap;

/**
 * Represents a foreign-key dependency instance between annotated classes.
 * User: simbre1
 * Date: 20/09/13
 */
public class ObjectDependency {
    private final Object dependentObject;
    private final KeyMap foreignKeyMap;

    public ObjectDependency(@NotNull final Object dependentObject, @NotNull final KeyMap foreignKeyMap){
        this.dependentObject = dependentObject;
        this.foreignKeyMap = foreignKeyMap;
    }

    @NotNull
    public Object getDependentObject(){
        return dependentObject;
    }

    @NotNull
    public KeyMap getForeignKeyMap(){
        return foreignKeyMap;
    }
}
