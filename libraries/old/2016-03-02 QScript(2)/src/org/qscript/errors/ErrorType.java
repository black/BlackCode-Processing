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

package org.qscript.errors;

/**
 * Enumeration of possible errors.
 * 
 * @author Peter Lager
 *
 */
public enum ErrorType {

	// Parser (Syntax errors)
	SYNTAX_ERROR				(0x00010001, "Syntax error"),
	UNMATCHED_PARENTHESIS		(0x00010002, "Unmatched parenthsis ')'"),
	UNMATCHED_FLOW_OPERATOR		(0x00010003, "Cannot find matching operator"),
	INVALID_COMMA_SEP			(0x00010004, "Unexpected comma separator"),
	UNTERMINATED_STRING			(0x00010005, "Unterminated literal string"),
	LEFT_PAREN_EXPECTED			(0x00010006, "')' expected"),
	ARGUMENT_EXPECTED			(0x00010007, "Argument expected"),
	OPERATOR_EXPECTED			(0x00010008, "Operator expected"),
//	UNKNOWN_OPERATOR			(0x00010009, "Unknown Operator"),
	INVALID_ARGUMENTS			(0x0001000A, "Invalid arguments"),
	EMPTY_SCRIPT				(0x0001000F, "Nothing to evaluate"),
	
	// Evaluation errors
	SCRIPT_UNPARSED			    (0x00020010, "The script must be parsed before evaluation"),
	INVALID_ARGUMENT_TYPES		(0x00020011, "Invalid argument type(s)"),
	INVALID_CAST				(0x00020012, "Invalid cast in argument"),
	UNITIALISED_VARIABLE		(0x00020013, "Variable ({0}) must be initialised before use"),
	LHS_VARIABLE_REQD			(0x00020014, "LHS must be a variable"),

    MAX_TIME_EXCEEDED			(0x00030020, "The maximum time allowed has been exceeded"),
	STOPPED_BY_USER				(0x00030021, "The script has been stopped by the user"),

    // System errors caused by unexpected exception thrown by JVM
	FATAL_PARSE_ERROR			(0x00020030, "FATAL Java exception whilst parsing "),
	FATAL_EVAL_ERROR			(0x00020031, "FATAL Java exception during evaluation ")	;
	
	public int code;
	public String message;


	private ErrorType(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
}
