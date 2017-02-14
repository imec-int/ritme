package uz.as.addressbookwebservice.rest.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.model.OrganisationType;
import uz.ehealth.ritme.outbound.organisation.OrganisationData;
import uz.ehealth.ritme.outbound.organisation.OrganisationService;
import uz.ehealth.ritme.plugins.PluginManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by bdcuyp0 on 23-5-2016.
 */
@Path("/v1/organisation")
public class CareInstitutionService {

    static final Logger LOG = LoggerFactory.getLogger(CareInstitutionService.class);

    @GET
    @Path("/JSON/{nihiiOrg}/{source}/organisation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstitution(
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("source") final String source,
            @Deprecated @QueryParam("what") String what,
            @Deprecated @QueryParam("where") String where,
            @Deprecated @QueryParam("who") String who,
            @QueryParam("ehp") String ehp,
            @QueryParam("nihii") String nihiiToSearch,
            @QueryParam("cbe") String cbe,
            @QueryParam("zipCode") String zipCode,
            @QueryParam("email") String email,
            @QueryParam("type") String type,
            @QueryParam("name") String name,
            @QueryParam("city") String city,
            @Context final HttpServletRequest request
    ) {


        OrganisationService service = PluginManager.get("ritme.outbound.organisation", OrganisationService.class);

        final OrganisationData[] organisationDatas;
        if (what != null && type == null) {
            type = what;
        }
        try {
            organisationDatas = service.query(ehp, nihiiToSearch, cbe, type != null ? OrganisationType.valueOf(type) : null, name == null ? who : name, city == null ? where : city, zipCode, email, request.getRemoteUser(), nihiiOrg);

            String result = JSONTools.marshal(organisationDatas);

            LOG.info(result);

            return Response.status(Response.Status.OK).entity(result).build();
        } catch (final Throwable e) {
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



