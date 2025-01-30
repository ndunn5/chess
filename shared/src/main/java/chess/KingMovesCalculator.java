package chess;

import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1,0},
                {0,1},
                {-1,0},
                {0,-1},
                {-1,-1},
                {1,1},
                {-1,1},
                {1,-1},
        };
        return AChessHelper.singleMove(board, myPosition, directions);
    }
}
