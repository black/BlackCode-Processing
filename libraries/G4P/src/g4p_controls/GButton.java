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

import g4p_controls.HotSpot.HSmask;
import g4p_controls.StyledString.TextLayoutInfo;

import java.awt.Graphics2D;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;

/**
 * This class is the Button component.
 * 
 * The button face can have either text or an image or both just
 * pick the right constructor. <br>
 * 
 * Three types of event can be generated :-  <br>
 * <b> PRESSED  RELEASED  CLICKED </b><br>
 * 
 * By default the button only fires the CLICKED event which is typical of 
 * most GUIs. G4P supports two other events PRESSED and RELEASED which can
 * be enabled using <pre>button1.fireAllEvents(true);</pre><br>
 * 
 * A PRESSED event is created if the mouse button is pressed down over the 
 * button face. When the mouse button is released one of two events will 
 * be generated, the RELEASED event if the mouse has moved since the 
 * PRESSED event or CLICKED event if it has not moved. If you use this 
 * feature remember to test the event type in the event-handler.<br>
 * 
 * Note that if you disable the button in its event handler e.g.
 * 
 * If you want the button is disable itself it should only be done on the 
 * CLICKED event e.g.
 * <pre>
 * public void handleButtonEvents(GButton button, GEvent event) {
 *   if (button == button1 && event == GEvent.CLICKED) {
 *       button1.setEnabled(false);
 *   }
 * }
 * do not try this with the RELEASED or PRESSED event as it will lead to inconsistent 
 * behaviour.
 * 
 * The image file can either be a single image which is used for 
 * all button states, or be a composite of 3 images (tiled horizontally)
 * which are used for the different button states OFF, OVER and DOWN 
 * in which case the image width should be divisible by 3. <br>
 * A number of setImages(...) methods exist to set button state images, these
 * can be used once the button is created.<br>
 * 
 * 
 * @author Peter Lager
 *
 */
public class GButton extends GTextIconBase {

	private static boolean roundCorners = true;
	private static float CORNER_RADIUS = 6;

	/**
	 * By default buttons are created with rounded corners. <br>
	 * 
	 *  This method can be used to change this setting for buttons yet to be created. Note that it does not affect any existing buttons.
	 *   
	 * @param useRoundCorners true for round corners or false for square corners.
	 */
	public static void useRoundCorners(boolean useRoundCorners){
		roundCorners = useRoundCorners;
	}
	
	// Mouse over status
	protected int status = 0;

	// Only report CLICKED events
	protected boolean reportAllButtonEvents = false;

	/**
	 * New button without text
	 * @param theApplet  the main sketch or GWindow control for this control
	 * @param p0 x position based on control mode
	 * @param p1 y position based on control mode
	 * @param p2 x position or width based on control mode
	 * @param p3 y position or height based on control mode
	 */
	public GButton(PApplet theApplet, float p0, float p1, float p2, float p3) {
		this(theApplet, p0, p1, p2, p3, "");
	}

	/**
	 * New button with text
	 * @param theApplet  the main sketch or GWindow control for this control
	 * @param p0 x position based on control mode
	 * @param p1 y position based on control mode
	 * @param p2 x position or width based on control mode
	 * @param p3 y position or height based on control mode
	 * @param text the button face text 
	 */
	public GButton(PApplet theApplet, float p0, float p1, float p2, float p3, String text) {
		super(theApplet, p0, p1, p2, p3);
		// Create mask for hotspots
		PGraphics mask = winApp.createGraphics((int) width, (int) height, JAVA2D);
		mask.beginDraw();
		mask.background(255);
		mask.fill(0);
		mask.stroke(0);
		mask.strokeWeight(1);
		if(roundCorners)
			mask.rect(0, 0, width-2, height-2, CORNER_RADIUS);
		else 
			mask.rect(0, 0, width-2, height-2);
		mask.endDraw();
		hotspots = new HotSpot[]{
				new HSmask(1, mask)		// control surface
		};

		// Initialise text and icon alignment
		PAD = roundCorners ? 4 : 2;
		textAlignH = GAlign.CENTER;
		textAlignV = GAlign.MIDDLE;
		iconPos = GAlign.EAST;
		iconAlignH = GAlign.CENTER;
		iconAlignV = GAlign.MIDDLE;
		calcZones(false, true);
		setText(text);	

		z = Z_SLIPPY;
		
		// Now register control with applet
		createEventHandler(G4P.sketchWindow, "handleButtonEvents", 
				new Class<?>[]{ GButton.class, GEvent.class }, 
				new String[]{ "button", "event" } 
				);
		registeredMethods = DRAW_METHOD | MOUSE_METHOD;
		cursorOver = HAND;
		// Must register control
		G4P.registerControl(this);
	}

