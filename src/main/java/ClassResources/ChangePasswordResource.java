package ClassResources;

import org.jboss.logging.Logger;

import LogIn.ChangePassword;
import Responses.LoginResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/changePassword")
public class ChangePasswordResource {
    private final Logger LOG = Logger.getLogger(LogFilePatternResource.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(String newPassword) {
        LOG.info("Received string from ChangePassword.tsx: " + newPassword);
        ChangePassword changePasswordObject = new ChangePassword();

        try {
            LoginResponse changePasswordResponse = changePasswordObject.changePassword(newPassword);
            if (changePasswordResponse.getPasswordMatch()) {
                // Password wasn't changed (same as old password)
                return Response.status(Response.Status.BAD_REQUEST).entity(changePasswordResponse).build();
            } else {
                // Password was successfully changed
                return Response.status(Response.Status.OK).entity(changePasswordResponse).build();
            }
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            // Return a generic error message to the client
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new LoginResponse("Ein unerwarteter Fehler ist aufgetreten.", false))
                    .build();
        }
    }
}
