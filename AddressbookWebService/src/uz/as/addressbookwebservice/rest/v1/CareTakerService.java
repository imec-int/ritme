package uz.as.addressbookwebservice.rest.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.medic.MedicService;
import uz.ehealth.ritme.plugins.PluginManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by bdcuyp0 on 23-5-2016.
 */
@Path("/v1/caretaker")
public class CareTakerService {

    static final Logger LOG = LoggerFactory.getLogger(CareTakerService.class);

    @GET
    @Path("/JSON/{nihiiOrg}/{source}/caretaker")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCaretaker(
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("source") final String source,
            @QueryParam("qualification") String qualification,
            @Deprecated @QueryParam("what") String what,
            @Deprecated @QueryParam("where") String where,
            @Deprecated @QueryParam("who") String who,
            @QueryParam("ssin") String ssin,
            @QueryParam("nihiiPers") String nihiiPers,
            @QueryParam("profession") String profession,
            @QueryParam("firstName") String firstName,
            @QueryParam("lastName") String lastName,
            @QueryParam("city") String city,
            @QueryParam("zipCode") String zipCode,
            @QueryParam("email") String email,
            @Context final HttpServletRequest request
    ) {


        MedicService service = PluginManager.get("ritme.outbound.caretaker", MedicService.class);

        try {
            final MedicData[] medicData = service.query(ssin, lastName == null ? who : lastName, firstName, zipCode, city == null ? where : city, profession == null ? what : profession, nihiiPers, qualification, email, request.getRemoteUser(), nihiiOrg);

            String result = JSONTools.marshal(medicData);

            LOG.info(result);

            return Response.status(Response.Status.OK).entity(result).build();
        } catch (final Throwable e ){
            LOG.error(e.getMessage(), e);
            Response.StatusType responseStatus = new Response.StatusType() {
                @Override
                public int getStatusCode() {
                    try {
                        return Integer.parseInt(e.getMessage().substring(e.getMessage().indexOf(':') + 1).trim());
                    } catch (Throwable e) {
                        return 500;
                    }
                }

                @Override
                public Response.Status.Family getFamily() {
                    return null;
                }

                @Override
                public String getReasonPhrase() {
                    return e.getCause().getMessage();
                }
            };
            throw new WebApplicationException(e, Response.status(responseStatus).build());
        }

    }


}
