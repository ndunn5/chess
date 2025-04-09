package websocket.commands;

import chess.ChessMove;

public class MakeMoveMessage extends UserGameCommand{

    private ChessMove move;

    public MakeMoveMessage(String authToken, int gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getChessMove() {
        return move;
    }
}
