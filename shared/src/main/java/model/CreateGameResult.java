package model;

public record CreateGameResult (int gameID, String message){
    public CreateGameResult(int gameID){
        this(gameID, null);
    }
    public CreateGameResult(String message){
        this(-1, message);
    }
}