	/**
	 * If the parameter is true all 3 event types PRESSED, RELEASED and CLICKED
	 * are generated, if false only CLICKED events are generated (default behaviour).
	 * @param all true for all events
	 */
	public void fireAllEvents(boolean all){
		reportAllButtonEvents = all;
	}

	/**
	 * Enable or disable the ability of the component to generate mouse events.<br>
	 * If the control is to be disabled when it is clicked then this will force the
	 * mouse off button image is used.
	 * @param enable true to enable else false
	 */
	public void setEnabled(boolean enable){
		super.setEnabled(enable);
		if(!enable)
			status = OFF_CONTROL;
	}

	/**
	 * 
	 * When a mouse button is clicked on a GButton it generates the GEvent.CLICKED event. If
	 * you also want the button to generate GEvent.PRESSED and GEvent.RELEASED events
	 * then you need the following statement.<br>
	 * <pre>btnName.fireAllEvents(true); </pre><br>
	 * 
	 * <pre>
	 * void handleButtonEvents(void handleButtonEvents(GButton button, GEvent event) {
	 *	  if(button == btnName &amp;&amp; event == GEvent.CLICKED){
	 *        // code for button click event
	 *    }
	 * </pre> <br>
	 * Where <pre><b>btnName</b></pre> is the GButton identifier (variable name) <br><br>
	 * 
	 */
	public void mouseEvent(MouseEvent event){
		if(!visible || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		currSpot = whichHotSpot(ox, oy);

		if(currSpot >= 0 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getAction()){
		case MouseEvent.PRESS:
			if(focusIsWith != this && currSpot >= 0  && z > focusObjectZ()){
				dragging = false;
				status = PRESS_CONTROL;
				takeFocus();
				if(reportAllButtonEvents)
					fireEvent(this, GEvent.PRESSED);
				bufferInvalid = true;
			}
			break;
		case MouseEvent.CLICK:
			// No need to test for isOver() since if the component has focus
			// and the mouse has not moved since MOUSE_PRESSED otherwise we 
			// would not get the Java MouseEvent.MOUSE_CLICKED event
			if(focusIsWith == this){
				status = OFF_CONTROL;
				bufferInvalid = true;
				loseFocus(null);
				dragging = false;
				fireEvent(this, GEvent.CLICKED);
			}
			break;
		case MouseEvent.RELEASE:	
			// if the mouse has moved then release focus otherwise
			// MOUSE_CLICKED will handle it
			if(focusIsWith == this && dragging){
				if(reportAllButtonEvents)
					fireEvent(this, GEvent.RELEASED);
				dragging = false;
				loseFocus(null);
				status = OFF_CONTROL;
				bufferInvalid = true;
			}
			break;
		case MouseEvent.MOVE:
			int currStatus = status;
			// If dragged state will stay as PRESSED
			if(currSpot >= 0)
				status = OVER_CONTROL;
			else
				status = OFF_CONTROL;
			if(currStatus != status)
				bufferInvalid = true;
			break;
		case MouseEvent.DRAG:
			dragging = (focusIsWith == this);
			break;
		}
	}

	public void draw(){
		if(!visible) return;

		// Update buffer if invalid
		updateBuffer();
		winApp.pushStyle();

		winApp.pushMatrix();
		// Perform the rotation
		winApp.translate(cx, cy);
		winApp.rotate(rotAngle);
		// Move matrix to line up with top-left corner
		winApp.translate(-halfWidth, -halfHeight);
		// Draw buffer
		winApp.imageMode(PApplet.CORNER);
		if(alphaLevel < 255)
			winApp.tint(TINT_FOR_ALPHA, alphaLevel);
		winApp.image(buffer, 0, 0);	
		winApp.popMatrix();		
		winApp.popStyle();
	}

	protected void updateBuffer(){
		if(bufferInvalid) {
			bufferInvalid = false;
			buffer.beginDraw();
			// Set the font and read the latest test
			Graphics2D g2d = buffer.g2;
			g2d.setFont(localFont);
			LinkedList<TextLayoutInfo> lines = stext.getLines(g2d);
			// Draw the button head
			buffer.clear();
			buffer.stroke(palette[3].getRGB());
			buffer.strokeWeight(1);
			switch(status){
			case OVER_CONTROL:
				buffer.fill(palette[6].getRGB());
				break;
			case PRESS_CONTROL:
				buffer.fill(palette[14].getRGB());
				break;
			default:
				buffer.fill(palette[4].getRGB());
			}
			// Draw button background
			if(roundCorners)
				buffer.rect(0, 0, width-2, height-2, CORNER_RADIUS);
			else
				buffer.rect(0, 0, width-2, height-2);
			
			// Now draw the Icon
			if(icon != null){
				icon.setFrame(status);
				buffer.image(icon.getFrame(), iconX, iconY);
			}
			//	Now draw the button surface (text and icon
			displayText(g2d, lines);
			buffer.endDraw();
		}	
	}
	
}
