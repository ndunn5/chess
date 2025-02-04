package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor currentTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.currentTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return null;
        } else {
            ChessPiece myPiece = board.getPiece(startPosition);
            TeamColor myTeamColor = myPiece.getTeamColor();
            Collection<ChessMove> allMoves = myPiece.pieceMoves(board, startPosition);
            Collection<ChessMove> returnMoves = new ArrayList<>();
            for (ChessMove move : allMoves) {
                returnMoves.add(move.clone());  // This uses the clone method you implemented
            }
            for (ChessMove move : allMoves){
                //gotta make sure the board can be cloned
            }
        }

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);
        if (!this.validMoves(startPosition).contains(move)) {
            throw new InvalidMoveException("Invalid move dude");
        }

        if (move.getPromotionPiece() != null){
            board.addPiece(endPosition, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        else{
            board.addPiece(endPosition, piece);
        }
        board.addPiece(startPosition, null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */



    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = AChessGameHelper.findKingPosition(board, teamColor);
        ChessPosition currentPosition;
        ChessPiece currentPiece;
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                currentPosition = new ChessPosition(row, col);
                currentPiece = board.getPiece(currentPosition);
                if(currentPiece != null && currentPiece.getTeamColor() != teamColor){
                    Collection<ChessMove> moves = currentPiece.pieceMoves(board, currentPosition);
                    for(ChessMove move : moves){
                        if(move.getEndPosition() == kingPosition){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
//        throw new RuntimeException("Not implemented");
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
//        throw new RuntimeException("Not implemented");
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
//        throw new RuntimeException("Not implemented");
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
//        throw new RuntimeException("Not implemented");
        return board;
    }
    //constuctor -> getters and setters -> isincheck -> validMoves -> makeMove -> all the checks
    //override .clone


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
