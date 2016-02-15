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

package org.quark.jasmine;

/**
 * Enumeration of possible errors.
 * 
 * @author Peter Lager
 *
 */
public enum ErrorType {

	// Jasmine library errors
	INVALID_STOP				("Invalid stop signal"),
	MALFORMED_STATEMENT			("Syntax error near "),
	ORPHAN_ELSE					("Else statement without matching 'if'"),
	ORPHAN_ENDIF				("Endif statement without matching 'if' or 'else'"),
	ORPHAN_WEND					("Wend statement without matching 'while'"),
	ORPHAN_UNTIL				("Until statement without matching 'repeat'"),
	ORPHAN_FOREND				("Endfor statement without matching 'for'"),
	UNMATCHED_PARENTHESIS		("Unmatched parenthsis ')'"),
	UNKNOWN_ERROR				("Unable to create eval() due to unknown syntax error")	,
	LHS_VARIABLE_REQD			("LHS must be a variable"),
	INVALID_NBR_OPERANDS		("Not enough operands"),
	INVALID_COMMA_SEP			("Unexpected comma separator"),
	INVALID_CHARACTERS			("Expression contains unexpected character(s)"),
	INVALID_CAST				("Answer can not be cast to ");


	public final String message;

	private ErrorType(String message) {
		this.message = message;
	}
	
}
