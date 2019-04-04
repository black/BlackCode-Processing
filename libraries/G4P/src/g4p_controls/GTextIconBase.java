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
import java.awt.font.TextLayout;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PImage;


/**
 * 
 * This is the base class for all controls that can display text and/or icon i.e. GButton, GLabel, GCheckbox and GOption. <br>
 * 
 * It allows the user to control the position of the icon relative to the text and also their alignment. <br>
 * 
 * <b>Icon Position</b><br>
 * Use the <b>setIconPos(GAlign pos);</b> where <b>pos</b> is either 
 * <pre> GAlign.NORTH, GAlign.SOUTH, GAlign.EAST or GAlign.WEST </pre> 
 * to position the icon relative to the text. <br><br>
 * The icon will reserve just enough space vertically (NORTH/SOUTH) or horizontally (EAST/WEST) to display 
 * itself. The <b>setIconAlign(GAlign horz, GAlign vert);</b> can be used to align the icon within its space so
 * usable combinations are <br><br>
 * 
 * <pre>
 * <b>Icon positions         Usable Icon Alignment </b>
 * NORTH, SOUTH           LEFT, CENTER, RIGHT 
 * EAST, WEST             TOP, MIDDLE, BOTTOM
 * </pre>
 * 
 * Specifying unusable icon alignments will be accepted but will have no visual effect on the control e.g. <br>
 * <pre>
 * control.setIconPos(GAlign.NORTH);  // Icon above text
 * control.setIconAlign(GAlign.TOP);  // Instruction is accepted but has no visual effect
 * </pre> <br>
 * 
 * <i>This is new to V4.0.6 replaces the GTextAlign and GTextIconAlignBase classes</i><br>
 * 
 * @author Peter Lager
 * 
 */
public abstract class GTextIconBase extends GTextBase {

	protected float PAD = 2;
	protected float GUTTER = 4;

	protected Zone textZone = new Zone();
	// Alignment within zone
	protected GAlign textAlignH = GAlign.LEFT;
	protected GAlign textAlignV =  GAlign.MIDDLE;

	protected Zone iconZone = new Zone();
	// Alignment within zone
	protected GAlign iconAlignH = GAlign.CENTER;
	protected GAlign iconAlignV =  GAlign.MIDDLE;

	// Icon. The position is set when we set the icon
	protected GIcon icon = null;
	protected int iconW = 0, iconH = 0;
	protected float iconX, iconY;


	// Position of icon with respect to text. Only valid values are
	// NORTH, SOUTH, EAST or WEST
	protected GAlign iconPos = GAlign.WEST;

