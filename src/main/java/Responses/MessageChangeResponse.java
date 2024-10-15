package Responses;

public class MessageChangeResponse {
    private String message;
    private boolean changed;
    private int patternId;

    public MessageChangeResponse(String message, boolean changed) {
        this.message = message;
        this.changed = changed;
    }

    public MessageChangeResponse(String message, boolean changed, int patternId) {
        this.message = message;
        this.changed = changed;
        this.patternId = patternId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getPatternId() {
        return patternId;
    }

    public void setPatternId(int patternId) {
        this.patternId = patternId;
    }

}
