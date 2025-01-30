package chess;

import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {-1,-1},
                {1,1},
                {-1,1},
                {1,-1}
        };
        return AChessHelper.multipleMoves(board, myPosition, directions);
    }
}