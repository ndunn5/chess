package websocket.commands;


public class ConnectMessage extends UserGameCommand {
    private String playerName;
    private String color;

    public ConnectMessage(String authToken, Integer gameID, String color) {
        super(CommandType.CONNECT, authToken, gameID);
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
