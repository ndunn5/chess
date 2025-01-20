package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessHelper {
    public static Collection<ChessMove> MultipleMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (int[] direction : directions) {
            ChessPosition CurrentPosition = myPosition;
            while (true) {
                CurrentPosition = CurrentPosition.offset(direction[0], direction[1]);

                if (!board.ValidMove(CurrentPosition)) {
                    break;
                }
                if (board.Occupied(myPosition, CurrentPosition) == 2) {
                    break;
                } else if (board.Occupied(myPosition, CurrentPosition) == 1) {
                    moves.add(new ChessMove(myPosition, CurrentPosition, null));
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, CurrentPosition, null));
                }
            }
        }
        return moves;
    }

    public static Collection<ChessMove> SingleMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (int[] direction : directions) {
            ChessPosition CurrentPosition = myPosition;
            CurrentPosition = CurrentPosition.offset(direction[0], direction[1]);

            if (board.ValidMove(CurrentPosition)) {
                if (board.Occupied(myPosition, CurrentPosition) == 1 || board.Occupied(myPosition, CurrentPosition) == 0) {
                    moves.add(new ChessMove(myPosition, CurrentPosition, null));
                }
            }
        }
        return moves;
    }
}