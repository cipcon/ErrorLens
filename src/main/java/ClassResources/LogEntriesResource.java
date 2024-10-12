package ClassResources;

import java.util.ArrayList;

import LogEntries.LogEntries;
import Responses.LogentriesResponse;
import Responses.MessageChangeResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/logEntries")
public class LogEntriesResource {

    @Path("/getLogentries")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLogentries() {
        LogEntries logEntries = new LogEntries();
        try {
            ArrayList<LogentriesResponse> logentries = logEntries.getLogentries();
            return Response.status(Response.Status.OK).entity(logentries).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Path("/deleteLogEntry")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteLogEntry(int logEntryId) {
        LogEntries logEntries = new LogEntries();
        try {
            MessageChangeResponse response = logEntries.deleteLogEntry(logEntryId);
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
