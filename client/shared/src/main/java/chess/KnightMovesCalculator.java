package chess;

import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1,2},
                {2,1},
                {-1,2},
                {-2,1},
                {1,-2},
                {2,-1},
                {-1,-2},
                {-2,-1},
        };
        return AChessHelper.singleMove(board, myPosition, directions);
    }
}
