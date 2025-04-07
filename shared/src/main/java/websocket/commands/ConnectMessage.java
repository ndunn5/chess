package websocket.commands;


public class ConnectMessage extends UserGameCommand {
    private String playerName;
    private String color;

    public ConnectMessage(String authToken, Integer gameID, String playerName, String color) {
        super(CommandType.CONNECT, authToken, gameID);
        this.playerName = playerName;
        this.color = color;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getColor() {
        return color;
    }
}
