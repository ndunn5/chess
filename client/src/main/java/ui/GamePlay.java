package ui;

import chess.ChessBoard;
import chess.ChessGame;
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
        return drawBoard(board, whiteOrBlack);
    }

    public String drawBoard(ChessBoard board, String whiteOrBlack) {
        StringBuilder boardDisplay = new StringBuilder();

        for (int row = 0; row < 10; row++) {
            if (row == 0 || row == 9) {
                boardDisplay.append(drawHeaderAndFooter(whiteOrBlack) + "\n");
            } else {
                if(whiteOrBlack.equals("WHITE")){
                    boardDisplay.append(drawMiddle(board, 9-row, whiteOrBlack) + "\n");
                }else{
                    boardDisplay.append(drawMiddle(board, row, whiteOrBlack) + "\n");
                }
            }
        }

        return boardDisplay.toString();
    }

    private StringBuilder drawHeaderAndFooter(String color) {
        StringBuilder headerAndFooterBuilder = new StringBuilder();
        headerAndFooterBuilder.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);

        if (color.equals("WHITE")) {
            for (String letter : sideLetters) {
                headerAndFooterBuilder.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + letter + " ");
            }
        } else {
            for (String letter : sideLetters.reversed()) {
                headerAndFooterBuilder.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + letter + " ");
            }
        }


        headerAndFooterBuilder.append(EscapeSequences.RESET_BG_COLOR);
        headerAndFooterBuilder.append(EscapeSequences.RESET_TEXT_COLOR);

        return headerAndFooterBuilder;
    }

    private StringBuilder drawMiddle(ChessBoard board, int row, String color) {
        StringBuilder middleLine = new StringBuilder();
        for (int col = 0; col < 10; col++) {
            if (col == 0 || col == 9) {
                middleLine.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                middleLine.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + sideNumbers.get(row) + " ");
            } else {
                if ((row + col) % 2 == 0) {
                    if(color.equals("WHITE")){
//                        middleLine.append(EscapeSequences.SET_BG_COLOR_WHITE);
                        middleLine.append(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    }
                    else{
                        middleLine.append(EscapeSequences.SET_BG_COLOR_WHITE);
//                        middleLine.append(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    }
                } else {
                    if(color.equals("WHITE")){
//                        middleLine.append(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                        middleLine.append(EscapeSequences.SET_BG_COLOR_WHITE);

                    }else{
//                        middleLine.append(EscapeSequences.SET_BG_COLOR_WHITE);
                        middleLine.append(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    }
                }
                ChessPiece piece = null;
                if(color.equals("WHITE")){
                    piece = board.getPiece(new ChessPosition(row, col));
                }else{
                    piece = board.getPiece(new ChessPosition(row, 9- col));
                }

                if (piece != null) {
                    middleLine.append(getLetterPiece(piece));
                } else {
                    middleLine.append(EscapeSequences.SET_TEXT_COLOR_WHITE + "   ");
                }

                middleLine.append(EscapeSequences.RESET_BG_COLOR);
                middleLine.append(EscapeSequences.RESET_TEXT_COLOR);
            }
        }

        middleLine.append(EscapeSequences.RESET_BG_COLOR);
        middleLine.append(EscapeSequences.RESET_TEXT_COLOR);

        return middleLine;
    }

    public StringBuilder getLetterPiece(ChessPiece piece) {
        StringBuilder pieceLetter = new StringBuilder();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            pieceLetter.append(EscapeSequences.SET_TEXT_COLOR_BLUE);
        } else {
            pieceLetter.append(EscapeSequences.SET_TEXT_COLOR_RED);
        }
        switch (piece.getPieceType()) {
            case PAWN -> pieceLetter.append(" " + "P" + " ");
            case KING -> pieceLetter.append(" " + "K" + " ");
            case QUEEN -> pieceLetter.append(" " + "Q" + " ");
            case ROOK -> pieceLetter.append(" " + "R" + " ");
            case BISHOP -> pieceLetter.append(" " + "B" + " ");
            case KNIGHT -> pieceLetter.append(" " + "K" + " ");

        }
        return pieceLetter;
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
