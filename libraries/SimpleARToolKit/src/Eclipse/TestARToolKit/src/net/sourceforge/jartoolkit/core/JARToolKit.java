// jARToolKit Version 2.0
// 
// A Java-binding to the Augmented Reality library ARToolKit
// supporting GL4Java, JOGL and Java3D
// 
// Copyright (C) 2004 Jörg Stöcklein <ozone_abandon@sf.net>
//					  Tim Schmidt <tisch@sf.net>
//
// www: jartoolkit.sf.net
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package net.sourceforge.jartoolkit.core;

/**
 * JARToolKit: Java binding to the AR Toolkit
 * 
 * Class encapsulate the AR-Toolkit. This class is a Java-Wrapper for the C
 * AR-Toolkit. Not all functions of the original AR-Toolkit are implemented,
 * only these one, I need to get AR running under Java. You're welcome to do the
 * rest :-) .
 * 
 * @author Jörg Stöcklein (ozone_abandon at sourceforge.net)
 * @version 2.0
 */
public class JARToolKit {
	static {
		System.loadLibrary("JARToolKit");
	}

	/** Hold the only instance of this class. */
	private static JARToolKit m_JARToolKit = null;

	/** The Version of JARToolKit. */
	final public static String version = "Version 1.0(alpha)";

	/**
	 * Create a JARToolKit instance. You can create an instance only once. If
	 * you call this static method more than once null will return.
	 * 
	 * @return A instance of a JARFrameGrabber class, or null, if you called
	 *         this method more than once.
	 */
	public static JARToolKit create() throws InstantiationException {
		if (m_JARToolKit != null) {
			throw new InstantiationException(
					"Can't create more than one insatnce of JARToolKit");
		}

		return (m_JARToolKit = new JARToolKit());
	}

	/**
	 * Wait the given milliseconds.
	 * 
	 * @param msec
	 *            milliseconds to wait.
	 */
	public static native void utilSleep(int msec);

	/**
	 * Return the current time of the timer in seconds.
	 * 
	 * @return time in seconds.
	 */
	public static native double utilTimer();

	/**
	 * Reset the internal timer.
	 */
	public static native void utilTimerReset();

	/**
	 * The constructor. You cannot use the constructor, you have to use the
	 * static method Create instead.
	 */
	private JARToolKit() {
	}

	/**
	 * Activate the given pattern.
	 * 
	 * @param pat_no
	 *            The pattern id that should be activated.
	 * @return 0, if pattern was activated, !=0 else.
	 */
	public native int activatePattern(int pat_no);

	/**
	 * De-activates a multi-pattern.
	 * 
	 * @param multiMarkerID
	 *            The id of the multi-pattern
	 * @return 0, if multi-pattern was de-activated, !=0 else.
	 */
	public native int arMultiDeactivate(int multiMarkerID);

	/**
	 * Free a multipattern.
	 * 
	 * @param multiMarkerID
	 *            The id of the multipattern
	 * @return 0, if multipattern was freed, !=0 else.
	 */
	public native int arMultiFreeConfig(int multiMarkerID);

	/**
	 * Deactivate the given pattern.
	 * 
	 * @param pat_no
	 *            The patternid that should be deactivated.
	 * @return 0, if pattern was deactivated, !=0 else.
	 */
	public native int deactivatePattern(int pat_no);

	/**
	 * Try to detect pattern in an image. Returns a list of patternids detected
	 * in the image.
	 * 
	 * @param dataPtr
	 *            Integer-array of the image searched in.
	 * @param thresh
	 *            Threshhold for converting image to B/W. Must be between 0 and
	 *            255.
	 * @return List of patternids detected in the image.
	 */
	public native int[] detectMarker(int dataPtr[], int thresh);

	/**
	 * Try to detect pattern in an image. Returns a list of patternids detected
	 * in the image.
	 * 
	 * @param dataPtr
	 *            Longpointer of the image searched in.
	 * @param thresh
	 *            Threshhold for converting image to B/W. Must be between 0 and
	 *            255.
	 * @return List of patternids detected in the image.
	 */
	public native int[] detectMarker(long dataPtr, int thresh);

