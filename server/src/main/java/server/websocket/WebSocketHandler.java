package server.websocket;

import com.mysql.cj.protocol.Message;
import com.mysql.cj.x.protobuf.Mysqlx;
import extramodel.JoinGameRequest;
import model.AuthData;
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
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
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
        String authToken = connectMessage.getAuthToken();
        int gameID = connectMessage.getGameID();
        String playerColor = null;
        try{
            AuthData authData = authDAO.getAuthDataWithAuthToken(authToken);
            if (authData == null){
                Connection errorConnection = new Connection(null, 0, null, session);
                broadcastMessage(gameID, new ErrorMessage("invalid authToken"), errorConnection);
                return;
            }//send load game message just to the user
            String playerName = authData.username();
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData.whiteUsername().equals(playerName)){
                playerColor = "WHITE";
            }else if(gameData.blackUsername().equals(playerName)){
                playerColor = "BLACK";
            }
            //get the game data, check if username is white, balck, or neither then observer
            Connection thisConnection = new Connection(playerName, gameID, authToken, session);
            connections.addSessionToGame(gameID, thisConnection);
            String message = playerName + " has joined as " + playerColor + ".";


            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            thisConnection.sendMessage(loadGameMessage);


            NotificationMessage notificationMessage = new NotificationMessage(message);
            broadcastMessage(gameID, notificationMessage, thisConnection);


        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }



    public void broadcastMessage(int gameID,  ServerMessage message, Connection ExceptThisConnection) {//use a connection object and compare using the username rather than the session
        var removeList = new ArrayList<Connection>();
        Set<Connection> relevantConnections = connections.getSessionForGameID(gameID);
        if (relevantConnections == null){
            Connection errorConnnection = new Connection(null, 0 , null, null);
            errorConnnection.sendMessage(new ErrorMessage("invalid gameID"));
            return;
        }
        for (Connection c : relevantConnections) {
            if (c.session.isOpen()) {
                if (ExceptThisConnection.playerName != c.playerName) {
                    c.sendMessage(message);
                }
            }else {
                removeList.add(c);
            }
        }
        for (var c: removeList){
            connections.removeSessionFromGame(gameID, c);
        }
    }
}
