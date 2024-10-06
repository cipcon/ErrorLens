package Patterns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import DBConnection.DBConnection;
import Requests.PatternRequest;
import Responses.MessageChangeResponse;

public class Patterns {
    // Add a new pattern to the database
    public MessageChangeResponse addPattern(PatternRequest addPatternRequest) {
        String message = "";
        boolean patternAdded = false;
        boolean patternNameExists = PatternNameExists(addPatternRequest.getPatternName());
        if (patternNameExists) {
            message = "Der Name des Patterns ist bereits vorhanden. Bitte wählen Sie einen anderen Namen";
            return new MessageChangeResponse(message, patternAdded);
        }
        try (Connection connection = DBConnection.connectToDB()) {
            String addPattern = "INSERT INTO pattern (pattern_name, pattern, pattern_beschreibung, schweregrad) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(addPattern);
            statement.setString(1, addPatternRequest.getPatternName());
            statement.setString(2, addPatternRequest.getPattern());
            statement.setString(3, addPatternRequest.getPatternDescription());
            statement.setString(4, addPatternRequest.getSeverity());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                message = "Pattern wurde erfolgreich hinzugefügt";
                patternAdded = true;
                return new MessageChangeResponse(message, patternAdded);
            } else {
                message = "Pattern konnte nicht hinzugefügt werden";
                return new MessageChangeResponse(message, patternAdded);
            }
        } catch (SQLException e) {
            message = e.getMessage();
        }
        return new MessageChangeResponse(message, patternAdded);
    }

    // Return an ArrayList with all patterns from the database
    public ArrayList<PatternRequest> listPatterns() {
        ArrayList<PatternRequest> allPatterns = new ArrayList<>();
        String listFromPatterns = "SELECT pattern_id, pattern_name, pattern, pattern_beschreibung, schweregrad FROM pattern";
        try (Connection connection = DBConnection.connectToDB()) {
            PreparedStatement statement = connection.prepareStatement(listFromPatterns);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int patternId = resultSet.getInt("pattern_id");
                String patternName = resultSet.getString("pattern_name");
                String pattern = resultSet.getString("pattern");
                String patternDescription = resultSet.getString("pattern_beschreibung");
                String severity = resultSet.getString("schweregrad");
                PatternRequest addPatternRequest = new PatternRequest(patternId, patternName, pattern,
                        patternDescription,
                        severity);
                allPatterns.add(addPatternRequest);
            }
            return allPatterns;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allPatterns;
    }

    public int getPatternID(String patternName) {
        String getPatternID = "SELECT pattern_id FROM pattern WHERE pattern_name = ?";
        try (Connection connection = DBConnection.connectToDB()) {
            PreparedStatement statement = connection.prepareStatement(getPatternID);
            statement.setString(1, patternName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int patternID = resultSet.getInt("pattern_id");
                return patternID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public MessageChangeResponse updatePattern(PatternRequest updatePatternRequest) {
        String message = "";
        boolean patternUpdated = false;
        boolean patternNameExists = PatternNameExists(updatePatternRequest.getPatternName());
        if (patternNameExists) {
            message = "Der Name des Patterns ist bereits vorhanden. Bitte wählen Sie einen anderen Namen";
            return new MessageChangeResponse(message, patternUpdated);
        }

        try (Connection connection = DBConnection.connectToDB()) {
            String updatePattern = "UPDATE pattern SET pattern_name = ?, pattern = ?, pattern_beschreibung = ?, schweregrad = ? WHERE pattern_id = ?";
            PreparedStatement statement = connection.prepareStatement(updatePattern);
            statement.setString(1, updatePatternRequest.getPatternName());
            statement.setString(2, updatePatternRequest.getPattern());
            statement.setString(3, updatePatternRequest.getPatternDescription());
            statement.setString(4, updatePatternRequest.getSeverity());
            statement.setInt(5, updatePatternRequest.getPatternId());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                message = "Pattern wurde erfolgreich aktualisiert";
                patternUpdated = true;
                return new MessageChangeResponse(message, patternUpdated);
            } else {
                message = "";
            }
        } catch (SQLException e) {
            message = e.getMessage();
        }
        return new MessageChangeResponse(message, patternUpdated);
    }

    public MessageChangeResponse deletePattern(int patternId) {
        String message = "";
        boolean patternDeleted = false;
        try (Connection connection = DBConnection.connectToDB()) {
            String deletePattern = "DELETE FROM pattern WHERE pattern_id = ?";
            PreparedStatement statement = connection.prepareStatement(deletePattern);
            statement.setInt(1, patternId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                message = "Pattern wurde erfolgreich gelöscht";
                patternDeleted = true;
                return new MessageChangeResponse(message, patternDeleted);
            } else {
                message = "";
            }
        } catch (SQLException e) {
            message = e.getMessage();
        }
        return new MessageChangeResponse(message, patternDeleted);
    }

    // Check if a pattern with the same name already exists
    private boolean PatternNameExists(String patternName) {
        boolean exist = false;
        String checkPatternName = "SELECT pattern_name FROM pattern WHERE pattern_name = ?";
        try (Connection connection = DBConnection.connectToDB()) {
            PreparedStatement statement = connection.prepareStatement(checkPatternName);
            statement.setString(1, patternName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                exist = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exist;
    }

}
