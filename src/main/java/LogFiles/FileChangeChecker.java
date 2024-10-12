package LogFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import DBConnection.DBConnection;
import LogEntries.LogEntries;
import Requests.LogFileRequest;
import Responses.MessageChangeResponse;

public class FileChangeChecker {
    private static ScheduledExecutorService scheduler;

    public static void startFileChangeChecker(long interval, TimeUnit timeUnit) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Running file change check..."); // Add this line for logging
            checkAllFilesForChanges();
        }, 0, interval, timeUnit);
    }

    public static void checkAllFilesForChanges() {
        ArrayList<LogFileRequest> logFiles = LogFile.listLogFiles();
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
                        LogEntries logEntries = new LogEntries();
                        logEntries.processLogFile(logFile);
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

    public static String convertToWSLPath(String path) {
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

    public static void stopFileChangeChecker() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Scheduler did not terminate in time");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
