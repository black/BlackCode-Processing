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
 * This is a type of 'argument' but has an identifier which can be 
 * used in an algorithm to access the value. <br>
 * In version 1 the variable identifier had to start with a $ symbol but
 * this is no longer the case with this version. <br>
 * 
 * @author Peter Lager
 *
 */
public class Variable extends Argument {

	// Variable name
	protected final String identifier;
	
	private Variable() {
		super(null);
		identifier = "";
	}
	
	/**
	 * Create a variable with a given identifier.
	 * 
	 * @param identifier variable name
	 */
	public Variable(String identifier) {
		super(null);
		this.identifier = identifier.toString();
	}

	/**
	 * Create a variable with a given identifier and initialise with
	 * the specified value.
	 * 
	 * @param identifier variable name
	 * @param value the initial value
	 */
	public Variable(String identifier, Object value) {
		super(value);
		this.identifier = identifier.toString();
	}

	/**
	 * Make a copy of the variable
	 * @param v the variable to copy
	 */
	public Variable(Variable v) {
		super(v.getValue());
		this.identifier = v.identifier;
	}

	/**
	 * Get the name (identifier) of this variable
	 */
	public String getIdentifier(){
		return identifier;
	}

	/**
	 * Returns false
	 */
	@Override
	public boolean isArgument() {
		return false;
	}

	/**
	 * Returns true
	 */
	@Override
	public boolean isVariable() {
		return true;
	}

	/**
	 * Used in Script and Parser classes when listing
	 */
	public String forListing(){
		Object s = getValue();
		return identifier + " [" + (s == null ? "null" : s.toString()) + "]";	
	}
	
	/**
	 * Returns a String representation of the identifier and current value (null if invalid).
	 */
	public String toString(){		
		Object s = getValue();
		return (s == null) ? "null" : s.toString();
	}
}
