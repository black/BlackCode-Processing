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

package org.qscript.operator;

import org.qscript.Argument;
import org.qscript.Script;
import org.qscript.Token;
import org.qscript.Vector;
import org.qscript.errors.EvaluationException;

public class AngleBetweenFunction extends Operator {

	public AngleBetweenFunction(String symbol, int nbrArgs, int priority,
			int type) {
		super(symbol, nbrArgs, priority, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Argument resolve(Script script, Token token, Argument[] args, Object... objects) throws EvaluationException {
		Argument a0 = args[0];
		Argument a1 = args[1];
		Argument a2 = args[2];
		if(a0.isVector && a1.isVector && a2.isVector){
			Vector v0 = (Vector)a0.getValue();
			Vector v1 = (Vector)a1.getValue();
			Vector v2 = (Vector)a2.getValue();

			double v1x = v1.x - v0.x, v1y = v1.y - v0.y, v1z = v1.z - v0.z; 
			double v2x = v2.x - v0.x, v2y = v2.y - v0.y, v2z = v2.z - v0.z; 

			double dot = v1x * v2x + v1y * v2y + v1z * v2z;
			double v1mag = Math.sqrt(v1x * v1x + v1y * v1y + v1z * v1z);
			double v2mag = Math.sqrt(v2x * v2x + v2y * v2y + v2z * v2z);
			// This should be a number between -1 and 1, since it's "normalized"
			double amt = dot / (v1mag * v2mag);
			// But if it's not due to rounding error, then we need to fix it
			if (amt <= -1) {
				return new Argument(Math.PI);
			} else if (amt >= 1) {
				return new Argument(0.0);
			}
			return new Argument(Math.acos(amt));
		}

		// If we get here then the arguments are invalid
		handleInvalidArguments(script, token);
		return null;
	}
	

}
