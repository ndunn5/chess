package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece myPawn = board.getPiece(myPosition);
        ChessPosition currentPosition = myPosition;
        if (myPawn.getTeamColor() == ChessGame.TeamColor.WHITE){
            currentPosition = currentPosition.adjustPosition(1, -1);
            if (board.checkValid(currentPosition) && board.checkOccupied(myPosition, currentPosition) == 1){
                moves.addAll(AChessHelper.promotePawn(myPawn, myPosition, currentPosition));
            }
            currentPosition = myPosition;
            currentPosition = currentPosition.adjustPosition(1, 1);
            if (board.checkValid(currentPosition) && board.checkOccupied(myPosition, currentPosition) == 1){
                moves.addAll(AChessHelper.promotePawn(myPawn, myPosition, currentPosition));
            }
            currentPosition = myPosition;
            currentPosition = currentPosition.adjustPosition(1, 0);
            if (board.checkValid(currentPosition) && board.checkOccupied(myPosition, currentPosition) == 0){
                moves.addAll(AChessHelper.promotePawn(myPawn, myPosition, currentPosition));
                if (myPosition.getRow() == 2){
                    currentPosition = currentPosition.adjustPosition(1, 0);
                    if (board.checkValid(currentPosition) && board.checkOccupied(myPosition, currentPosition) == 0) {
                        moves.addAll(AChessHelper.promotePawn(myPawn, myPosition, currentPosition));
                    }
                }
            }
        }
        else{
            currentPosition = currentPosition.adjustPosition(-1, -1);
            if (board.checkValid(currentPosition) && board.checkOccupied(myPosition, currentPosition) == 1){
                moves.addAll(AChessHelper.promotePawn(myPawn, myPosition, currentPosition));
            }
            currentPosition = myPosition;
            currentPosition = currentPosition.adjustPosition(-1, 1);
            if (board.checkValid(currentPosition) && board.checkOccupied(myPosition, currentPosition) == 1){
                moves.addAll(AChessHelper.promotePawn(myPawn, myPosition, currentPosition));
            }
            currentPosition = myPosition;
            currentPosition = currentPosition.adjustPosition(-1, 0);
            if (board.checkValid(currentPosition) && board.checkOccupied(myPosition, currentPosition) == 0){
                moves.addAll(AChessHelper.promotePawn(myPawn, myPosition, currentPosition));
                if (myPosition.getRow() == 7){
                    currentPosition = currentPosition.adjustPosition(-1, 0);
                    if (board.checkValid(currentPosition) && board.checkOccupied(myPosition, currentPosition) == 0) {
                        moves.addAll(AChessHelper.promotePawn(myPawn, myPosition, currentPosition));
                    }
                }
            }
        }
        return moves;
    }
}
