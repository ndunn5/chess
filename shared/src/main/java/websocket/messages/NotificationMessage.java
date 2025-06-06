package websocket.messages;

public class NotificationMessage extends ServerMessage {
    String message;


    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getNotificationMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}