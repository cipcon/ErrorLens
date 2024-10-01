package Responses;

public class MessageChangeResponse {
    private String message;
    private boolean changed;

    public MessageChangeResponse(String message, boolean changed) {
        this.message = message;
        this.changed = changed;
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

}
