/**
 * (./) Blob.java, 04/05/08
 * (by) Douglas Edric Stanley & Cousot Stéphane
 * (cc) some right reserved
 *
 * Part of the Processing/Java OpenCV Libraries project, for the Atelier Hypermedia, art 
 * school of Aix-en-Provence, and for the Processing and Java community of course.
 *
 *
 * THIS LIBRARY (AND ALSO THIS FILE) IS RELEASED UNDER A CREATIVE COMMONS ATTRIBUTION 3.0 LICENSE
 * ‹ http://creativecommons.org/licenses/by/3.0/ ›
 */
 


// package name
package hypermedia.video;


// external librairies
import processing.core.*;
import java.awt.*;



/**
 * A storage object containing a blob detected by OpenCV.
 * Returned by <code>blobs()</code> method.  
 *
 * @example blobs
 * @see OpenCV#blobs( int, int, int, boolean )
 * @see OpenCV#blobs( int, int, int, boolean, int )
 * @usage Application
 */
public class Blob {

	
	/** The area of the blob in pixels */
	public float area			= 0f;
	/** The length of the perimeter in pixels */
	public float length			= 0f;
	/** The centroid or barycenter of the blob */
	public Point centroid		= new Point();
	/** The containing rectangle of the blob */
	public Rectangle rectangle	= new Rectangle();
	/** The list of points defining the shape of the blob */
	public Point[] points		= new Point[0];
	/** Is this blob a hole inside of another blob? */
	public boolean isHole		= false;
	
	
	/** A list of color int, containing the image pixels created by loadPixels()
	 *
	 * @see #loadPixels
	 */
	public int[] pixels	= new int[0];
	
	
	
	/**
	 * Create a new Blob with the default properties.
	 */
	protected Blob() {}
	
	
	/**
	 * Create a new Blob with the given properties.
	 *
	 * @param area		the shape area
	 * @param length	the contour length
	 * @param centroid	the shape barycentre point
	 * @param rect		the shape rectangle
	 * @param points	the contour points
	 * @param isHole	true whether this blob is completly inside a bigger one
	 */
	protected Blob
	( float area, float length, Point centroid, Rectangle rect, Point[] points, boolean isHole )
	{
		this.area		= area;
		this.length		= length;
		this.centroid	= centroid;
		this.rectangle	= rect;
		this.points		= points;
		this.isHole		= isHole;
	}
	
	/**
	 * Generates an image from the current blob shape, using the current frame for pixel content.
	 *
	 * After the <code>loadPixels()</code> call, pixels can by accessed via the <code>pixels</code> variable.
	 *
	 * <p>This method should be called directly after the blob detection process to retrieve the correct
	 * data values.</p>
	 * <p>! NOT YET IMPLEMENTED</p>
	 * 
	 * @see #pixels
	 * @invisible
	 */
	public void loadPixels() {
	}
	
	/**
	 * Returns blob's image as PImage.
	 * <p>! NOT IMPLEMENTED YET</p>
	 *
	 * @return PImage
	 * @invisible
	 */
	public PImage image() {
		return new PImage( rectangle.width, rectangle.height );
	}
	
}

