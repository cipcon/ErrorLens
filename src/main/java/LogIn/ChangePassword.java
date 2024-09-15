package LogIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import DBConnection.DBConnection;

public class ChangePassword {
    public String changePassword(String password) {
        boolean newPass = false;

        if (password.isEmpty()) {
            return "Password is empty. Please type in your new password";
        }

        // check if the new password = last password
        try (Connection connection = DBConnection.connectToDB()) {
            String checkPass = "SELECT passwort FROM passwort";

            PreparedStatement statement = connection.prepareStatement(checkPass);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("passwort");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return "This is your last password, please choose a new one";
                } else {
                    newPass = true;
                }
            } else {
                return "No password found in the database";
            }
        } catch (SQLException e) {
            return e.getMessage();
        }

        if (newPass) {
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
        System.out.println(change.changePassword("12346"));
    }
}
