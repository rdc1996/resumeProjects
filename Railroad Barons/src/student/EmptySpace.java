package student;

import model.Space;

import java.io.Serializable;

public class EmptySpace implements Space, Serializable {

    private int row;
    private int col;

    public EmptySpace(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public boolean collocated(Space other) {
        if (other instanceof Space) {
            return (other.getRow() == this.row && other.getCol() == this.col);
        }
        return false;
    }

}
