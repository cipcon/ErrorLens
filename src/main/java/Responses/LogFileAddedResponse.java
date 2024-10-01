package Responses;

public class LogFileAddedResponse {
    private String message;
    private boolean logFileAdded;

    public LogFileAddedResponse(String message, boolean logFileAdded) {
        this.message = message;
        this.logFileAdded = logFileAdded;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isLogFileAdded() {
        return logFileAdded;
    }

    public void setLogFileAdded(boolean logFileAdded) {
        this.logFileAdded = logFileAdded;
    }

}
