package LogIn;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.mindrot.jbcrypt.BCrypt;

import DBConnection.DBConnection;
import Responses.LoginResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChangePassword {
    public LoginResponse changePassword(String newPassword) {
        LoginService logIn = new LoginService();

        if (logIn.login(newPassword).getPasswordMatch()) {
            return new LoginResponse("Sie haben das gleiche Passwort eingegeben. Bitte wählen Sie ein neues Passwort.",
                    true);
        }

        try (Connection connection = DBConnection.connectToDB()) {
            String changePass = "UPDATE passwort SET passwort = ?, datum_geandert = ?";
            java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

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
}
