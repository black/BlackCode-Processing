/**
 * (./) OpenCV.java v0.1.1
 * (by) Douglas Edric Stanley & Cousot Stéphane
 * (cc) some right reserved
 *
 * Part of the Processing Libraries project, for the Atelier Hypermedia, art 
 * school of Aix-en-Provence, and for the Processing community of course.
 *
 * -› http://ubaa.net/shared/processing/
 * -› http://hypermedia.loeil.org/processing/
 * -› http://www.processing.org/
 *
 *
 * THIS LIBRARY IS RELEASED UNDER A CREATIVE COMMONS ATTRIBUTION 3.0 LICENSE
 * › http://creativecommons.org/licenses/by/3.0/
 */




// package name
package hypermedia.video;


// Java librairies
import processing.core.*;
import java.awt.*;
import java.io.File;
import java.net.URL;






/**
 * The main object for all computer vision processes.
 * 
 * <div class="hide">
 * <p>
 * To use OpenCV library in a PApplet directly in pure Java, start with this small integration sample
 * <br />for more details about Processing see the <a href="http://processing.org/">home page</a>
 * </p>
 * <pre>
 *
 *	import processing.core.*;
 *	import hypermedia.video.OpenCV;
 *
 *	public class OpenCV_PApplet extends PApplet {
 *
 *		OpenCV cv = null;  // OpenCV object
 *
 *		// Initialise Objects
 *		public void setup() {
 *		    size( 640, 480 );              // set frame size
 *		    cv = new OpenCV( this );       // link OpenCV process to this PApplet
 *		    cv.capture( width, height );   // start video stream
 *		}
 *
 *		// Display the input camera stream in frame
 *		public void draw() {
 *		    cv.read();
 *		    image( cv.image(), 0, 0 );
 *		}
 *
 *		// Call the PApplet main method
 *		public static void main( String[] args ) {
 *		    PApplet.main( new String[]{"OpenCV_PApplet"} );
 *		} 
 *	}
 *
 * </pre>
 * </div>
 *
 * @author Douglas Edric Stanley - http://www.abstractmachine.net/
 * @author Cousot Stéphane  stef[at]ubaa.net
 * @version 0.1 18/12/07
 * 
 * @see <a href="http://dev.processing.org/reference/core/javadoc/processing/core/PApplet.html">
 * processing.core.PApplet</a>
 * @usage Application
 */
public class OpenCV {

	
	
	/**
	 * Type of Image
	 * @see #image(int)
	 * @see #remember(int)
	 */
	public static final int SOURCE	= 0;
	public static final int BUFFER	= 1;
	public static final int MEMORY	= 2;
	//public static final int ROI		= 3;
	
	
	
	/**
	 * Colorspace of image
	 * @see #convert(int)
	 */
	public static final int RGB  = 1;
	public static final int GRAY = 12;
	
	
	
	/**
	 * Thresholding method
	 * @see #threshold( float )
	 * @see #threshold( float, float, int )
	 */
	public static final int THRESH_BINARY		= 0; // dst(x,y) = src(x,y) > value ? max : 0
	public static final int THRESH_BINARY_INV	= 1; // dst(x,y) = src(x,y) > value ? 0 : max
	public static final int THRESH_TRUNC		= 2; // dst(x,y) = src(x,y) > value ? value : src(x,y)
	public static final int THRESH_TOZERO		= 3; // dst(x,y) = src(x,y) > value ? src(x,y) : 0
	public static final int THRESH_TOZERO_INV	= 4; // dst(x,y) = src(x,y) > value ? 0 : src(x,y)
	public static final int THRESH_OTSU			= 8;
	
	
	
	/**
	 * Flip mode
	 * @see #flip( int )
	 * @see #remember( int, int )
	 */
	public static final int FLIP_VERTICAL	= 0;
	public static final int FLIP_HORIZONTAL = 1;
	public static final int FLIP_BOTH		= -1;
	
	
	
	/**
	 * Interpolation method
	 * @see #interpolation( int )
	 */
	public static final int INTER_NN	 = 0; // nearest-neighbor (preserve hard eges)s
	public static final int INTER_LINEAR = 1; // bilinear interpolation
	public static final int INTER_CUBIC	 = 2; // bicubic interpolation (best for smooth gradients)
	public static final int INTER_AREA	 = 3; // resampling using pixel area relation.
	
	
	
	/**
	 * The maximum number of contour points available to blob detection (by default)
	 * @see #blobs( int, int, int, boolean )
	 * @see #blobs( int, int, int, boolean, int )
	 */
	public static final int MAX_VERTICES = 1024;
	
	
	
	/**
	 * Blur method
	 * @see #blur(int, int)
	 * @see #blur(int, int, int, float, float)
	 */
	//public static final int BLUR_NO_SCALE	= 0;
	public static final int BLUR			= 1;
	public static final int GAUSSIAN		= 2;
	public static final int MEDIAN			= 3;
	public static final int BILATERAL		= 4; // is a simple, non-iterative scheme for edge-preserving smoothing.
	
	
	/**
	 * Movie info selector
	 * @see #property(int)
	 * @see #jump(float, int)
	 */
	public static final int MOVIE_MILLISECONDS	= 0;
	public static final int MOVIE_FRAMES		= 1;
	public static final int MOVIE_RATIO			= 2;
	public static final int MOVIE_WIDTH			= 3;
	public static final int MOVIE_HEIGHT		= 4;
	public static final int MOVIE_FPS			= 5;
	//public static final int MOVIE_FOURCC		= 6;
	public static final int MOVIE_FRAME_COUNT	= 7;
	public static final int MOVIE_FORMAT		= 8;
	public static final int MOVIE_MODE			= 9;
	public static final int MOVIE_BRIGHTNESS    = 10;
	public static final int MOVIE_CONTRAST      = 11;
	public static final int MOVIE_SATURATION    = 12;
	public static final int MOVIE_HUE           = 13;
	public static final int MOVIE_GAIN          = 14;
	public static final int MOVIE_CONVERT_RGB   = 15;
	
	
	/** 
	 * Haar classifier flag
	 * @see #detect(float, int, int, int, int)
	 */
	public static final int HAAR_DO_CANNY_PRUNING	 = 1;
	public static final int HAAR_SCALE_IMAGE		 = 2;
	public static final int HAAR_FIND_BIGGEST_OBJECT = 4;
	public static final int HAAR_DO_ROUGH_SEARCH	 = 8;
	
	
	
