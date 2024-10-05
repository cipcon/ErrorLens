package ClassResources;

import org.jboss.logging.Logger;

import LogFiles.LogFile;
import Requests.LogFileRequest;
import Responses.MessageChangeResponse;
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
    public Response addLogFilePath(LogFileRequest addLogFileResponse) {
        LOG.info("Received object from MainPage.tsx: " + addLogFileResponse.getLogFileName());
        LOG.info("Received object from MainPage.tsx: " + addLogFileResponse.getLogFilePath());

        LogFile logFile = new LogFile();

        try {
            MessageChangeResponse logFileAddedResponse = logFile.addLogFile(addLogFileResponse);
            return Response.status(Response.Status.OK).entity(logFileAddedResponse).build();
        } catch (Exception e) {
            LOG.error("Error adding log file: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
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

    @Path("/updateLogFile")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateLogFile(LogFileRequest logFileRequest) {
        LogFile logFile = new LogFile();
        try {
            MessageChangeResponse logFileResponse = logFile.updateLogFile(logFileRequest);
            return Response.status(Response.Status.OK).entity(logFileResponse).build();
        } catch (Exception e) {
            LOG.error("Error updating log file: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Path("/deleteLogFile")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteLogFile(int logFileId) {
        LogFile logFile = new LogFile();
        try {
            MessageChangeResponse logFileResponse = logFile.deleteLogFile(logFileId);
            return Response.status(Response.Status.OK).entity(logFileResponse).build();
        } catch (Exception e) {
            LOG.error("Error deleting log file: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