	/**
	 * Base class for controls able to display text and icons.
	 * 
	 * @param theApplet  the main sketch or GWindow control for this control
	 * @param p0 x position based on control mode
	 * @param p1 y position based on control mode
	 * @param p2 x position or width based on control mode
	 * @param p3 y position or height based on control mode
	 */
	public GTextIconBase(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	/**
	 * Determines if this control has an icon and/or text then recalculates the 
	 * size / position of the text and icon zones,
	 */
	protected void calcZones(){
		boolean hasIcon = icon != null;
		boolean hasText = stext.length() != 0;
		calcZones(hasIcon, hasText);
	}

	/**
	 * Determines whether this control has an icon then recalculates the 
	 * size / position of the text and icon zones. The current text length 
	 * is zero (i.e. no text) then a parameter value of true will force the 
	 * calculation to assume there is some text. <br>
	 * 
	 * Useful to prevent errors when adding text to an icon only control.
	 * 
	 * @param hasText this control has text to display
	 */
	protected void calcZones(boolean hasText){
		boolean hasIcon = icon != null;
		calcZones(hasIcon, hasText);
	}

	/**
	 * This is the implementation method for recalculating the size / position of the text and icon zones,
	 * 
	 * @param hasIcon this control has an icon
	 * @param hasText this control has text to display
	 */
	protected void calcZones(boolean hasIcon, boolean hasText){		
		fixIconZone(hasIcon, hasText);		// Fix icon zone position and size. Also fix alignment of icon in zone
		fixTextZone(hasIcon, hasText);		// Fix text zone
		bufferInvalid = true;
	}

	/**
	 * Set the position for the icon relative to the text.
	 * 
	 * @param icon_pos GAlign.NORTH, SOUTH, EAST or WEST
	 */
	public void setIconPos(GAlign icon_pos){
		if(icon_pos != null && icon_pos.isPosAlign()){
			iconPos = icon_pos;
			calcZones();
		}
	}

	/**
	 * Change the alignment of an existing icon.
	 * 
	 * You can pass <b>null</b> if you don't want to change a particular alignment. 
	 * 
	 * @param horz horizontal alignment
	 * @param vert vertical alignment
	 */
	public void setIconAlign(GAlign horz, GAlign vert){
		if(icon != null){
			if(horz != null && horz.isHorzAlign() ) {
				iconAlignH = horz;
			}
			if(vert != null && vert.isVertAlign()){
				iconAlignV = vert;
			}
			calcIconPosInZone();
			bufferInvalid = true;
		}
	}

	/**
	 * Set the <b><i>non-animated</i></b> icon to be used, keeping the current icon alignment and position.  <br>
	 * 
	 * @param fname name of the icon image file
	 * @param nbrImages number of tiles in the image
	 */
	public void setIcon(String fname, int nbrImages){
		PImage iconImage = ImageManager.loadImage(winApp, fname);
		GIcon gicon = new GIcon(winApp, iconImage, nbrImages, 1);
		implSetIcon(gicon, null, null, null);
	}

	/**
	 * Set the <b><i>non-animated</i></b> icon to be used and the horizontal and/or vertical icon alignment. 
	 * Use the constants in GAlign e.g. <b>GAlign.LEFT</b> <br>
	 * 
	 * You can pass <b>null</b> if you don't want to change a particular alignment.   <br>
	 * 
	 * @param fname name of the icon image file
	 * @param nbrImages number of tiles in the image
	 * @param horz GAlign.LEFT CENTER or RIGHT
	 * @param vert GAlign.TOP, MIDDLE, BOTTOM
	 */
	public void setIcon(String fname, int nbrImages, GAlign horz, GAlign vert){
		PImage iconImage = ImageManager.loadImage(winApp, fname);
		GIcon gicon = new GIcon(winApp, iconImage, nbrImages, 1);
		implSetIcon(gicon, null, horz, vert);
	}

	/**
	 * Set the <b><i>non-animated</i></b> icon to be used and the horizontal and/or vertical icon alignment. 
	 * Use the constants in GAlign e.g. <b>GAlign.LEFT</b> <br>
	 * 
	 * You can pass <b>null</b> if you don't want to change a particular position/alignment.   <br>
	 * 
	 * @param fname name of the icon image file
	 * @param nbrImages number of tiles in the image
	 * @param pos GAlign.NORTH, SOUTH, EAST or WEST
	 * @param horz GAlign.LEFT, CENTER or RIGHT
	 * @param vert GAlign.TOP, MIDDLE, BOTTOM
	 */
	public void setIcon(String fname, int nbrImages, GAlign pos, GAlign horz, GAlign vert){
		PImage iconImage = ImageManager.loadImage(winApp, fname);
		GIcon gicon = new GIcon(winApp, iconImage, nbrImages, 1);
		implSetIcon(gicon, pos, horz, vert);
	}

	/**
	 * Set the <b><i>non-animated</i></b> icon to be used and the horizontal and/or vertical icon alignment. 
	 * Use the constants in GAlign e.g. <b>GAlign.LEFT</b> <br>
	 * 
	 * You can pass <b>null</b> if you don't want to change a particular position/alignment.   <br>
	 * 
	 * @param img the icon
	 * @param nbrImages number of tiles in the image
	 * @param horz GAlign.LEFT, CENTER or RIGHT
	 * @param vert GAlign.TOP, MIDDLE, BOTTOM
	 */
	public void setIcon(PImage img, int nbrImages, GAlign horz, GAlign vert){
		GIcon gicon = new GIcon(winApp, img, nbrImages, 1);
		implSetIcon(gicon, null, horz, vert);
	}

	/**
	 * Set the <b><i>non-animated</i></b> icon to be used for this control. <br> 
	 * Use the constants in GAlign e.g. <b>GAlign.LEFT</b> <br>
	 * 
	 * 
	 * You can pass <b>null</b> if you don't want to change a particular position/alignment.   <br>
	 * 
	 * @param img the icon
	 * @param nbrImages number of tiles in the image
	 * @param pos GAlign.NORTH, SOUTH, EAST or WEST
	 * @param horz GAlign.LEFT, CENTER or RIGHT
	 * @param vert GAlign.TOP, MIDDLE, BOTTOM
	 */
	public void setIcon(PImage img, int nbrImages, GAlign pos, GAlign horz, GAlign vert){
		GIcon gicon = new GIcon(winApp, img, nbrImages, 1);
		implSetIcon(gicon, pos, horz, vert);
	}

	/**
	 * Set the icon to be used for this control. <br> 
	 * Use the constants in GAlign e.g. <b>GAlign.LEFT</b> <br>
	 * 
	 * This is the only method that allows you to use an animated icon. Create and configure the icon before adding 
	 * to this control. You can use the getIcon() method to access its methods.
	 * 
	 * You can pass <b>null</b> if you don't want to change a particular position/alignment.   <br>
	 * 
	 * @param g_icon the icon to use
	 * @param pos GAlign.NORTH, SOUTH, EAST or WEST
	 * @param horz GAlign.LEFT, CENTER or RIGHT
	 * @param vert GAlign.TOP, MIDDLE, BOTTOM
	 */
	public void setIcon(GIcon g_icon, GAlign pos, GAlign horz, GAlign vert){
		implSetIcon(g_icon, pos, horz, vert);
	}

	/**
	 * Implementation method for all setIcon(...) methods.<br> 
	 * Use the constants in GAlign e.g. <b>GAlign.LEFT</b> <br>
	 * 
	 * You can pass <b>null</b> if you don't want to change a particular position/alignment. 
	 * 
	 * @param g_icon the icon
	 * @param pos GAlign.NORTH, SOUTH, EAST or WEST
	 * @param horz GAlign.LEFT, CENTER or RIGHT
	 * @param vert GAlign.TOP, MIDDLE, BOTTOM
	 */
	protected void implSetIcon(GIcon g_icon, GAlign pos, GAlign horz, GAlign vert){
		// If the supplied icon is being used then make a copy
		this.icon = (g_icon.owner != null) ? g_icon.copy() : g_icon;
		// Set the owner so it can mark it invalid during animation
		icon.owner = this;
		iconW = icon.width;
		iconH = icon.height;
		// We have loaded the image so validate alignment
		if(horz != null && horz.isHorzAlign())
			iconAlignH = horz;
		if(vert != null && vert.isVertAlign())
			iconAlignV = vert;
		if(pos != null && pos.isPosAlign())
			iconPos = pos;
		calcZones();
		bufferInvalid = true;
	}

	/**
	 * If an animated icon (GAnimIcon) is associated with this control then it is returned 
	 * otherwise it returns null. <br>
	 * 
	 * This is used to access the icon if we want to animate it e.g.
	 * <pre>control.getIcon().animate();</pre>
	 * @return the icon if any
	 */
	public GAnimIcon getIcon(){
		return icon.me();
	}

	/**
	 * Set the horizontal and/or vertical text alignment. Use the constants in GAlign 
	 * e.g. <b>GAlign.LEFT</b> <br>
	 * 
	 * If you want to set just one of these then pass null in the other 
	 * 
	 * @param horz GAlign.LEFT, CENTER, RIGHT or JUSTIFY
	 * @param vert GAlign.TOP, MIDDLE, BOTTOM
	 */
	public void setTextAlign(GAlign horz, GAlign vert){
		if(horz != null && horz.isHorzAlign()){
			textAlignH = horz;
			stext.setJustify(textAlignH == GAlign.JUSTIFY);
		}
		if(vert != null && vert.isVertAlign()){
			textAlignV = vert;
		}
		bufferInvalid = true;
	}

	/**
	 * Combines setting the text and text alignment in one method. <br>
	 * 
	 * If you want to set just one of the alignments then pass null 
	 * in the other.
	 * 
	 * @param text the text to display
	 * @param horz GAlign.LEFT, CENTER, RIGHT or JUSTIFY
	 * @param vert GAlign.TOP, MIDDLE, BOTTOM
	 */
	public void setText(String text, GAlign horz, GAlign vert){
		if(text == null)
			text = "";
		// If there is no existing text then we need to calculate the size of the text zone
		if(stext.length() == 0)
			calcZones(true);
		stext.setText(text, (int) textZone.w);
		// If we have no more text than make the icon zone fill the control
		if(stext.length() == 0)
			calcZones();
		setTextAlign(horz, vert);
		bufferInvalid = true;
	}

	/**
	 * Set the text to be displayed and calculate the wrap length taking into
	 * account any icon set.
	 * 
	 * @param text the text to display
	 */
	public void setText(String text){
		setText(text, null, null);
	}

	// ####################################################################################################
	// Internal convenience methods for calculating icon and text zone values.
	// ####################################################################################################

	/**
	 * Default implementation for displaying the control's text. Child classes may override this if needed.
	 */
	protected void displayText(Graphics2D g2d, LinkedList<TextLayoutInfo> lines){
		float sx = 0, tw = 0;
		// Get vertical position of text start based on alignment
		float textY;
		switch(textAlignV){
		case TOP:
			textY = 0;
			break;
		case BOTTOM:
			textY = textZone.h - stext.getTextAreaHeight();
			break;
		case MIDDLE:
		default:
			textY = (textZone.h - stext.getTextAreaHeight()) / 2;
		}
		// Now translate to text start position
		buffer.translate(textZone.x, textZone.y + textY);
		// Now display each line
		for(TextLayoutInfo lineInfo : lines){
			TextLayout layout = lineInfo.layout;
			buffer.translate(0, layout.getAscent());
			switch(textAlignH){
			case CENTER:
				tw = layout.getVisibleAdvance();
				tw = (tw > textZone.w) ? tw - textZone.w : tw;
				sx = (textZone.w - tw)/2;
				break;
			case RIGHT:
				tw = layout.getVisibleAdvance();
				tw = (tw > textZone.w) ? tw - textZone.w : tw;
				sx = textZone.w - tw;
				break;
			case LEFT:
			case JUSTIFY:
			default:
				sx = 0;		
			}
			// display text
			g2d.setColor(palette[2]);
			layout.draw(g2d, sx, 0);
			buffer.translate(0, layout.getDescent() + layout.getLeading());	
		}
	}

	// ####################################################################################################
	// Internal convenience methods for calculating icon and text zone values.
	// ####################################################################################################

	/**
	 * This method should be called whenever the icon alignment is changed.
	 */
	private void calcIconPosInZone(){
		//		if(icon != null) {
		switch(iconAlignH){
		case LEFT:
			iconX = iconZone.x;
			break;
		case CENTER:
			iconX = iconZone.x + (iconZone.w - iconW)/2;
			break;
		case RIGHT: // Default
		default:
			iconX = iconZone.x + iconZone.w - iconW;
			break;
		}
		switch(iconAlignV){
		case TOP:
			iconY = iconZone.y;
			break;
		case BOTTOM:
			iconY = iconZone.y + iconZone.h - iconH;
			break;
		case MIDDLE: // Default
		default:
			iconY = iconZone.y + (iconZone.h - iconH)/2;
		}
		bufferInvalid = true;
		//		}
	}


	private void fixTextZone(boolean hasIcon, boolean hasText){
		if(!hasIcon){ // No icon so MAX text zone
			textZone.x = PAD;
			textZone.y = PAD;
			textZone.w = width - 2 * PAD;
			textZone.h = height - 2 * PAD;
			stext.setWrapWidth((int) textZone.w); 
		}
		else {  // We have an icon!
			// Text Zone
			switch(iconPos){
			case WEST:
				textZone.x = iconZone.w + PAD + GUTTER;
				textZone.y = PAD;
				textZone.w = width - iconZone.w - 2 * PAD - GUTTER;
				textZone.h = height - 2 * PAD;
				break;
			case EAST:
				textZone.x = PAD;
				textZone.y = PAD;
				textZone.w = width - iconZone.w - 2 * PAD - GUTTER;
				textZone.h = height - 2 * PAD;
				break;
			case SOUTH:
				textZone.x = PAD;
				textZone.y = PAD;
				textZone.w = width - 2 * PAD;
				textZone.h = height - iconZone.h - 2 * PAD - GUTTER;
				break;
			case NORTH:
				textZone.x = PAD;
				textZone.y = iconZone.h + PAD + GUTTER;			
				textZone.w = width - 2 * PAD;
				textZone.h = height - iconZone.h - 2 * PAD - GUTTER;
				break;
			default:
				break;
			}
		}
		if(hasText) stext.setWrapWidth((int) textZone.w); 
	}

	private void fixIconZone(boolean hasIcon, boolean hasText){
		if(!hasIcon){   // No icon so MIN icon zone
			iconZone.clear();
			return;
		}
		if(hasText){    // Has text so use the least amount of space depending on icon position
			switch(iconPos){
			case WEST:
				iconZone.x = PAD;
				iconZone.y = PAD;
				iconZone.w = iconW;
				iconZone.h = PApplet.max(iconH, height - 2 * PAD);
				break;
			case EAST:
				iconZone.x = width - iconW - PAD;
				iconZone.y = PAD;
				iconZone.w = iconW;
				iconZone.h = PApplet.max(iconH, height - 2 * PAD);
				break;
			case SOUTH:
				iconZone.x = PAD;
				iconZone.y = height - iconH - PAD;
				iconZone.w = PApplet.max(iconW, width - 2 * PAD);
				iconZone.h = iconH;
				break;
			case NORTH:
				iconZone.x = PAD;
				iconZone.y = PAD;			
				iconZone.w = PApplet.max(iconW, width - 2 * PAD);
				iconZone.h = iconH;
				break;
			default:
				break;
			}
		}
		else { // No text so MAX icon zone
			// Text zone MAX
			iconZone.x = PAD;
			iconZone.y = PAD;
			iconZone.w = width - 2 * PAD;
			iconZone.h = height - 2 * PAD;
		}
		calcIconPosInZone();
	}

	/**
	 * Simple class to used to define text and icon zones in this type of control
	 * @author Peter Lager
	 *
	 */
	class Zone {

		public float x, y, w, h;

		public Zone(){
			x = y = w = h = 0;
		}

		public Zone(float x, float y, float w, float h) {
			super();
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		public void clear(){
			x = y = w = h = 0;
		}

		public String toString(){
			return "Zone " + x + "  " + y + "  " + w + "  " + h;
		}

	}
}
