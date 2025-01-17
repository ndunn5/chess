package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        System.out.print("WE GOT TO ROOK!");
        Collection<ChessMove> validMoves = new ArrayList<>();

        int initialRow = myPosition.getRow();
        initialRow --;
        int initialCol = myPosition.getColumn();
        initialCol --;

        ChessPosition startPosition = new ChessPosition(initialRow, initialCol);
        ChessPosition currPosition = new ChessPosition(initialRow, initialCol);
        int currRow = initialRow;
        int currCol = initialCol;
        while (currCol < 8){
            currCol ++;
            currPosition.col = currCol;
            if (board.getPiece(currPosition) != null) {
                ChessMove currMove = new ChessMove(startPosition, currPosition, null);
                validMoves.add(currMove);
                break;
            }
            else{
                ChessMove currMove = new ChessMove(startPosition, currPosition, null);
                validMoves.add(currMove);
            }
        }
        return validMoves;
    }

    @Override
    public String toString() {
        return "RookMovesCalculator{}";
    }
}

