package uz.as.recipewebservice.rest;


import uz.as.recipewebservice.rest.v1.PrescriberService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestConfig extends Application {


    @Override
    public Set<Class<?>> getClasses() {


        Set<Class<?>> classes = new HashSet<Class<?>>();

        classes.add(PrescriberService.class);


        return classes;
    }
}
