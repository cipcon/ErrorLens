package Requests;

public class AddLogFileResponse {
    private String logFileName;
    private String logFilePath;
    private java.sql.Date created;
    private java.sql.Date changed;
    private int lastRow;

    public AddLogFileResponse(String logFileName, String logFilePath, java.sql.Date created, java.sql.Date changed,
            int lastRow) {
        this.logFileName = logFileName;
        this.logFilePath = logFilePath;
        this.created = created;
        this.changed = changed;
        this.lastRow = lastRow;
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

    public void setCreated(java.sql.Date created) {
        this.created = created;
    }

    public java.sql.Date getChanged() {
        return this.changed;
    }

    public void setChanged(java.sql.Date changed) {
        this.changed = changed;
    }

    public int getLastRow() {
        return this.lastRow;
    }

    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

}
