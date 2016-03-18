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

package org.qscript;

/**
 * This is the base class for operators and arguments. <br>
 * The attributes of this class are used to store the location
 * and size of the 'characters' in the original text so they can
 * to highlight errors and tokens if the script is being traced.
 * 
 * @author Peter Lager
 *
 */
public abstract class Token {

	public static final Token DUMMY = new Argument(null);
	
	protected int line;
	protected int charStart;
	protected int charWidth;
	
	/**
	 * Default ctor
	 */
	protected Token(){
		line = -1;
		charStart = -1;
		charWidth = -1;
	}
	
	/**
	 * Used during parsing to record the text position of the ParseToken
	 * in the script.
	 * 
	 */
	public void setTextPosition(int charStart, int charWidth){
		line = 0;
		this.charStart = charStart;
		this.charWidth = charWidth;
	}
	
	public void setTextPosition(int line, int charStart, int charWidth) {
		this.line = line;
		this.charStart = charStart;
		this.charWidth = charWidth;
	}
	
	/**
	 * Get the line number for this token
	 */
	public int getLine() {
		return line;
	}
	
	/**
	 * Get the start position in the script line for this token
	 */
	public int getCharStart() {
		return charStart;
	}

	/**
	 * Get the number of characters used by this token in the script line
	 */
	public int getCharWidth() {
		return charWidth;
	}

	public boolean isArgument() { return false; }
	public boolean isFlowAction() { return false; }
	public boolean isVariable() { return false; }
	public boolean isThing() { return false; }
	public boolean isOperator() { return false; }
	public boolean isFlowOperator() { return false; }

	/**
	 * Used in Script and Parser classes when listing
	 */
	public String forListing(){
		return toString();	
	}


}
