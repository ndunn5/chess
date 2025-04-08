package ui.websocket;

import chess.ChessBoard;
import chess.ChessMove;
import model.GameData;
import websocket.messages.ServerMessage;

import java.util.Collection;

public interface GameHandler {
    public String showBoard(String currentBoard, String whiteOrBlack);
    public void printMessage(ServerMessage message);
    public String drawBoard(String whiteOrBlack, Collection<ChessMove> validMoves);
    ChessBoard getCurrentBoard();
}