	/**
	 * Standard Haar classifier cascade file used for object detection
	 * @see #cascade(String)
	 */
	public static final String CASCADE_FRONTALFACE_ALT_TREE	= "haarcascade_frontalface_alt_tree.xml";
	public static final String CASCADE_FRONTALFACE_ALT		= "haarcascade_frontalface_alt.xml";
	public static final String CASCADE_FRONTALFACE_ALT2		= "haarcascade_frontalface_alt2.xml";
	public static final String CASCADE_FRONTALFACE_DEFAULT	= "haarcascade_frontalface_default.xml";
	public static final String CASCADE_PROFILEFACE			= "haarcascade_profileface.xml";
	public static final String CASCADE_FULLBODY				= "haarcascade_fullbody.xml";
	public static final String CASCADE_LOWERBODY			= "haarcascade_lowerbody.xml";
	public static final String CASCADE_UPPERBODY			= "haarcascade_upperbody.xml";
	
	
	
	/** Processing applet */
	protected PApplet parent = null;
	/** Processing image buffer */
	protected PImage pbuffer = null;
	
	
	/** 
	 * OpenCV image/buffer width
	 * @see #height
	 */
	public int width	= 0;
	
	/**
	 * OpenCV image/buffer height
	 * @see #width
	 */
	public int height	= 0;
	
	/** storage variable for the brightnessContrast() method */
	private int brightness	= 0;
	private int contrast	= 0;
	
	/**
	 * The user plateform name
	 */
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	
	
	
	
	
	
	/**
	 * Load C++ library
	 */
	static {
		try { System.loadLibrary("OpenCV"); }
		catch( UnsatisfiedLinkError e ) {
			
			String msg = "[opencv fatal error] library not loaded !\n"
					   + "THIS VERSION OF OPENCV LIBRARY REQUIRE ADDITIONAL DEPENDENCIES.\n"
					   + "READ THE INSTALLATION INSTRUCTIONS AT http://ubaa.net/shared/processing/opencv/\n\n"
					   + "Verify that %s and the java.library.path property is correctly.\n\n"
					   + "error message: "+e.getMessage()
					   ;

			
			if ( OS.indexOf("mac")!=-1 )
				msg = String.format( msg, "you are running in 32-bit mode, the opencv.framework exists in '/Library/Frameworks' folder" );
			if ( OS.indexOf("windows")!=-1 )
				msg = String.format( msg, "the '\\path\\to\\OpenCV\\bin' exists in your system PATH");
			if ( OS.indexOf("linux")!=-1 )
				msg = String.format( msg, "'libcxcore.so', 'libcv.so', 'libcvaux.so', 'libml.so', "+
					   "and 'libhighgui.so' are placed (or linked) in one of your system shared "+
					   "libraries folder");
		
			System.err.println( "\n"+msg+"\n" );
		}
	}
	
	
	
	
	/**
	 * Create a new OpenCV object.
	 *
	 * @see #OpenCV( PApplet )
	 * @invisible
	 */
	public OpenCV() { this(null); }
	
	/**
	 * Create a new OpenCV object.
	 * @param parent	the Processing PApplet object (typically use "this")
	 */
	public OpenCV( PApplet parent ) {
		
		this.parent = parent;
		
		// update paths and register this object to the PApplet, 
		// while this library is used with Processing
		try {
			if ( parent instanceof PApplet ) parent.registerMethod("dispose", this);
		}
		catch( NoClassDefFoundError e ) {;}
		
	}

	/** 
	 * Forget all OpenCV resources.
	 * This method is automatically called by Processing when the PApplet shuts down.
	 *
	 * @see #stop()
	 * @invisible
	 */
	public void dispose() {
		stop();
	}
	

	
	/**
	 * Allocate required buffer with the given size.
	 * <p>This method allows you to create your own (blank) image buffer to work on.</p>
	 * <p>Except for creating an empty buffer, this method should not be directly used.<br />
	 * <code>capture()</code>, <code>movie()</code> and <code>loadImage()</code> methods will 
	 * automatically re-allocate the current buffer.<p>
	 *
	 * @see #capture( int, int )
	 * @see #movie( String )
	 * @see #loadImage( String )
	 *
	 * @see #copy(PImage)
	 * @see #copy(String)
	 * @see #copy(PImage, int, int, int, int, int, int, int, int)
	 * @see #copy(String, int, int, int, int, int, int, int, int)
	 * @see #copy(int[], int, int, int, int, int, int, int, int, int)
	 *
	 * @example copy
	 */
	public native void allocate( int width, int height );
	
	/**
	 * Free memory.
	 *
	 * @example	OpenCV
	 * @usage	Application
	 */
	private native void deallocate( int type );
	
	/**
	 * Stop OpenCV process.
	 *
	 * @usage	Application
	 */
	public void stop() { deallocate(-1); }
	
	
	
	/**
	 * Place the image source in memory.
	 * Used by <code>absDiff()</code> to compare two images (for background extraction, for example).
	 *
	 * @description Place the image (original or current) in memory.
	 * If specified, flip the stored image around the given axis. 
	 * Used by <code>absDiff()</code> to compare two images (for background extraction, for example).
	 *
	 * @see #remember( int, int )
	 * @see #remember( int )
	 * @see #absDiff()
	 *
	 * @example absDiff
	 */
	public void remember() {
		remember( SOURCE, FLIP_BOTH-1 );
	}
	
