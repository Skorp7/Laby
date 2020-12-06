package laby.logic;

/**
 *
 * @author sakorpi
 */
public class Pair {
    int y; int x;
    
    public Pair(int y, int x) {
        this.y = y;
        this.x = x;
    }
    @Override
    public String toString() {
        return "Koordinaatit: "+y+","+x;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        final Pair other = (Pair) obj;
        if (this.y == other.y && this.x == other.x) return true;
        return false;
    }
}