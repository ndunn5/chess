package chess;

import java.util.Objects;

public class ChessPosition {
    int row;
    int col;
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return col;
    }

    public ChessPosition offset(int rowOff, int colOff){
        int newRow = this.row + rowOff;
        int newColumn = this.col + colOff;

        return new ChessPosition(newRow, newColumn);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}


