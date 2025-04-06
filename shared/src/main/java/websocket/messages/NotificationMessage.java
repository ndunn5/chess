package websocket.messages;

public class NotificationMessage extends ServerMessage {
    String notificationMessage;


    public NotificationMessage(String notificationMessage) {
        super(ServerMessageType.NOTIFICATION);
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }
}