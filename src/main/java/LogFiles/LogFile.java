package LogFiles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import DBConnection.DBConnection;
import Requests.AddLogFileResponse;
import Requests.LogFilePathResponse;

public class LogFile {
    public String addLogFile(AddLogFileResponse addLogFileResponse) {
        String message = "";
        boolean logFileNameExists = LogFileNameExists(addLogFileResponse.getLogFileName());
        LogFilePathResponse logFilePath = LogFilePathExists(addLogFileResponse.getLogFilePath());

        if (logFileNameExists) {
            message = "Der Name der Logdatei ist bereits vorhanden. Bitte wählen Sie einen anderen Namen";
            return message;
        }

        if (logFilePath.isExist()) {
            message = "Die Logdatei befindet sich schon in der Liste. Der Name der Datei ist: "
                    + logFilePath.getlogFileName();
            return message;
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
                return message;
            } else {
                message = "";
            }

        } catch (Exception e) {
            message = e.getMessage();
        }
        return message;
    }

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

    public static void main(String[] args) {
        System.out.println(LogFile.LogFileNameExists("Nziua"));
        LogFilePathResponse logFilePathResponse = LogFilePathExists("12345");
        System.out.println(logFilePathResponse.isExist());
        System.out.println(logFilePathResponse.getlogFileName());
    }
}
