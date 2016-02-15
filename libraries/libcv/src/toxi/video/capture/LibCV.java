/* 
 * Copyright (c) 2006 Karsten Schmidt
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * http://creativecommons.org/licenses/LGPL/2.1/
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package toxi.video.capture;

import processing.core.*;

/**
 * LibCV is a basic video capture wrapper and computer vision library for
 * Processing. Internally it is only working with 8 bit images, but can be used
 * with any Java based capture approach implementing the SimpleCapture interface
 * via an wrapper.
 * 
 * <p>
 * LibCV automatically handles basic computer vision tasks like background
 * subtraction and the computation of difference frames for obtaining motion
 * data. You can choose to only analyze parts of the camera image by defining a
 * quad outlining the area to be examined.
 * </p>
 * 
 * <p>
 * All image buffers used internally and exposed via {@link #getBackground()},
 * {@link #getBuffer()} and {@link #getDeltaBuffer()} will only contain 8bit
 * data, but will be displayed in colour by Processing's <a
 * href="http://processing.org/reference/image_.html">image()</a> method. In
 * order to display these images correctly, the alpha channel is set to 100%. So
 * you'll have to make sure to mask the blue channel where the real data is
 * stored when doing any further analysis:
 * </p>
 * 
 * <p><code>
 * PImage buf=libcvInstance.getBuffer();<br/>
 * for(int i=0; i &lt; buf.pixels.length; i++) {<br/>
 *   int val=buf.pixels[i] & 0xff;<br/>
 *   ...<br/>
 * }</code>
 * </p>
 * 
 * <p>
 * The SimpleCapture interface is used to ensure a relative independence from
 * various capture backends. It should be very straightforward to write a
 * wrapper for the existing Processing Capture object making use of this
 * interface and to enable its use with this library.
 * </p>
 * 
 * @author Karsten Schmidt < i n f o [ a t ] t o x i . co . u k >
 * @version 0.1
 */

public class LibCV {

	/**
	 * actual capture object
	 */
	protected SimpleCapture capture;

	/**
	 * image objects used for CV purposes
	 */
	protected PImage buffer, prevBuffer, deltaBuffer, bg;

	/**
	 * buffer dimensions
	 */
	private int width, height;

	/**
	 * flag, if background image has been already learned
	 */
	private boolean bgInited = false;

	/**
	 * flag, if buffer needs transformation of quad -> rect
	 */
	private boolean isCorrected = false;

	/**
	 * quad coordinates of the actually used image area
	 */
	private int[] correctionX, correctionY;

	/**
	 * offscreen renderer to create stretched cam image
	 */
	private PGraphics correctionGfx;

	/**
	 * library host
	 */
	private PApplet app;

	/**
	 * Constructor for the library
	 * 
	 * @param a
	 *            host Processing applet/application
	 * @param c
	 *            capture object
	 */
	public LibCV(PApplet a, SimpleCapture c) {
		app = a;
		capture = c;
		width = capture.getWidth();
		height = capture.getHeight();
		buffer = new PImage(width, height);
		buffer.format = PImage.RGB;
		prevBuffer = new PImage(width, height);
		prevBuffer.format = PImage.RGB;
		deltaBuffer = new PImage(width, height);
		deltaBuffer.format = PImage.RGB;
		bg = new PImage(width, height);
		bg.format = PImage.RGB;
		correctionX = new int[] { width, 0, 0, width };
		correctionY = new int[] { height, height, 0, 0 };
		correctionGfx = app.createGraphics(width, height, PConstants.P3D);
		correctionGfx.defaults();
	}

	/**
	 * String description of a capture error occured previously.
	 * 
	 * @return string with error message or null if all is ok
	 */
	public String getError() {
		return capture.getError();
	}

	/**
	 * Calling this method will trigge reading a new frame and to return it. To
	 * save CPU resources this library is using a polling approach to acquire
	 * new frames from the capture object.
	 * 
	 * @return most recent captured frame
	 */
	public PImage getFrame() {
		return capture.getFrame();
	}