	/**
	 * Place the specified image in memory.
	 * Used by <code>absDiff()</code> to compare two images (for background extraction, for example).
	 *
	 * @param type	which image to be stored: SOURCE or BUFFER
	 *
	 * @see #remember( int, int )
	 * @see #remember()
	 * @see #absDiff()
	 *
	 */
	public void remember( int type ) {
		remember( type, FLIP_BOTH-1 );
	}
	
	/**
	 * Place the specified image in memory and flip the stored image around the given axis.
	 * Used by <code>absDiff()</code> to compare two images (for background extraction, for example).
	 *
	 * @param type	which image to be stored: SOURCE or BUFFER
	 * @param mode	the used axis: FLIP_HORIZONTAL, FLIP_VERTICAL or FLIP_BOTH
	 *
	 * @see #remember( int, int )
	 * @see #flip( int )
	 * @see #absDiff()
	 */
	public native void remember( int type, int mode );
	
	
	
	/**
	 * Convert the current image from one colorspace to another.
	 *
	 * @param type	the target color space : RGB, GRAY
	 */
	public native void convert( int type );
	
	
	/**
	 * Calculate the absolute difference between the image in memory and the current image.
	 *
	 * @see #remember()
	 * @see #remember( int )
	 * @see #remember( int, int )
	 */
	public native void absDiff();
	
	/**
	 * Flip the current image around vertical, horizontal or both axes.
	 *
	 * @param mode	the used axis, FLIP_HORIZONTAL, FLIP_VERTICAL or FLIP_BOTH
	 */
	public native void flip( int mode );

	
	/**
	 * Apply fixed-level threshold to the current image.
	 *
	 * <p>This method applies fixed-level thresholding to single-channel array. It is typically used
	 * to get bi-level (binary) image out of grayscale image or for removing a noise (filtering out
	 * pixels with too small or too large values).</p>
	 * <p>About types of thresholding</p>
	 * <ul>
	 *		<li>THRESH_BINARY		: dst(x,y) = src(x,y) > value ? max : 0</li>
	 *		<li>THRESH_BINARY_INV	: dst(x,y) = src(x,y) > value ? 0 : max</li>
	 *		<li>THRESH_TRUNC		: dst(x,y) = src(x,y) > value ? value : src(x,y)</li>
	 *		<li>THRESH_TOZERO		: dst(x,y) = src(x,y) > value ? src(x,y) : 0</li>
	 *		<li>THRESH_TOZERO_INV	: dst(x,y) = src(x,y) > value ? 0 : src(x,y)</li>
	 *		<li>THRESH_OTSU			: Use Otsu algorithm to choose the optimal threshold value,
	 * combine the flag with one of the above THRESH_* values</li>
	 * </ul>
	 *
	 * @param value	threshold value
	 * @param max	the maximum value to use with THRESH_BINARY and THRESH_BINARY_INV thresholding types.
	 * @param type	Types of thresholding
	 *
	 * @see #threshold( float )
	 */
	public native void threshold( float value, float max, int type );
	
	/**
	 * Applies fixed-level threshold to the current image using the THRESH_BINARY thresholding type
	 * and 255 as the maximum value.
	 *
	 * @param value	threshold value
	 *
	 * @see #threshold( float, float, int )
	 */
	public void threshold( float value ) {
		threshold( value, 255, THRESH_BINARY );
	}
	
	/**
	 * Adjust the image brightness with the specified value (in range of -128 to 128).
	 *
	 * @param value	the new brightness value
	 *
	 * @see #contrast( int )
	 * @example	brightcont
	 */
	public void brightness( int value ) {
		brightness = Math.min( 128, Math.max(value,-128) );
		brightnessContrast( brightness, contrast );
	}
	
	/**
	 * Adjust the image contrast with the specified value (in range of -128 to 128).
	 *
	 * @param value		the new contrast value
	 *
	 * @see #brightness( int )
	 * @example	brightcont
	 */
	public void contrast( int value ) {
		contrast = Math.min( 128, Math.max(value,-128) );
		brightnessContrast( brightness, contrast );
	}
	
	/**
	 * Adjust both image contrast and brightness with the passed values. 
	 *
	 * @param brightness	the new brightness value
	 * @param contrast		the new contrast value
	 *
	 * @see #brightness( int )
	 * @see #contrast( int )
	 * @invisible
	 */
	private native void brightnessContrast( int brightness, int contrast );
	
	/**
	 * Invert image.
	 */
	public native void invert();
	
	//public native void perspective();
	
	
	/**
	 * Smooth the image in one of several ways.
	 * <dl>
	 * <!--dt>CV_BLUR_NO_SCALE</dt>
	 * <dd><em>simple blur with no scaling</em></dd>
	 * <dd>for each pixel the result is a sum of pixels values in param1×param2 neighborhood of the 
	 * pixel. If the neighborhood size varies from pixel to pixel, compute the sums using integral 
	 * image (cvIntegral).</dd-->
	 * <dt>CV_BLUR</dt>
	 * <dd><em>simple blur</em></dd>
	 * <dd>for each pixel the result is a mean of pixel values param1×param2 neighborhood of the 
	 * pixel.</dd>
	 * <dt>CV_GAUSSIAN</dt>
	 * <dd><em>Gaussian blur</em></dd>
	 * <dd>the image is smoothed using the Gaussian kernel of size param1×param2. param3 and param4 
	 * may optionally be used to specify shape of the kernel.</dd>
	 * <dt>CV_MEDIAN</dt>
	 * <dd><em>median blur</em></dd>
	 * <dd>the image is smoothed using medial filter of size param1×param1. That is, for each pixel 
	 * the result is the median computed over param1×param1 neighborhood.</dd>
	 * <dt>CV_BILATERAL</dt><dd>bilateral filter</dd>
	 * <dd>the image is smoothed using a bilateral 3x3 filter with color sigma=param1 and space 
	 * sigma=param2. Information about bilateral filtering can be found at 
	 * <a href="http://www.dai.ed.ac.uk/CVonline/LOCAL_COPIES/MANDUCHI1/Bilateral_Filtering.html">
	 * Bilateral Filtering</a></dd>
	 * </dl>
	 *
	 * @param param1	The first parameter of smoothing operation. It should be odd (1, 3, 5, …), 
	 *					so that a pixel neighborhood used for smoothing operation is symmetrical 
	 *					relative to the pixel.
	 *
	 * @see #blur( int, int, int, float, float )
	 */
	public void blur( int type, int param1 ) {
		blur( type, param1, 0, 0f, 0f );
	}
	
