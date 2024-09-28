package ClassResources;

import LogIn.LoginService;
import Requests.LoginResponse;
import Requests.PasswordRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/login")
public class LoginResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(PasswordRequest request) {
        String password = request.getPassword();
        LoginService loginService = new LoginService();
        LoginResponse loginResponse = loginService.login(password);

        if (loginResponse.getPasswordMatch()) {
            return Response.status(Response.Status.OK).entity(loginResponse).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(loginResponse).build();
        }
    }
}
