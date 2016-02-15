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
 * This class wraps up the token together with information about where
 * to go once it has been evaluated.
 * 
 * @author Peter Lager
 *
 */
public class ScriptStep {

	public Token token = null;
	
	// The step immediately following this one
	ScriptStep next = null;

	// If we have to go back i.e. loop then this is where we go
	ScriptStep back = null;

	// If we have to go forward e.g. to skip parts of IF-ELSE-ENDIF 
	ScriptStep forward = null;
	
	public ScriptStep(Token token) {
		super();
		this.token = token;
	}
	
	// Used in algorithm debugging 
	int pos;

}