	/**
	 * Try to detect pattern in an image, alternative methode. Returns a list of
	 * patternids detected in the image.
	 * 
	 * @param dataPtr
	 *            Integer-array of the image searched in.
	 * @param thresh
	 *            Threshhold for converting image to B/W. Must be between 0 and
	 *            255.
	 * @return List of patternids detected in the image.
	 */
	public native int[] detectMarkerLite(int dataPtr[], int thresh);

	/**
	 * Try to detect pattern in an image, alternative methode. Returns a list of
	 * patternids detected in the image.
	 * 
	 * @param dataPtr
	 *            Longpointer of the image searched in.
	 * @param thresh
	 *            Threshhold for converting image to B/W. Must be between 0 and
	 *            255.
	 * @return List of patternids detected in the image.
	 */
	public native int[] detectMarkerLite(long dataPtr, int thresh);

	/**
	 * Overloaded method used by the garbage collector when no more references
	 * to the object exist.
	 */
	protected void finalize() {
		JARToolKit.m_JARToolKit = null;
	}

	/**
	 * Free the given pattern.
	 * 
	 * @param patt_no
	 *            The patternid that should be freed.
	 * @return 0, if pattern was freed, !=0 else.
	 */
	public native int freePattern(int patt_no);

	/**
	 * Return the camera transformation matrix.
	 * 
	 * @return Doublearray representing a 4x4 column-major matrix. (like OpenGL
	 *         matrices).
	 */
	public native double[] getCamTransMatrix();

	/**
	 * Fills the camera transformation matrix to the given doublearray.
	 * 
	 * @param matrix
	 *            Doublearray representing a 4x4 column-major matrix. (like
	 *            OpenGL matrices).
	 */
	public native void getCamTransMatrix(double[] matrix);

	/**
	 * Return the camera transformation matrix for Java3D.
	 * 
	 * @return Doublearray representing a 4x4 column-major matrix. (like OpenGL
	 *         matrices).
	 */
	public native double[] getCamTransMatrixJava3D();

	/**
	 * Fills the camera transformation matrix to the given doublearray for
	 * Java3D.
	 * 
	 * @param corrx
	 *            Correction in x
	 * @param corry
	 *            Correction in y
	 * @return Doublearray representing a 4x4 column-major matrix. (like OpenGL
	 *         matrices).
	 */
	public native double[] getCamTransMatrixJava3D(double corrx, double corry);

	/**
	 * Return the camera transformation matrix for Java3D.
	 * 
	 * @return Doublearray representing a 4x4 column-major matrix. (like OpenGL
	 *         matrices).
	 */
	public native void getCamTransMatrixJava3D(double[] matrix);

	/**
	 * Fills the camera transformation matrix to the given doublearray for
	 * Java3D.
	 * 
	 * @param matrix
	 *            Doublearray representing a 4x4 column-major matrix.
	 * @param corrx
	 *            Correctionfactor for x.
	 * @param corry
	 *            Correctionfactor for y. (like OpenGL matrices).
	 */
	public native void getCamTransMatrixJava3D(double[] matrix, double corrx,
			double corry);

	/**
	 * Fills the transformation matrix of a detected pattern to the given
	 * doublearray.
	 * 
	 * @param matrix
	 *            Doublearray representing a 4x4 column-major matrix.
	 * @param patternID
	 *            ID of the pattern.
	 * @param patternWidth
	 *            Width of the pattern.
	 * @param patternCenterX
	 *            Center of the pattern, x component.
	 * @param patternCenterY
	 *            Center of the pattern, y component.
	 * @return true, if the pattern was recognized, false else.
	 */
	public native boolean getTransMatrix(double[] matrix, int patternID,
			int patternWidth, float patternCenterX, float patternCenterY);

	/**
	 * Return the transformation matrix of a detected pattern.
	 * 
	 * @param patternID
	 *            ID of the pattern.
	 * @param patternWidth
	 *            Width of the pattern.
	 * @param patternCenterX
	 *            Center of the pattern, x component.
	 * @param patternCenterY
	 *            Center of the pattern, y component.
	 * @return Doublearray representing a 4x4 column-major matrix (like OpenGL
	 *         matrices), if the pattern was detected, the identity-marix else.
	 */
	public native double[] getTransMatrix(int patternID, int patternWidth,
			float patternCenterX, float patternCenterY);

