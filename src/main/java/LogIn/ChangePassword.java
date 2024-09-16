package LogIn;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.mindrot.jbcrypt.BCrypt;

import DBConnection.DBConnection;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChangePassword {
    public String changePassword(String password) {
        LoginService logIn = new LoginService();
        boolean oldPass = logIn.login(password).getPasswordMatch();

        if (!oldPass) {
            try (Connection connection = DBConnection.connectToDB()) {
                String changePass = "UPDATE passwort SET passwort = ?, datum_geandert = ?";
                java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                PreparedStatement statement = connection.prepareStatement(changePass);
                statement.setString(1, hashedPassword);
                statement.setDate(2, sqlDate);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    return "Password changed successfully";
                }
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        return "?";
    }

    public static void main(String[] args) {
        ChangePassword change = new ChangePassword();
        System.out.println(change.changePassword("1234"));
    }
}
