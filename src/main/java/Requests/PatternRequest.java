package Requests;

public class PatternRequest {

    private int patternId;
    private String patternName;
    private String pattern;
    private String patternDescription;
    private String severity;
    private int rank;

    public PatternRequest(int patternId, String patternName, String pattern, String patternDescription,
            String severity) {
        this.patternId = patternId;
        this.patternName = patternName;
        this.pattern = pattern;
        this.patternDescription = patternDescription;
        this.severity = severity;
    }

    public PatternRequest(String patternName, String pattern, String patternDescription, String severity) {
        this.patternName = patternName;
        this.pattern = pattern;
        this.patternDescription = patternDescription;
        this.severity = severity;
    }

    public PatternRequest(int patternId, String patternName, String pattern, String patternDescription,
            String severity, int rank) {
        this.patternId = patternId;
        this.patternName = patternName;
        this.pattern = pattern;
        this.patternDescription = patternDescription;
        this.severity = severity;
        this.rank = rank;
    }

    public PatternRequest(int patternId, int rank) {
        this.patternId = patternId;
        this.rank = rank;
    }

    public int getPatternId() {
        return patternId;
    }

    public void setPatternId(int patternId) {
        this.patternId = patternId;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPatternDescription() {
        return patternDescription;
    }

    public void setPatternDescription(String patternDescription) {
        this.patternDescription = patternDescription;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}
