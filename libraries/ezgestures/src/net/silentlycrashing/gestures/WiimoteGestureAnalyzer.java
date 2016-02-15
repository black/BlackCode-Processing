/*
  This file is part of the ezGestures project.
  http://www.silentlycrashing.net/ezgestures/

  Copyright (c) 2007-08 Elie Zananiri

  ezGestures is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software 
  Foundation, either version 3 of the License, or (at your option) any later 
  version.

  ezGestures is distributed in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
  A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with 
  ezGestures.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.silentlycrashing.gestures;

import java.awt.*;
import oscP5.*;
import processing.core.*;

/**
 * Records gestures made using a Wii Remote for analysis.
 * <p>Works using Andreas Schlegel's <a href="http://www.sojamo.de/libraries/oscP5/">oscP5</a> and <a href="http://code.google.com/p/darwiinosc/">darwiinosc</a> libraries.</p>
 */
/* $Id: WiimoteGestureAnalyzer.java 31 2008-11-12 16:01:48Z prisonerjohn $ */
public class WiimoteGestureAnalyzer extends GestureAnalyzer {
	public static final int A_BUTTON = 1;
	public static final int B_BUTTON = 2;
	public static final int MAX_SIZE = 15;
	
	private OscP5 oscClient;
	private Point currPoint;
	private boolean buttonPressed;
	
	/**
	 * Builds a WiimoteGestureAnalyzer with default button, incoming port, and minimum offset.
	 * 
	 * @param parent the parent PApplet
	 */
	public WiimoteGestureAnalyzer(PApplet parent) {
		this(parent, B_BUTTON, 5600, 30);
	}
	
	/**
	 * Builds a WiimoteGestureAnalyzer with default incoming port and minimum offset.
	 * 
	 * @param parent the parent PApplet
	 * @param button the Wiimote button to check
	 */
	public WiimoteGestureAnalyzer(PApplet parent, int button) {
		this(parent, button, 5600, 30);
	}
	
	/**
	 * Builds a WiimoteGestureAnalyzer with default minimum offset.
	 * 
	 * @param parent the parent PApplet
	 * @param button the Wiimote button to check
	 * @param port the incoming port for OSC messages
	 */
	public WiimoteGestureAnalyzer(PApplet parent, int button, int port) {
		this(parent, button, port, 30);
	}
	
	/**
	 * Builds a WiimoteGestureAnalyzer.
	 * 
	 * @param parent the parent PApplet
	 * @param button the Wiimote button to check
	 * @param port the incoming port for OSC messages
	 * @param min the minimum offset
	 */
	public WiimoteGestureAnalyzer(PApplet parent, int button, int port, int min) {
		super(parent, min);
		
		oscClient = new OscP5(this, port);
		
		// register the message listeners
		oscClient.plug(this, "irEvent", "/wii/irdata");
		oscClient.plug(this, "connectEvent", "/wii/connected");
		if (button == A_BUTTON) {
			oscClient.plug(this, "buttonEvent", "/wii/button/a");
		} else {
			oscClient.plug(this, "buttonEvent", "/wii/button/b");
		}
	}
	
	/**
	 * Handles a connect event.
	 * 
	 * @param val whether or not the Wiimote is connected
	 */
	public void connectEvent(int val) {
		if (val == 1) {
			PApplet.println("Wiimote connected!");
		} else {
			PApplet.println("Wiimote disconnected!");
		}
	}
	
	/**
	 * Handles a button event.
	 * 
	 * @param val whether or not the button is pressed
	 */
	public void buttonEvent(int val) {
		PApplet.println("button state is "+val);
		if (val == 1) {
			buttonPressed = true;
			start(currPoint);
		} else {
			buttonPressed = false;
			stop(currPoint);
		}
	}
	
	/**
     * Handles an IR event.
     * 
     * @param x1 x-value of IR spot 1
     * @param y1 y-value of IR spot 1
     * @param s1 size of IR spot 1
     * @param x2 x-value of IR spot 2
     * @param y2 y-value of IR spot 2
     * @param s2 size of IR spot 2
     * @param x3 x-value of IR spot 3
     * @param y3 y-value of IR spot 3
     * @param s3 size of IR spot 3
     * @param x4 x-value of IR spot 4
     * @param y4 y-value of IR spot 4
     * @param s4 size of IR spot 4
     */
	public void irEvent(float x1, float y1, float s1, float x2, float y2, float s2, float x3, float y3, float s3, float x4, float y4, float s4) {
		if ((s1 > MAX_SIZE) || (s2 > MAX_SIZE)) {
			// IR point is not available
			return;
		}
		
		currPoint = new Point((int)(p.width-((x1+x2)*p.width/2)), (int)((y1+y2)*p.height/2));
		
		if (!buttonPressed) {
			// button is not pressed
			return;
		}
		
		update(currPoint);
	}
	
	/**
	 * Draws the current pointer position to the canvas.
	 * <p>Can be registered for automatic drawing using drawPointer().</p>
	 */
	public void draw() {
		p.stroke(247, 247, 0);
		p.fill(247, 247, 0, 100);
		p.ellipse(currPoint.x, currPoint.y, 20, 20);
	}
	
	/**
	 * Sets whether the Wiimote pointer should be drawn or not.
	 * 
	 * @param d whether or not to draw the pointer
	 */
	public void drawPointer(boolean d) {
		if (d) {
			p.registerDraw(this);
		} else {
			p.unregisterDraw(this);
		}
	}
}
