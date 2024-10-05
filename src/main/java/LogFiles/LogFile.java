package LogFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import DBConnection.DBConnection;
import Requests.LogFileRequest;
import Responses.MessageChangeResponse;
import jakarta.enterprise.context.ApplicationScoped;
import Responses.LogFilePathResponse;

@ApplicationScoped
public class LogFile {

    /*
     * Problem with the creation time of the log file:
     * The problem is not in the code, but rather in how the underlying file system
     * and operating system report the creation time. Here are a few points to
     * consider:
     * 
     * File systems: Some file systems, particularly on Linux, don't store or don't
     * reliably store the creation time of files. They only maintain the last
     * modified time, last access time, and inode change time.
     * WSL (Windows Subsystem for Linux):
     * WSL has had issues in the past with accurately reporting file creation times
     * for Windows files.
     * Java's BasicFileAttributes: The creationTime() method of BasicFileAttributes
     * might return the Unix epoch start time (1970-01-01) when the actual creation
     * time is not available or not supported by the file system.
     */

    // Add a log file to the database
    public MessageChangeResponse addLogFile(LogFileRequest addLogFileResponse) {
        String message = "";
        boolean logFileAdded = false;
        boolean logFileNameExists = LogFileNameExists(addLogFileResponse.getLogFileName());
        LogFilePathResponse logFilePath = LogFilePathExists(addLogFileResponse.getLogFilePath());

        // Try both original and converted paths
        String originalPath = addLogFileResponse.getLogFilePath();
        String wslPath = convertToWSLPath(originalPath);

        Path path = Paths.get(wslPath);
        boolean pathExists = Files.exists(path);

        System.out.println("Original path: " + originalPath);
        System.out.println("Converted WSL path: " + wslPath);
        System.out.println("Path exists (WSL): " + pathExists);
        System.out.println("Absolute path: " + path.toAbsolutePath().toString());

        if (!pathExists) {
            // If WSL path doesn't exist, try the original Windows path
            path = Paths.get(originalPath);
            pathExists = Files.exists(path);
            System.out.println("Path exists (Windows): " + pathExists);
            System.out.println("Absolute Windows path: " + path.toAbsolutePath().toString());
        }

        if (!pathExists) {
            message = "Der angegebene Dateipfad existiert nicht: " + originalPath;
            System.out.println(message);

            // Add more detailed error information
            try {
                Files.getFileAttributeView(path, java.nio.file.attribute.BasicFileAttributeView.class);
            } catch (Exception e) {
                System.out.println("Error accessing file: " + e.getMessage());
                e.printStackTrace();
            }

            return new MessageChangeResponse(message, logFileAdded);
        }

        // Set the creation time and last modified time to the current time
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            java.sql.Timestamp changed = new java.sql.Timestamp(attributes.lastModifiedTime().toMillis());
            addLogFileResponse.setCreated(changed);
            addLogFileResponse.setChanged(changed);
            addLogFileResponse.setLastRow(0);
        } catch (IOException e) {
            System.out.println("Error reading file attributes: " + e.getMessage());
            e.printStackTrace();
            message = "Fehler beim Lesen der Dateiattribute: " + e.getMessage();
            return new MessageChangeResponse(message, logFileAdded);
        }

        if (logFilePath.isExist()) {
            message = "Die Logdatei befindet sich schon in der Liste. Der Name der Datei ist: "
                    + logFilePath.getlogFileName();
            return new MessageChangeResponse(message, logFileAdded);
        }

        if (logFileNameExists) {
            message = "Der Name der Logdatei ist bereits vorhanden. Bitte wählen Sie einen anderen Namen";
            return new MessageChangeResponse(message, logFileAdded);
        }

