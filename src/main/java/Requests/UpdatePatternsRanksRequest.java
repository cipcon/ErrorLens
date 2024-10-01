package Requests;

import java.util.ArrayList;

public class UpdatePatternsRanksRequest {
    private int logFileID;
    private ArrayList<PatternRequest> patterns;

    public UpdatePatternsRanksRequest(int logFileID, ArrayList<PatternRequest> patterns) {
        this.logFileID = logFileID;
        this.patterns = patterns;
    }

    public int getLogFileID() {
        return logFileID;
    }

    public ArrayList<PatternRequest> getPatterns() {
        return patterns;
    }

    public void setLogFileID(int logFileID) {
        this.logFileID = logFileID;
    }

    public void setPatterns(ArrayList<PatternRequest> patterns) {
        this.patterns = patterns;
    }

}
