package chess;

import java.util.ArrayList;
import java.util.Collection;

public class AChessHelper {
    public static Collection<ChessMove> multipleMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();
        for(int[] direction: directions){
            ChessPosition currentPosition = myPosition;
            while(true){
                //move it
                currentPosition = currentPosition.adjustPosition(direction[0], direction[1]);
                //check valid
                if (!board.checkValid(currentPosition)){
                    break;
                }
                if (board.checkOccupied(myPosition, currentPosition) == 2){
                    break;
                }
                if (board.checkOccupied(myPosition, currentPosition) == 1){
                    moves.add(new ChessMove(myPosition, currentPosition, null));
                    break;
                }
                if (board.checkOccupied(myPosition, currentPosition) == 0){
                    moves.add(new ChessMove(myPosition, currentPosition, null));
                }
                //check occupied
            }
        }
        return moves;
    }

    public static Collection<ChessMove> singleMove(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (int[] direction : directions) {
            ChessPosition currentPosition = myPosition;
            currentPosition = currentPosition.adjustPosition(direction[0], direction[1]);
            if (board.checkValid(currentPosition)) {
                if (board.checkOccupied(myPosition, currentPosition) < 2) {
                    moves.add(new ChessMove(myPosition, currentPosition, null));
                }
            }
        }
        return moves;
    }

    public static Collection<ChessMove> promotePawn(ChessPiece myPawn, ChessPosition myPosition, ChessPosition currentPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        if ((myPawn.getTeamColor() == ChessGame.TeamColor.WHITE && myPosition.getRow() == 7) || (myPawn.getTeamColor() == ChessGame.TeamColor.BLACK && myPosition.getRow() == 2)){
            moves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition, currentPosition, ChessPiece.PieceType.BISHOP));
        }
        else{
            moves.add(new ChessMove(myPosition, currentPosition, null));
        }
        return moves;
    }
}
