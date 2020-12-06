package laby.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.Label;

/**
 *
 * @author sakorpi
 */
public class PathFinder {
    int[][] map;
    int width;
    int height;
    int black = -16777216;
    int red = -65536;
    int blue = -16776961;
    int white = -1;
    int green = -16711936;
    int limit = -856619;
    Label info;
    
    public PathFinder(int[][] map, Label info) {
        this.map = map;
        this.height = map.length;
        this.width = map[0].length;
        this.info = info;
    }
    
    public int[][] getMapWithPath() {
        Pair[] fromto = findAB();
        if (fromto[0] == null) {
            info.setText("Ei löydetty alku- ja loppupisteitä");
            return null;
        }
        System.out.println("LÄHTÖ: " + fromto[0]);
        System.out.println("MAALI: " + fromto[1]);
        int routeLength = seekShortest(fromto[0], fromto[1]);
        info.setText("Lyhin reitti on: " + routeLength + " pikseliä.");
        if (routeLength == 0) info.setText("Reittiä löytynyt. \nKokeile muuttaa seinien läpäisevyyttä \ntai merkitse pisteet uudelleen.");
        
        return map;
    }
    
    private Pair[] findAB() {
        Pair[] AB = new Pair[2];
        for (int y=0; y <height; y++) {
            for (int x=0; x<width; x++) {
               if (map[y][x] == blue) {
                   AB[0] = new Pair(y, x);
               }
               if (map[y][x] == red) {
                   AB[1] = new Pair(y, x);
               }
            }
        }
        return AB;
    }
    
    private int seekShortest(Pair from, Pair to) {
        ArrayDeque<ArrayDeque<Pair>> que = new ArrayDeque<>();
        boolean[][] visited = new boolean[height][width];
        int[][] dist = new int[height][width];
        ArrayDeque<Pair> first = new ArrayDeque<>();
        first.add(from);
        que.addLast(first);
        visited[from.y][from.x] = true;
        dist[from.y][from.x] = 0;

        while (!que.isEmpty()) {
            ArrayDeque<Pair> path = que.pollFirst();
            Pair coord = path.peekLast();
            for (Pair neigh : findNeighbours(coord)) {
                if (neigh != null) {
                    if (visited[neigh.y][neigh.x]) continue;
                    ArrayDeque<Pair> newPath = path.clone();
                    newPath.addLast(neigh);
                    que.addLast(newPath);
                    visited[neigh.y][neigh.x] = true;
                    dist[neigh.y][neigh.x] = dist[coord.y][coord.x] + 1;
                    if (neigh.equals(to)) {
                        paintRoute(newPath, green);
                        break;
                    }
                }
            }
        }        
        return dist[to.y][to.x];
    }
    
    
    private void paintRoute(ArrayDeque<Pair> route, int color) {
        for (Pair coord : route ) {
            map[coord.y][coord.x] = color;
            map[coord.y+1][coord.x] = color;
            map[coord.y][coord.x+1] = color;
            map[coord.y-1][coord.x] = color;
            map[coord.y][coord.x-1] = color;  
        }
    }

    private ArrayList<Pair> findNeighbours(Pair coord) {
        if (coord.y < 0 || coord.x < 0 || coord.y >= height|| coord.x >= width) return null;
        if (map[coord.y][coord.x] == black) return null;
        ArrayList<Pair> possibleNeighs = new ArrayList<>(Arrays.asList(createNeigh(coord.y+1, coord.x),createNeigh(coord.y-1, coord.x),
            createNeigh(coord.y, coord.x+1),createNeigh(coord.y, coord.x-1)));
        return possibleNeighs;
    }
    
    private Pair createNeigh(int y, int x) {
        if (!(y< 0 || x < 0 || y >= height|| x >= width)) {
            if (map[y][x] > limit) {
                return new Pair(y, x);
            }
        }
        return null;
    }
    
    private void print(int[][] table) {
        for (int y = 0; y < table.length; y++) {
            for (int x = 0; x < table.length; x++) {
                System.out.print(table[y][x]);
            }
            System.out.println("");
        }
    }
}
