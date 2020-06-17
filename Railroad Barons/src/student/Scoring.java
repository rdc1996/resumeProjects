package student;

import model.Baron;
import model.RailroadBarons;
import model.Route;
import model.Station;
import java.util.*;

public class Scoring {

    private HashMap<Station, Vertex> graph;

    public Scoring() {
        this.graph = new HashMap<>();
    }

    public void addVertex(Station data) {
        Vertex vertex = new Vertex(data);
        graph.put(data, vertex);
    }

    public void connect(Station data1, Station data2) {
        Vertex vertex1 = graph.get(data1);
        Vertex vertex2 = graph.get(data2);

        vertex1.addNeighbor(vertex2);
        vertex2.addNeighbor(vertex1);
    }

    public boolean pathExistsNS(Station start, int end, Baron baron, RailroadBarons game) {
        Set<Vertex> visited = new HashSet<>();
        Queue<Vertex> vertices = new LinkedList<>();

        Vertex starting = graph.get(start);
        //Vertex ending = graph.get(end);

        visited.add(starting);
        //visited.add(ending);
        vertices.add(starting);
        while(!(vertices.isEmpty())) {
            Vertex next= vertices.poll();
            if (next.getData().getRow() == 0) {
                return true;
            }
            else {
                for (Object neighbor : next.getNeighbors()) {
                    neighbor = (Vertex) neighbor;
                    if (!visited.contains(neighbor)) {
                        visited.add((Vertex)neighbor);

                        Station nextStation = ((Vertex) neighbor).getData();
                        Station currentStation = next.getData();

                        if (currentStation.getCol() == nextStation.getCol()){
                            if(currentStation.getRow() < nextStation.getRow()){
                                Route r = game.getRailroadMap().getRoute(currentStation.getRow()+1, currentStation.getCol());
                                if(r.getBaron()==baron){
                                    System.out.println(currentStation.getRow());
                                    vertices.add((Vertex)neighbor);
                                }
                            }
                            else{
                                Route r = game.getRailroadMap().getRoute(currentStation.getRow()-1, currentStation.getCol());
                                if(r.getBaron()==baron){
                                    System.out.println(currentStation.getRow());
                                    vertices.add((Vertex)neighbor);
                                }

                            }
                        }
                        else{

                            if(currentStation.getCol() < nextStation.getCol()){
                                Route r = game.getRailroadMap().getRoute(currentStation.getRow(), currentStation.getCol()+1);
                                if(r.getBaron()==baron){
                                    System.out.println(currentStation.getRow());
                                    vertices.add((Vertex)neighbor);
                                }
                            }
                            else{
                                Route r = game.getRailroadMap().getRoute(currentStation.getRow(), currentStation.getCol()-1);
                                if(r.getBaron()==baron){
                                    System.out.println(currentStation.getRow());
                                    vertices.add((Vertex)neighbor);
                                }

                            }
                        }


                    }
                }
            }
        }
        return false;
    }


    public boolean pathExistsEW(Station start, int end, Baron baron, RailroadBarons game) {
        Set<Vertex> visited = new HashSet<>();
        Queue<Vertex> vertices = new LinkedList<>();

        Vertex starting = graph.get(start);
        //Vertex ending = graph.get(end);

        visited.add(starting);
        vertices.add(starting);
        //visited.add(ending);

        while(!(vertices.isEmpty())) {
            Vertex next= vertices.poll();
            System.out.println(next.getData().getCol());
            if (next.getData().getCol() == end-1) {
                return true;
            }
            else {
                for (Object neighbor : next.getNeighbors()) {
                    neighbor = (Vertex) neighbor;
                    if (!visited.contains(neighbor)) {
                        visited.add((Vertex)neighbor);

                        Station nextStation = ((Vertex) neighbor).getData();
                        Station currentStation = next.getData();

                        if (currentStation.getCol() == nextStation.getCol()){
                            if(currentStation.getRow() < nextStation.getRow()){
                                Route r = game.getRailroadMap().getRoute(currentStation.getRow()+1, currentStation.getCol());
                                if(r.getBaron()==baron){
                                    System.out.println(currentStation.getCol());
                                    vertices.add((Vertex)neighbor);
                                }
                            }
                            else{
                                Route r = game.getRailroadMap().getRoute(currentStation.getRow()-1, currentStation.getCol());
                                if(r.getBaron()==baron){
                                    System.out.println(currentStation.getCol());
                                    vertices.add((Vertex)neighbor);
                                }

                            }
                        }
                        else{

                            if(currentStation.getCol() < nextStation.getCol()){
                                Route r = game.getRailroadMap().getRoute(currentStation.getRow(), currentStation.getCol()+1);
                                if(r.getBaron()==baron){
                                    System.out.println(currentStation.getCol());
                                    vertices.add((Vertex)neighbor);
                                }
                            }
                            else{
                                Route r = game.getRailroadMap().getRoute(currentStation.getRow(), currentStation.getCol()-1);
                                if(r.getBaron()==baron){
                                    System.out.println(currentStation.getCol());
                                    vertices.add((Vertex)neighbor);
                                }

                            }
                        }


                    }
                }
            }
        }
        return false;
    }
}
