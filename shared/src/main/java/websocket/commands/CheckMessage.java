package websocket.commands;

public class CheckMessage extends UserGameCommand{
    public CheckMessage(String authToken, Integer gameID, String playerName, String color) {
        super(CommandType.CHECK, authToken, gameID);
    }

    }


