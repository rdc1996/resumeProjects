package student;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Map implements MapMaker, Serializable {

    public static List<Station> stations = new ArrayList<>();
    private List<Route> routes = new ArrayList<>();
    private List<Track> tracks = new ArrayList<>();

    public RailroadMap readMap(InputStream in) throws RailroadBaronsException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = in.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
        }
        catch (IOException ex) {}
        Scanner scanner = new Scanner(new ByteArrayInputStream(baos.toByteArray()));
        String line = scanner.nextLine();
        String[] firstLine = line.split(" ");
        if (firstLine.length >= 4) {
            while (!(line.equals("##ROUTES##"))) {
                String[] strings = line.split(" ", 4);
                stations.add(new StationSpace(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]),
                        Integer.parseInt(strings[0]), strings[3]));
                line = scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                String[] lines = line.split(" ");
                int originInt = Integer.parseInt(lines[0]);
                int destinationInt = Integer.parseInt(lines[1]);
                StationSpace origin = null;
                StationSpace destination = null;
                for (Station s : stations) {
                    StationSpace so = (StationSpace)s;
                    if (so.getNumber() == originInt) {
                        origin = so;
                    }
                    else if (so.getNumber() == destinationInt) {
                        destination = so;
                    }
                }
                Orientation orientation = null;
                if (origin.getCol() == destination.getCol()) {
                    orientation = Orientation.VERTICAL;
                }
                if (origin.getRow() == destination.getRow()) {
                    orientation = Orientation.HORIZONTAL;
                }
                routes.add(new RouteClass(origin, destination, orientation));
            }
            for (Route r : routes) {
                RouteClass ro = (RouteClass)r;
                tracks.addAll(ro.getTracks());
            }
            return new RailMap(stations, tracks, routes);
        }
        else {
            try {
                ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
                Object object = oin.readObject();
                if (object instanceof RailroadMap) {
                    return (RailroadMap) object;
                }
            }
            catch (IOException ex) {
                System.out.println(ex);;
            }
            catch (ClassNotFoundException cnfe) {
                System.out.println("Error2");
            }
        }
        return null;
    }

    public void writeMap(RailroadMap map, OutputStream out) throws RailroadBaronsException {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(map);
        }
        catch (IOException ex) {}
    }

//    public static void main(String[] args) throws IOException, RailroadBaronsException {
//        Map mapMaker = new Map();
//        FileInputStream fr = new FileInputStream(args[0]);
//        RailroadMap map = mapMaker.readMap(fr);
//
//        OutputStream out = new FileOutputStream(args[0] + ".txt");
//        mapMaker.writeMap(map, out);
//
//        InputStream fr2 = new FileInputStream("maps/simple.rbmap.txt");
//        mapMaker.readMap(fr2);
//
//    }
}
