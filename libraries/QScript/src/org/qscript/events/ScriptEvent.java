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

package org.qscript.events;

import java.util.EventObject;

import org.qscript.Script;
import org.qscript.errors.ErrorType;

/**
 * Base class for all scripting events
 * 
 * @author Peter Lager
 *
 */
public abstract class ScriptEvent extends EventObject {


	private static final long serialVersionUID = -6894909173582142609L;

	protected final String f0 = "{0}  [Line {1} : Pos {2}]";

	public final ErrorType etype;
	public final int lineNo;
	public final int pos;
	public final int width;
	public final Object[] extra;

	protected String message;

	/**
	 * The basis for all script events
	 * 
	 * @param script the source of the event
	 * @param etype the error type (if event caused by error)
	 * @param lineNo line number where the event was fired
	 * @param pos the position in the source code where the token starts
	 * @param width the width (chars) of the token
	 * @param extra this extra object is specific to this event
	 */
	public ScriptEvent(Script script, ErrorType etype, int lineNo, int pos, int width, Object[] extra) {
		super(script);
		this.etype = etype;
		this.lineNo = lineNo;
		this.pos = pos;
		this.width = width;
		this.extra = extra;
	}

	/**
	 * The basis for all script events
	 * 
	 * @param script the source of the event
	 * @param etype the error type (if event caused by error)
	 * @param lineNo line number where the event was fired
	 * @param pos the position in the source code where the token starts
	 * @param width the width (chars) of the token
	 */
	public ScriptEvent(Script script, ErrorType etype, int lineNo, int pos, int width) {
		this(script, etype, lineNo, pos,width, null);
	}

	/**
	 * Get the source of this event i.e. the script. Does the same as getScript()
	 */
	public Script getSource(){
		return (Script)source;
	}
	
	/**
	 * Get the script that fired this event.
	 */
	public Script getScript(){
		return (Script)source;
	}
	
	/**
	 * Get the event message
	 */
	public String getMessage(){
		return message;
	}
	
	public String toString(){
		return message;  
	}

}
