import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * @author Ellie Boyd 26 April 2021
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {

		if (p2.getX() > point.getX() && p2.getY() < point.getY()) { //if point is in quadrant 1:
			if (hasChild(1)) getChild(1).insert(p2); //if there's already a child, call insert recursively
			else c1 = new PointQuadtree<E>(p2, (int)point.getX(), y1, x2, (int)point.getY()); //otherwise the point becomes the child
		}

		else if (p2.getX() < point.getX() && p2.getY() < point.getY()) { //if point is in quadrant 2:
			if (hasChild(2)) getChild(2).insert(p2); //if there's already a child call insert recursively
			else c2 = new PointQuadtree<E>(p2, x1, y1, (int)point.getX(), (int)point.getY()); //otherwise the point is the child
		}

		else if (p2.getX() < point.getX() && p2.getY() > point.getY()) { //same as above for quadrant 3
			if (hasChild(3)) getChild(3).insert(p2);
			else c3 = new PointQuadtree<E>(p2, x1, (int)point.getY(), (int)point.getX(), y2);
		}

		else if (p2.getX() > point.getX() && p2.getY() > point.getY()) { //quadrant 4
			if (hasChild(4)) getChild(4).insert(p2);
			else c4 = new PointQuadtree<E>(p2, (int)point.getX(), (int)point.getY(), x2, y2);
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {

		int size = 1;
		if (hasChild(1)) size += c1.size(); //if there is a child in quadrant X, call size recursively on child's 4 quadrants
		if (hasChild(2)) size += c2.size();
		if (hasChild(3)) size += c3.size();
		if (hasChild(4)) size += c4.size();
		return size;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		List<E> p = new ArrayList<E>();
		points(p); //calls recursive helper function 'points'
		return p;
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		List<E> foundPointsInCircle = new ArrayList<E>();
		foundPoints(foundPointsInCircle, cx, cy, cr); //calls recursive helper function foundPoints
		return foundPointsInCircle;
	}

	// TODO: YOUR CODE HERE for any helper methods

	/**
	 * Recursively helps allPoints by going through children and adding to the list of points
	 * @param points	list of all points
	 */
	public void points(List<E> points) {
		if (hasChild(1)) c1.allPoints();
		else points.add(c1.point);
		if (hasChild(2)) c2.allPoints();
		else points.add(c2.point);
		if (hasChild(3)) c3.allPoints();
		else points.add(c3.point);
		if (hasChild(4)) c4.allPoints();
		else points.add(c4.point);
	}

	/**
	 * Recursively helps findInCircle by adding to a list of points
	 * @param points	list of points
	 * @param cx		circle x coordinate
	 * @param cy		circle y coordinate
	 * @param cr		circle radius
	 */
	public void foundPoints(List<E> points, double cx, double cy, double cr) {
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) { //if the circle intersects a quadrant:
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) { //if point is inside of the circle:
				points.add(point);
			}
			if (c1 != null) c1.foundPoints(points, cx, cy, cr); //recursively calls on all children
			if (c2 != null) c2.foundPoints(points, cx, cy, cr);
			if (c3 != null) c3.foundPoints(points, cx, cy, cr);
			if (c4 != null) c4.foundPoints(points, cx, cy, cr);
		}
	}
}
