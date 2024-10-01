package Requests;

import java.util.Date;

public class LogFileRequest {
    private int logFileID;
    private String logFileName;
    private String logFilePath;
    private java.sql.Date created;
    private java.sql.Date changed;
    private int lastRow;

    public LogFileRequest(String logFileName, String logFilePath, java.sql.Date created, java.sql.Date changed,
            int lastRow) {
        this.logFileName = logFileName;
        this.logFilePath = logFilePath;
        this.created = created;
        this.changed = changed;
        this.lastRow = lastRow;
    }

    public LogFileRequest(int logFileID, String logFileName, String logFilePath, java.sql.Date changed) {
        this.logFileID = logFileID;
        this.logFileName = logFileName;
        this.logFilePath = logFilePath;
        this.changed = changed;
    }

    public LogFileRequest(int logFileID, String logFileName, String logFilePath) {
        this.logFileID = logFileID;
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

    public java.sql.Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = (java.sql.Date) created;
    }

    public java.sql.Date getChanged() {
        return this.changed;
    }

    public void setChanged(Date changed) {
        this.changed = (java.sql.Date) changed;

    }

    public int getLastRow() {
        return this.lastRow;
    }

    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

}
