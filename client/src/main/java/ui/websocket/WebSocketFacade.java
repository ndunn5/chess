package ui.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
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
                    gameHandler.printMessage(serverMessage);
                }
            });
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

    private void connect(ConnectMessage connectMessage) {
        try{
            sendMessage(connectMessage);
        } catch (ResponseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void makeMove(MakeMoveMessage makeMoveMessage) {
        try{
            sendMessage(makeMoveMessage);
        } catch (ResponseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void resign(ResignMessage resignMessage){
        try{
            sendMessage(resignMessage);
        } catch (ResponseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void leave(LeaveMessage leaveMessage){
        try{
            sendMessage(leaveMessage);
        } catch (ResponseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


        private void sendMessage(UserGameCommand message) throws ResponseException {
        try {
            String jsonMessage = new Gson().toJson(message);
            this.session.getBasicRemote().sendText(jsonMessage);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void onMessage(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class); //numba one
        gameHandler.printMessage(serverMessage); //surgeon
    }

}