        try (Connection connection = DBConnection.connectToDB()) {
            String addLogFile = "INSERT INTO logfile(logfile_name, logfile_pfad, geaendert_am, erstellungs_datum, letze_zeile) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(addLogFile);
            statement.setString(1, addLogFileResponse.getLogFileName());
            statement.setString(2, addLogFileResponse.getLogFilePath());
            statement.setTimestamp(3, addLogFileResponse.getChanged());
            statement.setTimestamp(4, addLogFileResponse.getCreated());
            statement.setInt(5, addLogFileResponse.getLastRow());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                message = "Logdatei wurde erfolgreich hinzugefügt";
                logFileAdded = true;
                return new MessageChangeResponse(message, logFileAdded);
            } else {
                message = "";
            }

        } catch (Exception e) {
            message = e.getMessage();
        }
        return new MessageChangeResponse(message, logFileAdded);
    }

    private static String convertToWSLPath(String path) {
        // Replace all backslashes with forward slashes
        String unixPath = path.replace("\\", "/");

        // Check if the path is a WSL path
        if (unixPath.startsWith("//wsl.localhost/")) {
            // Remove the leading //wsl.localhost/ and prepend /mnt/
            return "/mnt/" + unixPath.substring("//wsl.localhost/".length());
        }

        // Check if the path is already a WSL path
        if (unixPath.startsWith("/mnt/") || unixPath.startsWith("/")) {
            return unixPath;
        }

        // Handle Windows path
        // Remove drive letter and colon
        String pathWithoutDrive = unixPath.replaceFirst("^[A-Za-z]:", "");

        // Prepend /mnt/ and lowercase drive letter
        return "/mnt/" + Character.toLowerCase(unixPath.charAt(0)) + pathWithoutDrive;
    }

    // Return an ArrayList with the name and the path of the logfile and the last
    // time it was aktualised(This represents also last time the logfile was
    // filtered).
    public static ArrayList<LogFileRequest> listLogFiles() {
        ArrayList<LogFileRequest> allLogFiles = new ArrayList<>();
        String listFromLogFiles = "SELECT logfile_id, logfile_name, logfile_pfad, geaendert_am FROM logfile";

        try (Connection connection = DBConnection.connectToDB()) {
            PreparedStatement statement = connection.prepareStatement(listFromLogFiles);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int logFileID = resultSet.getInt("logfile_id");
                String logFileName = resultSet.getString("logfile_name");
                String logFilePath = resultSet.getString("logfile_pfad");
                java.sql.Timestamp changed = resultSet.getTimestamp("geaendert_am");
                LogFileRequest addLogFileRequest = new LogFileRequest(logFileID, logFileName, logFilePath,
                        changed);
                allLogFiles.add(addLogFileRequest);
            }
            return allLogFiles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allLogFiles;
    }

    // Update the name and the path of a log file
    public MessageChangeResponse updateLogFile(LogFileRequest updateLogFileRequest) {
        String message = "";
        boolean logFileUpdated = false;
        if (LogFileNameExists(updateLogFileRequest.getLogFileName())) {
            message = "Der Name der Logdatei ist bereits vorhanden. Bitte wählen Sie einen anderen Namen";
            return new MessageChangeResponse(message, logFileUpdated);
        }

        try (Connection connection = DBConnection.connectToDB()) {
            String updateLogFileName = "UPDATE logfile SET logfile_name = ?, logfile_pfad = ? WHERE logfile_id = ?";
            PreparedStatement statement = connection.prepareStatement(updateLogFileName);
            statement.setString(1, updateLogFileRequest.getLogFileName());
            statement.setString(2, updateLogFileRequest.getLogFilePath());
            statement.setInt(3, updateLogFileRequest.getLogFileID());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                message = "Logdatei wurde erfolgreich aktualisiert";
                logFileUpdated = true;
                return new MessageChangeResponse(message, logFileUpdated);
            } else {
                message = "";
            }
        } catch (Exception e) {
            message = e.getMessage();
        }
        return new MessageChangeResponse(message, logFileUpdated);
    }

    public MessageChangeResponse deleteLogFile(int logFileID) {
        String message = "";
        boolean logFileDeleted = false;
        try (Connection connection = DBConnection.connectToDB()) {
            String deleteLogFile = "DELETE FROM logfile WHERE logfile_id = ?";
            PreparedStatement statement = connection.prepareStatement(deleteLogFile);
            statement.setInt(1, logFileID);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                message = "Logdatei wurde erfolgreich gelöscht";
                logFileDeleted = true;
                return new MessageChangeResponse(message, logFileDeleted);
            } else {
                message = "";
            }
        } catch (Exception e) {
            message = e.getMessage();
        }
        return new MessageChangeResponse(message, logFileDeleted);
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

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> scheduledFuture;

    public static void startFileChangeChecker(long interval, TimeUnit timeUnit) {
        scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Running file change check..."); // Add this line for logging
            checkAllFilesForChanges();
        }, 0, interval, timeUnit);
    }

    private static void checkAllFilesForChanges() {
        ArrayList<LogFileRequest> logFiles = listLogFiles();
        for (LogFileRequest logFile : logFiles) {
            try {
                String wslPath = convertToWSLPath(logFile.getLogFilePath());
                Path path = Paths.get(wslPath);
                if (Files.exists(path)) {
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    FileTime lastModifiedTime = attrs.lastModifiedTime();
                    java.sql.Timestamp lastModified = new java.sql.Timestamp(lastModifiedTime.toMillis());

                    if (lastModified.after(logFile.getChanged())) {
                        // File has been modified, update the database
                        updateLastModifiedTime(logFile.getLogFileID(), lastModified);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error checking file: " + logFile.getLogFilePath() + " - " + e.getMessage());
            }
        }
    }

    public static MessageChangeResponse updateLastModifiedTime(int logFileId, java.sql.Timestamp lastModified) {
        String message = "";
        boolean logFileUpdated = false;
        try (Connection connection = DBConnection.connectToDB()) {
            String updateLogFileName = "UPDATE logfile SET geaendert_am = ? WHERE logfile_id = ?";
            PreparedStatement statement = connection.prepareStatement(updateLogFileName);
            statement.setTimestamp(1, lastModified);
            statement.setInt(2, logFileId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                message = "Logdatei wurde erfolgreich aktualisiert";
                logFileUpdated = true;
                return new MessageChangeResponse(message, logFileUpdated);
            } else {
                message = "Fehler beim Aktualisieren der Logdatei";
            }
        } catch (Exception e) {
            message = e.getMessage();
        }
        return new MessageChangeResponse(message, logFileUpdated);
    }

    public static void stopFileChangeChecker() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}