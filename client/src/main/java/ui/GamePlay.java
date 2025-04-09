package ui;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import extramodel.JoinGameRequest;
import model.JoinGameResult;
import model.LogoutRequest;
import model.LogoutResult;
import server.ServerFacade;
import chess.ChessGame;
import chess.ChessBoard;
import ui.websocket.GameHandler;
import ui.websocket.GameUI;
import ui.websocket.WebSocketFacade;
import websocket.commands.ConnectMessage;
import websocket.commands.LeaveMessage;
import websocket.commands.MakeMoveMessage;

import java.util.*;

public class GamePlay {
    //start with server side stuff(make a separate class that can also access the DAOs)-> make sure its working in the webiste-> test cases  -> go to the client side things
    //be careful with copying and pasting from petshop
    //think through how someone can break things
    //an observer tries to make move or resign
    //wrong color makes move for the other color
    //instruction page just on websockets
    //can use any color for highlight
    //do something for pawn promotion
    //show what move is being made and name of player
    //reread spek, websocket notifations
    private final ServerFacade server;
    private String serverUrl;
    private final ArrayList<String> sideLetters = new ArrayList<>(Arrays.asList(" ", "a", "b", "c", "d", "e", "f", "g", "h", " "));
    private final ArrayList<String> sideNumbers = new ArrayList<>(Arrays.asList(" ", "1", "2", "3", "4", "5", "6", "7", "8", " "));
//    private ChessBoard currentBoard = new ChessBoard();
//    private String clientColor = "WHITE";
    private ChessGame chessGame = new ChessGame();
    private GameHandler gameHandler;
    private String authToken;
    private int gameID;
    private String playerName;
    String currentColor;
    private PostLoginClient postLoginClient;
    WebSocketFacade ws;
    PromoteClient promoteClient;


    public GamePlay(String serverUrl, GameHandler gameHandler, PostLoginClient postLoginClient) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.gameHandler = gameHandler;
        this.postLoginClient = postLoginClient;
        this.currentColor = postLoginClient.getCurrentColor();
        this.gameID = postLoginClient.getGameID();
        this.authToken = postLoginClient.getAuthToken();
        try{
            this.ws = new WebSocketFacade(serverUrl, gameHandler);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

//        ws = postLoginClient.getWebSocketFacade();
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case ("logout") -> logout();
                case ("redraw") -> redraw();
                case ("highlight") -> highlight(params);
                case ("leave") -> leave();
                case ("move") -> makeMove(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public void updateGamePlay(PostLoginClient postLoginClient, PromoteClient promoteClient){
        this.postLoginClient = postLoginClient;
        this.currentColor = postLoginClient.getCurrentColor();
        this.gameID = postLoginClient.getGameID();
        this.authToken = postLoginClient.getAuthToken();
        this.promoteClient = promoteClient;
    }


    public String help() {
        return """
                - redraw - chess board
                - leave - current game 
                - make move - to win
                - resign - a game
                - highlight - legal moves
                - help - with possible commands
                """;
    }

    public String redraw(){
//        return gameHandler.showBoard(new Gson().toJson(currentBoard), clientColor);
            return gameHandler.drawBoard(currentColor, null);
//            return gameHandler.drawBoard(currentBoard, clientColor, null);
    }

    public String highlight(String... params) throws ResponseException{
        if(params.length == 1){
            String letterAndNumber = params[0];
            if (checkValidLetterAndNumber(letterAndNumber)){
                ChessPosition chessPosition = getChessPosition(letterAndNumber);
                ChessPiece chessPiece = gameHandler.getCurrentBoard().getPiece(chessPosition);
//                ChessPiece chessPiece = currentBoard.getPiece(chessPosition);
                if (chessPiece == null){
                    throw new ResponseException(400, "there is no piece there");
                }
                chessGame.setBoard(gameHandler.getCurrentBoard());

                Collection<ChessMove> validMoves = chessGame.validMoves(chessPosition);
                if (validMoves.isEmpty()){
                    return "no valid moves";
                }
                return gameHandler.drawBoard(currentColor, validMoves);
//                return gameHandler.drawBoard(currentBoard, clientColor, validMoves);
//                return letterAndNumber;
            }else{
                return "invalid letter and number. Type letter(a-h) first and then number (1-8)";
            }
        }
        throw new ResponseException(400, "Expected: <letter(a-h) number (1-8)>");
    }

    private ChessPosition getChessPosition(String letterAndNumber) {
        int col = "abcdefgh".indexOf(Character.toLowerCase(letterAndNumber.charAt(0))) + 1;
        int row = Character.getNumericValue(letterAndNumber.charAt(1));
        ChessPosition chessPosition = new ChessPosition(row, col);
        return chessPosition;
    }

    private boolean checkValidLetterAndNumber(String letterAndNumber){
        List<String> justLetters = sideLetters.subList(1, sideLetters.size());
        List<String> justNumbers = sideNumbers.subList(1, sideNumbers.size());


        if (letterAndNumber.length() == 2) {
            String letter = String.valueOf(letterAndNumber.charAt(0));
            String number = String.valueOf(letterAndNumber.charAt(1));
            if (justLetters.contains(letter) && justNumbers.contains(number)) {
                return true;
            } else{
                return false;
            }
        }else{
            return false;
        }
}

    public String logout() throws ResponseException {
        try {
            LogoutResult logoutResult = server.handleLogout(new LogoutRequest(PreLoginClient.getAuthToken()));
            Repl.updateState(State.SIGNEDOUT);
            return "You signed out";
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
    }


    public String leave(){
//        try{
//            WebSocketFacade ws = new WebSocketFacade(serverUrl, gameHandler);
//            ws.leave(new LeaveMessage(authToken, gameID, playerName, currentColor));
//        } catch (ResponseException e) {
//            throw new RuntimeException(e);
//        }
        return "dummy string";
    }

    public String makeMove(String... params){
        if (params.length == 2){
            String startLetterAndNumber = params[0];
            String endLetterAndNumber = params[1];
            if (checkValidLetterAndNumber(startLetterAndNumber) && checkValidLetterAndNumber(endLetterAndNumber)){
                ChessPosition startPosition = getChessPosition(startLetterAndNumber);
                ChessPosition endPosition = getChessPosition(endLetterAndNumber);
                ChessPiece piece = gameHandler.getCurrentBoard().getPiece(startPosition);

                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    if (piece.getPieceType() == ChessPiece.PieceType.PAWN && endPosition.getRow() == 8){
                        Repl.updateState(State.PROMOTE);
                        return "what would you like to promote to?";
                    }
                } else{
                    if (piece.getPieceType() == ChessPiece.PieceType.PAWN && endPosition.getRow() == 1){
                        Repl.updateState(State.PROMOTE);
                        return "what would you like to promote to?";
                    }
                }
                ChessPiece.PieceType test = promoteClient.getReturnPieceType();
                if (promoteClient.getReturnPieceType() != ChessPiece.PieceType.PAWN){
                    ChessMove move = new ChessMove(startPosition, endPosition, promoteClient.getReturnPieceType());
                    MakeMoveMessage makeMoveMessage = new MakeMoveMessage(authToken, gameID, move);
                    ws.makeMove(makeMoveMessage);
                } else{
                    ChessMove move = new ChessMove(startPosition, endPosition, null);
                    MakeMoveMessage makeMoveMessage = new MakeMoveMessage(authToken, gameID, move);
                    ws.makeMove(makeMoveMessage);
                }
            } else{
                return "Expected: move <a-h1-8> <a-h1-8>";
            }
        }else{
            return "Expected: move <a-h1-8> <a-h1-8>";
        }
        return "";
    }


}
