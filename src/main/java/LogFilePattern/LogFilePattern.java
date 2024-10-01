package LogFilePattern;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import DBConnection.DBConnection;
import Responses.MessageChangeResponse;

public class LogFilePattern {
    public MessageChangeResponse addPatternToLogFile(int logFileID, int patternID) {
        String message = "";
        boolean changed = false;
        try (Connection connection = DBConnection.connectToDB()) {
            String addPatternToLogFile = "INSERT INTO logfile_pattern (logfile_id, pattern_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(addPatternToLogFile);
            statement.setInt(1, logFileID);
            statement.setInt(2, patternID);
            statement.executeUpdate();
        } catch (SQLException e) {
            message = e.getMessage();
        }
        return new MessageChangeResponse(message, changed);
    }
}
