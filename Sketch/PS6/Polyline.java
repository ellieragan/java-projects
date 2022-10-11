
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 * @author ellie boyd, 06-04-21
 */
public class Polyline implements Shape {
	private int x1, y1;		// two endpoints
	private Color color;
	private ArrayList<Point> shape;

	public Polyline(int x1, int y1, Color color) {
		shape = new ArrayList<>();
		this.x1 = x1;
		this.y1 = y1;
		this.color = color;
		Point point = new Point(x1, y1);
		shape.add(point);
	}

	public Polyline(ArrayList<Point> shape, Color color) {
		this.x1 = (int) shape.get(1).getX();
		this.y1 = (int) shape.get(1).getY();
		this.shape = shape;
		this.color = color;
	}

	public void marker(int x, int y) {
		if (x != x1 || y != y1) {
			Point point = new Point(x, y);
			shape.add(point);
			x1 = x;
			y1 = y;
		}
	}
	@Override
	public void moveBy(int dx, int dy) {
		for (Point p : shape) {
			p.x += dx;
			p.y += dy;
			//shape.get(p).x += dx;
			//shape.get(p).y += dy;
//			Point newP2 = new Point((int) shape.get(p).getX() + dx, (int) shape.get(p).getY());
//			shape.put(p, newP2);
		}
	}

	@Override
	public Color getColor() { return color; }

	@Override
	public void setColor(Color color) { this.color = color; }
	
	@Override
	public boolean contains(int x, int y) {
		for (int i = 0; i < shape.size() - 1; i ++) {
			if (Segment.pointToSegmentDistance(x, y, (int) shape.get(i).getX(), (int) shape.get(i).getY(), (int) shape.get(i + 1).getX(), (int) shape.get(i + 1).getY()) < 3) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		for (int i = 0; i < shape.size() - 1; i ++) {
			g.setColor(color);
			g.drawLine((int) shape.get(i).getX(), (int) shape.get(i).getY(), (int) shape.get(i + 1).getX(), (int) shape.get(i + 1).getY());
		}

		//g.drawLine((int) shape.get(shape.size() - 2).getX(), (int) shape.get(shape.size() - 2).getY(), (int) shape.get(shape.size() - 1).getX(), (int) shape.get(shape.size() - 1).getY());
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		for (Point p : shape) {
			string.append(p.getX()).append(" ").append(p.getY()).append(" ");
		}
		//System.out.println(" to string: " + string.toString());
		return "polyline "+color.getRGB()+" "+string.toString().substring(0, string.length() - 1);
	}
}