	/**
	 * Return the transformation matrix of a detected pattern.
	 * 
	 * @param matrix
	 *            Doublearray representing a 4x4 column-major matrix.
	 * @param patternID
	 *            ID of the pattern.
	 * @param patternWidth
	 *            Width of the pattern.
	 * @param patternCenterX
	 *            Center of the pattern, x component.
	 * @param patternCenterY
	 *            Center of the pattern, y component.
	 * @param prev_conv
	 *            detected martix of the pattern.
	 * @return true, if the pattern was recognized, false else.
	 */
	public native boolean getTransMatrixCont(double[] matrix, int patternID,
			int patternWidth, float patternCenterX, float patternCenterY,
			double prev_conv[]);

	/**
	 * Return the transformation matrix of a detected pattern.
	 * 
	 * @param patternID
	 *            ID of the pattern.
	 * @param patternWidth
	 *            Width of the pattern.
	 * @param patternCenterX
	 *            Center of the pattern, x component.
	 * @param patternCenterY
	 *            Center of the pattern, y component.
	 * @param prev_conv
	 *            detected martix of the pattern.
	 * @return Doublearray representing a 4x4 column-major matrix (like OpenGL
	 *         matrices), if the pattern was detected, the identity-marix else.
	 */
	public native double[] getTransMatrixCont(int patternID, int patternWidth,
			float patternCenterX, float patternCenterY, double prev_conv[]);

	/**
	 * Return the transformation matrix of a detected pattern. Special method
	 * for use in Java3D.
	 * 
	 * @param matrix
	 *            Doublearray representing a 4x4 column-major matrix.
	 * @param patternID
	 *            ID of the pattern.
	 * @param patternWidth
	 *            Width of the pattern.
	 * @param patternCenterX
	 *            Center of the pattern, x component.
	 * @param patternCenterY
	 *            Center of the pattern, y component.
	 * @param prev_conv
	 *            detected martix of the pattern.
	 * @return true, if the pattern was recognized, false else.
	 */
	public native boolean getTransMatrixContJava3D(double[] matrix,
			int patternID, int patternWidth, float patternCenterX,
			float patternCenterY, double prev_conv[]);

	/**
	 * Return the transformation matrix of a detected pattern. Special method
	 * for use in Java3D.
	 * 
	 * @param patternID
	 *            ID of the pattern.
	 * @param patternWidth
	 *            Width of the pattern.
	 * @param patternCenterX
	 *            Center of the pattern, x component.
	 * @param patternCenterY
	 *            Center of the pattern, y component.
	 * @param prev_conv
	 *            detected martix of the pattern.
	 * @return Doublearray representing a 4x4 column-major matrix (like OpenGL
	 *         matrices), if the pattern was detected, the identity-marix else.
	 */
	public native double[] getTransMatrixContJava3D(int patternID,
			int patternWidth, float patternCenterX, float patternCenterY,
			double prev_conv[]);

	/**
	 * Fills the transformation matrix of a detected pattern to the given
	 * doublearray. Special method for use in Java3D.
	 * 
	 * @param matrix
	 *            Doublearray representing a 4x4 column-major matrix.
	 * @param patternID
	 *            ID of the pattern.
	 * @param patternWidth
	 *            Width of the pattern.
	 * @param patternCenterX
	 *            Center of the pattern, x component.
	 * @param patternCenterY
	 *            Center of the pattern, y component.
	 * @return true, if the pattern was recognized, false else.
	 */
	public native boolean getTransMatrixJava3D(double[] matrix, int patternID,
			int patternWidth, float patternCenterX, float patternCenterY);

	/**
	 * Return the transformation matrix of a detected pattern. Special method
	 * for use in Java3D.
	 * 
	 * @param patternID
	 *            ID of the pattern.
	 * @param patternWidth
	 *            Width of the pattern.
	 * @param patternCenterX
	 *            Center of the pattern, x component.
	 * @param patternCenterY
	 *            Center of the pattern, y component.
	 * @return Doublearray representing a 4x4 column-major matrix (like OpenGL
	 *         matrices), if the pattern was detected, the identity-marix else.
	 */
	public native double[] getTransMatrixJava3D(int patternID,
			int patternWidth, float patternCenterX, float patternCenterY);

