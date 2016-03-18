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
import org.qscript.Variable;
import org.qscript.errors.ErrorType;
import org.qscript.errors.Messages;

/**
 * Event fired when we get an evaluation (runtime) error
 * 
 * @author Peter Lager
 *
 */
public class EvaluationErrorEvent extends ScriptEvent {


	private static final long serialVersionUID = -5872311805375631830L;

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
	public EvaluationErrorEvent(Script script, ErrorType etype, int lineNo, int pos, int width, Object[] extra) {
		super(script, etype, lineNo, pos, width, extra);
		message = makeMessage();
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
	public EvaluationErrorEvent(Script script, ErrorType etype, int lineNo, int pos, int width) {
		this(script, etype, lineNo, pos, width, null);
	}


	/**
	 * Create the message that will be returned 
	 */
	private String makeMessage(){
		if(etype == ErrorType.UNITIALISED_VARIABLE){
			String id = (extra == null || extra.length < 1) ? "#" : extra[0].toString();
			String epart = Messages.build(etype.message, id);
			System.out.println(etype.message);
			System.out.println(id);
			System.out.println(epart);
			return Messages.build(f0, epart, lineNo, pos);
		}
		if(lineNo < 0)
			return etype.message;
		return Messages.build(f0, etype.message, lineNo, pos);
	}


}

