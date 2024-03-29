import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 * @author Ellie Boyd
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor = null;       // color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece
	private BufferedImage recoloredImage;	// the recolored webcam

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {

		if (displayMode == 'p') { //display mode painting
			g.drawImage(painting, 0, 0, null);
		}

		if (displayMode == 'r') { //display mode recolored webcam
			g.drawImage(recoloredImage, 0, 0, null);
		}

		if (displayMode == 'w') { //display mode webcam (unaltered)
			g.drawImage(image, 0, 0, null);
		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		if (displayMode == 'r') { //recolored webcam
			finder = new RegionFinder(image);
			if (targetColor != null) {
				finder.findRegions(targetColor); //find regions of color targetColor
				finder.recolorImage(); //recolor webcam
				recoloredImage = finder.getRecoloredImage();
				//for (Point point : finder.largestRegion()) { //recolor the largest region on the webcam
					//image.setRGB(point.x, point.y, color.getRGB());
				}
			else { recoloredImage = image; } //if no color has been selected just show the live webcam
		}

		if (displayMode == 'p') { //painting
			finder = new RegionFinder(image);
			if (targetColor != null) {
				finder.findRegions(targetColor); //find regions of color targetColor
				for (Point point : finder.largestRegion()) { //use the largest one to paint onto a white screen ('painting')
					painting.setRGB((int) point.getX(), (int) point.getY(), paintColor.getRGB());
				}
			}
		}
	}
	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (image != null) { //sets targetColor as the color at mouse press x,y
			targetColor = new Color(image.getRGB(x, y));
		}
	}
	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
