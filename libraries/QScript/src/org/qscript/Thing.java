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
 * This class is the key to extending QScript to work with and manipulate user
 * defined clases. <br>
 * 
 * It inherits from the class Variable class so each user defined object can have
 * its own identifier so that a script can handle multiple instances of the user
 * defined class. <br>
 * 
 * 
 * @author Peter Lager
 *
 */
public final class Thing extends Variable {

	/**
	 * Prevent a null Thing
	 */
	private Thing(String identifier) {
		super(identifier);
	}

	/**
	 * Create a variable with a given identifier and initialise with
	 * the specified value.
	 * 
	 * @param identifier variable name
	 * @param value the initial value
	 */
	public Thing(String identifier, Object value) {
		super(identifier, value);
	}

	/**
	 * Make a shallow copy of the variable
	 * @param v the variable to copy
	 */
	public Thing(Thing v) {
		super(v.identifier, v.getValue());
//		this.setValue(v.getValue());
	}

	/**
	 * Returns true
	 */
	@Override
	public boolean isThing() {
		return true;
	}

	/** Returns false */
	public boolean isBoolean() { return false; 	}

	/** Returns false */
	public boolean isDouble() { return false; 	}

	/** Returns false */
	public boolean isString() { return false; 	}

	/** Returns false */
	public boolean isNumeric() { return false; 	}

	/** Returns false */
	public boolean isVector() { return false; 	}

	/** Returns false */
	public boolean isComplex() { return false; 	}


}
