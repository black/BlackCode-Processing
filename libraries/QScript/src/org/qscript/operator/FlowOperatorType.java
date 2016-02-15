/*
 * Copyright (c) 2014 Peter Lager
 * <quark(a)lagers.org.uk> http:www.lagers.org.uk
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented;
 * you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such,
 * and must not be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 */

package org.qscript.operator;

/**
 * Enumeration of flow operator types identifying those which go together.
 * 
 * @author Peter Lager
 *
 */
public enum FlowOperatorType {
	IF (	"IF", 		1, 	Location.START,	true ),
	ELSE (	"ELSE", 	1, 	Location.INTER, false ),
	ENDIF (	"ENDIF", 	1, 	Location.END, 	false ),
	WHILE (	"WHILE", 	2, 	Location.START, true  ),
	WEND (	"WEND", 	2, 	Location.END, 	false ),
	REPEAT ("REPEAT", 	3, 	Location.START, false ),
	UNTIL (	"UNTIL", 	3, 	Location.END,	true ),
	END (	"END", 		4, 	Location.ALONE,	false );
	
	//OTHER (	"OTHER", 	0, 	Location.NONE,	false ); // This last one is for everything else.


	private final String token;
	private final int group;
	private final Location position;
	// TRUE		If this operator has a boolean condition that condition evaluates to true
	// and this should cause a jump then this is true
	private boolean conditionAttached = false;

	private FlowOperatorType(String token, int group, Location loc, boolean conditionAttached){
		this.token = token;
		this.group = group;
		this.position = loc;
		this.conditionAttached = conditionAttached;
	}

	/**
	 * Is this a start flow construct?
	 */
	public boolean isSTART(){
		return position == Location.START;
	}

	/**
	 * Is this an intermediate flow construct?
	 */
	public boolean isINTER(){
		return position == Location.INTER;
	}

	/**
	 * Is this an end flow construct?
	 */
	public boolean isEND(){
		return position == Location.END;
	}

	/**
	 * Is this a stand-alone flow construct?
	 */
	public boolean isALONE(){
		return position == Location.ALONE;
	}

	public boolean hasConditionalTest(){
		return conditionAttached;
	}

	/**
	 * Determines whether the supplied flow construct is a valid end construct for this one.
	 * @param possibleEnd flow construct to test
	 * @return true if valid
	 */
	public boolean isMatchingStartFor(FlowOperatorType possibleEnd){	
		if(group == possibleEnd.group && position.location < possibleEnd.position.location)
			return true;
		else
			return false;
	}

	/**
	 * Determines whether the supplied flow construct is a valid start construct for this one.
	 * @param possibleStart flow construct to test
	 * @return true if valid
	 */
	public boolean isMatchingEndFor(FlowOperatorType possibleStart){	
		if(group == possibleStart.group && position.location > possibleStart.position.location)
			return true;
		else
			return false;
	}

	/**
	 * Get the text used to identify this construct in the algorithm.
	 * @return token text e.g. "IF", "REPEAT" etc.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Enumeration of relative positions.
	 */
	private enum Location {
		ALONE(0), START(1), INTER(2), END(3);

		private final int location;

		private Location(int value){
			this.location = value;
		}
	}
}
