package LogIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.mindrot.jbcrypt.BCrypt;

import DBConnection.DBConnection;
import Requests.LoginResponse;
import jakarta.enterprise.context.ApplicationScoped;

// was passiert, wenn man das Passwort vergisst?
@ApplicationScoped
public class LoginService {
    public LoginResponse login(String password) {
        String message = "";
        boolean passwordMatch = false;
        String changePassword = "";

        if (password.isEmpty()) {
            message = "Passwort ist leer";
            return new LoginResponse(message, passwordMatch, changePassword);
        }

        try (Connection connection = DBConnection.connectToDB()) {
            String checkPass = "SELECT passwort, datum_geandert FROM passwort";

            PreparedStatement statement = connection.prepareStatement(checkPass);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("passwort");
                java.sql.Date lastPasswordChangeDate = resultSet.getDate("datum_geandert");

                LocalDate currendDate = LocalDate.now();
                long daysSincePasswordChange = ChronoUnit.DAYS.between(lastPasswordChangeDate.toLocalDate(),
                        currendDate);

                if (daysSincePasswordChange > 30) {
                    changePassword = ("Ihr Passwort wurde seit " + daysSincePasswordChange
                            + " Tagen nicht geändert. Bitte ändern Sie Ihr Passwort.");
                }
                if (BCrypt.checkpw(password, hashedPassword)) {
                    message = "Passwort ist richtig, Sie werden in Kürze authentifiziert";
                    passwordMatch = true;
                    return new LoginResponse(message, passwordMatch, changePassword);
                } else {
                    message = "Falsches Passwort";
                    return new LoginResponse(message, passwordMatch, changePassword);
                }
            } else {
                message = "Keines Passwort in der DB gefunden";
                return new LoginResponse(message, passwordMatch, changePassword);
            }
        } catch (SQLException e) {
            message = e.getMessage();
        }
        return new LoginResponse(message, passwordMatch, changePassword);
    }

    public static void main(String[] args) {
        LoginService logIn = new LoginService();
        System.out.println(logIn.login("1234").getChangePassword());
        System.out.println(logIn.login("1234").getMessage());
        System.out.println(logIn.login("1234").getPasswordMatch());
    }
}
