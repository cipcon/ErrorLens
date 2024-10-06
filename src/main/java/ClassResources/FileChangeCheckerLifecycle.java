package ClassResources;

import LogFiles.FileChangeChecker;
import Requests.IntervalTimeUnitRequest;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Singleton;

import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

// This class is responsible for starting the file change checker when the application starts.
// It is also responsible for stopping the file change checker when the application stops.
@Path("/fileChangeChecker")
@Singleton
public class FileChangeCheckerLifecycle {
    private static final Logger LOG = Logger.getLogger(FileChangeCheckerLifecycle.class);

    private static final long DEFAULT_INTERVAL = 1;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.HOURS;

    // This method is called when the application starts.
    // It starts the file change checker with a 1 hour interval for testing
    void onStart(@Observes StartupEvent ev) {
        changeCheckInterval(new IntervalTimeUnitRequest(DEFAULT_INTERVAL, DEFAULT_TIME_UNIT));
    }

    // This method is called when the application stops.
    // It stops the file change checker.
    void onStop(@Observes ShutdownEvent ev) {
        stopFileChangeChecker();
    }

    // This method is called to start the file change checker with a specified
    // interval and time unit.
    @Path("/checkInterval")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeCheckInterval(IntervalTimeUnitRequest intervalTimeUnitRequest) {
        try {
            FileChangeChecker.startFileChangeChecker(intervalTimeUnitRequest.getInterval(),
                    intervalTimeUnitRequest.getTimeUnit());
            String message = "Überprüfungsintervall auf " + intervalTimeUnitRequest.getInterval() + " "
                    + intervalTimeUnitRequest.getTimeUnit().toString() + " geändert.";
            LOG.info(message);
            return Response.status(Response.Status.OK)
                    .entity(message)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    // This method is called to stop the file change checker.
    @Path("/stop")
    @POST
    public void stopFileChangeChecker() {
        try {
            FileChangeChecker.stopFileChangeChecker();
            System.out.println("File change checker stopped.");
        } catch (Exception e) {
            System.out.println("Error stopping file change checker: " + e.getMessage());
        }
    }

    @Path("/checkNow")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkNow() {
        LOG.info("Checking for changes now.");
        try {
            FileChangeChecker.checkAllFilesForChanges();
            return Response.status(Response.Status.OK).entity("{\"message\": \"Check completed successfully\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
}