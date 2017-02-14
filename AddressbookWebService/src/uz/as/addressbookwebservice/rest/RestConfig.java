/**
 * Created by bdcuyp0 on 8-7-2016.
 */
package uz.as.addressbookwebservice.rest;


import uz.as.addressbookwebservice.rest.v1.CareInstitutionService;
import uz.as.addressbookwebservice.rest.v1.CareTakerService;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestConfig extends Application {


    @Override
    public Set<Class<?>> getClasses() {


        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(CareTakerService.class);
        classes.add(CareInstitutionService.class);


        return classes;
    }
}
