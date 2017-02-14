package uz.as.vitalinkwebservice.rest.v2;


import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.as.vitalinkwebservice.rest.utils.DateParam;
import uz.ehealth.ritme.model.MedicatieSchema;
import uz.ehealth.ritme.model.MedicatieSchemaItemStatus;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.medic.MedicService;
import uz.ehealth.ritme.plugins.PluginManager;
import uz.ehealth.ritme.vitalink.DefaultVitalinkService;
import uz.ehealth.ritme.vitalink.MedicationSchemeService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

/**
 * Created by bdcuyp0 on 26-6-2015.
 */
@Path("/v2/medicatieschema")
public class MedicatieSchemaService {

    static final Logger LOG = LoggerFactory.getLogger(MedicatieSchemaService.class);



    @GET
    @Path("/JSON/{nihiiOrg}/{ssinPatient}")
    @Produces(MediaType.APPLICATION_JSON)
    @Nullable
    public MedicatieSchema getJSON(
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String ssinPatient,
            @QueryParam("endDateAfter") final DateParam endDate,
            @QueryParam("excludeStatus") final List<MedicatieSchemaItemStatus> status,
            @Context final HttpServletRequest request){

        if(nihiiOrg == null){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if(ssinPatient == null){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        MedicationSchemeService scheme = PluginManager.get("ritme.medicationscheme", MedicationSchemeService.class);

        try {
            return scheme.retrieveActualMedicationSchemeAsMedicatieSchemaItems(DefaultVitalinkService.getMedicData(request.getRemoteUser(), nihiiOrg), nihiiOrg, ssinPatient, endDate == null ? null : endDate.getDate(), status);
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

    @GET
    @Path("/Version/{nihiiOrg}/{ssinPatient}")
    @Produces(MediaType.APPLICATION_JSON)
    @Nullable
    public MedicatieSchema getVersion(
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String ssinPatient,
            @Context final HttpServletRequest request){

        if(nihiiOrg == null){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if(ssinPatient == null){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        MedicationSchemeService scheme = PluginManager.get("ritme.medicationscheme", MedicationSchemeService.class);

        try {
            return scheme.retrieveActualMedicationSchemeVersion(DefaultVitalinkService.getMedicData(request.getRemoteUser(), nihiiOrg), nihiiOrg, ssinPatient, null, Collections.<MedicatieSchemaItemStatus>emptyList());
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

    @GET
    @Path("/Version/subject/{ssinPatient}")
    @Produces(MediaType.APPLICATION_JSON)
    @Nullable
    public MedicatieSchema getVersion(
            @PathParam("ssinPatient") final String ssinPatient,
            @Context final HttpServletRequest request) {

        if (ssinPatient == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        MedicationSchemeService scheme = PluginManager.get("ritme.medicationscheme", MedicationSchemeService.class);
        /*user will be a service user and nihiiOrg is not known, we will discover it*/
        MedicData lastInvolvedMedic = PluginManager.get("ritme.outbound.medic", MedicService.class).getLastInvolvedMedic(ssinPatient, request.getRemoteUser(), null);


        try {
            if (lastInvolvedMedic == null) {
                throw new RuntimeException("404", new Exception("No last involved supervisor found"));
            }
            return scheme.retrieveActualMedicationSchemeVersion(lastInvolvedMedic, lastInvolvedMedic.getOrgNihii()[0], ssinPatient, null, Collections.<MedicatieSchemaItemStatus>emptyList());
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

    @GET
    @Path("/JSON/subject/{ssinPatient}")
    @Produces(MediaType.APPLICATION_JSON)
    @Nullable
    public MedicatieSchema getJSON(
            @PathParam("ssinPatient") final String ssinPatient,
            @QueryParam("endDateAfter") final DateParam endDate,
            @QueryParam("excludeStatus") final List<MedicatieSchemaItemStatus> status,
            @Context final HttpServletRequest request) {


        if (ssinPatient == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        MedicationSchemeService scheme = PluginManager.get("ritme.medicationscheme", MedicationSchemeService.class);
        /*user will be a service user and nihiiOrg is not known, we will discover it*/
        MedicData lastInvolvedMedic = PluginManager.get("ritme.outbound.medic", MedicService.class).getLastInvolvedMedic(ssinPatient, request.getRemoteUser(), null);

        try {
            if (lastInvolvedMedic == null) {
                throw new RuntimeException("404", new Exception("No last involved supervisor found"));
            }
            return scheme.retrieveActualMedicationSchemeAsMedicatieSchemaItems(lastInvolvedMedic, lastInvolvedMedic.getOrgNihii()[0], ssinPatient, endDate == null ? null : endDate.getDate(), status);
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
