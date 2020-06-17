package student;

import model.*;

import java.io.Serializable;

public class TrackSpace implements Track, Serializable {

    private int row;
    private int col;
    private Orientation orientation;
    private Baron baron;
    private Route route;

    public TrackSpace(int row, int col, Orientation orientation, Baron baron, Route route) {
        this.row = row;
        this.col = col;
        this.orientation = orientation;
        this.baron = baron;
        this.route = route;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public Orientation getOrientation() {
        return this.orientation;
    }

    public Baron getBaron() {
        return this.baron;
    }

    public Route getRoute() {
        return this.route;
    }

    public boolean collocated(Space other) {
        if (other instanceof Space) {
            return (other.getRow() == this.row && other.getCol() == this.col);
        }
        return false;
    }

}
