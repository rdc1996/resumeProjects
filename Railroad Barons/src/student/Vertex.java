package student;

import model.Station;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
    private Station data;

    private List<Vertex> neighbors;

    public Vertex(Station data) {
        this.data=data;
        neighbors = new ArrayList<>();
    }

    public Station getData(){
        return data;
    }

    public List getNeighbors(){
        return neighbors;
    }

    public void addNeighbor(Vertex vertex) {
        this.neighbors.add(vertex);
    }

}