	/**
	 * Smooth the image in one of several ways.
	 *
	 * @param param1	The first parameter of smoothing operation. It should be odd (1, 3, 5, …), 
	 *					so that a pixel neighborhood used for smoothing operation is symmetrical 
	 *					relative to the pixel.
	 * @param param2	The second parameter of smoothing operation. In case of simple 
	 *					scaled/non-scaled and Gaussian blur if param2 is zero, it is set to param1.
	 *					When not 0, it should be odd too.
	 * @param param3	In case of Gaussian kernel this parameter may specify Gaussian sigma 
	 *					(standard deviation). If it is zero, it is calculated from the kernel size:
	 *					sigma = (n/2 - 1)*0.3 + 0.8, where n=param1 for horizontal kernel,
	 *					n=param2 for vertical kernel. With the standard sigma for small kernels 
	 *					(3×3 to 7×7) the performance is better. If param3 is not zero, while param1 
	 *					and param2 are zeros, the kernel size is calculated from the sigma 
	 *					(to provide accurate enough operation).
	 * @param param4	In case of non-square Gaussian kernel the parameter may be used to specify a
	 *					different (from param3) sigma in the vertical direction.
	 *
	 * @see #blur( int, int )
	 */
	public native void blur( int type, int param1, int param2, float param3, float param4 );
	
	/**
	 * Set global interpolation method.
	 * By default the interpolation method is set to <code>INTER_LINEAR</code>.
	 * Used by all methods (ex. <code>copy()</code>, <code>loadImage()</code>, …) that resize the image.
	 *
	 * <dl>
	 * <dt>INTER_NN</dt>
	 * <dd>nearest-neighbor (preserve hard egdes)</dd>
	 * <dt>INTER_LINEAR</dt>
	 * <dd>bilinear interpolation</dd>
	 * <dt>INTER_CUBIC</dt>
	 * <dd>bicubic interpolation (best for smooth gradients)</dd>
	 * <dt>INTER_AREA</dt>
	 * <dd>resampling using pixel area relation</dd>
	 * </dl>
	 *
	 * @param method	INTER_NN, INTER_LINEAR, INTER_CUBIC or INTER_AREA
	 * @example copy
	 */
	public native void interpolation( int method );
	
	
	
	/**
	 * Copy an image into the current OpenCV buffer.
	 * <em>Note: any alpha channel will be ignored</em>.
	 *
	 * @description Copy the image (or a part of it) into the current OpenCV buffer (or a part of it).
	 * Resizing allowed.
	 * <em>Note: any alpha channel will be ignored</em>.	 
	 *
	 * @param image	the image to copy into buffer
	 *
	 * @see #allocate(int,int)
	 * @see #interpolation( int )
	 * @see #copy( String )
	 * @see #copy( PImage, int, int, int, int, int, int, int, int )
	 * @see #copy( String, int, int, int, int, int, int, int, int )
	 * @see #copy( int[], int, int, int, int, int, int, int, int, int )
	 */
	public void copy( PImage image ) {
		copy( image, 0, 0, image.width, image.height, 0, 0, image.width, image.height );
	}
	
	/**
	 * Copy the specified image file into the current OpenCV buffer.
	 * <em>Note: any alpha channel will be ignored</em>.
	 *
	 * @param file	the filepath of the image to copy into buffer
	 *
	 * @see #allocate(int,int)
	 * @see #interpolation( int )
	 * @see #copy( PImage )
	 * @see #copy( PImage, int, int, int, int, int, int, int, int )
	 * @see #copy( int[], int, int, int, int, int, int, int, int, int )
	 */
	public void copy( String file ) {
		copy( file, 0, 0, -1, -1, 0, 0, -1, -1 );
	}
	
	/**
	 * Copy the image (or a part of it) into the current OpenCV buffer (or a part of it).
	 * Resizing allowed.
	 * <em>Note: any alpha channel will be ignored</em>.
	 *
	 * @param image	the filepath of the image to copy into buffer
	 *
	 * @see #allocate(int,int)
	 * @see #interpolation( int )
	 * @see #copy( PImage )
	 * @see #copy( String )
	 * @see #copy( String, int, int, int, int, int, int, int, int )
	 * @see #copy( int[], int, int, int, int, int, int, int, int, int )
	 * @see #copy( int[], int, int, int, int, int, int, int, int, int )
	 */
	public void copy( PImage image, int sx, int sy, int swidth, int sheight, int dx, int dy, int dwidth, int dheight ) {
		copy( image.pixels, image.width, sx, sy, swidth, sheight, dx, dy, dwidth, dheight );
	}
	
