package websocket.commands;

public class checkMessage extends UserGameCommand{
    public checkMessage(String authToken, Integer gameID, String playerName, String color) {
        super(CommandType.CHECK, authToken, gameID);
    }

    }


