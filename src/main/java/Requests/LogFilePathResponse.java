package Requests;

public class LogFilePathResponse {
    private boolean exist;
    private String logFileName;

    public LogFilePathResponse(boolean exist, String logFileName) {
        this.exist = exist;
        this.logFileName = logFileName;
    }

    public boolean isExist() {
        return this.exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public String getlogFileName() {
        return this.logFileName;
    }

    public void setlogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

}
