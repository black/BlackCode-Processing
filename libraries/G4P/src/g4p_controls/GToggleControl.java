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

import g4p_controls.HotSpot.HSrect;
import g4p_controls.StyledString.TextLayoutInfo;

import java.awt.Graphics2D;
import java.util.LinkedList;

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
public abstract class GToggleControl extends GTextIconBase {

	protected GToggleGroup group = null;

	protected boolean selected = false;

	/**
	 * Toggle control for option and checkboxes
	 * @param theApplet  the main sketch or GWindow control for this control
	 * @param p0 x position based on control mode
	 * @param p1 y position based on control mode
	 * @param p2 x position or width based on control mode
	 * @param p3 y position or height based on control mode
	 */
	public GToggleControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
		opaque = false;
		hotspots = new HotSpot[]{
				new HSrect(1, 0, 0, width, height)		// control surface
		};
	}

	/**
	 * Set the icon to be used for this control. <br> 
	 * Use the constants in GAlign e.g. <b>GAlign.LEFT</b> <br>
	 * 
	 * This is the only method that allows you to use an animated icon. Create and configure the icon before adding 
	 * to this control. You can use the getIcon
	 * 
	 * You can pass <b>null</b> if you don't want to change a particular position/alignment.   <br>
	 * 
	 * @param g_icon the icon to use
	 * @param pos GAlign.NORTH, SOUTH, EAST or WEST
	 * @param horz GAlign.LEFT, CENTER or RIGHT
	 * @param vert GAlign.TOP, MIDDLE, BOTTOM
	 */
	public void setIcon(GIcon g_icon, GAlign pos, GAlign horz, GAlign vert){
		super.setIcon(g_icon, pos, horz, vert);
		if(icon.me() != null){
			GAnimIcon ai = (GAnimIcon)icon;
			// Do not overwrite existing animations
			if(!ai.hasClip("SELECT"))
				ai.storeAnim("SELECT", ai.anim_clip.start, ai.anim_clip.end, ai.anim_clip.interval, 1) ;
			if(!ai.hasClip("DESELECT"))
				ai.storeAnim("DESELECT", ai.anim_clip.end, ai.anim_clip.start, ai.anim_clip.interval, 1) ;
			setSelected(false);
		}
		bufferInvalid = true;
	}

	/**
	 *  This method is called if this control is added to a toggle group. A toggle group
	 *  enforces single option selection from the group. Override this with an empty method
	 *  to allow each toggle control to be independent of others.
	 * @param tg
	 */
	protected void setToggleGroup(GToggleGroup tg) {
		this.group = tg;
	}	

	/**
	 * Gets the toggle group for this control. 
	 * @return the toggle group or null if not part of a toggle group.
	 */
	public GToggleGroup getToggleGroup(){
		return group;
	}

	/**
	 * @return true if this control is selected else returns false.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Set whether this is or is not selected.
	 * @param selected mark the control as selected or not
	 */
	public void setSelected(boolean selected) {
		// Buffer is already invalid or the status has changed.
		bufferInvalid = bufferInvalid | this.selected != selected; 
		GAnimIcon ai = icon.me();
		if(ai != null){
			if(selected)
				ai.animate("SELECT");
			else
				ai.animate("DESELECT");
		}
		if(selected && group != null)
			group.makeSelected(this);
		this.selected = selected;
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
				// We have to do something if 
				// 1) It has no group e.g. GCheckbox
				// 2) If something unselected has been selected e.g. GOption 
				if(!selected || group == null){
					setSelected(!selected);
					if(selected){
						fireEvent(this, GEvent.SELECTED);
					}
					else if(group == null){
						fireEvent(this, GEvent.DESELECTED);
					}
				}
				loseFocus(null);
			}
			break;
		case MouseEvent.DRAG:
			dragging = true;
			break;
		case MouseEvent.RELEASE:
			// Release focus without firing an event - that would 
			// have been done already
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
			bufferInvalid = false;
			buffer.beginDraw();
			Graphics2D g2d = buffer.g2;
			g2d.setFont(localFont);
			// Get the latest lines of text
			LinkedList<TextLayoutInfo> lines = stext.getLines(g2d);	
			// Back ground colour
			buffer.background(opaque ? palette[6].getRGB() : palette[2].getRGB() & 0xFFFFFF | 0x00010101);
			// If there is an icon draw it
			if(icon.me() == null){
				buffer.image(icon.getFrame(selected ? 1 : 0), iconX, iconY);
			}
			else {
				buffer.image(icon.getFrame(), iconX, iconY);
			}
			//	Now draw the button surface (text and icon)
			displayText(g2d, lines);
			buffer.endDraw();
		}	
	}
}
