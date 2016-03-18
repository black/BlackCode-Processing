package org.qscript.errors;

import org.qscript.Token;

/**
 * The parent class for all exceptions thrown by the QScript library classes..
 * 
 * @author Peter Lager
 *
 */
@SuppressWarnings("serial")
public abstract class ScriptException extends Exception {

	protected static final String LONG_MESSAGE = "{0} in line: {1} : at char: {2}";
	protected static final String SHORT_MESSAGE = "{0}";

	public final int lineNo;
	public final int charNo;
	public final int charWidth;
	public final ErrorType type;
	
	public ScriptException(String message) {
		super(message);
		lineNo = -1;
		charNo = -1;
		charWidth = 0;
		type = null;
	}

	/**
	 * Create a ScriptException.
	 * 
	 * @param token the token where the exception occurred (can be null for generic error)
	 * @param type the enumerated error type placeholder for additional information
	 */
	public ScriptException(Token token, ErrorType type) {
		super(Messages.build(LONG_MESSAGE, type.message, token.getLine(), token.getCharStart()));
		this.lineNo = token.getLine();
		this.charNo = token.getCharStart();
		this.charWidth = token.getCharWidth();
		this.type = type;
	}

	/**
	 * Create a ScriptException.
	 * 
	 * @param type the enumerated error type placeholder for additional information
	 */
	public ScriptException(ErrorType type){
		super(Messages.build(SHORT_MESSAGE, type.message));
		this.lineNo = -1;
		this.charNo = 0;
		this.charWidth = 0;
		this.type = type;	
	}
		
}
