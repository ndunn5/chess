package server.websocket;

import com.mysql.cj.protocol.Message;
import extramodel.JoinGameRequest;
import model.GameData;
import model.JoinGameResult;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import org.eclipse.jetty.websocket.api.Session;
import spark.Request;
import spark.Response;
import dataaccess.*;
import websocket.commands.ConnectMessage;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

//@onWebSocketError onError(throwable: Throwable): void
//@OnWebScoketMessage onMessage(session: Session, str: string): void
        //1.determing the message type
        //2. call one of the following methods to process te message


//look at petshop. pretty good example
//connect(message)
//makeMove(message)
//leaveGame(message)
//resignGame(message)

//sendMessage(message, session)
//broadcastMessage(gameID, message, exceptThisSession)

@WebSocket
public class WebSocketHandler {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    private Session session;

    private final ConnectionManager connections = new ConnectionManager();


    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    @OnWebSocketError
    public void onError(Throwable throwable){
        System.err.println("WebSocket Error: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        this.session = session;
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(new Gson().fromJson(message, ConnectMessage.class));
        }
    }


    //A user connected to the game as a player (black or white). The notification message should
    // include the player’s name and which side they are playing (black or white).
    private void connect(ConnectMessage connectMessage){
        int gameID = connectMessage.getGameID();
        String playerName = connectMessage.getUsername();
        String playerColor = connectMessage.getColor();

        connections.addSessionToGame(gameID, session);
        String msg = playerName + " has joined as " + playerColor + ".";
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        broadcastMessage(gameID, serverMessage, session);
    }

    public void broadcastMessage(int gameID,  ServerMessage serverMessage, Session exceptThisSession){
        var removeMap = new HashMap<Integer, Session>();
        for (int currentGameID: connections.connections.keySet()){
            Set<Session> sessions = connections.getSessionForGameID(currentGameID);
            for (Session s: sessions){
                if(s.isOpen()){
                    if (gameID != currentGameID && exceptThisSession != s){
                        try{
                            s.getRemote().sendString(serverMessage.toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else{
                    removeMap.put(currentGameID, s);
                }

            }
        }
        for (var removeGameID : removeMap.keySet()){
            connections.removeSessionFromGame(removeGameID, removeMap.get(removeGameID));
        }
    }
}
