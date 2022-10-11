import java.awt.*;
import java.util.ArrayList;

/**
 * @author ellie boyd, 06-04-21
 */
public class Message {
    private Sketch sketch;
    public Message(Sketch sketch) {
        this.sketch = sketch;
    }

    /**
     * runs through a String message, parses it, and calls the necessary methods of the given sketch to change
     * shapes in the sketch as requested by the message
     * @param message
     */
    public synchronized void parse(String message) {
        //System.out.println(message);
        String[] string = message.split(" ");

        if (string[0].equals("draw")) {

            if (string[1].equals("ellipse")) {
                Color color = new Color(Integer.parseInt(string[6]));
                Shape shape = new Ellipse(Integer.parseInt(string[2]), Integer.parseInt(string[3]), Integer.parseInt(string[4]), Integer.parseInt(string[5]), color);
                sketch.insert(shape);
            }
            else if (string[1].equals("rectangle")) {
                Color color = new Color(Integer.parseInt(string[6]));
                Shape shape = new Rectangle(Integer.parseInt(string[2]), Integer.parseInt(string[3]), Integer.parseInt(string[4]), Integer.parseInt(string[5]), color);
                sketch.insert(shape);
            }
            else if (string[1].equals("segment")) {
                Color color = new Color(Integer.parseInt(string[6]));
                Shape shape = new Segment(Integer.parseInt(string[2]), Integer.parseInt(string[3]), Integer.parseInt(string[4]), Integer.parseInt(string[5]), color);
                sketch.insert(shape);
            }
            else if (string[1].equals("polyline")) {
                Color color = new Color(Integer.parseInt(string[2]));
                String newString = message.substring(24);
                String[] split = newString.split(" ");
                ArrayList<Point> polyline = new ArrayList<>();

                for (int i = 0; i < split.length - 1; i += 2) {
                    Point point = new Point((int)Double.parseDouble(split[i]), (int)Double.parseDouble(split[i + 1]));
                    polyline.add(point);
                }

                Polyline shape = new Polyline(polyline, color);
                //System.out.println("printed 3 " + shape.toString());
                sketch.insert(shape);
            }
        }
        else if (string[0].equals("move")) {

            if (string[1].equals("ellipse")) {
                Integer ID = (int) Double.parseDouble(string[9]); //server.getSketch().findID((int)Double.parseDouble(string[string.length - 2]), (int)Double.parseDouble(string[string.length-1])); //number format exception
                Ellipse shape = (Ellipse) sketch.map.get(ID);
                sketch.move((int) Double.parseDouble(string[7]), (int) Double.parseDouble(string[8]), shape);
                sketch.map.put(ID, shape);
            }
            else if (string[1].equals("rectangle")) {
                Integer ID = (int) Double.parseDouble(string[9]); //server.getSketch().findID((int)Double.parseDouble(string[string.length - 2]), (int)Double.parseDouble(string[string.length-1])); //number format exception
                Rectangle shape = (Rectangle) sketch.map.get(ID);
                sketch.move((int) Double.parseDouble(string[7]), (int) Double.parseDouble(string[8]), shape);
                sketch.map.put(ID, shape);
            }
            else if (string[1].equals("segment")) {
                Integer ID = (int) Double.parseDouble(string[9]); //server.getSketch().findID((int)Double.parseDouble(string[string.length - 2]), (int)Double.parseDouble(string[string.length-1])); //number format exception
                Segment shape = (Segment) sketch.map.get(ID);
                sketch.move((int) Double.parseDouble(string[7]), (int) Double.parseDouble(string[8]), shape);
                sketch.map.put(ID, shape);
            }
            else if (string[1].equals("polyline")) {
                //System.out.println("printed 2 " + message);
                Integer ID = (int) Double.parseDouble(string[string.length - 1]); //server.getSketch().findID((int)Double.parseDouble(string[string.length - 2]), (int)Double.parseDouble(string[string.length-1])); //number format exception
                Shape shape = sketch.map.get(ID);
                //System.out.println(ID);
                //System.out.println(sketch.map.keySet());
                sketch.move((int) Double.parseDouble(string[string.length - 3]), (int) Double.parseDouble(string[string.length - 2]), shape);
                sketch.map.put(ID, shape);
            }
        }
        else if (string[0].equals("recolor")) {
            Integer ID = Integer.parseInt(string[string.length - 1]);
            Shape shape = sketch.map.get(ID);
            Color color = new Color(Integer.parseInt(string[1]));
            sketch.recolor(color, shape);
        }
        else if (string[0].equals("delete")) {
            Integer ID = Integer.parseInt(string[1]);
            sketch.delete(ID);
        }

        else if (string[0].equals("create")) { //method for recreating shapes that already exist on the server but are not on a new editor
            Integer ID = Integer.parseInt(string[string.length - 1]);

            if (string[1].equals("ellipse")) {
                Color color = new Color(Integer.parseInt(string[6]));
                Shape shape = new Ellipse(Integer.parseInt(string[2]), Integer.parseInt(string[3]), Integer.parseInt(string[4]), Integer.parseInt(string[5]), color);
                sketch.map.put(ID, shape);
            }
            else if (string[1].equals("rectangle")) {
                Color color = new Color(Integer.parseInt(string[6]));
                Shape shape = new Rectangle(Integer.parseInt(string[2]), Integer.parseInt(string[3]), Integer.parseInt(string[4]), Integer.parseInt(string[5]), color);
                sketch.map.put(ID, shape);
            }
            else if (string[1].equals("segment")) {
                Color color = new Color(Integer.parseInt(string[6]));
                Shape shape = new Segment(Integer.parseInt(string[2]), Integer.parseInt(string[3]), Integer.parseInt(string[4]), Integer.parseInt(string[5]), color);
                sketch.map.put(ID, shape);
            }
            else if (string[1].equals("polyline")) {
                Color color = new Color(Integer.parseInt(string[2]));
                String newString = message.substring(26);
                String[] split = newString.split(" ");
                ArrayList<Point> polyline = new ArrayList<>();

                for (int i = 0; i < split.length - 2; i += 2) {
                    Point point = new Point((int)Double.parseDouble(split[i]), (int)Double.parseDouble(split[i + 1]));
                    polyline.add(point);
                }

                Polyline shape = new Polyline(polyline, color);
                sketch.map.put(ID, shape);
            }
        }
    }
}