	/**
	 * Uses the passed in image as background. Automatically enables background
	 * subtraction for {@link #processFrame(PImage)}
	 * 
	 * @param img
	 *            image to be used as background buffer
	 * @throws IncompatibleSizeException
	 *             if image has different size than buffers.
	 */
	public void setBackground(PImage img) throws IncompatibleSizeException {
		try {
			int[] vpixels;
			vpixels = getCorrectedPixels(img);
			System.arraycopy(vpixels, 0, bg.pixels, 0, bg.pixels.length);
			hasBackground(true);
		} catch (IncompatibleSizeException e) {
			throw e;
		}
	}

	/**
	 * Retrieves the currently used background buffer.
	 * 
	 * @return background as PImage.
	 */
	public PImage getBackground() {
		return bg;
	}

	/**
	 * Mixes the passed image with the currently used background. This is
	 * helpful when "learning" the background by forming an average image over
	 * period of time (e.g. average of 100 frames).
	 * 
	 * @param img
	 *            image to be mixed with existing background
	 * @throws IncompatibleSizeException,
	 *             if image has different size than buffers.
	 */
	public void accumulateBackground(PImage img)
			throws IncompatibleSizeException {
		try {
			int[] vpixels;
			vpixels = getCorrectedPixels(img);
			for (int i = 0; i < vpixels.length; i++) {
				bg.pixels[i] = 0xff000000 | (((bg.pixels[i] & 0xff) + (0xff ^ (vpixels[i] >> 8 & 0xff))) >> 1);
			}
			hasBackground(true);
		} catch (IncompatibleSizeException e) {
			throw e;
		}
	}

	/**
	 * Tells the library if background subtraction should be used when
	 * processing frames
	 * 
	 * @param s
	 *            true, if background is used
	 */
	public void hasBackground(boolean s) {
		bgInited = s;
		bg.updatePixels();
	}

	/**
	 * Returns status of the library's background processing setting.
	 * 
	 * @return true, if background subtraction is enabled
	 */
	public boolean hasBackground() {
		return bgInited;
	}

	/**
	 * Performs background subtraction on the passed image (if enabled) and
	 * updates the delta buffer containing movement information.
	 * 
	 * @param img
	 *            image to be processed as frame
	 * @throws IncompatibleSizeException,
	 *             if image size differs from buffer's
	 */
	public void processFrame(PImage img) throws IncompatibleSizeException {
		int[] vpixels;
		try {
			vpixels = getCorrectedPixels(img);
			PImage t = prevBuffer;
			prevBuffer = buffer;
			buffer = t;
			int[] bufpixels = buffer.pixels;
			if (!bgInited) {
				for (int i = 0; i < vpixels.length; i++) {
					bufpixels[i] = 0xff000000 | (0xff ^ vpixels[i] >> 8 & 0xff);
				}
			} else {
				int[] bgpixels = bg.pixels;
				for (int i = 0; i < vpixels.length; i++) {
					bufpixels[i] = 0xff000000 | max(
							0,
							((0xff ^ (vpixels[i] >> 8 & 0xff)) - (bgpixels[i] & 0xff)));
				}
			}
			buffer.updatePixels();
			computeDeltaFrame();
		} catch (IncompatibleSizeException e) {
			throw e;
		}
	}

	/**
	 * Computes the movement information between current and previous frame
	 * 
	 */
	private void computeDeltaFrame() {
		int[] bpix = buffer.pixels;
		int[] ppix = prevBuffer.pixels;
		int[] dpix = deltaBuffer.pixels;
		for (int i = 0; i < bpix.length; i++) {
			dpix[i] = 0xff000000 | abs((bpix[i] & 0xff) - (ppix[i] & 0xff));
		}
		deltaBuffer.updatePixels();
	}

	/**
	 * Returns the buffer containing the difference to the previous frame
	 * 
	 * @return delta buffer as PImage
	 */
	public PImage getDeltaBuffer() {
		return deltaBuffer;
	}

	private final int max(int a, int b) {
		return a > b ? a : b;
	}

	private final int abs(int a) {
		return a > 0 ? a : -a;
	}

