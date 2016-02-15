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

package org.qscript;

import java.util.List;

import org.qscript.errors.EvaluationException;
import org.qscript.errors.SyntaxException;

/**
 * This class has static methods to provide a simple API for 
 * users to evaluate an expression or script in the main
 * process thread. They should only be used when the expected
 * evaluation time is to be very short. A long evaluation time
 * can make the application unresponsive. <br>
 * 
 * The methods fall into two categories depending on how exceptions 
 * (parsing and runtime errors) are handled - <br>
 * <ol>
 * <li>BASIC - Any error created during the parsing or evaluation of 
 * the q-script will cause a message on the default console output 
 * device. All basic methods return a Result object, but this is only
 * valid if no errors occur.</li>
 * <li>EXPERT - Parsing of the q-script can throw a SyntaxException 
 * error. After a successful parse the script will be evaluated and
 * an EvaluationException error maybe thrown. In this mode the user 
 * much catch these exceptions in their code. Expert methods will 
 * return a Result object <b>only</b> if the evaluation was successful
 *  i.e. no exceptions thrown.</li>
 * </ol>
 *  
 * 
 * 
 * @author Peter Lager
 *
 */
public final class Solver {

	// #################################################
	//            BASIC MODE METHODS
	// #################################################
	
	/**
	 * Basic Mode: <br>
	 * Evaluate a single line of q-script.
	 * 
	 * @param line a single line of q-script
	 * @return the result of the evaluation
	 */
	public static Result evaluate(String line){
		Script script = new Script(line);
		script.parse();
		if(script.isParsed())
			return script.evaluate();
		return null;
	}

	/**
	 * Basic Mode: <br>
	 * Evaluate multiple lines of q-script.
	 * 
	 * @param lines a list of lines of q-script
	 * @return the result of the evaluation
	 */
	public static Result evaluate(List<String> lines){
		Script script = new Script(lines);
		script.parse();
		if(script.isParsed())
			return script.evaluate();
		return null;
	}

	/**
	 * Basic Mode: <br>
	 * Evaluate multiple lines of q-script.
	 * 
	 * @param lines an array of lines of q-script
	 * @return the result of the evaluation
	 */
	public static Result evaluate(String[] lines){
		Script script = new Script(lines);
		script.parse();
		if(script.isParsed())
			return script.evaluate();
		return null;
	}

	/**
	 * Basic Mode: <br>
	 * Evaluate a q-script.
	 * 
	 * @param script a q-script
	 * @return the result of the evaluation
	 */
	public static Result evaluate(Script script){
		script.parse();
		if(script.isParsed())
			return script.evaluate();
		return null;
	}

	// #################################################
	//            EXPERT MODE METHODS
	// #################################################
	
	/**
	 * Expert Mode: <br>
	 * If you use this method then you are responsible to catch
	 * any exceptions thrown. <br>
	 * 
	 * @param line a single line of q-script
	 * @return the result of the evaluation
	 * @throws SyntaxException
	 * @throws EvaluationException
	 */
	public static Result evaluate_(String line) 
			throws SyntaxException, EvaluationException {
		Script script = new Script(line);
		return action_(script);
	}

	/**
	 * Expert Mode: <br>
	 * If you use this method then you are responsible to catch
	 * any exceptions thrown. <br>
	 * Evaluate multiple lines of q-script.
	 * 
	 * @param lines an array of lines of q-script
	 * @return the result of the evaluation
	 * @throws SyntaxException
	 * @throws EvaluationException
	 */
	public static Result evaluate_(String[] lines) 
			throws SyntaxException, EvaluationException {
		Script script = new Script(lines);
		return action_(script);
	}

	/**
	 * Expert Mode: <br>
	 * If you use this method then you are responsible to catch
	 * any exceptions thrown. <br>
	 * Evaluate multiple lines of q-script.
	 * 
	 * @param lines a list of lines of q-script
	 * @return the result of the evaluation
	 * @throws SyntaxException
	 * @throws EvaluationException
	 */
	public static Result evaluate_(List<String> lines) 
			throws SyntaxException, EvaluationException {
		Script script = new Script(lines);
		return action_(script);
	}

	/**
	 * Evaluate script action in expert mode
	 * 
	 * @throws SyntaxException 
	 * @throws EvaluationException  
	 */
	private static Result action_(Script script) 
			throws SyntaxException, EvaluationException {
		Result result = null;
		try {
			script.parse_();
			result = script.evaluate_();
		} catch (SyntaxException e) {
			throw e;
		} catch (EvaluationException e) {
			throw e;
		}
		return result;
	}

}

