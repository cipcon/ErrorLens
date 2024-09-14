package DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
    private static final String url = "jdbc:mariadb://localhost:3306/LogAnalyzer";
    private static final String username = "root";
    private static final String password = "root";

    // Private constructor to prevent instantiation
    private DBConnection() {

    }

    // Method to establish a new database connection
    public static Connection connectToDB() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // Method to close resources
    public static void closeResources(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
