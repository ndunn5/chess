package chess;

import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator{
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
        return AChessHelper.multipleMoves(board, myPosition, directions);
    }
}
