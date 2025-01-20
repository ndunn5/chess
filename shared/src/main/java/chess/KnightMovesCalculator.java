package chess;

import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1, 2},
                {2, 1},
                {-1, 2},
                {-2, 1},
                {1, -2},
                {2, -1},
                {-1, -2},
                {-2, -1}
        };
        return ChessHelper.SingleMoves(board, myPosition, directions);
    }
}
