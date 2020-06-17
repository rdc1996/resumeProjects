package student;

import model.Space;
import model.Station;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StationSpace implements Station, Serializable {

    private int row;
    private int col;
    private String name;
    private int number;

    public StationSpace(int row, int col, int number, String name) {
        this.row = row;
        this.col = col;
        this.name = name;
        this.number = number;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public String getName() {
        return this.name;
    }

    public int getNumber() {
        return this.number;
    }

    public boolean collocated(Space other) {
        if (other instanceof Space) {
            return (other.getRow() == this.row && other.getCol() == this.col);
        }
        return false;
    }

}
