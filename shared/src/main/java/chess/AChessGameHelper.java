package chess;

public class AChessGameHelper {
    public static ChessPosition findKingPosition(ChessBoard board, ChessGame.TeamColor teamColor){
        ChessPosition currentPosition;
        ChessPiece currentPiece;
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                currentPosition = new ChessPosition(row, col);
                currentPiece = board.getPiece(currentPosition);
                if(currentPiece.getTeamColor() == teamColor && currentPiece.getPieceType() == ChessPiece.PieceType.KING){
                    return currentPosition;
                }
            }
        }
        return null;
    }
}
