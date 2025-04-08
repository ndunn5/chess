package ui.websocket;

import chess.ChessBoard;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    GameHandler gameHandler;


    public WebSocketFacade(String url, GameHandler gameHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()){
                        case ServerMessage.ServerMessageType.NOTIFICATION:
                            NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                            WebSocketFacade.this.notificaitonOnMessage(notificationMessage);
                            break;
                        case ServerMessage.ServerMessageType.LOAD_GAME:
                            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            WebSocketFacade.this.loadGameOnMessage(loadGameMessage);
                            break;
                        case ServerMessage.ServerMessageType.ERROR:
                            ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                            WebSocketFacade.this.errorOnMessage(errorMessage);
                            break;
                    }



                }
            });
//            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
//                @Override
//                public void onMessage(String message) {
//                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//                    gameHandler.printMessage(serverMessage);
//                }
//            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    @Override
    public void onError(Session session, Throwable thr) {
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
    }

    public void connect(ConnectMessage connectMessage) {
        try {
            sendConnectMessage(connectMessage);
//            sendMessage(connectMessage);
        } catch (ResponseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void makeMove(MakeMoveMessage makeMoveMessage) {
        try {
            sendMakeMoveMessage(makeMoveMessage);
        } catch (ResponseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void resign(ResignMessage resignMessage) {
        try {
            sendResignMessage(resignMessage);
        } catch (ResponseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void leave(LeaveMessage leaveMessage) {
        try {
            sendLeaveMessage(leaveMessage);
        } catch (ResponseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendConnectMessage(ConnectMessage connectMessage) throws ResponseException {
        try {
            String jsonMessage = new Gson().toJson(connectMessage);
            this.session.getBasicRemote().sendText(jsonMessage);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void sendMakeMoveMessage(MakeMoveMessage makeMoveMessage) throws ResponseException {
        try {
            String jsonMessage = new Gson().toJson(makeMoveMessage);
            this.session.getBasicRemote().sendText(jsonMessage);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void sendResignMessage(ResignMessage resignMessage) throws ResponseException {
        try {
            String jsonMessage = new Gson().toJson(resignMessage);
            this.session.getBasicRemote().sendText(jsonMessage);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void sendLeaveMessage(LeaveMessage leaveMessage) throws ResponseException {
        try {
            String jsonMessage = new Gson().toJson(leaveMessage);
            this.session.getBasicRemote().sendText(jsonMessage);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void loadGameOnMessage(LoadGameMessage loadGameMessage){
        System.out.print(gameHandler.getColor());
        gameHandler.showBoard(loadGameMessage.getGameData().game().getBoard(), gameHandler.getColor());
    }

    public void notificaitonOnMessage(NotificationMessage notificationMessage){
        gameHandler.printMessage(notificationMessage);
    }

    public void errorOnMessage(ErrorMessage errorMesssage){
        gameHandler.printMessage(errorMesssage);
    }
}
