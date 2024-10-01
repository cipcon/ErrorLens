package Requests;

public class PatternLogFileRequest {
    private int logFileID;
    private int patternID;
    private int rank;

    public PatternLogFileRequest(int logFileID, int patternID, int rank) {
        this.logFileID = logFileID;
        this.patternID = patternID;
        this.rank = rank;
    }

    public PatternLogFileRequest(int logFileID, int patternID) {
        this.logFileID = logFileID;
        this.patternID = patternID;
    }

    public int getLogFileID() {
        return logFileID;
    }

    public void setLogFileID(int logFileID) {
        this.logFileID = logFileID;
    }

    public int getPatternID() {
        return patternID;
    }

    public void setPatternID(int patternID) {
        this.patternID = patternID;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}
