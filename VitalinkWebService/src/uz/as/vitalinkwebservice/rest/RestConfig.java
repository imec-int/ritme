package uz.as.vitalinkwebservice.rest;


import uz.as.vitalinkwebservice.rest.v1.CompareMedicatieSchemaService;
import uz.as.vitalinkwebservice.rest.v1.SubstituteMedicationService;
import uz.as.vitalinkwebservice.rest.v1.SumehrRestService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestConfig extends Application {


    @Override
    public Set<Class<?>> getClasses() {


        Set<Class<?>> classes = new HashSet<Class<?>>();

        classes.add(uz.as.vitalinkwebservice.rest.v1.MedicatieSchemaService.class);
        classes.add(uz.as.vitalinkwebservice.rest.v2.MedicatieSchemaService.class);
        classes.add(CompareMedicatieSchemaService.class);
        classes.add(SubstituteMedicationService.class);

        classes.add(SumehrRestService.class);

        return classes;
    }
}
