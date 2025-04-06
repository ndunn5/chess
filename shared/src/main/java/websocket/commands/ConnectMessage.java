package websocket.commands;


public class ConnectMessage extends UserGameCommand {
    private String username;
    private String color;

    public ConnectMessage(String authToken, Integer gameID, String username, String color) {
        super(CommandType.CONNECT, authToken, gameID);
        this.username = username;
        this.color = color;
    }

    public String getUsername() {
        return username;
    }

    public String getColor() {
        return color;
    }
}
