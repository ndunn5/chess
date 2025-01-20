package chess;

import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1, 1}, //up right
                {-1, 1}, //up left
                {-1, -1},//down left
                {1, -1}, //down right
        };
        return ChessHelper.MultipleMoves(board, myPosition, directions);
    }
}
