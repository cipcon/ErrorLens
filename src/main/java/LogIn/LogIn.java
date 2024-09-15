package LogIn;

import java.sql.Connection;

import DBConnection.DBConnection;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LogIn {
    public String loginMethod(String password) {
        String message = "";

        try (Connection connection = DBConnection.connectToDB()) {

        } catch (Exception e) {
            return "An error occured while processing your request";
        }

        return message;
    }
}
