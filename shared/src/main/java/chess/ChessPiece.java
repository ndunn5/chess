package chess;

import java.util.Collection;
import java.util.Objects;
import chess.RookMovesCalculator;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }
    public ChessGame.TeamColor getTeamColor()  {
        return pieceColor;
    }

    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type){
            case KING:
                return new KingMovesCalculator().pieceMoves(board, myPosition);
            case QUEEN:
                return new QueenMovesCalculator().pieceMoves(board, myPosition);
            case BISHOP:
                return new BishopMovesCalculator().pieceMoves(board, myPosition);
            case KNIGHT:
                return new KnightMovesCalculator().pieceMoves(board, myPosition);
            case ROOK:
                return new RookMovesCalculator().pieceMoves(board, myPosition);
            case PAWN:
                return new PawnMovesCalculator().pieceMoves(board,myPosition);
            default:
                throw new RuntimeException("some other piece dude...");
        }
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