	/**
	 * Copy the specified image file (or a part of it) into the current OpenCV buffer (or a part of it).
	 * Resizing allowed.
	 * <em>Note: any alpha channel will be ignored</em>.
	 *
	 *
	 * @see #allocate(int,int)
	 * @see #interpolation( int )
	 * @see #copy( PImage )
	 * @see #copy( PImage, int, int, int, int, int, int, int, int )
	 * @see #copy( int[], int, int, int, int, int, int, int, int, int )
	 * @see #copy( int[], int, int, int, int, int, int, int, int, int )
	 */
	public native void copy( String file, int sx, int sy, int swidth, int sheight, int dx, int dy, int dwidth, int dheight );
	
	
	/**
	 * Copies a region of pixels from an image to the current buffer at the specified position and with the given
	 * dimensions.
	 * Resizing allowed.
	 * <em>Note: any alpha channel will be ignored</em>.
	 *
	 * @param pixels	the image data
	 * @param step		the width of one horizontal line of pixels
	 * @param sx		the source x coordinate
	 * @param sy		the source y coordinate
	 * @param swidth		the source width
	 * @param sheight		the source height
	 * @param dx		the destination x coordinate
	 * @param dy		the destination y coordinate
	 * @param dwidth		the destination width
	 * @param dheight		the destination height
	 *
	 * @see #allocate(int,int)
	 * @see #interpolation( int )
	 *
	 * @see #copy( PImage )
	 */
	public native void copy( int[] pixels, int step, int sx, int sy, int swidth, int sheight, int dx, int dy, int dwidth, int dheight );
	
	
	/**
	 * Revert to the original image.
	 * By default returns to RGB image, or GRAY (if specified).
	 *
	 * @see #blobs( int, int, int, boolean, int )
	 * @see #blobs( int, int, int, boolean )
	 */
	public void restore() { restore(RGB); } // TODO : reset memory ROI if exists
	
	/**
	 * Restore the current image data from the original image with the given color space.
	 *
	 * @param type	the target color space : RGB or GRAY
	 *
	 * @see #blobs( int, int, int, boolean, int )
	 * @see #blobs( int, int, int, boolean )
	 */
	public native void restore( int type );
	
	
	
	
	/**
	 * Retrieve cuurent (or specified) image data.
	 * @param type	the type of the image: SOURCE, BUFFER, MEMORY
	 */
	public native int[] pixels( int type );
	public int[] pixels() { return pixels( BUFFER ); }
	
	
	
	/**
	 * Load an image from the specified file.
	 * <p>Currently the following file formats are supported:</p>
	 * <ul>
	 * <li>Windows bitmaps - BMP, DIB</li>
	 * <li>JPEG files - JPEG, JPG, JPE</li>
	 * <li>Portable Network Graphics - PNG</li>
	 * <li>Portable image format - PBM, PGM, PPM</li>
	 * <li>Sun rasters - SR, RAS</li>
	 * <li>TIFF files - TIFF, TIF</li>
	 * </ul>
	 * <p><i>note : this method automatically updates previous memory allocation</i></p>
	 *
	 * Currently there is an impressive OpenCV-caused memory leak on Macintosh computers
	 * when recalling this method successively. To fix this bug, load the image file from
	 * Processing and pass the pixels over using <code>copy()</code>.
	 *
	 * @param file	String : the name of file to be loaded
	 *
	 * @see #loadImage( String, int, int )
	 */
	public void loadImage( String file ) {
		loadImage( file, -1, -1 );
	}
	
	/**
	 * Load an image from the specified file with the given size dependin of the current scale 
	 * interpolation method.
	 * <p>Currently the following file formats are supported:</p>
	 * <ul>
	 * <li>Windows bitmaps - BMP, DIB</li>
	 * <li>JPEG files - JPEG, JPG, JPE</li>
	 * <li>Portable Network Graphics - PNG</li>
	 * <li>Portable image format - PBM, PGM, PPM</li>
	 * <li>Sun rasters - SR, RAS</li>
	 * <li>TIFF files - TIFF, TIF</li>
	 * </ul>
	 * <p><i>note : this method automatically updates previous memory allocation</i></p>
	 *
	 * @param file		the name of file to be loaded
	 * @param width		the destination image (buffer) width size
	 * @param height	the destination image (buffer) height size
	 *
	 * @see #interpolation( int )
	 * @see #loadImage( String )
	 */
	public native void loadImage( String file, int width, int height );
	
	
	/**
	 * Return the current OpenCV image.
	 *
	 * @description Return the current (or specified) OpenCV image
	 * @see #image(int)
	 * @return PImage
	 * @example capture
	 */
	public PImage image() {  return image(BUFFER);  }
	
	/** Return the specified OpenCV image
	 *
	 * @param type	the type of the image: SOURCE, BUFFER, MEMORY
	 * @see #image()
	 * @return PImage
	 */
	public PImage image( int type ) {
		
		
		// empty image
		// -> display message & exit
		if ( width==0 && height==0 ) {
			System.err.println( "OpenCV could not define source dimensions.\n" );
			return null;
		}

		if ( parent instanceof PApplet ) {
			
			// create image
			if ( pbuffer==null || pbuffer.width!=width || pbuffer.height!=height ) {
				pbuffer = parent.createImage( width, height, parent.RGB );
			}
			pbuffer.pixels = pixels(type);
			pbuffer.updatePixels();

			// return image
			return pbuffer;
		}
		else return null;
	}
	
	
	
	
	////////////////////////////////////// BLOB'S DETECTION ////////////////////////////////////////
	
	
	/** 
	 * Blob and contour detection.
	 * 
	 * <p>This function looks for contours within the image, returning a list of <code>Blob</code> objects.</p>
	 * <p>When searching for blobs, you must define the minimum and maximum size in pixels. This can be calculated by any number of means; for example, if you want to limit blobs to no larger than half the Region Of Interest, pass <code>(width * height) / 2</code> for the <code>maxArea</code>. Blobs are then ordered from largest to smallest.</p>
	 *
	 * <p><code>Blob</code> objects contain all the relevant information about each contour (area, length, centroid, isHole, …)</p>
	 *
	 * @description Blob and contour detection.
	 * 
	 * <p>This function looks for contours within the image, returning a list of <code>Blob</code> objects.</p>
	 * <p>When searching for blobs, you must define the minimum and maximum size in pixels. For example, if you want to limit blobs to no larger than half the Region Of Interest, pass <code>(width * height) / 2</code> for the <code>maxArea</code>.</p>
	 *
	 * <p>Blobs are returned in order from largest to smallest.</p>
	 *
	 * <p><a href="#"><code>Blob</code></a> objects contain all the relevant information about each contour (area, length, centroid, isHole, …)</p>
	 *
	 * <p>The optional <code>maxVertices</code> parameter, allows you to increase the overall number of points defining the contour of the blob. Use this parameter if for some reason your blob shapes are incomplete. Augementing this parameter can affect performance.</p>
	 *
	 * <p>Careful! This function will damage the current buffered image while searching for contours. If you want to do any subsequent work with this image, you will need to call the <code>restore()</code> method after this one.</p>
	 * 
	 * @param minArea	the minimum area to search for (total pixels)
	 * @param maxArea	the maximum area to search for (total pixels)
	 * @param maxBlobs	the maximim number of blobs to return
	 * @param findHoles	accept blobs fully inside of other blobs
	 *
	 * @see #blobs( int, int, int, boolean, int )
	 * @see Blob
	 * @see #restore()
	 * @see #restore( int )
	 * @see #MAX_VERTICES
	 *
	 * @return Blob[]
	 */
	public Blob[] blobs( int minArea, int maxArea, int maxBlobs, boolean findHoles ) { 
		return blobs( minArea, maxArea, maxBlobs, findHoles, MAX_VERTICES );
	}
	
