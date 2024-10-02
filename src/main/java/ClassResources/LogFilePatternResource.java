package ClassResources;

import java.util.ArrayList;

import org.jboss.logging.Logger;

import LogFilePattern.LogFilePattern;
import Requests.PatternLogFileRequest;
import Requests.PatternRequest;
import Requests.UpdatePatternsRanksRequest;
import Responses.MessageChangeResponse;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/logFilePattern")
public class LogFilePatternResource {
    private static final Logger LOG = Logger.getLogger(LogFilePatternResource.class);

    @Path("/addLogFilePattern")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPatternToLogFile(PatternLogFileRequest addPatternToLogFile) {
        LOG.info("Received object from MainPage.tsx: " + addPatternToLogFile.getLogFileID() + " "
                + addPatternToLogFile.getPatternID());

        LogFilePattern logFilePattern = new LogFilePattern();

        try {
            MessageChangeResponse patternAddedToLogFileResponse = logFilePattern
                    .addPatternToLogFile(addPatternToLogFile);
            return Response.status(Response.Status.OK).entity(patternAddedToLogFileResponse).build();
        } catch (Exception e) {
            LOG.error("Error adding log file pattern: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Path("/getPatternsForLogFile")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPatternsForLogFile(int logFileID) {
        LOG.info("Received object from MainPage.tsx: " + logFileID);

        LogFilePattern logFilePattern = new LogFilePattern();

        try {
            ArrayList<PatternRequest> patternsForLogFile = logFilePattern.getPatternsForLogFile(logFileID);
            return Response.status(Response.Status.OK).entity(patternsForLogFile).build();
        } catch (Exception e) {
            LOG.error("Error getting patterns for log file: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Path("/updatePatternRanks")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePatternRanks(UpdatePatternsRanksRequest updatePatternsRanksRequest) {
        LOG.info("Received object from MainPage.tsx: " + updatePatternsRanksRequest.getLogFileID());
        for (PatternRequest pattern : updatePatternsRanksRequest.getPatterns()) {
            LOG.info("Pattern ID: " + pattern.getPatternId() + " Rank: " + pattern.getRank());
        }

        LogFilePattern logFilePattern = new LogFilePattern();

        try {
            MessageChangeResponse patternRanksUpdatedResponse = logFilePattern
                    .updatePatternRanks(updatePatternsRanksRequest);
            return Response.status(Response.Status.OK).entity(patternRanksUpdatedResponse).build();
        } catch (Exception e) {
            LOG.error("Error updating pattern ranks: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Path("/deletePatternFromLogFile")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deletePatternFromLogFile(PatternLogFileRequest deletePatternFromLogFile) {
        LOG.info("Received object from MainPage.tsx: " + deletePatternFromLogFile.getLogFileID());

        LogFilePattern logFilePattern = new LogFilePattern();

        try {
            MessageChangeResponse patternDeletedFromLogFileResponse = logFilePattern
                    .deletePatternFromLogFile(deletePatternFromLogFile);
            return Response.status(Response.Status.OK).entity(patternDeletedFromLogFileResponse).build();
        } catch (Exception e) {
            LOG.error("Error deleting pattern from log file: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    public static void main(String[] args) {
        /*
         * LogFilePatternResource logFilePatternResource = new LogFilePatternResource();
         * ArrayList<PatternRequest> patterns = new ArrayList<>();
         * patterns.add(new PatternRequest(2, 1));
         * patterns.add(new PatternRequest(3, 2));
         * patterns.add(new PatternRequest(4, 3));
         * UpdatePatternsRanksRequest updatePatternsRanksRequest = new
         * UpdatePatternsRanksRequest(4, patterns);
         * MessageChangeResponse messageChangeResponse = (MessageChangeResponse)
         * logFilePatternResource
         * .updatePatternRanks(updatePatternsRanksRequest).getEntity();
         * System.out.println(messageChangeResponse.getMessage());
         */
    }

}