	/**
	 * applies any active keystone correction transform to the passed in image.
	 * Image will be reprojected to the quad set via
	 * {@see LibCV#setCorrectionPointID(int, int, int)}.
	 * 
	 * @param img
	 *            image containing the raw captured frame
	 * @return possibly transformed image as int[] array
	 * @throws IncompatibleSizeException,
	 *             if image size differs from buffer's
	 */
	private int[] getCorrectedPixels(PImage img)
			throws IncompatibleSizeException {
		if (img.width != width || img.height != height)
			throw new IncompatibleSizeException(
					"The background image must be of equal dimension as LibCV");
		// PImage dest=new PImage(img.width,img.height);
		if (isCorrected) {
			correctionGfx.background(0);
			correctionGfx.beginShape(PConstants.QUADS);
			correctionGfx.texture(img);
			correctionGfx.vertex(0, 0, correctionX[0], correctionY[0]);
			correctionGfx.vertex(width - 1, 0, correctionX[1], correctionY[1]);
			correctionGfx.vertex(width - 1, height - 1, correctionX[2],
					correctionY[2]);
			correctionGfx.vertex(0, height - 1, correctionX[3], correctionY[3]);
			correctionGfx.endShape();
			correctionGfx.updatePixels();
			// System.arraycopy(correctionGfx.pixels,0,dest.pixels,0,dest.pixels.length);
			return correctionGfx.pixels;
		} else {
			// System.arraycopy(img.pixels,0,dest.pixels,0,dest.pixels.length);
			return img.pixels;
		}
		// return dest;
	}

	/**
	 * Returns the buffer containing the most recent frame (depending on when
	 * this method has been called, in either processed or unprocessed form)
	 * 
	 * @return the most recent captured buffer
	 */
	public PImage getBuffer() {
		return buffer;
	}

	/**
	 * Enables/disable the trapezoid correction of incoming frames based on the
	 * quad defined via {@link #setCorrectionPointID(int, int, int)}
	 * 
	 * @param state
	 *            true, if frames are to be stretched
	 */
	public void useCorrection(boolean state) {
		isCorrected = state;
	}

	/**
	 * Returns the status of the library's pixel correction setting
	 * 
	 * @return true, if trapezoid correction is enabled
	 */
	public boolean isCorrected() {
		return isCorrected;
	}

	/**
	 * Updates the coordinate of one of the points of the correction quad. The
	 * quad is used to only consider points located within the shape for the
	 * processing of frames. If correction is used, this quad will be stretched
	 * to form a rectangle and so eliminating all pixels outside the shape. This
	 * is very helpful if you only want to analyze a part of the camera image,
	 * or if your camera is skewed it will also automatically perform some kind
	 * of perspective correction.
	 * 
	 * @param id
	 *            index of the point to be set (0 = top-left, 1 = top-right, 2 =
	 *            bottom - right, 3 = bottom - left)
	 * @param x
	 *            point's x coordinate
	 * @param y
	 *            point's y coordinate
	 */
	public void setCorrectionPointID(int id, int x, int y) {
		if (id >= 0 && id < 4) {
			correctionX[id] = x;
			correctionY[id] = y;
			System.out.println(id + " -> " + x + ";" + y);
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	/**
	 * Returns the currently used quad for stretching/correcting images.
	 * 
	 * @return quad
	 */
	public Quad getCorrectionQuad() {
		return new Quad(correctionX, correctionY);
	}

	/**
	 * Retrieves the pixel width of the buffers
	 * 
	 * @return image width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Retrieves the pixel height of the buffers
	 * 
	 * @return image width
	 */
	public int getHeight() {
		return height;
	}

	public void stop() {
		if (capture!=null) {
			capture.shutdown();
		}
		capture=null;
		buffer=null;
		bg=null;
		deltaBuffer=null;
	}
	
	/**
	 * Utility datatype for storing a set of four 2D coordinates
	 */
	public class Quad {
		public int[] x, y;

		public Quad(int[] xx, int[] yy) {
			x = xx;
			y = yy;
		}
	}
}