	/** Blob and contour detection.
	 * 
	 * <p>This function looks for contours within the image, returning a list of <code>Blob</code> objects.</p>
	 * <p>When searching for blobs, you must define the minimum and maximum size in pixels. This can be calculated by any number of means; for example, if you want to limit blobs to no larger than half the Region Of Interest, pass <code>(width * height) / 2</code> for the <code>maxArea</code>. Blobs are then ordered from largest to smallest.</p>
	 *
	 * <p><code>Blob</code> objects contain all the relevant information about each contour (area, length, centroid, isHole, …)</p>
	 *
	 * <p>The optional <code>maxVertices</code> parameter, allows you to increase the overall number of points defining the contour of the blob. Use this parameter if for some reason your blob shapes are incomplete.</p>
	 * 
	 * @param minArea	the minimum area to search for (total pixels)
	 * @param maxArea	the maximum area to search for (total pixels)
	 * @param maxBlobs	the maximim number of blobs to return
	 * @param findHoles	accept blobs fully inside of other blobs
	 * @param maxVertices	the maximum number of points used to define the contour
	 *
	 * @see #blobs( int, int, int, boolean )
	 * @see Blob
	 * @see #restore()
	 * @see #restore( int )
	 * @see #MAX_VERTICES
	 *
	 * @return Blob[]
	 */
	public native Blob[] blobs
	( int minArea, int maxArea, int maxBlobs, boolean findHoles, int maxVertices );
	
	
	
	/**
	 * Blob detection
	 * @invisible
	 * @deprecated
	 * @param minArea	the minimum area to searched
	 * @param maxArea	the maximum area to searched
	 */
	public void findContours( int minArea, int maxArea, int maxBlobs, boolean findHoles ) { 
		findContours( minArea, maxArea, maxBlobs, findHoles, MAX_VERTICES );
	}
	
	/** 
	 * @invisible
	 * @deprecated
	 */
	public native void findContours( int minArea, int maxArea, int maxBlobs, boolean findHoles, int maxVertices );
	/** 
	 * @invisible
	 * @deprecated
	 */	
	public native int blobCount();
	/** 
	 * @invisible
	 * @deprecated
	 */	
	public native float area( int index );
	/** 
	 * @invisible
	 * @deprecated
	 */
	public native float arcLength( int index );
	/** 
	 * @invisible
	 * @deprecated
	 */
	public native Point centroid( int index );
	/** 
	 * @invisible
	 * @deprecated
	 */
	public native Rectangle rectangle( int index );
	/** 
	 * @invisible
	 * @deprecated
	 */
	public native int pointCount( int index );
	/** 
	 * @invisible
	 * @deprecated
	 */
	public native Point[] points( int index );
	/** 
	 * @invisible
	 * @deprecated
	 */
	public native boolean isHole( int index );
	
	
	
	/////////////////////////////////// CAPTURE AND MOVIE FILE /////////////////////////////////////
	
	
	/**
	 * Allocate and initialize resources for reading a video stream from the default camera.
	 * <br /><i>note : this method automatically updates previous memory allocation</i>
	 *
	 * @description Allocate and initialize resources for reading a video stream from a camera.
	 * @param width		the width of the video stream image
	 * @param height	the height of the video stream image
	 *
	 * @see #capture( int, int, int )
	 * @see #movie( String )
	 */
	public void capture( int width, int height ) {
		capture( width, height, 0 );
	}
	
	/**
	 * Allocate and initialize resources for reading a video stream from the specified camera.
	 * <br /><i>note : this method automatically updates previous memory allocation</i>
	 *
	 * @param width		the width of the video stream image
	 * @param height	the height of the video stream image
	 * @param index		the index of the camera to be used
	 *
	 * @see #capture( int, int )
	 * @see #movie( String )
	 */
	public native void capture( int width, int height, int index );
	
