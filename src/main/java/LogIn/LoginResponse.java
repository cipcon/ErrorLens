package LogIn;

public class LoginResponse {
    private String message;
    private boolean passwordMatch;
    private String changePassword;

    public LoginResponse(String message, boolean passwordMatch, String changePassword) {
        this.message = message;
        this.passwordMatch = passwordMatch;
        this.changePassword = changePassword;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getPasswordMatch() {
        return this.passwordMatch;
    }

    public void setPasswordMatch(boolean passwordMatch) {
        this.passwordMatch = passwordMatch;
    }

    public String getChangePassword() {
        return this.changePassword;
    }

    public void setChangePassword(String changePassword) {
        this.changePassword = changePassword;
    }

}
