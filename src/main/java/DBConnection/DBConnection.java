package DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
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

}

/*
 * In modern Java, starting with Java 7, you can use the try-with-resources
 * statement to automatically close resources like Connection,
 * PreparedStatement, and ResultSet.
 *
 * When using try-with-resources, any resource that implements the AutoCloseable
 * or Closeable interface (like these JDBC objects)
 * will automatically be closed when the block exits, either normally or due to
 * an exception.
 */

/*
 * it's enough to use one try-with-resources block to manage all of the
 * resources (like Connection, PreparedStatement, and ResultSet) together. In
 * fact, this is a recommended approach because it simplifies your code.
 * 
 * When you declare multiple resources in a single try-with-resources block, all
 * the resources will be closed in the reverse order of their creation, which is
 * exactly what you want for JDBC resources (i.e., ResultSet should close first,
 * followed by PreparedStatement, and finally the Connection).
 */