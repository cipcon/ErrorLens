package LogIn;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.mindrot.jbcrypt.BCrypt;

import DBConnection.DBConnection;
import Requests.LoginResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChangePassword {
    public LoginResponse changePassword(String password) {
        LoginService logIn = new LoginService();
        boolean samePassword = logIn.login(password).getPasswordMatch();
        System.out.println(samePassword);

        if (!samePassword) {
            try (Connection connection = DBConnection.connectToDB()) {
                String changePass = "UPDATE passwort SET passwort = ?, datum_geandert = ?";
                java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                PreparedStatement statement = connection.prepareStatement(changePass);
                statement.setString(1, hashedPassword);
                statement.setDate(2, sqlDate);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    return new LoginResponse("Passwort wurde erfolgreich geändert.", false);
                } else {
                    return new LoginResponse("Etwas ist schief gelaufen.", false);
                }
            } catch (Exception e) {
                return new LoginResponse(e.getMessage(), false);
            }
        }
        return new LoginResponse("Sie haben das gleiche Passwort eingegeben. Bitte wählen Sie ein neues Passwort.",
                samePassword);
    }

    public static void main(String[] args) {
        ChangePassword changePassword = new ChangePassword();
        LoginResponse loginResponse = changePassword.changePassword("1234");
        System.out.println(loginResponse.getPasswordMatch());
    }
}
