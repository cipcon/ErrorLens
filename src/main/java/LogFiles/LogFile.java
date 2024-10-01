package LogFiles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import DBConnection.DBConnection;
import Requests.AddLogFileRequest;
import Responses.LogFileAddedResponse;
import Responses.LogFilePathResponse;

public class LogFile {

    // Add a log file to the database
    public LogFileAddedResponse addLogFile(AddLogFileRequest addLogFileResponse) {
        String message = "";
        boolean logFileAdded = false;
        boolean logFileNameExists = LogFileNameExists(addLogFileResponse.getLogFileName());
        LogFilePathResponse logFilePath = LogFilePathExists(addLogFileResponse.getLogFilePath());

        if (logFileNameExists) {
            message = "Der Name der Logdatei ist bereits vorhanden. Bitte wählen Sie einen anderen Namen";
            return new LogFileAddedResponse(message, logFileAdded);
        }

        if (logFilePath.isExist()) {
            message = "Die Logdatei befindet sich schon in der Liste. Der Name der Datei ist: "
                    + logFilePath.getlogFileName();
            return new LogFileAddedResponse(message, logFileAdded);
        }

        try (Connection connection = DBConnection.connectToDB()) {
            String addLogFile = "INSERT INTO logfile(logfile_name, logfile_pfad, geaendert_am, erstellungs_datum, letze_zeile) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(addLogFile);
            statement.setString(1, addLogFileResponse.getLogFileName());
            statement.setString(2, addLogFileResponse.getLogFilePath());
            statement.setDate(3, addLogFileResponse.getChanged());
            statement.setDate(4, addLogFileResponse.getCreated());
            statement.setInt(5, addLogFileResponse.getLastRow());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                message = "Logdatei wurde erfolgreich hinzugefügt";
                logFileAdded = true;
                return new LogFileAddedResponse(message, logFileAdded);
            } else {
                message = "";
            }

        } catch (Exception e) {
            message = e.getMessage();
        }
        return new LogFileAddedResponse(message, logFileAdded);
    }

    // Return an ArrayList with the name and the path of the logfile and the last
    // time it was aktualised(This represents also last time the logfile was
    // filtered).
    public static ArrayList<AddLogFileRequest> listLogFiles() {
        ArrayList<AddLogFileRequest> allLogFiles = new ArrayList<>();
        String listFromLogFiles = "SELECT logfile_id, logfile_name, logfile_pfad, geaendert_am FROM logfile";

        try (Connection connection = DBConnection.connectToDB()) {
            PreparedStatement statement = connection.prepareStatement(listFromLogFiles);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int logFileID = resultSet.getInt("logfile_id");
                String logFileName = resultSet.getString("logfile_name");
                String logFilePath = resultSet.getString("logfile_pfad");
                java.sql.Date changed = resultSet.getDate("geaendert_am");
                AddLogFileRequest addLogFileRequest = new AddLogFileRequest(logFileID, logFileName, logFilePath,
                        changed);
                allLogFiles.add(addLogFileRequest);
            }
            return allLogFiles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allLogFiles;
    }

    // Check if a log file name exists in the database
    public static boolean LogFileNameExists(String logFileName) {
        boolean exist = false;
        try (Connection connection = DBConnection.connectToDB()) {
            String checkLogFileName = "SELECT logfile_name FROM logfile WHERE logfile_name = ?";
            PreparedStatement statement = connection.prepareStatement(checkLogFileName);
            statement.setString(1, logFileName);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                exist = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return exist;
    }

    public static LogFilePathResponse LogFilePathExists(String logFilePath) {
        boolean exist = false;
        String logFileName = "";
        try (Connection connection = DBConnection.connectToDB()) {
            String checkLogFilePath = "SELECT logfile_pfad, logfile_name FROM logfile WHERE logfile_pfad = ?";
            PreparedStatement statement = connection.prepareStatement(checkLogFilePath);
            statement.setString(1, logFilePath);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                logFileName = resultSet.getString("logfile_name");
                exist = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new LogFilePathResponse(exist, logFileName);
    }

    // Test the addLogFile method
    public static void main(String[] args) {
        /*
         * LogFile logFile = new LogFile();
         * 
         * java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
         * AddLogFileRequest addLogFileResponse = new AddLogFileRequest("Banica",
         * "Junior", today, today, 69);
         * 
         * LogFileAddedResponse logFileAddedResponse =
         * logFile.addLogFile(addLogFileResponse);
         * 
         * System.out.println(logFileAddedResponse.getMessage());
         * System.out.println(logFileAddedResponse.isLogFileAdded());
         */

        ArrayList<AddLogFileRequest> listLogFiles = LogFile.listLogFiles();
        for (AddLogFileRequest logFile : listLogFiles) {
            System.out.println(logFile.getLogFileName());
        }
    }
}
