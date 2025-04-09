package websocket.commands;

public class LeaveMessage extends UserGameCommand {
    public LeaveMessage(String authToken, Integer gameID, String playerName, String color) {
        super(CommandType.LEAVE, authToken, gameID);
    }
}



