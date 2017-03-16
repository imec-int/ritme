package uz.as.recipewebservice.rest.v1;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.as.recipewebservice.rest.JSONTools;
import uz.ehealth.ritme.model.MedicatieVoorschriftItem;
import uz.ehealth.ritme.recipe.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * Created by bdcuyp0 on 26-6-2015.
 * Could not find resource for relative : /v1/prescription/JSON/71032209/Recipe/subject/73100906111/prescription of full path: http://10.250.227.203:8080/recipewebservice/api/v1/prescription/JSON/71032209/subject/73100906111/prescription
 */
@Path("/v1/prescription")
public class PrescriberService {

    static final Logger LOG = LoggerFactory.getLogger(PrescriberService.class);
    private static final PrescriptionService SCHEME = new DefaultPrescriptionService();

    @POST
    @Path("/JSON/{nihiiOrg}/{source}/subject/{ssinPatient}/prescription")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response send(
            final String data,
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String ssinPatient,
            @PathParam("source") final String source,
            @Context final HttpServletRequest request) {
        CreatePrescriptionInput input = JSONTools.unmarshal(data, CreatePrescriptionInput.class);


        try {
            URI result = SCHEME.createPrescription(request.getRemoteUser(), ssinPatient, nihiiOrg, input.getItems());
            return Response.status(Response.Status.CREATED).contentLocation(result).build();

        } catch (final Throwable e) {
            LOG.error(e.getMessage(), e);
            Response.StatusType status = new Response.StatusType() {
                @Override
                public int getStatusCode() {
                    return createStatusCode(e);

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
            throw new WebApplicationException(e, Response.status(status).build());
        }


    }

    private int createStatusCode(final Throwable e) {
        try {
            if (e.getMessage() != null) {
                return Integer.parseInt(e.getMessage().substring(e.getMessage().indexOf(':') + 1).trim());
            }
        } catch (Exception exc) {
            LOG.error(exc.getMessage(), e);
        }
        return 500;

    }

    @POST
    @Path("/JSON/{nihiiOrg}/{source}/subject/{ssinPatient}/prescriptions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sortAndSend(
            final String data,
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String ssinPatient,
            @PathParam("source") final String source,
            @Context final HttpServletRequest request) {
        CreatePrescriptionInput input = JSONTools.unmarshal(data, CreatePrescriptionInput.class);

        try {
            List<MedicatieVoorschriftItem> result = SCHEME.sortAndCreatePrescriptions(request.getRemoteUser(), ssinPatient, nihiiOrg, input.getItems());
            String output = JSONTools.marshal(new CreatePrescriptionOutput(result));
            return Response.status(Response.Status.OK).entity(output).build();

        } catch (final Throwable e) {
            LOG.error(e.getMessage(), e);
            Response.StatusType status = new Response.StatusType() {
                @Override
                public int getStatusCode() {
                    return createStatusCode(e);
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
            throw new WebApplicationException(e, Response.status(status).build());
        }


    }

    @POST
    @Path("/JSON/{nihiiOrg}/{source}/subject/{ssinPatient}/notification/{receiverNihii}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendNotification(
            final String data,
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String ssinPatient,
            @PathParam("receiverNihii") final String receiverNihii,
            @PathParam("source") final String source,
            @Context final HttpServletRequest request) {

        NotificationInput input = JSONTools.unmarshal(data, NotificationInput.class);
        try {
        SCHEME.sendNotification(input.getMessage(), input.getItems(), ssinPatient, receiverNihii, nihiiOrg, request.getRemoteUser());
            return Response.ok().build();

        } catch (final Throwable e) {
            LOG.error(e.getMessage(), e);
            Response.StatusType status = new Response.StatusType() {
                @Override
                public int getStatusCode() {
                    return createStatusCode(e);
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
            throw new WebApplicationException(e, Response.status(status).build());
        }


    }

    /* /recipewebservice/api/v1/prescription/JSON/71032209/recipe/subject/12345602546/prescription/BEP1CSR39A04 */
    @POST
    @Path("/JSON/{nihiiOrg}/{source}/subject/{ssinPatient}/revoke/{rid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response revokePrescription(
            final String data,
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String ssinPatient,
            @PathParam("rid") final String rid,
            @PathParam("source") final String source,
            @Context final HttpServletRequest request) {

        RevokeInput input = JSONTools.unmarshal(data, RevokeInput.class);
        try {
            SCHEME.revokePrescription(input.getReason(), ssinPatient, rid, nihiiOrg, request.getRemoteUser());
            return Response.ok().build();

        } catch (final Throwable e) {
            LOG.error(e.getMessage(), e);
            Response.StatusType status = new Response.StatusType() {
                @Override
                public int getStatusCode() {
                    return createStatusCode(e);
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
            throw new WebApplicationException(e, Response.status(status).build());
        }


    }

    @GET
    @Path("/JSON/{nihiiOrg}/{source}/subject/{ssinPatient}/prescriptions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listOpenPrescriptions(
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String ssinPatient,
            @PathParam("source") final String source,
            @QueryParam("prescriber") final String nihiiPrescriber,
            @Context final HttpServletRequest request) {

        try {
            final String nihii;
            if (StringUtils.isEmpty(nihiiPrescriber)) {
                nihii = nihiiOrg;
            } else {
                nihii = nihiiPrescriber;
            }
            List<URI> uris = SCHEME.listOpenPrescriptions(ssinPatient, nihii, nihiiOrg, request.getRemoteUser());

            return Response.ok(new ListPrescriptionOutput(uris), MediaType.APPLICATION_JSON_TYPE).build();

        } catch (final Throwable e) {
            LOG.error(e.getMessage(), e);
            Response.StatusType status = new Response.StatusType() {
                @Override
                public int getStatusCode() {
                    return createStatusCode(e);
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
            throw new WebApplicationException(e, Response.status(status).build());
        }


    }



}
