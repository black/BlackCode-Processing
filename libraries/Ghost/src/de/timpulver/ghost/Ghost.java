//
// Ghost.java
// Ghost (v.0.1.3) is released under the MIT License.
//
// Copyright (c) 2012, Tim Pulver http://timpulver.de
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//


/* 
 * Tested with Processing 2.0b6 (win8 64bit)
 * Adapted code from jungalero (processing.org forum)
 * 
 * TODO:
 * - reposition screenshot image on window move / resize
 * 		- take screenshot of whole desktop
 * 		- implement move(int x, int y)
 * 			- grab pixles to fit new area from old screenshot
 * - two different modes? AWTUtil (win only) + Robot  
 */

package de.timpulver.ghost;

import java.awt.AWTException;
import com.sun.awt.AWTUtilities;

import java.awt.Frame;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.lang.reflect.Method;
//import java.awt.DisplayMode;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;


public abstract class Ghost implements PConstants{
	private PApplet p;
	private PImage screenShotArea, screenShotFull;
	private boolean clearBackground = true;
	boolean redrawScreenShot = false;
	protected static final String DEFAULT_RENDERER = JAVA2D; 
	private String renderer;

	private int x, y, w, h;

	/**
	 * Creates a transparent window. Will remove screen boarders and other decoration 
	 * and display a screenshot of the area behind this frame, so it looks transparent.
	 * @param p Processing PApplet
	 * @param x x-coordinate of the window
	 * @param y y-coordinate of the window
	 * @param w width of the window
	 * @param h height of the window
	 * @param renderer the renderer to use (JAVA2D, P2D, P3D, OPENGL)
	 * @see PConstants
	 */
	public void init(PApplet p, int x, int y, int w, int h, String renderer){
		this.p = p;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.renderer = renderer;
		// remove frame components
		p.frame.removeNotify();
		p.frame.setUndecorated(true);
		// if we are on a mac, remove the drop shadow
		if(isMac()){
			removeDropShadow(p);
		}
		p.size(w, h, renderer);
		screenShotArea = getScreenCapture(x, y, w, h); // take screenshot of selected area
		screenShotFull = getFullscreenCapture(); // take fullscreen capture too, in case window moves
		p.registerMethod("pre", this); // new in Processing 2.0
		redrawScreenShot = true; // draw the image in pre()
	}
	
	/**
	 * Checks if the platform is a mac
	 * @return true, if it's a mac
	 */
	private boolean isMac(){
		return System.getProperty("os.name").toLowerCase().indexOf("mac") != -1;
	}
	
	/**
	 * Trys to call an AWTUtilities function to remove the drop shadow on OS X. 
	 * TODO: Test if program crashes if funtion is not available (Java < 1.60_10)
	 * @param p Processing PApplet
	 */
	private void removeDropShadow(PApplet p){
		try {
			/*
            Window win = p.frame;
            //invoke AWTUtilities.setWindowOpacity(win, 0.0f);
            Class awtutil = Class.forName("com.sun.awt.AWTUtilities");
            Method setWindowOpaque = awtutil.getMethod("setWindowOpaque", Window.class, boolean.class);
            //setWindowOpaque.invoke(win, false);
            setWindowOpaque.invoke(p.frame, false);
            */
			AWTUtilities.setWindowOpaque(p.frame, false);
        } catch (Exception e) {
            //e.printStackTrace();
        	System.err.println("WARNING: Could not remove the drop shadow from Processing frame! " +
					"You may need to install the latest Java version.");
        }
	}
	
	/**
	 * Sets the screen to a new position, 
	 * actual reposition will be done on next draw() / pre() call. 
	 * @param x new x position of the window
	 * @param y new y position of the window
	 */
	public void setPosition(int x, int y){
		if(!clearBackground){
			System.err.println("WARNING: setPosition() clears the screen");
		}
		this.x = x;
		this.y = y;
		screenShotArea.copy(screenShotFull, x, y, w, h, 0, 0, w, h);
		redrawScreenShot = true;
	}
	
	
	/**
	 * Equals a background(0) in the Processing draw() function. 
	 * When set to <i>true</i>, the background image will be drawn every frame, 
	 * so everything beneath it will not be visible any more. 
	 * @param b Whether or not the background should be cleared every frame
	 */
	public void clearBackground(boolean b){
		this.clearBackground = b;
	}
	
	/**
	 * Will be called before draw() in the main sketch. 
	 * We need to reposition the window every frame and 
	 * draw the screenshot as a background. Do not call this from your sketch!
	 */
	public void pre(){
		  p.frame.setLocation(x, y);
		  if(clearBackground || redrawScreenShot){
			  p.image(screenShotArea,0,0, w, h);
			  redrawScreenShot = false;
		  }
	}

	/**
	 * Returns an image of the screen (full)
	 */
	private PImage getFullscreenCapture(){
	  return getScreenCapture(0, 0, p.displayWidth, p.displayHeight);
	}

	/**
	 * Returns an area of the screen (screenshot)
	 * @param x Top-Left corner of the area to copy (x)
	 * @param y Top Left corner of the area to copy (y)
	 * @param w Width of the rectangle
	 * @param h Height of the rectangle
	 * @return Screenshot of the current desktop,  
	 * when you call this in Processing before size() the sketch will 
	 * not be included in the Screenshot. 
	 */
	public static PImage getScreenCapture(int x, int y, int w, int h){
	  GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	  GraphicsDevice[] gs = ge.getScreenDevices();
	  //DisplayMode mode = gs[0].getDisplayMode();
	  Rectangle bounds = new Rectangle(x, y, w, h);
	  BufferedImage desktop = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

	  try {
	    desktop = new Robot(gs[0]).createScreenCapture(bounds);
	  }
	  catch(AWTException e) {
	    System.err.println("Screen capture failed.");
	  }
	  return (new PImage(desktop));
	}
}
