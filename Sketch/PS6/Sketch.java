import java.awt.*;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * @author ellie boyd, 06-04-21
 */

public class Sketch {
    public TreeMap<Integer, Shape> map = new TreeMap<>();

    /**
     * inserts a shape into the sketch map and assigns it to an ID
     * @param shape
     */
    public synchronized void insert(Shape shape) {
        int i = -1;
        for (Integer integer : map.keySet()) {
            if (integer > i) {
                i = integer;
            }
        }
        map.put(i + 1, shape);
    }

    /**
     * finds the highest ID (most recently created shape) that contains x and y
     * @param x
     * @param y
     * @return ID
     */
    public synchronized Integer findID(int x, int y) {
        for (Integer i : map.descendingKeySet()) {
            if (map.containsKey(i) && map.get(i).contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * calls a shape's move method to move it by the specified change in x and change in y
     * @param dx
     * @param dy
     * @param shape
     */
    public synchronized void move(int dx, int dy, Shape shape) {
		shape.moveBy(dx, dy);

    }

    /**
     * calls a shape's recolor method
     * @param color
     * @param shape
     */
    public synchronized void recolor(Color color, Shape shape) {
        shape.setColor(color);
    }

    /**
     * removes a shape from the sketch map using its ID number
     * @param ID
     */
    public synchronized void delete(Integer ID) {
        map.remove(ID);
    }
}
