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
 * Exception for all errors throw by this library.
 * 
 * @author Peter Lager
 *
 */
public final class JasmineException extends Exception {
	
	private static final long serialVersionUID = -5497913404255822984L;
	
	public final Exception other;
	public final ErrorType type;
	public final String symbol;
	public final int lineNo;
	public final int pos;
	
	
	JasmineException(ErrorType type, String symbol, int lineNo, int pos) {
		super(type.message);
		this.other = null;
		this.type = type;
		this.symbol = symbol;
		this.lineNo = lineNo;
		this.pos = pos;
	}
	
	JasmineException(ErrorType type) {
		this(type, "", 0, 0);
	}
	
	JasmineException(ErrorType type, String extra) {
		this(type, extra, 0, 0);
	}
	
	JasmineException(ErrorType type, Exp exp) {
		this(type, exp.symbol, exp.lineNo, exp.pos);
	}
	
	public JasmineException(Exception e) {
		super(e.getMessage());
		this.other = e;
		this.type = null;
		this.symbol = "";
		this.lineNo = 0;
		this.pos = 0;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder(type.message);
		if(symbol.length() > 0)
			sb.append("   '" + symbol + "'");
		sb.append("\n\tin line " + lineNo + " position " + pos);
		return sb.toString();
	}

}
