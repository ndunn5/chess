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
import websocket.commands.ConnectMessage;
import websocket.commands.MakeMoveMessage;
import websocket.commands.ResignMessage;
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
            if (gameData == null) {
                Connection errorConnection = new Connection(null, 0, null, session);
                errorConnection.sendMessage(new ErrorMessage("invalid gamID"));
                return;
            }
            if (gameData.whiteUsername().equals(playerName)){
                playerColor = "WHITE";
            } else if (gameData.blackUsername().equals(playerName)){
                playerColor = "BLACK";
            }
            //get the game data, check if username is white, balck, or neither then observer
            Connection thisConnection = new Connection(playerName, gameID, authToken, session);
            connections.addSessionToGame(gameID, thisConnection);
            String message = playerName + " has joined as " + playerColor + ".";
            if (playerColor == null) {
                message = playerName + " has joined as an observer.";
            }
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            thisConnection.sendMessage(loadGameMessage);


            NotificationMessage notificationMessage = new NotificationMessage(message);
            broadcastMessage(gameID, notificationMessage, thisConnection);


        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static ChessGame.TeamColor getCurrentColor(GameData gameData, String playerName, ChessGame.TeamColor playerColor) {

        if (gameData.whiteUsername().equals(playerName)) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (gameData.blackUsername().equals(playerName)) {
            playerColor = ChessGame.TeamColor.BLACK;
        }
        return playerColor;
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
            currentColor = getCurrentColor(gameData, playerName, currentColor);
            try {
                if (!chessGame.validMoves((chessMove.getStartPosition())).contains(chessMove) || !chessGame.getTeamTurn().equals(currentColor) || chessGame.isGameOver()) {
                    Connection errorConnection = new Connection(null, 0, null, session);
                    errorConnection.sendMessage(new ErrorMessage("invalid move"));
                    return;
                }
                chessGame.makeMove(makeMoveMessage.getChessMove());
                gameDAO.updateGame(gameData); //gameData may not be updated we will see

                LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
                broadcastMessage(gameID, loadGameMessage, null);

                String message = playerName + " made this chessmove :" + chessMove.toString();
                NotificationMessage notificationMessage = new NotificationMessage(message);
                Connection thisConnection  = getCorrectConnection(connections.getSessionForGameID(gameID), playerName);
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
        chessGame.setGameOver(true);
        try{
            gameDAO.updateGame(gameData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        String message = playerName + " resigned" ;
        NotificationMessage notificationMessage = new NotificationMessage(message);
        broadcastMessage(gameID, notificationMessage, null);
    }

    public boolean checkCorrectColorMove(ChessBoard board, ChessMove move, String currentTurn){
        if (board.getPiece(move.getStartPosition()).getTeamColor().equals(currentTurn)){
            return true;
        }else{
            return false;
        }
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
