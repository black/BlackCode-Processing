/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2012 Peter Lager

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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.util.LinkedList;

import org.gamecontrolplus.gui.MStyledString.TextLayoutInfo;

import processing.awt.PGraphicsJava2D;
import processing.core.PApplet;
import processing.event.MouseEvent;

/**
 * This class forms the basis for any two-state type control (toggle switch). <br>
 * A toggle control can be in one of 2 states <b>selected</b> or <b>not selected</b>
 * and is the base class for the GOption and GCheckbox controls. 
 * 
 * @author Peter Lager
 *
 */
public abstract class MToggleControl extends MTextIconAlignBase {

	protected MToggleGroup group = null;

	protected boolean selected = false;

	public MToggleControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
		// The image buffer is just for the typing area
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.rectMode(PApplet.CORNER);
		buffer.g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		opaque = false;
		hotspots = new MHotSpot[]{
				new MHotSpot.HSrect(1, 0, 0, width, height)		// control surface
		};
	}

	// This method is called if this control is added to a toggle group. A toggle group
	// enforces single option selection from the group. Override this with an empty method
	// to allow each toggle control to be independant of others.
	protected void setToggleGroup(MToggleGroup tg) {
		this.group = tg;
	}	
	
	/**
	 * Get the toggle group. If null is returned then it is not part 
	 * of the group.
	 */
	public MToggleGroup getToggleGroup(){
		return group;
	}
	
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		if(this.selected != selected)
			bufferInvalid = true;
		if(selected && group != null)
			group.makeSelected(this);
		this.selected = selected;
	}

//	public void setSelected() {
//		setSelected(true);
//	}

	/*
	 * Only executed when clicked in the GUI.
	 */
	protected void hasBeenClicked(){
		if(group == null){
			// Independent action e.g. check box
			selected = !selected;
			bufferInvalid = true;
		}
		else {
			// Only need to do something if we click on an unselected option
			if(!selected)
				setSelected(true);
		}
	}
	
	public void mouseEvent(MouseEvent event){
		// If this option does not belong to a group then ignore mouseEvents
		if(!visible || !enabled || !available) return;


		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		currSpot = whichHotSpot(ox, oy);
		// currSpot == 1 for text display area
		if(currSpot >= 0 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;


		switch(event.getAction()){
		case MouseEvent.PRESS:
			if(focusIsWith != this && currSpot >= 0 && z > focusObjectZ()){
				dragging = false;
				takeFocus();
			}
			break;
		case MouseEvent.CLICK:
			if(focusIsWith == this){
				hasBeenClicked();
				loseFocus(null);
				if(selected)
					fireEvent(this, MEvent.SELECTED);
				else if(group == null)
					fireEvent(this, MEvent.DESELECTED);
			}
			break;
		case MouseEvent.DRAG:
			dragging = true;
			break;
		case MouseEvent.RELEASE:
			// Release focus without firing an event - that would have 
			// been done
			if(focusIsWith == this && dragging)
				this.loseFocus(null);
			dragging = false;
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
			buffer.beginDraw();
			Graphics2D g2d = buffer.g2;
			// Get the latest lines of text
			LinkedList<TextLayoutInfo> lines = stext.getLines(g2d);	
			bufferInvalid = false;

			// Back ground colour
			if(opaque == true)
				buffer.background(palette[6]);
			else
				buffer.background(buffer.color(255,0));
			// Calculate text and icon placement
			calcAlignment();
			// If there is an icon draw it
			if(iconW != 0)
				if(selected)
					buffer.image(bicon[1], siX, siY);
				else
					buffer.image(bicon[0], siX, siY);
			float wrapWidth = stext.getWrapWidth();
			float sx = 0, tw = 0;
			buffer.translate(stX, stY);
			for(TextLayoutInfo lineInfo : lines){
				TextLayout layout = lineInfo.layout;
				buffer.translate(0, layout.getAscent());
				switch(textAlignH){
				case CENTER:
					tw = layout.getAdvance();
					tw = (tw > wrapWidth) ? tw - wrapWidth : tw;
					sx = (wrapWidth - tw)/2;
					break;
				case RIGHT:
					tw = layout.getAdvance();
					tw = (tw > wrapWidth) ? tw - wrapWidth : tw;
					sx = wrapWidth - tw;
					break;
				case LEFT:
				case JUSTIFY:
				default:
					sx = 0;		
				}
				// display text
				g2d.setColor(jpalette[2]);
				lineInfo.layout.draw(g2d, sx, 0);
				buffer.translate(0, layout.getDescent() + layout.getLeading());
			}
			buffer.endDraw();
		}	
	}
}
