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
import org.qscript.Complex;
import org.qscript.Script;
import org.qscript.Token;
import org.qscript.Vector;
import org.qscript.errors.EvaluationException;

public class LerpFunction extends Operator {

	public LerpFunction(String symbol, int nbrArgs, int priority, int type) {
		super(symbol, nbrArgs, priority, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Argument resolve(Script script, Token token, Argument[] args,
			Object... objects) throws EvaluationException {
		testForUninitialisedVars(script, args);
		Argument a0 = args[0];
		Argument a1 = args[1];
		Argument a2 = args[2];
		if(a0.isNumeric && a1.isNumeric && a2.isNumeric){
			double n1 = a0.toDouble();
			double n2 = a1.toDouble();
			double t = a2.toDouble();
			return new Argument(new Double(n1 + (n2 - n1)*t));
		}
		if(a0.isVector && a1.isVector && a2.isNumeric){
			Vector v0 = (Vector)a0.getValue();
			Vector v1 = (Vector)a1.getValue();
			double t = a2.toDouble();
			return new Argument(new Vector(v0.x + (v1.x - v0.x) *t, v0.y + (v1.y - v0.y) *t, v0.z + (v1.z - v0.z) *t));
		}
		if(a0.isComplex && a1.isComplex && a2.isNumeric){
			Complex c0 = (Complex)a0.getValue();
			Complex c1 = (Complex)a1.getValue();
			double t = a2.toDouble();
			return new Argument(Complex.lerp(c0,  c1,  t));
		}
		
		handleInvalidArguments(script, token);
		return null;
	}
	
}
