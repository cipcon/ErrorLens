package ClassResources;

import LogIn.LoginService;
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
        boolean passwordMatch = loginService.login(password).getPasswordMatch();

        if (passwordMatch) {
            return Response.status(Response.Status.OK).entity(loginService).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(loginService).build();
        }
    }

    public static void main(String[] args) {
        LoginResource loginResource = new LoginResource();
        PasswordRequest request = new PasswordRequest();
        request.setPassword("1234");
        Response response = loginResource.login(request);
        System.out.println(response.getStatus());
    }
}
