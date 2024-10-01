package ClassResources;

import org.jboss.logging.Logger;

import LogFiles.LogFile;
import Requests.AddLogFileRequest;
import Responses.LogFileAddedResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/logFile")
public class LogFileResource {
    private static final Logger LOG = Logger.getLogger(LogFileResource.class);

    @Path("/addLogFile")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addLogFilePath(AddLogFileRequest addLogFileResponse) {
        LOG.info("Received object from MainPage.tsx: " + addLogFileResponse.getLogFileName());

        LogFile logFile = new LogFile();

        LogFileAddedResponse logFileAddedResponse = logFile.addLogFile(addLogFileResponse);

        if (logFileAddedResponse.isLogFileAdded()) {
            return Response.status(Response.Status.OK).entity(logFileAddedResponse).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(logFileAddedResponse).build();
        }

    }

    @Path("/listLogFiles")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listLogFiles() {
        try {
            return Response.status(Response.Status.OK).entity(LogFile.listLogFiles()).build();
        } catch (Exception e) {
            LOG.error("Error listing log files: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

}