	/**
	 * Initialize the camara-parameter.
	 * 
	 * @return 0 If no error occurs.
	 */
	public native int initCparam();

	/**
	 * Load a pattern to the toolkit.
	 * 
	 * @param filename
	 *            The filename of the pattern.
	 * @return PatternID<=0 or <0, if an error occurs.
	 */
	public native int loadPattern(String filename);

	/**
	 * Recieve a specific transform-matrix from the multipattern.
	 * 
	 * @param matrix
	 *            Doublearray representing a 4x4 column-major matrix.
	 * @param multiMarkerID
	 *            The ID of the multipattern.
	 * @param markerNumber
	 *            The marker number of the multipattern, 0<=markerNumber<Max
	 *            number of Marker assigned to this multipattern.
	 * @return true, if the pattern was recognized, false else.
	 */
	public native boolean multiGetTransMat(double[] matrix, int multiMarkerID,
			int markerNumber);

	/**
	 * Recieve a specific transform-matrix from the multipattern.
	 * 
	 * @param multiMarkerID
	 *            The ID of the multipattern.
	 * @param markerNumber
	 *            The marker number of the multipattern, 0<=markerNumber<Max
	 *            number of Marker assigned to this multipattern.
	 * @return Doublearray representing a 4x4 column-major matrix (like OpenGL
	 *         matrices), if the pattern was detected, the identity-marix else.
	 */
	public native double[] multiGetTransMat(int multiMarkerID, int markerNumber);

	/**
	 * Recieve a specific transform-matrix from the multipattern. Special method
	 * for use in Java3D.
	 * 
	 * @param matrix
	 *            Doublearray representing a 4x4 column-major matrix.
	 * @param multiMarkerID
	 *            The ID of the multipattern.
	 * @param markerNumber
	 *            The marker number of the multipattern, 0<=markerNumber<Max
	 *            number of Marker assigned to this multipattern.
	 * @return true, if the pattern was recognized, false else.
	 */
	public native boolean multiGetTransMatJava3D(double[] matrix,
			int multiMarkerID, int markerNumber);

	/**
	 * Recieve a specific transform-matrix from the multipattern. Special method
	 * for use in Java3D.
	 * 
	 * @param multiMarkerID
	 *            The ID of the multipattern.
	 * @param markerNumber
	 *            The marker number of the multipattern, 0<=markerNumber<Max
	 *            number of Marker assigned to this multipattern.
	 * @return Doublearray representing a 4x4 column-major matrix (like OpenGL
	 *         matrices), if the pattern was detected, the identity-marix else.
	 */
	public native double[] multiGetTransMatJava3D(int multiMarkerID,
			int markerNumber);

	/**
	 * Activates a multipattern.
	 * 
	 * @param multiMarkerID
	 *            The id of the multipattern
	 * @return 0, if multipattern was activated, !=0 else.
	 */
	public native int multiPatternActivate(int multiMarkerID);

	/*
	 * Multipattern Stuff
	 */

	/**
	 * Load a multipattern configuration file. Returns the id of the
	 * multipattern.
	 * 
	 * @param filename
	 *            The filename of the configuration file.
	 * @return -1, if an error occurs, multiMarkerID >=0 else.
	 */
	public native int multiReadConfigFile(String filename);

	/**
	 * Change the size of the camera-parameter to the given values.
	 * 
	 * @param width
	 *            Width to change to.
	 * @param height
	 *            Height to change to.
	 * @return 0, if no error occurs, !=0 else.
	 */
	public native int paramChangeSize(int width, int height);

	/*
	 * Utility
	 */

	/**
	 * Display the current camera-parameter. The method prints the parameter to
	 * the console using the printf in C. In the next version this method should
	 * return a string you can print in Java.
	 */
	public native void paramDisplay();

	/*
	 * public static int JARSavePatt( int dataPtr[], JARMarkerInfo marker_info,
	 * String filename ) { return 0; }
	 */

	/*
	 * Methods for parameter
	 */

	/**
	 * Load the camera-parameter-file.
	 * 
	 * @param filename
	 *            The filename of the parameter-file.
	 * @return 0, if no error occurs, !=0 else.
	 */
	public native int paramLoad(String filename);
}
