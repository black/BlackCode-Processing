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
import org.qscript.Variable;
import org.qscript.errors.ErrorType;
import org.qscript.errors.EvaluationException;
import org.qscript.errors.SyntaxException;
import org.qscript.events.EvaluationErrorEvent;

/**
 * This is the base class for all operators to be recognized by the expression parser. <br>
 * There are four operator types - <br>
 * MARKER <br>
 * These do not perform any evaluation themselves so they do not 
 * require any arguments. Examples include WEND, ELSE, comma, 
 * parenthesis. <br>
 * CONSTANT <br>
 * These are simply named values (e.g. PI, E, EOL) so they have 
 * no arguments to process but must have a high priority (40) so
 * that its value is retrieved before further evaluation. <br>
 * INFIX_SYMBOL <br>
 * An infix symbols is any symbol that sits between the two operands 
 * it has to process. These include the <br>
 * <ul>
 * <li>normal mathematical operators such as + = / * </li>
 * <li>assignment operator = </li>
 * <li>the logical operators && AND || OR </li>
 * <li>comparison operators such as == > <= != </li>
 * </ul> <br>
 * It does mean that infix symbols always require exactly two arguments. <br>
 * FUNCTION <br>
 * A function is any symbol that takes one or more arguments and 
 * evaluates them. All functions have the format <br>
 * <pre><i>symbol</i>(<i>comma separated list of arguments</i>)</pre> <br>
 * These include all the common mathematical functions
 * such as sqrt, cos, sin; but the library also has some extras such as rnd, pow <br>
 * All functions have the same priority (30) <br> <br>
 * It is possible to create user defined operators and add them to the standard set.  
 * 
 * 
 * @author Peter Lager
 *
 */
public abstract class Operator extends Token {

	public static final int MARKER       = 0;
	public static final int CONSTANT     = 1;
	public static final int INFIX_SYMBOL = 2;
	public static final int FUNCTION     = 3;

	final String symbol;
	final String regex;
	final int nbrArgs;
	final int priority;
	final private int op_type;


	/**
	 * This is the constructor used to create an operator object.
	 * 
	 * @param symbol the symbol/name that identifies this operator.
	 * @param nbrArgs the number of arguments needed by this operator
	 * @param priority the value used to determine the order of precedence
	 * @param type the operator type
	 */
	public Operator(String symbol, int nbrArgs, int priority, int type) {
		this.symbol = symbol;
		this.regex = RegexMaker.regexFromSymbol(this.symbol);
		this.nbrArgs = nbrArgs;
		this.priority = priority;
		this.op_type = type;
	}

	/**
	 * 
	 * @param script the Script object responsible for evaluation
	 * @param token the current token being evaluated
	 * @param args the arguments for this operator
	 * @param objects ignore it has been included for possible future use
	 * @return the result of this operation or null if there is no need to store the result
	 * @throws SyntaxException
	 * @throws EvaluationException
	 */
	public abstract Argument resolve(Script script, Token token, Argument[] args, Object...objects) 
			throws EvaluationException; 

	/**
	 * This method checks for uninitialized variables and should be
	 * called by the first statement in the method resolve().
	 * 
	 * The only exception is the Assignment class where the left hand
	 * operand may be an uninitialized variable. 
	 * 
	 * @param script the Script object responsible for evaluation
	 * @param args list of Arguments to check
	 * @throws SyntaxException
	 * @throws EvaluationException
	 */
	protected boolean testForUninitialisedVars(Script script, Argument[] args) throws EvaluationException {
		for(int i = 0; i < nbrArgs; i++) 
			testForUninitialisedVar(script, args[i]);
		return true;
	}

	/**
	 * This method checks to see if a variable has been initialized.
	 * 
	 * @param script the Script object responsible for evaluation
	 * @param args list of Arguments to check
	 * @return true if the variable has been initialized
	 * @throws SyntaxException
	 * @throws EvaluationException
	 */
	protected boolean testForUninitialisedVar(Script script, Argument arg) throws EvaluationException {
		if(arg.isVariable() && script.getVariable((Variable)arg) == null ) {
			script.fireEvent(EvaluationErrorEvent.class,  ErrorType.UNITIALISED_VARIABLE, arg, new Object[] { ((Variable) arg).getIdentifier() } );
			script.throwEvaluationException(ErrorType.UNITIALISED_VARIABLE, arg);
		}	
		return true;
	}


	/**
	 * Call this method if the arguments are the incorrect types for
	 * the operator
	 * @param script the Script object responsible for evaluation
	 * @param charNo
	 * @param charWidth
	 * @throws EvaluationException
	 */
	protected void handleInvalidArguments(Script script, Token token) throws EvaluationException {
		script.fireEvent(EvaluationErrorEvent.class, ErrorType.INVALID_ARGUMENT_TYPES, token);
		script.throwEvaluationException(ErrorType.INVALID_ARGUMENT_TYPES, token);
	}

	/**
	 * Get the operator identifying symbol
	 */
	public String getSymbol(){
		return symbol;
	}

	/**
	 * Get the number of arguments needed by this operator
	 */
	public int getNbrArgs(){
		return nbrArgs;
	}

	/**
	 * Get the operator priority value. This is used to determine 
	 * the order that operators are evaluated.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Get the operator type
	 */
	public int getOpType(){
		return op_type;
	}

	/**
	 * Returns true
	 */
	@Override
	public boolean isOperator() {
		return true;
	}

	/**
	 * Used in Script and Parser classes when listings
	 */
	public String forListing(){
		return toString();	
	}

	public String toString(){
		if(priority < 30 || priority >= 40)
			return symbol + " \tPr "+ priority;
		else
			return symbol + "(" + nbrArgs + ") \tPr "+ priority;
	}


}