	/**
	 * Allocate and initialize resources for reading a video file from the specified file name.
	 * <br />
	 * Movie must be located in the sketch's data directory, otherwise you must specified the full 
	 * path to access to the movie file without an error.
	 * <br />
	 * <i>notes : this method automatically updates previous memory allocation,
	 * sound will not be available</i>
	 * <p>Supported codecs and file formats depends on the backend video library :</p>
	 * <ul>
	 * <li>Video for Windows (VfW) for Windows users</li>
	 * <li>FFMPEG on Linux</li>
	 * <li>QuickTime for Mac OS X</li>
	 * </ul>
	 * <p>For more information about video on OpenCV or about reading your video files 
	 * on all platforms, see 
	 * <a href="http://opencvlibrary.sourceforge.net/VideoCodecs">VideoCodecs</a></p>
	 *
	 * @param filename	the filepath of the movie file
	 *
	 * @see #movie( String, int, int )
	 * @see #capture( int, int )
	 * @see #capture( int, int, int )
	 * @see #jump( int )
	 * @see #jump( float )
	 * @see #jump( float, int )
	 *
	 * @return the attached OpenCV movie player
	 */
	 public void movie( String filename ) {
		 loadMovie( filename, -1, -1 );
	 }
	
	
	/**
	 * Allocate and initialize resources for reading a video file from the specified file name, with
	 * the given frames dimensions.
	 * <br />
	 * Movie must be located in the sketch's data directory, otherwise you must specified the full 
	 * path to access to the movie file without an error.
	 * <br />
	 * <i>notes : this method automatically updates previous memory allocation,
	 * sound will not be available</i>
	 * <p>Supported codecs and file formats depends on the backend video library :</p>
	 * <ul>
	 * <li>Video for Windows (VfW) for Windows users</li>
	 * <li>FFMPEG on Linux</li>
	 * <li>QuickTime for Mac OS X</li>
	 * </ul>
	 * <p>For more information about video on OpenCV or about reading your video files 
	 * on all platforms, see 
	 * <a href="http://opencvlibrary.sourceforge.net/VideoCodecs">VideoCodecs</a></p>
	 *
	 * @param filename	the filepath of the movie file
	 * @param width		the width of the movie image
	 * @param height	the height of the movie image
	 *
	 * @see #movie( String )
	 * @example movie
	 * @see #jump( int )
	 * @see #jump( float )
	 * @see #jump( float, int )
	 */
	public void movie( String filename, int width, int height ) {
		loadMovie( filename, width, height );
	}
	
	
	/** 
	 * @invisible
	 * @deprecated
	 */	
	private native void loadMovie( String filename, int width, int height );
	
	
	/**
	 * Grab a new frame from the input camera or a movie file.
	 * @example capture
	 */
	public native void read();
	
	
	/**
	 * Return the required property from the current capture.
	 *
	 * @param property	either: MOVIE_WIDTH, MOVIE_HEIGHT, MOVIE_FPS, MOVIE_FRAME_COUNT, 
	 * MOVIE_MILLISECONDS, MOVIE_FRAMES, MOVIE_RATIO
	 */
	public native float property( int property );
	
	/**
	 * Jump to a specified movie frame.
	 *
	 * @param millis	the time in milliseconds of the desired frame
	 * @example movie
	 * @see #movie( String )
	 * @see #movie( String, int, int )
	 * @see #jump( float )
	 * @see #jump( float, int )
	 */
	public void jump( int millis ) {
		jump( (float) millis, MOVIE_MILLISECONDS );
	}
	
	/**
	 * Jump to a specified movie frame.
	 * For a ratio equals to 0.0 jump to the begining of the movie, 0.1 to end.
	 *
	 * @param ratio	the time expressed as a ratio (0.0 - 1.0) of the desired frame
	 * @see #jump( int )
	 * @see #jump( float, int )
	 * @see #movie( String )
	 * @see #movie( String, int, int )
	 */	
	public void jump( float ratio ) {
		jump( Math.max(Math.min(ratio,1),0), MOVIE_RATIO );
	}
	
	/**
	 * Jump to a specified movie frame.
	 *
	 * @param value	the time (according to type) of the desired frame
	 * @param type	use: MOVIE_MILLISECONDS, MOVIE_FRAMES or MOVIE_RATIO
	 *
	 * @see #jump( float )
	 * @see #jump( int )
	 * @see #movie( String )
	 * @see #movie( String, int, int )
	 */	
	public native void jump( float value, int type  );
	
	
	
	
	/**
	 * Set image region of interest to the given rectangle.
	 *
	 * This methods isolates a sub-section of the current image for all subsequent activity.
	 *
	 * To clear the Region of Interest back to the entire image, call <code>ROI(null);</code>
	 *
	 * @param rect	the new region of interest or <code>null</code> to reset
	 */
	public void ROI( Rectangle rect ) {
		if ( rect==null ) ROI( 0, 0, width, height );
		else ROI( rect.x, rect.y, rect.width, rect.height );
	}
	
	/**
	 * Set image region of interest to the given rectangle.
	 *
	 * This methods isolates a sub-section of the current image for all subsequent activity.
	 *
	 * To clear the Region of Interest back to the entire image, call <code>ROI(null);</code>
	 *
	 * @param x			the region x-coordinate
	 * @param y			the region y-coordinate
	 * @param width		the region width
	 * @param height	the region height
	 */
	public native void ROI( int x, int y, int width, int height );
	
	
	/**
	 * Prepend the absolute path for the given file name.
	 * <p>
	 * This method try to return a full path to an item in the sketch/program folder, in the data
	 * folder for Processing user, the system OpenCV's resources folder ('data' or 'resources'), 
	 * in any resources folder for Java user.
	 * The search order is the 'data' folder, the current user directory, any resources folder.
	 * </p>
	 *
	 * @param file	the filename to be search
	 * @return String
	 * @invisible
	 */
	public String absolutePath( String file ) {
		
		
		String[] paths = new String[0];
		String pwd,dat,home;
		
		
		
		// retrieve the current and data folders
		// and user home directory
		pwd = parent!=null ? parent.sketchPath("") : System.getProperty("user.dir")+File.separator;
		dat = parent!=null ? parent.dataPath("") : pwd+"data"+File.separator;
		home = System.getProperty("user.home");
		
		
		
		// list paths (in order) depending on which plateform this program is running
		
		if ( OS.indexOf("windows")!=-1 ) {
			
			String[] PATH = System.getProperty("java.library.path").split(";");
			
			for( int i=0; i<PATH.length; i++ ) {
				if ( PATH[i].indexOf("OpenCV")==-1 ) continue;
				
				paths = new String[]{
					dat,
					pwd,
					PATH[i].replaceFirst("bin","")+"data\\haarcascades\\"
				};
				break;
			}
		}
		
		if ( OS.indexOf("mac")!=-1 )
			paths = new String[]{
				dat,
				pwd,
				"/System/Library/Frameworks/OpenCV.framework/Resources/",
				"/Library/Frameworks/OpenCV.framework/Resources/",
				home+"/Library/Frameworks/OpenCV.framework/Resources/"
			};
		
		if ( OS.indexOf("linux")!=-1 )
			paths = new String[]{
				dat,
				pwd,
				"/usr/share/opencv/haarcascades/",
				"/usr/local/share/opencv/haarcascades/"
			};
		
		
		
		// return path if file exits in one of these directories (or is already absolute path)
		if ( new File(file).isAbsolute() ) return file;
		for( int i=0; i<paths.length; i++ ) {
			if ( new File(paths[i]+file).exists() ) return paths[i]+file;
		}
	
		
		
		// return path if any JAR resources match the given file
		try {
			return this.getClass().getClassLoader().getResource(file).getPath();
		}
		catch( NullPointerException e ) {;}
		
		
		// if not found, return the given file
		return file;
	}
	
	
	
	
	////////////////////////////////////// OBJECT DETECTION ////////////////////////////////////////
	
	
	
	
	
