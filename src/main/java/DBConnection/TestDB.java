package DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestDB {
    public static int addPassword() {
        String message;
        int rowsAffected = 0;
        try (Connection connection = DBConnection.connectToDB()) {
            String addPass = "INSERT INTO passwort(passwort, datum_geandert) VALUES(?, ?)";
            java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
            try (PreparedStatement statement = connection.prepareStatement(addPass)) {
                statement.setString(1, "parola");
                statement.setDate(2, sqlDate);
                rowsAffected = statement.executeUpdate();
                return rowsAffected;
            } catch (SQLException e) {
                message = e.getMessage();
            }
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
