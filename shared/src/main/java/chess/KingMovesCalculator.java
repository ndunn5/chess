package chess;

import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1, 1}, //up right
                {-1, 1}, //up left
                {-1, -1},//down left
                {1, -1}, //down right
                {0, 1}, //up
                {0, -1},//down
                {1, 0}, //right
                {-1, 0}
        };
        return ChessHelper.SingleMoves(board, myPosition, directions);
    }
}