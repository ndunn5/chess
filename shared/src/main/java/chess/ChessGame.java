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

//start with dataacsess-> Data model classes -> makerequest and result classes
//getting started in phase 3 FOLLOW STEP BY STEP
//for testing endpoints
//localhose:8080 to run the temporary frontend
public class ChessGame {

    private ChessBoard board;
    private TeamColor currentTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.currentTurn = TeamColor.WHITE;
        board.resetBoard();
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
        Collection<ChessMove> returnMoves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(startPosition);
        if (board.getPiece(startPosition) == null) {
            return null;
        }
        else {
            TeamColor myTeamColor = myPiece.getTeamColor();
            Collection<ChessMove> allMoves = myPiece.pieceMoves(board, startPosition);
            ChessBoard originalBoard = board.clone();
            for (ChessMove move : allMoves){
                //gotta make sure the board can be cloned
                ChessPosition targetPosition = move.getEndPosition();
                ChessPiece targetPiece = board.getPiece(targetPosition);
                board.addPiece(targetPosition, myPiece);
                board.addPiece(startPosition, null);
                if(!isInCheck(myTeamColor)){
                    returnMoves.add(move);
                }
                board.addPiece(startPosition, myPiece);
                board.addPiece(targetPosition, targetPiece);
//                this.setBoard(originalBoard);
            }
        }
    return returnMoves;
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
        if(board.getPiece(startPosition) == null){
            throw new InvalidMoveException("you dont got no piece");
        }
        ChessPiece piece = board.getPiece(startPosition);
        if (!this.validMoves(startPosition).contains(move)) {
            throw new InvalidMoveException("Invalid move dude");
        }
        if(getTeamTurn() != piece.getTeamColor()){
            throw new InvalidMoveException("wait yo turn");
        }
        if (move.getPromotionPiece() != null){
            board.addPiece(endPosition, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        else{
            board.addPiece(endPosition, piece);
        }
        board.addPiece(startPosition, null);
        if(currentTurn == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        }
        else{
            setTeamTurn(TeamColor.WHITE);
        }
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
                        if(move.getEndPosition().equals(kingPosition)){
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
        ChessPosition kingPosition = AChessGameHelper.findKingPosition(board, teamColor);
        ChessPiece currentPiece;
        if(!isInCheck(teamColor)){
            return false;
        }
        else{ //lets loop through these bad boys
            for(int row = 1; row <= 8; row++){
                for(int col = 1; col <= 8; col++){
                    ChessPosition currentPosition = new ChessPosition(row, col);
                    currentPiece = board.getPiece(currentPosition);
                    if(currentPiece != null){
                        Collection<ChessMove> currentMoves = validMoves(currentPosition);
                        if(currentPiece.getTeamColor() == teamColor && !currentMoves.isEmpty()){
                            return false;
                        }
                    }
                }
            }
        }
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
        if(isInCheck(teamColor)){
            return false;
        }
        ChessPiece currentPiece;
        if (!isInCheck(teamColor)) {
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition currentPosition = new ChessPosition(row, col);
                    currentPiece = board.getPiece(currentPosition);
                    if (currentPiece != null){
                        if (currentPiece.getTeamColor() == teamColor) {
                            Collection<ChessMove> currentMoves = validMoves(currentPosition);
                            if(!currentMoves.isEmpty()){
                                return false;
                            }
                        }
                    }
                }
            }
        }
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
//    override .clone


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn);
    }
}