	/**
	 * Load into memory the descriptor file for a trained cascade classifier.
	 * Required by the object detection method (see <code>detect()</code>).
	 *
	 * <p>While you may use your own cascade description files, this library links as well to a standard family of detection cascades (<code>CASCADE_FRONTALFACE_DEFAULT</code>, <code>CASCADE_FULLBODY</code>, …). These are installed automatically in the Windows installer and most Linux packages. For those using Macs, we have included these files in our <code>opencv-framework-*.*.dmg</code> installer. If you use a different Mac installer, you will have to import these files on your own.</p>
	 *
	 * <p>It is recommended that you avoid loading the file at each cycle.</p>
	 *
	 * <p>Available detection cascade flags :</p>
	 *
	 * <ul>
	 * <li>CASCADE_FRONTALFACE_ALT_TREE</li>
	 * <li>CASCADE_FRONTALFACE_ALT</li>
	 * <li>CASCADE_FRONTALFACE_ALT2</li>
	 * <li>CASCADE_FRONTALFACE_DEFAULT</li>
	 * <li>CASCADE_PROFILEFACE</li>
	 * <li>CASCADE_FULLBODY</li>
	 * <li>CASCADE_LOWERBODY</li>
	 * <li>CASCADE_UPPERBODY</li>
	 * </ul>
	 *
	 * @param file	The filepath, or standard cascade flag (see above) of the Haar classifier cascade file to be used for object detection
	 * @see #detect()
	 * @see #detect( float, int, int, int, int )
	 *
	 * @example faceDetect
	 */
	public native void cascade( String file );
	
	
	/**
	 * Detect object(s) in the current image depending on the current cascade description.
	 * <p>This method finds rectangular regions in the current image that are likely to contain objects the 
	 * cascade has been trained to recognize. It returns found regions as a sequence of rectangles.</p>
	 * <p>The default parameters (scale=1.1, min_neighbors=3, flags=0) are tuned for accurate
	 * (but slow) object detection. For a faster operation on real-time images, the more preferable 
	 * settings are: scale=1.2, min_neighbors=2, flags=HAAR_DO_CANNY_PRUNING, 
	 * min_size=<minimum possible face size> (for example, ~1/4 to 1/16 of the image area in case of
	 * video conferencing).</p>
	 *
	 * <p>Mode of operation flags:</p>
	 * <dl>
	 *
	 * <dt>HAAR_SCALE_IMAGE<dt>
	 * <dd>for each scale factor used the function will downscale the image rather than "zoom" the feature coordinates in the classifier cascade. Currently, the option can only be used alone, i.e. the flag can not be set together with the others.</dd>
	 * <dt>HAAR_DO_CANNY_PRUNING</dt>
	 * <dd>If it is set, the function uses Canny edge detector to reject some image regions that contain too few or too much edges and thus can not contain the searched object. The particular threshold values are tuned for face detection and in this case the pruning speeds up the processing.</dd>
	 * <dt>HAAR_FIND_BIGGEST_OBJECT</dt>
	 * <dd>If it is set, the function finds the largest object (if any) in the image. That is, the output sequence will contain one (or zero) element(s).</dd>
	 * <dt>HAAR_DO_ROUGH_SEARCH</dt>
	 * <dd>It should be used only when CV_HAAR_FIND_BIGGEST_OBJECT is set and min_neighbors > 0. If the flag is set, the function does not look for candidates of a smaller size as soon as it has found the object (with enough neighbor candidates) at the current scale. Typically, when min_neighbors is fixed, the mode yields less accurate (a bit larger) object rectangle than the regular single-object mode (flags=HAAR_FIND_BIGGEST_OBJECT), but it is much faster, up to an order of magnitude. A greater value of min_neighbors may be specified to improve the accuracy.</dd>
	 * </dl>
	 * <p>Note, that in single-object mode HAAR_DO_CANNY_PRUNING does not improve performance much and can even slow down the processing.</p>
	 *
	 *
	 * @param scale	The factor by which the search window is scaled between the subsequent scans, for example, 1.1 means increasing window by 10%.
	 * @param min_neighbors	Minimum number (minus 1) of neighbor rectangles that makes up an object. All the groups of a smaller number of rectangles than min_neighbors-1 are rejected. If min_neighbors is 0, the function does not any grouping at all and returns all the detected candidate rectangles, which may be useful if the user wants to apply a customized grouping procedure.
	 * @param flags	Mode of operation. It can be a combination of zero or more of the above flags.
	 * @param min_width	Minimum window size. By default, it is set to the size of samples the classifier has been trained on (~20×20 for face detection).
	 * @param min_height	Minimum window size. By default, it is set to the size of samples the classifier has been trained on (~20×20 for face detection).
	 *
	 *
	 * @see #cascade( String )
	 * @example faceDetect
	 */
	public native Rectangle[] detect( float scale, int min_neighbors, int flags, int min_width, int min_height );
	public Rectangle[] detect() {
		return detect( 1.1f, 3, 0, 0, 0 );
	}
	
	
}
