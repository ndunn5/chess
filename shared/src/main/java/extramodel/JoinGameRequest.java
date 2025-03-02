package extramodel;

public class JoinGameRequest {
    String playerColor;
    int gameID;
    String authToken;

    public JoinGameRequest(String playerColor, int gameID){
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public void addAuthToken(String authToken){
        this.authToken = authToken;
    }

    public String getPlayerColor(){
        return playerColor;
    }

    public int getGameID(){
        return gameID;
    }

    public String getAuthToken(){
        return authToken;
    }
}
