package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {0, 1}, //up
                {0, -1},//down
                {1, 0}, //right
                {-1, 0}
        };
        return ChessHelper.MultipleMoves(board, myPosition, directions);
    }
}
//        System.out.print("WE GOT TO ROOK!");
//        Collection<ChessMove> validMoves = new ArrayList<>();
//
//        int initialRow = myPosition.getRow();
//        initialRow --;
//        int initialCol = myPosition.getColumn();
//        initialCol --;
//
//        ChessPosition startPosition = new ChessPosition(initialRow, initialCol);
//        ChessPosition currPosition = new ChessPosition(initialRow, initialCol);
//        ChessMove currMove = new ChessMove(startPosition, currPosition, null);
//        int currRow = initialRow;
//        int currCol = initialCol;
//        while (currRow > 1) {
//            System.out.print(currRow);
//            currRow--;
//            currPosition.row = currRow;
//            if (board.getPiece(currPosition) != null) {
//                currMove.startPosition = startPosition;
//                currMove.endPosition = currPosition;
//                validMoves.add(currMove);
//                break;
//            } else {
//                currMove.startPosition = startPosition;
//                currMove.endPosition = currPosition;
//                validMoves.add(currMove);
//            }
//        }
//

//        currRow = initialRow;
//        while (currRow > 1) {
//            System.out.print(currRow);
//            currRow--;
//            currPosition.row = currRow;
//            if (board.getPiece(currPosition) != null) {
//                currMove.startPosition = startPosition;
//                currMove.endPosition = currPosition;
//                validMoves.add(currMove);
//                break;
//            } else {
//                currMove.startPosition = startPosition;
//                currMove.endPosition = currPosition;
//                validMoves.add(currMove);
//            }
//        }
        return validMoves;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

