package websocket.commands;

public class CheckMateMessage extends UserGameCommand{
    public CheckMateMessage(String authToken, Integer gameID, String playerName, String color) {
        super(CommandType.CHECK_MATE, authToken, gameID);
    }
}

