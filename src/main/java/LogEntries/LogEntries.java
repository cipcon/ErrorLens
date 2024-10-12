package LogEntries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import DBConnection.DBConnection;
import LogFilePattern.LogFilePattern;
import LogFiles.FileChangeChecker;
import Requests.LogFileRequest;
import Requests.PatternRequest;
import Responses.LogentriesResponse;
import Responses.MessageChangeResponse;

public class LogEntries {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /*
     * public void processAllLogFiles() {
     * ArrayList<LogFileRequest> logFiles = LogFile.listLogFiles();
     * for (LogFileRequest logFile : logFiles) {
     * processLogFile(logFile);
     * }
     * }
     */

    public void processLogFile(LogFileRequest logFile) {
        LogFilePattern logFilePattern = new LogFilePattern();
        ArrayList<PatternRequest> patterns = logFilePattern
                .getPatternsForLogFile(logFile.getLogFileID());

        for (PatternRequest pattern : patterns) {
            System.out.println("Pattern: " + pattern.getPattern());
        }
        String filePath = FileChangeChecker.convertToWSLPath(logFile.getLogFilePath());
        File file = new File(filePath);
        int lastProcessedLine = logFile.getLastRow();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int currentLine = 0;

            // Skip to the last processed line
            while (currentLine < lastProcessedLine && reader.readLine() != null) {
                currentLine++;
            }

            while ((line = reader.readLine()) != null) {
                currentLine++;
                processLogEntry(line, logFile.getLogFileID(), patterns);
            }

            // Update the last processed line in the database
            updateLastProcessedLine(logFile.getLogFileID(), currentLine);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////////////////
    public void processLogFileWithoutCheckingLastRow(LogFileRequest logFile, PatternRequest patternDetails) {
        System.out.println("Processing log file without checking last row: " + logFile.getLogFileName() + " with ID: "
                + logFile.getLogFileID() + " and path: " + logFile.getLogFilePath() + " and last row: "
                + logFile.getLastRow());

        String filePath = FileChangeChecker.convertToWSLPath(logFile.getLogFilePath());
        File file = new File(filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                LocalDateTime entryDate = extractDate(line);
                if (entryDate == null) {
                    continue; // Skip entries without a valid date, but continue processing
                }

                if (matchPattern(line, patternDetails.getPattern())) {
                    insertLogEntry(line, logFile.getLogFileID(), patternDetails.getPatternId(), entryDate);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /////////////////////////////////////////////////////////////////////////

    private void processLogEntry(String logEntry, int logFileId, ArrayList<PatternRequest> patterns) {
        LocalDateTime entryDate = extractDate(logEntry);
        if (entryDate == null) {
            return; // Skip entries without a valid date
        }

        for (PatternRequest pattern : patterns) {
            if (matchPattern(logEntry, pattern.getPattern())) {
                insertLogEntry(logEntry, logFileId, pattern.getPatternId(), entryDate);
            }
        }
    }

    private LocalDateTime extractDate(String logEntry) {
        // Assuming log entries start with a date in the format "YYYY-MM-DD HH:MM:SS"
        Pattern datePattern = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})");
        Matcher matcher = datePattern.matcher(logEntry);
        if (matcher.find()) {
            String dateStr = matcher.group(1);
            return LocalDateTime.parse(dateStr, DATE_FORMAT);
        }
        return null;
    }

    private boolean matchPattern(String logEntry, String patternRegex) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(logEntry);
        return matcher.find();
    }

    private void insertLogEntry(String entry, int logFileId, int patternId, LocalDateTime entryDate) {
        if (checkforDuplicate(entry, logFileId)) {
            System.out.println("Log entry already exists: " + entry);
            return;
        }

        String insertQuery = "INSERT INTO logeintrag (logeintrag_beschreibung, logfile_id, pattern_id, gefunden_am) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.connectToDB();
                PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, entry);
            statement.setInt(2, logFileId);
            statement.setInt(3, patternId);
            statement.setTimestamp(4, Timestamp.valueOf(entryDate));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkforDuplicate(String entry, int logFileId) {
        String selectQuery = "SELECT COUNT(*) FROM logeintrag WHERE logeintrag_beschreibung = ? AND logfile_id = ?";
        try (Connection connection = DBConnection.connectToDB();
                PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, entry);
            statement.setInt(2, logFileId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<LogentriesResponse> getLogentries() {
        ArrayList<LogentriesResponse> logentries = new ArrayList<>();
        try (Connection connection = DBConnection.connectToDB()) {
            String selectQuery = "SELECT le.logeintrag_id, le.logeintrag_beschreibung, le.gefunden_am, lf.logfile_name, p.pattern_name, p.schweregrad "
                    + "FROM logeintrag le JOIN logfile lf ON le.logfile_id = lf.logfile_id "
                    + "JOIN pattern p ON le.pattern_id = p.pattern_id "
                    + "ORDER BY le.gefunden_am DESC";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                LogentriesResponse logentry = new LogentriesResponse();
                logentry.setLogeintrag_id(resultSet.getInt("logeintrag_id"));
                logentry.setLogeintrag_beschreibung(resultSet.getString("logeintrag_beschreibung"));
                logentry.setGefunden_am(resultSet.getDate("gefunden_am"));
                logentry.setLogfile_name(resultSet.getString("logfile_name"));
                logentry.setPattern_name(resultSet.getString("pattern_name"));
                logentry.setSchweregrad(resultSet.getString("schweregrad"));
                logentries.add(logentry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logentries;
    }

    private void updateLastProcessedLine(int logFileId, int lastProcessedLine) {
        String updateQuery = "UPDATE logfile SET letze_zeile = ? WHERE logfile_id = ?";
        try (Connection connection = DBConnection.connectToDB();
                PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setInt(1, lastProcessedLine);
            statement.setInt(2, logFileId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MessageChangeResponse deleteLogEntry(int logEntryId) {
        String deleteQuery = "DELETE FROM logeintrag WHERE logeintrag_id = ?";
        try (Connection connection = DBConnection.connectToDB();
                PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setInt(1, logEntryId);
            statement.executeUpdate();
            return new MessageChangeResponse("Logeintrag erfolgreich gelöscht", true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new MessageChangeResponse("Fehler beim Löschen des Logeintrags", false);
    }
}
