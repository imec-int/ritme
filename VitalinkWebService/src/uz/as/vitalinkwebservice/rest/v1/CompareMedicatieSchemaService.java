package uz.as.vitalinkwebservice.rest.v1;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;
import uz.ehealth.ritme.comparison.CompareService;
import uz.ehealth.ritme.comparison.ComparisonInput;
import uz.ehealth.ritme.comparison.ComparisonUIInput;
import uz.ehealth.ritme.core.JSONTools;
import uz.ehealth.ritme.plugins.PluginManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by bdcuyp0 on 26-6-2015.
 */
@Path("/v1/compare-medicatieschema")
public class CompareMedicatieSchemaService {


    private ObjectMapper objectMapper;
    private static final CompareService SERVICE = PluginManager.get("ritme.compare-medicatieschema", CompareService.class);

    @POST
    @Path("/JSON")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Nullable
    public String getJSON(
            final String items,
            @Context final HttpServletRequest request) {
        ComparisonInput input = JSONTools.unmarshal(items, ComparisonInput.class);
        return JSONTools.marshal(SERVICE.compare(input));


    }

    @POST
    @Path("/JSON/{nihiiOrg}/{source}/subject/{ssinPatient}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Nullable
    public String getCompareUIData(
            final String items,
            @PathParam("nihiiOrg") final String nihiiOrg,
            @PathParam("ssinPatient") final String patientSSIN,
            @PathParam("source") final String source,
            @Context final HttpServletRequest request) {
        ComparisonUIInput input = JSONTools.unmarshal(items, ComparisonUIInput.class);

        return SERVICE.createComparisonUIData(input.getLeft(), input.getRight(), input.getValue(), nihiiOrg, patientSSIN, request.getRemoteUser());


    }


}
