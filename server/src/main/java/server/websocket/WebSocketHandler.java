package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.OnOpen;

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

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket opened for session: ");
    }

    @OnWebSocketError
    public void onError(Throwable throwable) {
        System.err.println("WebSocket Error: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        this.session = session;

        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(new Gson().fromJson(message, ConnectMessage.class));
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveMessage.class));
            case RESIGN -> resign(new Gson().fromJson(message, ResignMessage.class));
            case LEAVE -> leave(new Gson().fromJson(message, LeaveMessage.class));
        }
    }

    private void connect(ConnectMessage connectMessage) {
        String authToken = connectMessage.getAuthToken();
        int gameID = connectMessage.getGameID();
        String playerColor = null;
        try {
            AuthData authData = authDAO.getAuthDataWithAuthToken(authToken);
            if (authData == null) {
                Connection errorConnection = new Connection(null, 0, null, session);
                errorConnection.sendMessage(new ErrorMessage("invalid authToken"));
                return;
            }
            String playerName = authData.username();
            GameData gameData = gameDAO.getGame(gameID);
            String message;
            if (gameData == null) {
                Connection errorConnection = new Connection(null, 0, null, session);
                errorConnection.sendMessage(new ErrorMessage("invalid gamID"));
                return;
            }//can also check if its null
            if (playerName.equals(gameData.blackUsername())){
                playerColor = "BLACK";
                message = playerName + " has joined as " + playerColor + ".";
            } else if (playerName.equals(gameData.whiteUsername())){
                playerColor = "WHITE";
                message = playerName + " has joined as " + playerColor + ".";
            }else{
                message = playerName + " has joined as an observer.";
            }

//            if (gameData.whiteUsername() == null){
//                playerColor = "BLACK";
//            } else if (gameData.blackUsername() == null){
//                playerColor = "WHITE";
//            }else{
//                message = playerName + " has joined as an observer.";
//            }

//            if (gameData.whiteUsername() == playerName){
//                playerColor = "WHITE";
//            } else if (gameData.blackUsername() == playerName){
//                playerColor = "BLACK";
//            }
            //get the game data, check if username is white, balck, or neither then observer
            Connection thisConnection = new Connection(playerName, gameID, authToken, session);
            connections.addSessionToGame(gameID, thisConnection);
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            thisConnection.sendMessage(loadGameMessage);


            NotificationMessage notificationMessage = new NotificationMessage(message);
            broadcastMessage(gameID, notificationMessage, thisConnection);


        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static ChessGame.TeamColor getCurrentColor(GameData gameData, String playerName) {
        if (gameData.whiteUsername()!= null && gameData.whiteUsername().equals(playerName)) {
            return ChessGame.TeamColor.WHITE;
        } else if (gameData.blackUsername()!= null && gameData.blackUsername().equals(playerName)) {
            return ChessGame.TeamColor.BLACK;
        }else{
            return null;
        }
    }

    public void makeMove(MakeMoveMessage makeMoveMessage) {
        ChessMove chessMove = makeMoveMessage.getChessMove();
        int gameID = makeMoveMessage.getGameID();
        AuthData authData;
        ChessBoard board = null;
        ChessGame.TeamColor currentColor = null;
        try {
            authData = authDAO.getAuthDataWithAuthToken(makeMoveMessage.getAuthToken());
            if (authData == null){
                Connection errorConnection = new Connection(null, 0, null, session);
                errorConnection.sendMessage(new ErrorMessage("unauthorized"));
                return;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        String playerName = authData.username();
        try {
            GameData gameData = gameDAO.getGame(makeMoveMessage.getGameID());
            ChessGame chessGame = gameData.game();
            board = gameData.game().getBoard();
            currentColor = getCurrentColor(gameData, playerName);
            NotificationMessage notificationMessage;
            String message;
            Connection thisConnection  = getCorrectConnection(connections.getSessionForGameID(gameID), playerName);
            try {
                if (!chessGame.validMoves((chessMove.getStartPosition())).contains(chessMove) || !chessGame.getTeamTurn().equals(currentColor) || chessGame.isGameOver()) {
                    Connection errorConnection = new Connection(null, 0, null, session);
                    errorConnection.sendMessage(new ErrorMessage("invalid move"));
                    return;
                }
                if (currentColor == ChessGame.TeamColor.WHITE){
                    if (gameData.game().isInCheck(ChessGame.TeamColor.BLACK)){
                        message = gameData.blackUsername() + " is in check";
                        notificationMessage = new NotificationMessage(message);
                        broadcastMessage(gameID, notificationMessage, thisConnection);
                    }
                } else{
                    if (gameData.game().isInCheck(ChessGame.TeamColor.WHITE)){
                        message = gameData.whiteUsername() + " is in check";
                        notificationMessage = new NotificationMessage(message);
                        broadcastMessage(gameID, notificationMessage, thisConnection);
                    }
                }
                chessGame.makeMove(makeMoveMessage.getChessMove());
                gameDAO.updateGame(gameData); //gameData may not be updated we will see

                LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
                broadcastMessage(gameID, loadGameMessage, null);

                message = playerName + " made this chessmove: " + chessMove.getStartPosition() + " to " +  chessMove.getEndPosition();
//                System.out.print(message);
                notificationMessage = new NotificationMessage(message);
                broadcastMessage(gameID, notificationMessage, thisConnection);

            } catch (InvalidMoveException e) {
                throw new RuntimeException(e);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void resign(ResignMessage resignMessage){
        int gameID = resignMessage.getGameID();
        String playerName;
        GameData gameData;
        ChessGame.TeamColor teamColor;
        try{
            playerName = authDAO.getAuthDataWithAuthToken(resignMessage.getAuthToken()).username();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        Set<Connection> relevantConnections = connections.getSessionForGameID(gameID);
        Connection thisConnection = getCorrectConnection(relevantConnections, playerName);
        try{
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        ChessGame chessGame = gameData.game();
        if (chessGame.isGameOver() == true){
            Connection errorConnection = new Connection(null, 0, null, session);
            errorConnection.sendMessage(new ErrorMessage("resigners can't resign"));
            return;
        }
        chessGame.setGameOver(true);
        try{
            gameDAO.updateGame(gameData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        teamColor = getCurrentColor(gameData, playerName);
        if (teamColor == null){
            Connection errorConnection = new Connection(null, 0, null, session);
            errorConnection.sendMessage(new ErrorMessage("observers can't resign"));
            return;
        }
        String message = playerName + " resigned" ;
        NotificationMessage notificationMessage = new NotificationMessage(message);
        broadcastMessage(gameID, notificationMessage, null);


    }

    public void leave(LeaveMessage leaveMessage){
        int gameID = leaveMessage.getGameID();
        String playerName;
        GameData gameData;
        ChessGame.TeamColor teamColor;
        try{
            playerName = authDAO.getAuthDataWithAuthToken(leaveMessage.getAuthToken()).username();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        Set<Connection> relevantConnections = connections.getSessionForGameID(gameID);
        Connection thisConnection = getCorrectConnection(relevantConnections, playerName);
        try{
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        teamColor = getCurrentColor(gameData, playerName);
        GameData updatedGameData = new GameData(0, null, null, null, null);
        if (teamColor == ChessGame.TeamColor.WHITE){
            updatedGameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else if (teamColor == ChessGame.TeamColor.BLACK){
            updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
        }
        try{
            gameDAO.updateGame(updatedGameData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        String message = playerName + " left." ;
        NotificationMessage notificationMessage = new NotificationMessage(message);
        broadcastMessage(gameID, notificationMessage, thisConnection);
        connections.removeSessionFromGame(gameID, thisConnection);
    }

    public Connection getCorrectConnection(Set<Connection> relevantConnections, String playerName){
        for (Connection c : relevantConnections){
            if(c.playerName.equals(playerName)){
                return c;
            }
        }
        return null;
    }


    public void broadcastMessage(int gameID, ServerMessage message, Connection ExceptThisConnection) {//use a connection object and compare using the username rather than the session
        var removeList = new ArrayList<Connection>();
        Set<Connection> relevantConnections = connections.getSessionForGameID(gameID);
        if (relevantConnections == null) {
            Connection errorConnnection = new Connection(null, 0, null, null);
            errorConnnection.sendMessage(new ErrorMessage("invalid gameID"));
            return;
        }
        for (Connection c : relevantConnections) {
            if (c.session.isOpen()) {
                if (ExceptThisConnection == null){
                    c.sendMessage(message);
                } else if (ExceptThisConnection.playerName != c.playerName) {
                    c.sendMessage(message);
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.removeSessionFromGame(gameID, c);
        }
    }
}
