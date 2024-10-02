package ClassResources;

import LogIn.ChangePassword;
import Responses.LoginResponse;
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
        try {
            ChangePassword changePassword = new ChangePassword();
            LoginResponse loginResponse = changePassword.changePassword(newPassword);
            if (loginResponse.getPasswordMatch()) {
                // Password wasn't changed (same as old password)
                return Response.status(Response.Status.BAD_REQUEST).entity(loginResponse).build();
            } else {
                // Password was successfully changed
                return Response.status(Response.Status.OK).entity(loginResponse).build();
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

    public static void main(String[] args) {
        ChangePasswordResource changePasswordResource = new ChangePasswordResource();
        Response response = changePasswordResource.changePassword("1234");
        System.out.println("Status: " + response.getStatus());

        LoginResponse loginResponse = (LoginResponse) response.getEntity();
        System.out.println("Message: " + loginResponse.getMessage());
        System.out.println("Password Match: " + loginResponse.getPasswordMatch());
    }
}
