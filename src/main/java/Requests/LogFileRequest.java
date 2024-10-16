package Requests;

public class LogFileRequest {
    private int logFileID;
    private String logFileName;
    private String logFilePath;
    private java.sql.Timestamp created;
    private java.sql.Timestamp changed;
    private int lastRow;

    public LogFileRequest() {
    }

    public LogFileRequest(int logFileID, String logFileName, String logFilePath, java.sql.Timestamp created,
            java.sql.Timestamp changed,
            int lastRow) {
        this.logFileID = logFileID;
        this.logFileName = logFileName;
        this.logFilePath = logFilePath;
        this.created = created;
        this.changed = changed;
        this.lastRow = lastRow;
    }

    public LogFileRequest(String logFileName, String logFilePath, java.sql.Timestamp created,
            java.sql.Timestamp changed,
            int lastRow) {
        this.logFileName = logFileName;
        this.logFilePath = logFilePath;
        this.created = created;
        this.changed = changed;
        this.lastRow = lastRow;
    }

    public LogFileRequest(int logFileID, String logFileName, String logFilePath, java.sql.Timestamp changed,
            int lastRow) {
        this.logFileID = logFileID;
        this.logFileName = logFileName;
        this.logFilePath = logFilePath;
        this.changed = changed;
        this.lastRow = lastRow;
    }

    public LogFileRequest(int logFileID, String logFileName, String logFilePath) {
        this.logFileID = logFileID;
        this.logFileName = logFileName;
        this.logFilePath = logFilePath;
    }

    public LogFileRequest(String logFileName, String logFilePath) {
        this.logFileName = logFileName;
        this.logFilePath = logFilePath;
    }

    public int getLogFileID() {
        return logFileID;
    }

    public void setLogFileID(int logFileID) {
        this.logFileID = logFileID;
    }

    public String getLogFileName() {
        return this.logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public String getLogFilePath() {
        return this.logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public java.sql.Timestamp getCreated() {
        return this.created;
    }

    public void setCreated(java.sql.Timestamp created) {
        this.created = created;
    }

    public java.sql.Timestamp getChanged() {
        return this.changed;
    }

    public void setChanged(java.sql.Timestamp changed) {
        this.changed = changed;

    }

    public int getLastRow() {
        return this.lastRow;
    }

    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

}
