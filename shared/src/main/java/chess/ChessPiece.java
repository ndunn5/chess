package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
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
        switch(type) {
            case PAWN:
                return new PawnMovesCalculator().pieceMoves(board, myPosition);
            case ROOK:
                return new RookMovesCalculator().pieceMoves(board, myPosition);
            case KNIGHT:
                return new KnightMovesCalculator().pieceMoves(board, myPosition);
            case BISHOP:
                return new BishopMovesCalculator().pieceMoves(board, myPosition);
            case QUEEN:
                return new QueenMovesCalculator().pieceMoves(board,myPosition);
            case KING:
                return new KingMovesCalculator().pieceMoves(board, myPosition);
            default:
                System.out.print("hmmmm");
                return null;
        }
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

    public ChessPiece copy() {
        ChessPiece copy = new ChessPiece(this.getTeamColor(), this.getPieceType());
        return copy;
    }
}
