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

import g4p_controls.StyledString.TextLayoutInfo;

import java.awt.Graphics2D;
import java.util.LinkedList;

import processing.core.PApplet;

/**
 * The label component.
 * 
 * This control can display text with or without an icon. 
 * 
 * @author Peter Lager
 *
 */
public class GLabel extends GTextIconBase {

	/**
	 * Create an empty label
	 * use setText and setIcon to change the text and icon.
	 * 
	 * @param theApplet  the main sketch or GWindow control for this control
	 * @param p0 x position based on control mode
	 * @param p1 y position based on control mode
	 * @param p2 x position or width based on control mode
	 * @param p3 y position or height based on control mode
	 */
	public GLabel(PApplet theApplet, float p0, float p1, float p2, float p3) {
		this(theApplet, p0, p1, p2, p3, "       ");
	}	
		
	/**
	 * Create a label control.
	 * 
	 * use setIcon to add an icon
	 * 
	 * @param theApplet  the main sketch or GWindow control for this control
	 * @param p0 x position based on control mode
	 * @param p1 y position based on control mode
	 * @param p2 x position or width based on control mode
	 * @param p3 y position or height based on control mode
	 * @param text the initial text to display
	 */
	public GLabel(PApplet theApplet, float p0, float p1, float p2, float p3, String text) {
		super(theApplet, p0, p1, p2, p3);
		opaque = false;
		
		// Initialise text and icon alignment
		PAD = 2;
		textAlignH = GAlign.LEFT;
		textAlignV = GAlign.MIDDLE;
		iconPos = GAlign.EAST;
		iconAlignH = GAlign.CENTER;
		iconAlignV = GAlign.MIDDLE;
		
		// Start with text only so the text zone is sized correctly
		calcZones(false, true);
		setText(text);	

		// Now register control with applet
		registeredMethods = DRAW_METHOD;
		// Must register control
		G4P.registerControl(this);
	}

	/**
	 * This will change this control's height without changing the width so that 
	 * it just fits round the text and icon (if any).
	 * 
	 * This is only for backward compatibility and maybe removed in later releases. Use
	 * the method
	 * <pre>    resizeToFit(boolean horz, boolean vert)</pre>
	 * instead.
	 */
	@Deprecated 
	public void setHeightToFit(){
		resizeToFit(false, true);
	}
	
	/**
	 * 	
	 * This will change this label's size so that it just fits round the text 
	 * and icon either horizontally, vertically or both.
	 * 
	 * @param horz adjust the width
	 * @param vert adjust the height
	 */
	public void resizeToFit(boolean horz, boolean vert){
		// Anything to do?
		if(!horz && !vert) return;
		// Update buffer to ensure we have the latest values for text size etc.
		updateBuffer();
		System.out.println(stext.getMaxLineLength() + "   " + stext.getTextAreaHeight());
		int high = Math.round(height);
		int wide = Math.round(width);
		switch(iconPos){
		case NORTH:
		case SOUTH:
			if(vert)
				high = Math.round(stext.getTextAreaHeight() + iconH + 3 * PAD);
			if(horz)
				wide = Math.round(Math.max(stext.getMaxLineLength(), iconW) + 2*PAD);
			break;
		case EAST:
		case WEST:
			if(vert)
				high = Math.round(Math.max(stext.getTextAreaHeight(), iconH) + 2 * PAD);
			if(horz)
				wide = Math.round(stext.getMaxLineLength() + iconW + 2*PAD + GUTTER);
			break;
			default:
		}
		resize(wide, high); // resize this control
		calcZones();
//		System.out.println(stext.getMaxLineLength() + "   " + stext.getTextAreaHeight());
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
			Graphics2D g2d = buffer.g2;
			// **********************************************************************************************************
			// **********************************************************************************************************
			// **********************************************************************************************************
//			if(tagNo == 1){
//				setTextRenderingHints(g2d, 3);
//			}
//			else {
//				setTextRenderingHints(g2d, 0);
//			}
			// **********************************************************************************************************
			// **********************************************************************************************************
			// **********************************************************************************************************
			g2d.setFont(localFont);

			// Get the latest lines of text
			LinkedList<TextLayoutInfo> lines = stext.getLines(g2d);	
			// Back ground colour
			buffer.background(opaque ? palette[6].getRGB() : palette[2].getRGB() & 0xFFFFFF | 0x00010101);
			
			// If there is an icon draw it
			if(icon != null){
				buffer.image(icon.getFrame(), iconX, iconY);
			}
			//	Now draw the button surface (text and icon)
			displayText(g2d, lines);
			buffer.endDraw();
		}	
	}

}
