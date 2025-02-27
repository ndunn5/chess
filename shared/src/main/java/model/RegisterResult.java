package model;

public record RegisterResult (String username, String authToken, int statusCode, String message){

    public RegisterResult(String username, String authToken) {
        this(username, authToken, 200, null);
    }

    public RegisterResult(int statusCode, String message) {
        this(null, null, statusCode, message);
    }
}

