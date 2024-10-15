package LogFilePattern;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import DBConnection.DBConnection;
import LogEntries.LogEntries;
import LogFiles.LogFile;
import Requests.LogFileRequest;
import Requests.PatternLogFileRequest;
import Requests.PatternRequest;
import Requests.UpdatePatternsRanksRequest;
import Responses.MessageChangeResponse;

public class LogFilePattern {

    // Add a pattern to a log file. If the pattern is already in the log file, it
    // will not be added again.
    public MessageChangeResponse addPatternToLogFile(PatternLogFileRequest addPatternToLogFile) {
        String message = "";
        boolean changed = false;

        boolean patternExists = checkIfPatternExistsInLogFile(addPatternToLogFile.getLogFileID(),
                addPatternToLogFile.getPatternID());
        if (patternExists) {
            message = "Das Pattern ist bereits in diesem Logfile vorhanden";
            return new MessageChangeResponse(message, changed);
        }
        try (Connection connection = DBConnection.connectToDB()) {
            String addToLogFilePattern = "INSERT INTO logfile_pattern (logfile_id, pattern_id, reihenfolge_platz_pattern) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(addToLogFilePattern);
            statement.setInt(1, addPatternToLogFile.getLogFileID());
            statement.setInt(2, addPatternToLogFile.getPatternID());
            statement.setInt(3, addPatternToLogFile.getRank());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                LogEntries logEntries = new LogEntries();
                LogFileRequest logFileRequest = LogFile.getLogFileByID(addPatternToLogFile.getLogFileID());
                LogFilePattern logFilePattern = new LogFilePattern();
                PatternRequest pattern = logFilePattern.getPatternByID(addPatternToLogFile.getPatternID());
                logEntries.processLogFileWithoutCheckingLastRow(logFileRequest, pattern);
                changed = true;
                message = "Pattern wurde hinzugefügt";
            } else {
                message = "Fehler beim Hinzufügen des Patterns zu dem Logfile";
            }

        } catch (SQLException e) {
            message = e.getMessage();
        }
        return new MessageChangeResponse(message, changed);
    }

    // Get all patterns for a log file. The patterns are ordered by their rank.
    public ArrayList<PatternRequest> getPatternsForLogFile(int logFileID) {
        ArrayList<PatternRequest> patterns = new ArrayList<>();
        try (Connection connection = DBConnection.connectToDB()) {
            String query = "SELECT lp.pattern_id, lp.reihenfolge_platz_pattern, p.pattern_name, p.pattern, p.pattern_beschreibung, p.schweregrad "
                    + "FROM logfile_pattern lp JOIN pattern p ON lp.pattern_id = p.pattern_id WHERE logfile_id = ? "
                    + "ORDER BY reihenfolge_platz_pattern";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, logFileID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                patterns.add(new PatternRequest(resultSet.getInt("pattern_id"), resultSet.getString("pattern_name"),
                        resultSet.getString("pattern"), resultSet.getString("pattern_beschreibung"),
                        resultSet.getString("schweregrad"), resultSet.getInt("reihenfolge_platz_pattern")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patterns;
    }

    public PatternRequest getPatternByID(int patternID) {
        PatternRequest pattern = null;
        try (Connection connection = DBConnection.connectToDB()) {
            String query = "SELECT * FROM pattern WHERE pattern_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, patternID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                pattern = new PatternRequest(resultSet.getInt("pattern_id"), resultSet.getString("pattern_name"),
                        resultSet.getString("pattern"), resultSet.getString("pattern_beschreibung"),
                        resultSet.getString("schweregrad"), 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pattern;
    }

    // Update the rank of a pattern in a log file
    public MessageChangeResponse updatePatternRanks(UpdatePatternsRanksRequest updatePatternsRanksRequest) {
        String message = "";
        boolean changed = false;
        int patternsSize = updatePatternsRanksRequest.getPatterns().size();
        int totalRowsAffected = 0;

        try (Connection connection = DBConnection.connectToDB()) {
            connection.setAutoCommit(false);
            String updateRank = "UPDATE logfile_pattern SET reihenfolge_platz_pattern = ? WHERE logfile_id = ? AND pattern_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateRank)) {
                for (PatternRequest pattern : updatePatternsRanksRequest.getPatterns()) {
                    statement.setInt(1, pattern.getRank());
                    statement.setInt(2, updatePatternsRanksRequest.getLogFileID());
                    statement.setInt(3, pattern.getPatternId());
                    totalRowsAffected += statement.executeUpdate();
                }
            }

            if (totalRowsAffected == patternsSize) {
                connection.commit();
                message = "Reihenfolge der Patterns wurde aktualisiert";
                changed = true;
            } else {
                connection.rollback();
                message = "Fehler beim Aktualisieren der Reihenfolge der Patterns im Logfile";
            }
        } catch (SQLException e) {
            message = "Datenbankfehler: " + e.getMessage();
        }

        return new MessageChangeResponse(message, changed);
    }

    // Delete a pattern from a log file. If the pattern is not in the log file, the
    // function will return an error message.
    public MessageChangeResponse deletePatternFromLogFile(PatternLogFileRequest deletePatternFromLogFile) {
        String message = "";
        boolean changed = false;
        boolean patternExists = checkIfPatternIsInLogFile(deletePatternFromLogFile.getLogFileID(),
                deletePatternFromLogFile.getPatternID());
        if (!patternExists) {
            message = "Das Pattern ist in diesem Logfile nicht vorhanden";
            return new MessageChangeResponse(message, changed);
        }
        try (Connection connection = DBConnection.connectToDB()) {
            String deletePattern = "DELETE FROM logfile_pattern WHERE logfile_id = ? AND pattern_id = ?";
            PreparedStatement statement = connection.prepareStatement(deletePattern);
            statement.setInt(1, deletePatternFromLogFile.getLogFileID());
            statement.setInt(2, deletePatternFromLogFile.getPatternID());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                message = "Pattern wurde gelöscht";
                changed = true;
            } else {
                message = "Fehler beim Löschen des Patterns aus dem Logfile";
            }
        } catch (SQLException e) {
            message = e.getMessage();
        }
        return new MessageChangeResponse(message, changed);
    }

    // Check if a pattern is in a log file. Used to prevent duplicate patterns in a
    // log file.
    public boolean checkIfPatternIsInLogFile(int logFileID, int patternID) {
        boolean patternExists = false;
        try (Connection connection = DBConnection.connectToDB()) {
            String checkIfPatternExists = "SELECT * FROM logfile_pattern WHERE logfile_id = ? AND pattern_id = ?";
            PreparedStatement statement = connection.prepareStatement(checkIfPatternExists);
            statement.setInt(1, logFileID);
            statement.setInt(2, patternID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                patternExists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patternExists;
    }

    // Check if a pattern exists in a log file. Used to prevent duplicate patterns
    // in a log file.
    public boolean checkIfPatternExistsInLogFile(int logFileID, int patternID) {
        boolean patternExists = false;
        try (Connection connection = DBConnection.connectToDB()) {
            String checkIfPatternExists = "SELECT * FROM logfile_pattern WHERE logfile_id = ? AND pattern_id = ?";
            PreparedStatement statement = connection.prepareStatement(checkIfPatternExists);
            statement.setInt(1, logFileID);
            statement.setInt(2, patternID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                patternExists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patternExists;
    }

}
