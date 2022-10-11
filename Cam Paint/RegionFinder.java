import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 * @author Ellie Boyd (for PS1)
 */
public class RegionFinder {
	private static final int maxColorDiff = 200;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored
	private ArrayList<ArrayList<Point>> regions;			// collection of regions


	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		regions = new ArrayList<ArrayList<Point>>();
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x=0; x < image.getWidth(); x++) { //looping over all pixels in frame
			for (int y=0; y < image.getHeight(); y++) {

				if (visited.getRGB(x, y) == 0 && colorMatch(new Color(image.getRGB(x,y)), targetColor)) {
					ArrayList<Point> pointsToVisit = new ArrayList<Point>(); //points to check
					ArrayList<Point> pointsVisited = new ArrayList<Point>(); //points that have been checked & proven to be members of the current region
					Point point = new Point(x,y);
					pointsToVisit.add(point); //add pixel at x, y to list of points that need to be checked

					while (pointsToVisit.size() > 0) { //while PointsToVisit isn't empty:
						point = pointsToVisit.remove(0); //remove pixel from the front of the queue

						if(visited.getRGB((int)point.getX(),(int)point.getY())==0){ //if point hasn't been visited before:
							pointsVisited.add(point); // add it to the region of points visited
							visited.setRGB((int)point.getX(), (int)point.getY(), 1);

							for (int x2=Math.max((int)point.getX() - 1, 0); x2 <= Math.min((int)point.getX() + 1, image.getWidth() - 1); x2++) { //loop over neighbors from x-1 to x+1 and y-1 to y+1 to check to see if they haven't been visited and if they're the right color. if both are true:
								for (int y2=Math.max((int)point.getY() - 1, 0); y2 <= Math.min((int)point.getY() + 1, image.getHeight() - 1); y2++) {

									if ((x2!=point.getX() || y2!=point.getY()) && visited.getRGB(x2, y2) == 0 && colorMatch(new Color(image.getRGB(x2, y2)), targetColor)) {//add to list of pointsToVisit
										Point point2 = new Point(x2, y2);
										pointsToVisit.add(point2);
									}
								}
							}
						}
					}

					if (pointsVisited.size() >= minRegion) {
						regions.add(pointsVisited); //if the created region (pointsVisited) is the right size, add it to list of lists 'regions'
					}
				}
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		int d = (c1.getRed() - c2.getRed()) * (c1.getRed() - c2.getRed())
				+ (c1.getGreen() - c2.getGreen()) * (c1.getGreen() - c2.getGreen())
				+ (c1.getBlue() - c2.getBlue()) * (c1.getBlue() - c2.getBlue());
		return d < maxColorDiff; } //if current pixel is within maxColorDiff of target color, returns True

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		int maxRegionSize = 0; //tracks largest region size
		ArrayList<Point> maxRegion = new ArrayList<>();

		for (ArrayList<Point> region : regions) { //loops through regions
			if (region.size() > maxRegionSize) {
				maxRegionSize = region.size();
				maxRegion = region;
			}
		}
		return maxRegion; //returns largest region (arraylist of points)
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		for(ArrayList<Point> region : regions) {
			Color color = new Color((int)(Math.random()*16777216));
			for (Point point : region) {
				recoloredImage.setRGB((int)point.getX(), (int)point.getY(), color.getRGB());
			}
		}
	}
}
