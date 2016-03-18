/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2008-12 Peter Lager

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package org.gamecontrolplus.gui;

import processing.core.PApplet;

/**
 * A two-state toggle control. <br>
 * 
 * GCheckbox objects (also known as tick boxes) are two-state toggle switches that are
 * used independently of other tick boxes.
 * 
 * @author Peter Lager
 *
 */
public class MCheckbox extends MToggleControl {

	/**
	 * Create an option button without text.
	 * 
	 * @param theApplet that will display the control
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param p3
	 */	
	public MCheckbox(PApplet theApplet, float p0, float p1, float p2, float p3) {
		this(theApplet, p0, p1, p2, p3, "");
	}
	
	/**
	 * Create an option button with text.
	 * 
	 * @param theApplet that will display the control
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param text text to be displayed
	 */
	public MCheckbox(PApplet theApplet, float p0, float p1, float p2, float p3, String text) {
		super(theApplet, p0, p1, p2, p3);
		opaque = false;
		setText(text);
		setIcon("tick.png", 2, MAlign.LEFT, null);
		setTextAlign(MAlign.LEFT, null);
		z = Z_SLIPPY;
		// Now register control with applet
		createEventHandler(M4P.sketchWindow, "handleToggleControlEvents", 
				new Class<?>[]{ MToggleControl.class, MEvent.class }, 
				new String[]{ "checkbox", "event" } 
		);
		registeredMethods = DRAW_METHOD | MOUSE_METHOD;
		cursorOver = HAND;
		M4P.registerControl(this);

//		super(theApplet, p0, p1, p2, p3);
//		opaque = false;
//		setText(text);
//		setIcon("tick.png", 2, MAlign.LEFT, null);
//		setTextAlign(MAlign.LEFT, null);
//		z = Z_SLIPPY;
//		// Now register control with applet
//		createEventHandler(M4P.sketchApplet, "handleToggleControlEvents", 
//				new Class<?>[]{ MToggleControl.class, MEvent.class }, 
//				new String[]{ "checkbox", "event" } 
//		);
//		registeredMethods = DRAW_METHOD | MOUSE_METHOD;
//		cursorOver = HAND;
//		M4P.addControl(this);
	}

	/**
	 * This enforces independent action because this control cannot be added
	 * to a toggle group
	 */
	@Override
	protected void setToggleGroup(MToggleGroup tg) {}
	
}
