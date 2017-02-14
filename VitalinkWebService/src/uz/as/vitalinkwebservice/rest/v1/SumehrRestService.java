package uz.as.vitalinkwebservice.rest.v1;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.ehealth.ritme.model.VitalinkMetadata;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.medic.MedicService;
import uz.ehealth.ritme.plugins.PluginManager;
import uz.ehealth.ritme.vitalink.DefaultSumehrService;
import uz.ehealth.ritme.vitalink.DefaultVitalinkService;
import uz.ehealth.ritme.vitalink.SumehrService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Date: 2-3-2016.
 */
@Path("v1/sumehr")
public class SumehrRestService {
    private static final Logger LOG = LoggerFactory.getLogger(SumehrRestService.class);

    private static final String HUB_NIHII_DUMMY = "1990000827";

    //region vanuit KWS

    @GET
    @Path("/check/{nihiiOrg}/{ssinPatient}")
    @Produces(MediaType.APPLICATION_JSON)
    @NotNull
    public Response hasSumehrs(@PathParam("nihiiOrg") final String nihiiOrg,
                               @PathParam("ssinPatient") final String ssinPatient,
                               @Context final HttpServletRequest request){
        if (nihiiOrg == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (ssinPatient == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        final SumehrService scheme = new DefaultSumehrService();

        try {
            final List<VitalinkMetadata> metadata = scheme.getMetadataAsJSON(DefaultVitalinkService.getMedicData(request.getRemoteUser(), nihiiOrg), nihiiOrg, ssinPatient);

            if(!metadata.isEmpty()){
                return Response.status(Response.Status.OK).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (final Throwable e) {
            LOG.error(e.getMessage(), e);
            Response.StatusType status = new Response.StatusType() {
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
            throw new WebApplicationException(e, Response.status(status).build());
        }
    }

    @GET
    @Path("/metadata/{nihiiOrg}/{ssinPatient}")
    @Produces(MediaType.APPLICATION_JSON)
    @Nullable
    public List<VitalinkMetadata> getMetaData(
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String ssinPatient,
            @Context final HttpServletRequest request) {

        if (nihiiOrg == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (ssinPatient == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        final SumehrService scheme = new DefaultSumehrService();

        try {
            return scheme.getMetadataAsJSON(DefaultVitalinkService.getMedicData(request.getRemoteUser(), nihiiOrg), nihiiOrg, ssinPatient);
        } catch (final Throwable e) {
            LOG.error(e.getMessage(), e);
            Response.StatusType status = new Response.StatusType() {
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
            throw new WebApplicationException(e, Response.status(status).build());
        }
    }

    @GET
    @Path("/XML/{nihiiOrg}/{ssinPatient}")
    @Produces("multipart/mixed")
    @PartType("application/xml")
    @Nullable
    public List<byte[]> getXML(
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String ssinPatient,
            @Context final HttpServletRequest request) {

        if (nihiiOrg == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (ssinPatient == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        final SumehrService scheme = new DefaultSumehrService();

        try {
            return scheme.retrieveSumehrAsXml(DefaultVitalinkService.getMedicData(request.getRemoteUser(), nihiiOrg), nihiiOrg, ssinPatient);
        } catch (final Throwable e) {
            LOG.error(e.getMessage(), e);
            Response.StatusType status = new Response.StatusType() {
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
            throw new WebApplicationException(e, Response.status(status).build());
        }
    }

    //endregion

    //region vanuit Hub

    @GET
    @Path("/metadata/hub/{nihiiUser}/{ssinPatient}")
    @Produces(MediaType.APPLICATION_JSON)
    @Nullable
    public List<VitalinkMetadata> getMetaDataHub(
            @PathParam("nihiiUser") final String nihiiUser,
            @PathParam("ssinPatient") final String ssinPatient,
            @Context final HttpServletRequest request) {

        if (nihiiUser == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (ssinPatient == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        final SumehrService scheme = new DefaultSumehrService();

        try {
            return scheme.getMetadataAsJSON(getMedicData(request.getRemoteUser(),nihiiUser,HUB_NIHII_DUMMY), HUB_NIHII_DUMMY, ssinPatient);
        } catch (final Throwable e) {
            LOG.error(e.getMessage(), e);
            Response.StatusType status = new Response.StatusType() {
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
            throw new WebApplicationException(e, Response.status(status).build());
        }
    }

    @GET
    @Path("/XML/hub/{nihiiUser}/{ssinPatient}")
    @Produces("multipart/mixed")
    @PartType("application/xml")
    @Nullable
    public List<byte[]> getXMLHub(
            @PathParam("nihiiUser") final String nihiiUser,
            @PathParam("ssinPatient") final String ssinPatient,
            @Context final HttpServletRequest request) {

        if (nihiiUser == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (ssinPatient == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        final SumehrService scheme = new DefaultSumehrService();

        try {
            return scheme.retrieveSumehrAsXml(getMedicData(request.getRemoteUser(),nihiiUser,HUB_NIHII_DUMMY), HUB_NIHII_DUMMY, ssinPatient);
        } catch (final Throwable e) {
            LOG.error(e.getMessage(), e);
            Response.StatusType status = new Response.StatusType() {
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
            throw new WebApplicationException(e, Response.status(status).build());
        }
    }

    private MedicData getMedicData(String technicalUser,  String nihiiUser, String nihiiOrg) {
        MedicService medicService = PluginManager.get("ritme.internal.caretaker", MedicService.class);
        MedicData[] results = medicService.query(null, null, null, null, null, null, nihiiUser, null, null, technicalUser, nihiiOrg);
        return results[0];
    }

    //endregion
}
