package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import model.LogoutRequest;
import model.LogoutResult;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GamePlay {

    private final ServerFacade server;
    private final String serverUrl;
    private final ArrayList<String> sideLetters = new ArrayList<>(Arrays.asList(" ", "a", "b", "c", "d", "e", "f", "g", "h", " "));
    private final ArrayList<String> sideNumbers = new ArrayList<>(Arrays.asList(" ", "1", "2", "3", "4", "5", "6", "7", "8", " "));


    public GamePlay(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case ("logout") -> logout();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String showBoard(String boardState, String whiteOrBlack) {
//        "This is where the board will be printed. It needs to take in an object in and also white orientation or black orientation";
        Gson gson = new Gson();

        JsonObject jsonObject = JsonParser.parseString(boardState).getAsJsonObject();
        JsonObject boardObject = jsonObject.getAsJsonObject("board");
        ChessBoard board = gson.fromJson(boardObject, ChessBoard.class);
        if (whiteOrBlack.equals("WHITE")) {
            return drawWhiteBoard(board, whiteOrBlack);
        }

        return boardState + whiteOrBlack;
    }

    public String drawWhiteBoard(ChessBoard board, String whiteOrBlack) {
        StringBuilder boardDisplay = new StringBuilder();

        for(int row = 0; row < 10; row++){
            if (row == 0 || row == 9) {
                boardDisplay.append(drawHeaderAndFooter(whiteOrBlack) + "\n");
            }
            else{
                boardDisplay.append(drawMiddle(board, row, whiteOrBlack) + "\n");
            }
        }

        return boardDisplay.toString();
    }

    private StringBuilder drawHeaderAndFooter(String color) {
        StringBuilder headerAndFooterBuilder = new StringBuilder();
        headerAndFooterBuilder.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);

        for (String letter : sideLetters) {
            headerAndFooterBuilder.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + letter + " ");
        }

        headerAndFooterBuilder.append(EscapeSequences.RESET_BG_COLOR);
        headerAndFooterBuilder.append(EscapeSequences.RESET_TEXT_COLOR);

        return headerAndFooterBuilder;
    }

    private StringBuilder drawMiddle(ChessBoard board, int row, String color) {
        StringBuilder middleLine = new StringBuilder();
        for(int col = 0; col < 10; col++){
            if(color.equals("WHITE")){
                if(col == 0 || col == 9){
                    middleLine.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                    middleLine.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + sideNumbers.get(row) + " ");
                }else{

                    if ((row + col) % 2 == 0) {
                        middleLine.append(EscapeSequences.SET_BG_COLOR_WHITE);
                    } else {
                        middleLine.append(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    }

                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    if (piece != null) {
                        middleLine.append(EscapeSequences.SET_TEXT_COLOR_BLACK + " " + "p" + " ");
                    } else {
                        middleLine.append(EscapeSequences.SET_TEXT_COLOR_WHITE + "   ");
                    }

                    middleLine.append(EscapeSequences.RESET_BG_COLOR);
                    middleLine.append(EscapeSequences.RESET_TEXT_COLOR);
                }
            }
        }

        middleLine.append(EscapeSequences.RESET_BG_COLOR);
        middleLine.append(EscapeSequences.RESET_TEXT_COLOR);

        return middleLine;
    }


    public String help() {
        return """
                - create <NAME> - a game
                - list - games 
                - join <ID> [WHITE|BLACK] - a game
                - observe <ID> - a game
                - logout - when you are done
                - quit - playing chess
                - help - with possible commands
                """;
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
}
