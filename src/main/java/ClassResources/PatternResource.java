package ClassResources;

import java.util.ArrayList;

import org.jboss.logging.Logger;

import Patterns.Patterns;
import Requests.PatternRequest;
import Responses.MessageChangeResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/pattern")
public class PatternResource {
    private static final Logger LOG = Logger.getLogger(PatternResource.class);

    @Path("/addPattern")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPattern(PatternRequest addPatternRequest) {
        LOG.info("Received object from MainPage.tsx: " + addPatternRequest.getPatternName());

        Patterns pattern = new Patterns();

        MessageChangeResponse MessageChangeResponse = pattern.addPattern(addPatternRequest);

        if (MessageChangeResponse.isChanged()) {
            LOG.info("Pattern added successfully");
            return Response.status(Response.Status.OK).entity(MessageChangeResponse).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageChangeResponse).build();
        }
    }

    @Path("/listPatterns")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listPatterns() {
        Patterns pattern = new Patterns();

        try {
            ArrayList<PatternRequest> allPatterns = pattern.listPatterns();
            return Response.status(Response.Status.OK).entity(allPatterns).build();
        } catch (Exception e) {
            LOG.error("Error listing patterns: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Path("/getPatternID")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPatternID(String patternName) {
        LOG.info(patternName);
        Patterns pattern = new Patterns();
        try {
            int patternID = pattern.getPatternID(patternName);
            return Response.status(Response.Status.OK).entity(patternID).build();
        } catch (Exception e) {
            LOG.error("Error getting pattern ID: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Path("/updatePattern")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePattern(PatternRequest addPatternRequest) {
        Patterns pattern = new Patterns();
        try {
            MessageChangeResponse MessageChangeResponse = pattern.updatePattern(addPatternRequest);
            return Response.status(Response.Status.OK).entity(MessageChangeResponse).build();
        } catch (Exception e) {
            LOG.error("Error updating pattern: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Path("/deletePattern")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deletePattern(int patternId) {
        Patterns pattern = new Patterns();
        try {
            MessageChangeResponse MessageChangeResponse = pattern.deletePattern(patternId);
            return Response.status(Response.Status.OK).entity(MessageChangeResponse).build();
        } catch (Exception e) {
            LOG.error("Error deleting pattern: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
