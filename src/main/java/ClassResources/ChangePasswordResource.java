package ClassResources;

import LogIn.ChangePassword;
import Requests.LoginResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/changePassword")
public class ChangePasswordResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(String newPassword) {
        ChangePassword changePassword = new ChangePassword();
        LoginResponse loginResponse = changePassword.changePassword(newPassword);
        if (loginResponse.getPasswordMatch()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(loginResponse).build();
        } else {
            return Response.status(Response.Status.OK).entity(loginResponse).build();
        }

    }
}
