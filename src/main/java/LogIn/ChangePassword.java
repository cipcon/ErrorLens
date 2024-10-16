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

    public static void main(String[] args) {
        ChangePassword changePassword = new ChangePassword();
        System.out.println(changePassword.changePassword("1234").getPasswordMatch());
        System.out.println(changePassword.changePassword("1234").getMessage());
        // $2a$10$FDHTCfi9MmWj8LCVAKn6ceF09qBlj2rAgNi1HpeH0T8HNqUx5q2M2
        // $2a$10$FDHTCfi9MmWj8LCVAKn6ceF09qBlj2rAgNi1HpeH0T8HNqUx5q2M2
    }
}
