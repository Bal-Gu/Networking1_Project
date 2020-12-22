public class Messages {
    private String username = "";
    private String message = "";

    public Messages(String username, String message) {
        this.message = message;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }
}
//fuck this