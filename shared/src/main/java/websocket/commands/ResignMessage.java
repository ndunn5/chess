package websocket.commands;

public class ResignMessage extends UserGameCommand {

    public ResignMessage(String authToken, Integer gameID, String playerName, String color) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}



