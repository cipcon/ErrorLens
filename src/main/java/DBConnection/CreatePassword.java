package DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

public class CreatePassword {
    public static int addPassword(String password) {
        String message;
        int rowsAffected = 0;
        try (Connection connection = DBConnection.connectToDB()) {
            String addPass = "INSERT INTO passwort(passwort, datum_geandert) VALUES(?, ?)";
            java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
            PreparedStatement statement = connection.prepareStatement(addPass);
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
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

        addPassword("1234");
    }
}
