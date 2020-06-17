package student;

import model.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RouteClass implements Route, Serializable {

    private Baron baron;
    private List<Track> tracks;
    private Station origin;
    private Station destination;
    private Orientation orientation;

    public RouteClass(Station origin, Station destination, Orientation orientation) {
        this.baron = Baron.UNCLAIMED;
        this.origin = origin;
        this.destination = destination;
        this.orientation = orientation;

        tracks = new ArrayList<>();

        if (orientation.equals(Orientation.HORIZONTAL)) {
            for (int i = 0; i < (destination.getCol() - origin.getCol()); i++) {
                tracks.add(new TrackSpace(origin.getRow(), origin.getCol() + i, orientation, baron, this));
            }
        }
        else if (orientation.equals(Orientation.VERTICAL)) {
            for (int i = 0; i < (destination.getRow() - origin.getRow()); i++) {
                tracks.add(new TrackSpace(origin.getRow() + i, origin.getCol(), orientation, baron, this));
            }
        }
    }

    public Baron getBaron() {
        return this.baron;
    }

    public Station getOrigin() {
        return this.origin;
    }

    public Station getDestination() {
        return this.destination;
    }

    public Orientation getOrientation() {
        return this.orientation;
    }

    public List<Track> getTracks() {
        return this.tracks;
    }

    public int getLength() {
        return this.tracks.size();
    }

    public int getPointValue() {
        int length = this.getLength() - 1;
        switch (length) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 4;
            case 4:
                return 7;
            case 5:
                return 10;
            case 6:
                return 15;
            default:
                return (5 * (length - 3));

        }
    }

    public boolean includesCoordinate(Space space) {
        int col = space.getCol();
        int row = space.getRow();
        for (Track track: tracks) {
            if (track.getCol() == col && track.getRow() == row) {
                return true;
            }
        }
        return false;
    }

    public boolean claim(Baron claimant) {
        if (this.baron.equals(Baron.UNCLAIMED)) {
            this.baron = claimant;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof RouteClass){
            Route route = (Route) obj;
            if(this.getOrigin()==route.getOrigin() && this.getOrientation()==route.getOrientation() && this.getDestination()==route.getDestination()){
                return true;
            }
        }
        return false;
    }

}
