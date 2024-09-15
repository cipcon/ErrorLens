package DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestDB {
    public static int addPassword() {
        String message;
        int rowsAffected = 0;
        try (Connection connection = DBConnection.connectToDB()) {
            String addPass = "INSERT INTO passwort(passwort, datum_geandert) VALUES(?, ?)";
            java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
            PreparedStatement statement = connection.prepareStatement(addPass);
            String pass = "1234";
            String hashedPassword = BCrypt.hashpw(pass, BCrypt.gensalt());
            statement.setString(1, hashedPassword);
            statement.setDate(2, sqlDate);
            rowsAffected = statement.executeUpdate();
            return rowsAffected;
        } catch (SQLException e) {
            message = e.getMessage();
        }
        System.out.println(message);
        return rowsAffected;
    }

    public static void main(String[] args) {

        addPassword();
    }
}
