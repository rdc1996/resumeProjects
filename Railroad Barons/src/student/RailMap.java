package student;

import model.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RailMap implements RailroadMap {
    private List<RailroadMapObserver> observers = new ArrayList<RailroadMapObserver>();
    private Space[][] spaces;
    private List<Station> stations ;
    private List<Track> tracks;
    private List<Route> routes;
    public static Scoring graph = new Scoring();

    public RailMap(List<Station> stations, List<Track> tracks, List<Route> routes){
        //this.spaces = spaces;
        this.stations=stations;
        this.tracks=tracks;
        this.routes=routes;
        spaces = new Space[this.getRows()][this.getCols()];
        for (int i =0; i<this.getRows(); i++){
            for (int j=0; j<this.getCols(); j++){
                spaces[i][j]= new EmptySpace(i,j);
                for (Track s : tracks){
                    if (s.getCol()==j && s.getRow() ==i){
                        spaces[i][j]=s;
                    }
                }
                for (Station s : stations){
                    if (s.getCol()==j && s.getRow() ==i){
                        spaces[i][j]=s;
                    }
                }

            }
        }
        for (Station s : stations) {
            graph.addVertex(s);
        }
    }
    @Override
    public void addObserver(RailroadMapObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(RailroadMapObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getRows() {
        Station first = stations.get(0);
        int greatestRow = first.getRow();

        for (Station stat:stations){
            Station stats = (Station)stat;
            if (stats.getRow()>greatestRow){
                greatestRow = stats.getRow();
            }
        }
        return greatestRow + 1;
    }

    @Override
    public int getCols() {
        Station first = stations.get(0);
        int greatestCol = first.getCol();

        for (Station stat:stations){
            Station stats = (Station)stat;
            if (stats.getCol()>greatestCol){
                greatestCol = stats.getCol();
            }
        }
        return greatestCol + 1;
    }


    @Override
    public Space getSpace(int row, int col) {
        return spaces[row][col];
    }

    @Override
    public Route getRoute(int row, int col) {
        if (spaces[row][col] instanceof TrackSpace){
            TrackSpace space = (TrackSpace)spaces[row][col];
            return space.getRoute();
        }
        else return null;

    }

    @Override
    public void routeClaimed(Route route) {
       /* for (Object listR:routes){
            RouteClass router = (RouteClass) listR;
            if(router.equals(route)){
                routes.remove(router);
                routes.add(route);
            }
        } */
        for (RailroadMapObserver ob : observers){
            ob.routeClaimed(this, route);
        }
    }

    @Override
    public int getLengthOfShortestUnclaimedRoute() {
        //RouteClass first = (RouteClass)routes.get(0);
        int length = 99; //first.getLength();

        for (Route route:routes){

            if (route.getBaron()==Baron.UNCLAIMED && route.getLength()<length){
                length = route.getLength();
            }
        }
        return length;
    }

    @Override
    public Collection<Route> getRoutes() {
        return routes;
    }

    public List<Station> getStations() {
        return this.stations;
    }
}
