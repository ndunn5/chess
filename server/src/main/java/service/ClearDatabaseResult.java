package service;

public class ClearDatabaseResult {
    private int statusCode;
    private String errorMessage;

    public ClearDatabaseResult(int statusCode, String errorMessage){
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    public ClearDatabaseResult(int statusCode){
        this.statusCode = statusCode;
    }

    public int getStatusCode(){
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
