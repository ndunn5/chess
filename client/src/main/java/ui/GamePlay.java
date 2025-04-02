package ui;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import model.LogoutRequest;
import model.LogoutResult;
import server.ServerFacade;
import chess.ChessGame;
import chess.ChessBoard;

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
    private final String serverUrl;
    private final ArrayList<String> sideLetters = new ArrayList<>(Arrays.asList(" ", "a", "b", "c", "d", "e", "f", "g", "h", " "));
    private final ArrayList<String> sideNumbers = new ArrayList<>(Arrays.asList(" ", "1", "2", "3", "4", "5", "6", "7", "8", " "));
    private ChessBoard currentBoard = new ChessBoard();
    private String clientColor = "WHITE";
    private ChessGame chessGame = new ChessGame();


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
                case ("redraw") -> redraw();
                case ("highlight") -> highlight(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String showBoard(String boardState, String whiteOrBlack) {
        this.clientColor = whiteOrBlack;
        Gson gson = new Gson();
        JsonObject jsonObject = JsonParser.parseString(boardState).getAsJsonObject();
        JsonObject boardObject = jsonObject.getAsJsonObject("board");
        this.currentBoard = gson.fromJson(boardObject, ChessBoard.class);
        return drawBoard(currentBoard, whiteOrBlack, null);
    }

    public String drawBoard(ChessBoard board, String whiteOrBlack, Collection<ChessMove> validMoves) {
        StringBuilder boardDisplay = new StringBuilder();
        Collection<ChessPosition> justChessEndPositions = new ArrayList<>();
        if (validMoves != null){
            for(ChessMove validMove: validMoves){
                justChessEndPositions.add(validMove.getEndPosition());
            }
        }

        for (int row = 0; row < 10; row++) {
            if (row == 0 || row == 9) {
                boardDisplay.append(drawHeaderAndFooter(whiteOrBlack) + "\n");
            } else {
                if(whiteOrBlack.equals("WHITE")){
                    boardDisplay.append(drawMiddle(board, 9-row, whiteOrBlack, justChessEndPositions) + "\n");
                }else{
                    boardDisplay.append(drawMiddle(board, row, whiteOrBlack, justChessEndPositions) + "\n");
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

    private StringBuilder drawMiddle(ChessBoard board, int row, String color, Collection<ChessPosition> endPositions) {
        StringBuilder middleLine = new StringBuilder();
        boolean highlight = false;
        for (int col = 0; col < 10; col++) {
            ChessPosition currentPosition;
            if (color.equals("WHITE")){
                currentPosition = new ChessPosition(row, col);
            } else{
                currentPosition = new ChessPosition(row, 9- col);
            }
            highlight = endPositions.contains(currentPosition);
            if (col == 0 || col == 9) {
                middleLine.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                middleLine.append(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + sideNumbers.get(row) + " ");
            } else {
                if(highlight){
                    middleLine.append(EscapeSequences.SET_BG_COLOR_YELLOW);
                }
                else if ((row + col) % 2 == 0) {
                    if(color.equals("WHITE")){
                        middleLine.append(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                    }
                    else{
                        middleLine.append(EscapeSequences.SET_BG_COLOR_WHITE);
                    }
                } else {
                    if(color.equals("WHITE")){
                        middleLine.append(EscapeSequences.SET_BG_COLOR_WHITE);

                    }else{
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
            case KNIGHT -> pieceLetter.append(" " + "N" + " ");

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

    public String redraw(){
        return drawBoard(currentBoard, clientColor, null);
    }

    public String highlight(String... params) throws ResponseException{
        if(params.length == 1){
            String letterAndNumber = params[0];
            if (checkValidLetterAndNumber(letterAndNumber)){
                int row = Character.getNumericValue(letterAndNumber.charAt(1));
                int col = sideLetters.indexOf(String.valueOf(letterAndNumber.charAt(0)));
                ChessPosition chessPosition = new ChessPosition(row, col);
                ChessPiece chessPiece = currentBoard.getPiece(chessPosition);
                if (chessPiece == null){
                    throw new ResponseException(400, "there is no piece there");
                }
                Collection<ChessMove> validMoves = chessGame.validMoves(chessPosition);
                if (validMoves.isEmpty()){
                    return "no valid moves";
                }
                return drawBoard(currentBoard, clientColor, validMoves);
//                return letterAndNumber;
            }else{
                return "invalid letter and number. Type letter(a-h) first and then number (1-8)";
            }
        }
        throw new ResponseException(400, "Expected: <letter(a-h) number (1-8)>");
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
}
