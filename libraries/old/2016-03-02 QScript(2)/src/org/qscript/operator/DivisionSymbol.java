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

/**
 * / (division)
 * 
 * @author Peter Lager
 *
 */
public class DivisionSymbol extends Operator {

	public DivisionSymbol(String token, int nbrArgs, int priority, int type) {
		super(token, nbrArgs, priority, type);
	}

	@Override
	public Argument resolve(Script script, Token token, Argument[] args, Object...objects) 
			throws EvaluationException {
		testForUninitialisedVars(script, args);
		Argument a0 = args[0];
		Argument a1 = args[1];
		if(a0.isNumeric && a1.isNumeric){
			if(a0.isInteger && a1.isInteger)
				return new Argument(a0.toInteger() / a1.toInteger());	
			else
				return new Argument(a0.toDouble() / a1.toDouble());
		}
		// Divide vector by scalar
		if(a0.isVector && a1.isNumeric)
			return new Argument( ((Vector)a0.getValue()).div(a1.toDouble()) );
		
//		// At least one of these must be complex if we are going to do this
//		if(a0.isComplex && a1.isComplex)
//			return new Argument( Complex.div((Complex)a0.getValue(), (Complex)a1.getValue()));
//		if(a0.isComplex && a1.isNumeric)
//			return new Argument( Complex.div((Complex)a0.getValue(), a1.toDouble()));
//		if(a0.isNumeric && a1.isComplex)
//			return new Argument( Complex.div(a0.toDouble(), (Complex)a1.getValue()));
		if(a0.isComplex && a1.isComplex)
			return new Argument( Complex.div(a0.toComplex(), a1.toComplex()));
		if(a0.isComplex && a1.isNumeric)
			return new Argument( Complex.div(a0.toComplex(), a1.toDouble()));
		if(a0.isNumeric && a1.isComplex)
			return new Argument( Complex.div(a0.toDouble(), a1.toComplex()));
		
		// If we get here then the arguments are invalid		
		handleInvalidArguments(script, token);
		return null;
	}; 

}
