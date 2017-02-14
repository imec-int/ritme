package uz.as.vitalinkwebservice.rest.v1;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.model.Medication;
import uz.ehealth.ritme.model.MedicationIdType;
import uz.ehealth.ritme.plugins.PluginManager;
import uz.ehealth.ritme.substitution.SubstitutionService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by bdcuyp0 on 16-11-2015.
 */
@Path("/v1/substitute-medication")
public class SubstituteMedicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubstituteMedicationService.class);


    @POST
    @Path("/JSON/{orgNihii}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Nullable
    public String getJSON(
            @PathParam("orgNihii") final String orgNihii,
            @QueryParam("formularium") final Boolean formularium,
            @QueryParam("type") final String type,
            @QueryParam("level") final Integer level,
            final String data,
            @Context final HttpServletRequest request) {
        Medication input = JSONTools.unmarshal(data, Medication.class);
        LOGGER.debug(data);
        SubstitutionService service = PluginManager.get("ritme.substitute-medication", SubstitutionService.class);
        final String output = JSONTools.marshal(service.substitute(request.getRemoteUser(), orgNihii, input, (formularium == null ? false : formularium), (type == null ? null : MedicationIdType.valueOf(type)), (level == null ? 0 : level)));
        LOGGER.debug(output);
        return output;
    }


}
