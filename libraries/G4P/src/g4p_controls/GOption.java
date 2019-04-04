/*
  Part of the G4P library for Processing 
  	http://www.lagers.org.uk/g4p/index.html
	http://sourceforge.net/projects/g4p/files/?source=navbar

  Copyright (c) 2016 Peter Lager

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

package g4p_controls;

import processing.core.PApplet;

/**
 * A two-state toggle control. <br>
 * 
 * GOption objects (also known as radio buttons) are two-state toggle switches that can either
 * be used independently or if added to a GToggleGroup control part of a single selection 
 * option group.
 * 
 * @author Peter Lager
 *
 */
public class GOption extends GToggleControl{

	/**
	 * Create an option button without text.
	 * 
	 * @param theApplet  the main sketch or GWindow control for this control
	 * @param p0 x position based on control mode
	 * @param p1 y position based on control mode
	 * @param p2 x position or width based on control mode
	 * @param p3 y position or height based on control mode
	 */
	public GOption(PApplet theApplet, float p0, float p1, float p2, float p3) {
		this(theApplet, p0, p1, p2, p3, "");
	}

	/**
	 * Create an option button with text.
	 * 
	 * @param theApplet  the main sketch or GWindow control for this control
	 * @param p0 x position based on control mode
	 * @param p1 y position based on control mode
	 * @param p2 x position or width based on control mode
	 * @param p3 y position or height based on control mode
	 * @param text text to be displayed
	 */
	public GOption(PApplet theApplet, float p0, float p1, float p2, float p3, String text) {
		super(theApplet, p0, p1, p2, p3);
		opaque = false;
		
		// Initialise text and icon alignment
		PAD = 1;
		textAlignH = GAlign.LEFT;
		textAlignV = GAlign.MIDDLE;
		iconPos = GAlign.WEST;
		// Start with text only so the text zone is sized correctly
		calcZones(false, true);
		setText(text);
		setIcon("pinhead.png", 2, GAlign.CENTER, GAlign.MIDDLE);
		// Setting the icon will resize the text and icon zones, so no need to do it again.
		
		z = Z_SLIPPY;
		// Now register control with applet
		createEventHandler(G4P.sketchWindow, "handleToggleControlEvents", 
				new Class<?>[]{ GToggleControl.class, GEvent.class }, 
				new String[]{ "option", "event" } 
		);
		registeredMethods = DRAW_METHOD | MOUSE_METHOD;
		cursorOver = HAND;
		G4P.registerControl(this);
	}

}
