package uz.as.vitalinkwebservice.rest.v1;


import net.sf.saxon.TransformerFactoryImpl;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import uz.as.vitalinkwebservice.rest.utils.DateParam;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.model.MedicatieSchemaItem;
import uz.ehealth.ritme.model.MedicatieSchemaItemStatus;
import uz.ehealth.ritme.model.VitalinkMetadata;
import uz.ehealth.ritme.outbound.hospital.HospitalService;
import uz.ehealth.ritme.outbound.medic.MedicData;
import uz.ehealth.ritme.outbound.medic.MedicService;
import uz.ehealth.ritme.outbound.patient.PatientService;
import uz.ehealth.ritme.plugins.PluginManager;
import uz.ehealth.ritme.vitalink.DefaultVitalinkService;
import uz.ehealth.ritme.vitalink.MedicationSchemeService;
import uz.ehealth.ritme.vitalink.SaveInput;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;

/**
 * Created by bdcuyp0 on 26-6-2015.
 */
@Path("/v1/medicatieschema")
public class MedicatieSchemaService {

    static final Logger LOG = LoggerFactory.getLogger(MedicatieSchemaService.class);

    private static final String HUB_NIHII_DUMMY = "1990000827";

    //region vanuit KWS

    @GET
    @Path("/XML/{nihiiOrg}/{ssinPatient}")
    @Produces("multipart/mixed")
    @PartType("application/xml")
    @Nullable
    public List<byte[]> getXML(
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
            return scheme.retrieveActualMedicationSchemeAsXml(DefaultVitalinkService.getMedicData(request.getRemoteUser(), nihiiOrg), nihiiOrg, ssinPatient, endDate == null ? null : endDate.getDate(), status);
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
    @Path("/MetaData/{nihiiOrg}/{ssinPatient}")
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

        MedicationSchemeService scheme = PluginManager.get("ritme.medicationscheme", MedicationSchemeService.class);

        try {
            return scheme.getMetadataAsJSON(DefaultVitalinkService.getMedicData(request.getRemoteUser(), nihiiOrg), nihiiOrg, ssinPatient);
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
    @Path("/HTML/{nihiiOrg}/{ssinPatient}")
    @Produces("text/html")
    @Nullable
    public String getHTML(
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String ssinPatient,
            @QueryParam("endDateAfter") final DateParam endDate,
            @QueryParam("excludeStatus") final List<MedicatieSchemaItemStatus> status,
            @Context final HttpServletRequest request) {

        if (nihiiOrg == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (ssinPatient == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        MedicationSchemeService scheme = PluginManager.get("ritme.medicationscheme", MedicationSchemeService.class);


        try {

            List<byte[]> xmls = scheme.retrieveActualMedicationSchemeAsXml(DefaultVitalinkService.getMedicData(request.getRemoteUser(), nihiiOrg), nihiiOrg, ssinPatient, endDate == null ? null : endDate.getDate(), status);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);

            Document rootDoc = documentBuilderFactory.newDocumentBuilder().newDocument();
            Element rootElement = rootDoc.createElement("root");
            rootDoc.appendChild(rootElement);

            for (byte[] xml : xmls) {
                Document childDocument = documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(xml)));
                Node adoptedNode = rootDoc.adoptNode(childDocument.getDocumentElement());
                rootElement.appendChild(adoptedNode);
            }

            try {
                DOMSource domSource = new DOMSource(rootDoc);
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                //TransformerFactory tf = TransformerFactory.newInstance();
                TransformerFactory tf = new TransformerFactoryImpl();
                Transformer transformer = tf.newTransformer(new StreamSource(this.getClass().getResourceAsStream("/medication-scheme_NL.xsl")));
                transformer.transform(domSource, result);
                writer.flush();
                String html = writer.toString();
                LOG.debug(html);
                return html;
            } catch (TransformerException ex) {
                LOG.debug(ex.getMessage(), ex);
                return null;
            }


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
    @Path("/JSON/{nihiiOrg}/{ssinPatient}")
    @Produces(MediaType.APPLICATION_JSON)
    @Nullable
    public List<MedicatieSchemaItem> getJSON(
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
            return scheme.retrieveActualMedicationSchemeAsMedicatieSchemaItems(DefaultVitalinkService.getMedicData(request.getRemoteUser(), nihiiOrg), nihiiOrg, ssinPatient, endDate == null ? null : endDate.getDate(), status).getItems();
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

    @POST
    @Path("/JSON/{nihiiOrg}/{source}/{schemaversion}/subject/{ssinPatient}/medication-scheme/{item}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(
            final String data,
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String ssinPatient,
            @PathParam("source") final String source,
            @PathParam("item") final String item,
            @PathParam("schemaversion") final String schemaversie,
            @Context final HttpServletRequest request) {
        SaveInput input = JSONTools.unmarshal(data, SaveInput.class);

        MedicationSchemeService scheme = PluginManager.get("ritme.medicationscheme", MedicationSchemeService.class);
        try {
            URI result = scheme.saveMedicatieSchemaItem(DefaultVitalinkService.getMedicData(request.getRemoteUser(), nihiiOrg), nihiiOrg, PluginManager.get("ritme.outbound.hospital", HospitalService.class).getData(nihiiOrg, request.getRemoteUser()), input.getItems(), PluginManager.get("ritme.outbound.patient", PatientService.class).getData(ssinPatient, request.getRemoteUser(), nihiiOrg), schemaversie);
            return Response.status(Response.Status.CREATED).contentLocation(result).build();

        } catch (final Throwable e) {
            LOG.error(e.getMessage(), e);
            Response.StatusType status = new Response.StatusType() {
                @Override
                public int getStatusCode() {
                    return Integer.parseInt(e.getMessage().substring(e.getMessage().indexOf(':') + 1).trim());
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
    @Path("/MetaData/hub/{nihiiOrg}/{ssinPatient}")
    @Produces(MediaType.APPLICATION_JSON)
    @Nullable
    public List<VitalinkMetadata> getMetaDataHub(
            @PathParam("nihiiOrg") final String nihiiUser,
            @PathParam("ssinPatient") final String ssinPatient,
            @Context final HttpServletRequest request) {

        if (nihiiUser == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (ssinPatient == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        MedicationSchemeService scheme = PluginManager.get("ritme.medicationscheme", MedicationSchemeService.class);

        try {
            return scheme.getMetadataAsJSON(getMedicData(request.getRemoteUser(), nihiiUser, HUB_NIHII_DUMMY), HUB_NIHII_DUMMY, ssinPatient);
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
    @Path("/XML/hub/{nihiiOrg}/{ssinPatient}")
    @Produces("multipart/mixed")
    @PartType("application/xml")
    @Nullable
    public List<byte[]> getXMLHub(
            @PathParam("nihiiOrg") final String nihiiUser,
            @PathParam("ssinPatient") final String ssinPatient,
            @QueryParam("endDateAfter") final DateParam endDate,
            @QueryParam("excludeStatus") final List<MedicatieSchemaItemStatus> status,
            @Context final HttpServletRequest request){

        if(nihiiUser == null){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if(ssinPatient == null){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        MedicationSchemeService scheme = PluginManager.get("ritme.medicationscheme", MedicationSchemeService.class);

        try {
            return scheme.retrieveActualMedicationSchemeAsXml(getMedicData(request.getRemoteUser(), nihiiUser, HUB_NIHII_DUMMY), HUB_NIHII_DUMMY, ssinPatient, endDate.getDate(), status);
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

    private MedicData getMedicData(String technicalUser, String nihiiUser, String nihiiOrg) {
        MedicService medicService = PluginManager.get("ritme.internal.caretaker", MedicService.class);
        MedicData[] results = medicService.query(null, null, null, null, null, null, nihiiUser, null, null, technicalUser, nihiiOrg);
        return results[0];
    }

    //endregion

}
