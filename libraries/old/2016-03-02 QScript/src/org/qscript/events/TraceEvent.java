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

import org.qscript.Script;
import org.qscript.errors.ErrorType;
import org.qscript.errors.Messages;

/**
 * This event is fired after evaluating a token at runtime. It is used to trace the execution 
 * of a script and can be enabled/disabled with the script.tracOn(boolean) method.
 * 
 * 
 * @author Peter Lager
 *
 */
public class TraceEvent extends ScriptEvent {

	private static final long serialVersionUID = -5556023375062043601L;

	/**
	 * 
	 * @param script the source of the event
	 * @param etype the error type (if event caused by error)
	 * @param lineNo line number where the event was fired
	 * @param pos the position in the source code where the token starts
	 * @param width the width (chars) of the token
	 * @param extra the ParseToken just evaluated
	 */
	public TraceEvent(Script script, ErrorType etype, int lineNo, int pos, int width, Object[] extra) {
		super(script, etype, lineNo, pos, width, extra);
		message = Messages.build(f0, "Trace @ ", lineNo, pos);
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
	public TraceEvent(Script script, ErrorType etype, int lineNo, int pos, int width) {
		this(script, etype, lineNo, pos, width, null);
	}

}
