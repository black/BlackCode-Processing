/*
  Part of the G4P library for Processing 
  	http://www.lagers.org.uk/g4p/index.html
	http://sourceforge.net/projects/g4p/files/?source=navbar

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

package g4p_controls;

/**
 * This class provides an enumeration that is used to control the alignment
 * of text and images. <br>
 * 
 * It also defines the constants for the position of the icon relative to 
 * the text.
 * 
 * @author Peter Lager
 *
 */
public enum GAlign {

	INVALID			( 0x0000, "INVALID", "Invalid alignment" ),
	
	// Horizontal alignment constants
	// valid IDs 0 <= id < 15
	LEFT 			( 0x0001, "LEFT", "Left align text" ),
	CENTER 			( 0x0002, "CENTER", "Centre text horizontally" ),
	RIGHT			( 0x0004, "RIGHT", "Right align text" ),
	JUSTIFY  		( 0x0008, "JUSTIFY", "Justify text" ),
	
	// Vertical alignment constants
	// valid IDs 16 <= id < 31
	TOP 			( 0x0010, "TOP", "Align text to to top" ),
	MIDDLE	 		( 0x0020, "MIDDLE", "Centre text vertically" ),
	BOTTOM 			( 0x0040, "BOTTOM", "Align text to bottom" ),

	// Position of icon relative to text
	// valid IDs 16 <= id < 31
	SOUTH 			( 0x0100, "SOUTH", "Place icon below text" ),
	NORTH 			( 0x0200, "NORTH", "Place icon above text" ),
	WEST 			( 0x0400, "WEST", "Place icon left of text" ),
	EAST 			( 0x0800, "WEST", "Place icon right of text" );
	
	/**
	 * Get an alignment based from its textual ID.
	 * 
	 * @param textID the text ID to search for
	 * @return the alignment or INVALID if not found
	 */
	public static GAlign getFromText(String textID){
		textID = textID.toUpperCase();
		if(textID.equals("LEFT"))
			return LEFT;
		if(textID.equals("CENTER"))
			return CENTER;
		if(textID.equals("RIGHT"))
			return RIGHT;
		if(textID.equals("JUSTIFY"))
			return JUSTIFY;
		if(textID.equals("TOP"))
			return TOP;
		if(textID.equals("MIDDLE"))
			return MIDDLE;
		if(textID.equals("BOTTOM"))
			return BOTTOM;
		if(textID.equals("SOUTH"))
			return SOUTH;
		if(textID.equals("NORTH"))
			return NORTH;
		if(textID.equals("WEST"))
			return WEST;
		if(textID.equals("EAST"))
			return EAST;
		return INVALID;
	}
	
	private int alignID;
	private String alignText;
	private String description;

	/**
	 * A private constructor to prevent alignments being create outside this class.
	 * 
	 * @param id numeric ID
	 * @param text textual ID
	 * @param desc verbose description of alignment
	 */
	private GAlign(int id, String text, String desc ){
		alignID = id;
		alignText = text;
		description = desc;
	}
	
	
	/**
	 * @return the textual ID of this alignment.
	 */
	public String getTextID(){
		return alignText;
	}
	
	/**
	 * @return the textual verbose description of this alignment e.g. "Right align text"
	 */
	public String getDesc(){
		return description;
	}
	
	/**
	 * Is this a horizontal alignment constant?
	 * @return true if horizontally aligned else false.
	 */
	public boolean isHorzAlign(){
		return (alignID & 0x000F) != 0;
	}
	
	/**
	 * Is this a vertical alignment constant?
	 * @return true if vertically aligned else false.
	 */
	public boolean isVertAlign(){
		return (alignID & 0x00F0) != 0;
	}
	
	/**
	 * @return true is this is an icon alignment constant
	 */
	public boolean isPosAlign(){
		return (alignID & 0x0F00) != 0;
	}
	
	/**
	 * @return a full description of this alignment constant
	 */
	public String toString(){
		return "ID = " + alignText + " {" + alignID + "}  " + description;
	}
	
}
