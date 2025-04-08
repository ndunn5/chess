package ui.websocket;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class GameUI implements GameHandler{
    private WebSocketFacade wsFacade;
    private final ArrayList<String> sideLetters = new ArrayList<>(Arrays.asList(" ", "a", "b", "c", "d", "e", "f", "g", "h", " "));
    private final ArrayList<String> sideNumbers = new ArrayList<>(Arrays.asList(" ", "1", "2", "3", "4", "5", "6", "7", "8", " "));
    private ChessBoard currentBoard;
    private String clientColor = "WHITE";
    private ChessGame chessGame = new ChessGame();

    public GameUI(String serverURL) {
        try{
            this.wsFacade = new WebSocketFacade(serverURL, this);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public void updateGame(GameData game) {
//        //do something with wsFacade
//    }

    @Override
    public void printMessage(ServerMessage message) {
        System.out.println(message.toString());
    }

    @Override
    public ChessBoard getCurrentBoard(){
        return currentBoard;
    }

    @Override
    public String getColor(){
        return clientColor;
    }

    public String showBoard(ChessBoard board, String whiteOrBlack) {//can just pass in the object itself
        clientColor = whiteOrBlack;
//        Gson gson = new Gson();
//        JsonObject jsonObject = JsonParser.parseString(boardState).getAsJsonObject();
//        JsonObject boardObject = jsonObject.getAsJsonObject("board");
//        currentBoard = gson.fromJson(boardObject, ChessBoard.class);
        currentBoard = board;
        System.out.print(drawBoard(whiteOrBlack, null));
        return drawBoard(whiteOrBlack, null);
    }

    public String drawBoard(String whiteOrBlack, Collection<ChessMove> validMoves) {
        StringBuilder boardDisplay = new StringBuilder();
        Collection<ChessPosition> justChessEndPositions = new ArrayList<>();
        if (validMoves != null){
            for(ChessMove validMove: validMoves){
                justChessEndPositions.add(validMove.getEndPosition());
                if(!justChessEndPositions.contains(validMove.getStartPosition())){
                    justChessEndPositions.add(validMove.getStartPosition());
                }
            }
        }
        boardDisplay.append("\n");

        for (int row = 0; row < 10; row++) {
            if (row == 0 || row == 9) {
                boardDisplay.append(drawHeaderAndFooter(whiteOrBlack) + "\n");
            } else {
                if(whiteOrBlack.equals("WHITE")){
                    boardDisplay.append(drawMiddle(currentBoard, 9-row, whiteOrBlack, justChessEndPositions) + "\n");
                }else{
                    boardDisplay.append(drawMiddle(currentBoard , row, whiteOrBlack, justChessEndPositions) + "\n");
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
}


