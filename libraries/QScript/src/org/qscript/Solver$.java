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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.qscript.errors.EvaluationException;
import org.qscript.errors.SyntaxException;

/**
 * This class is similar to Solver in that it has static evaluate() methods to
 * evaluate an expression or script, but now done in its own thread. These methods 
 * are suitable for very complex expressions and algorithms that may block the
 * main process thread. <br>
 * Since the script is being evaluated in their own thread the result will not be
 * known until some future time. It is only when the user attempts to retrieve the
 * evaluation result that any errors have to be dealt with. <br>
 * 
 * All the evaluate methods return a Solve$ object. This object can be used to retrieve 
 * the evaluation result using either the get() or get_() methods. Calling either 
 * of these two methods will block the calling thread until the task has completed,
 * either because the evaluation ended normally, was cancelled or a syntax / 
 * evaluation error occurred. The Solve$ object can also be used (in a non-blocking way)
 * to cancel the task, see if task is done or has been cancelled. <br>
 * 
 * If the get() method is used to retrieve the result any errors are simply outputted
 * to the Java console (BASIC mode). If the get_() method is used (EXPERT mode) then 
 * the user much catch any SyntaxException or EvaluationException thrown. <br/>
 *  
 * the script has completed
 * The methods fall into two categories depending on how exceptions 
 * (initialization and runtime errors) are handled - <br>
 * 
 * @author Peter Lager
 *
 */
public final class Solver$ implements Callable<Result> {


	private static final ExecutorService pool = Executors.newCachedThreadPool();

	/**
	 * Need to call this to release resources 
	 */
	public static void shutDown(){
		if(pool != null)
			pool.shutdownNow();
	}

	/**
	 * Basic Mode: <br>
	 * Evaluate a single line of q-script.
	 * 
	 * @param line a single line of q-script
	 * @return the Solver object
	 */
	public static Solver$ evaluate(String line){
		Script script = new Script(line);
		return action(script);
	}

	/**
	 * Basic Mode: <br>
	 * Evaluate multiple lines of q-script.
	 * 
	 * @param lines an array of lines of q-script
	 * @return the Solver object
	 */
	public static Solver$ evaluate(String[] lines){
		Script script = new Script(lines);
		return action(script);
	}

	/**
	 * Basic Mode: <br>
	 * Evaluate multiple lines of q-script.
	 * 
	 * @param lines a list of lines of q-script
	 * @return the Solver object
	 */
	public static Solver$ evaluate(List<String> lines){
		Script script = new Script(lines);
		return action(script);
	}

	/**
	 * Basic Mode: <br>
	 * Evaluate a q-script.
	 * 
	 * @param script the q-script
	 * @return the Solver object
	 */
	public static Solver$ evaluate(Script script){
		return action(script);
	}



	/**
	 * Action to be taken by the static evaluate methods.
	 * 
	 * @param script the script to be evaluated
	 * @return the Solver object
	 */
	private static Solver$ action(Script script){
		Solver$ runner = new Solver$(script);
		runner.result = pool.submit(runner);
		return runner;
	}


	// ===================================================================================
	// ===================================================================================
	//
	// Class instance implementation
	//
	// ===================================================================================
	// ===================================================================================

	private Script script = null;
	private boolean valid = false;

	private Future<Result> result = null;


	/**
	 * Create the Solver$ object to evaluate the script 
	 * 
	 * @param script the script to evaluate.
	 */
	private Solver$(Script script){
		if(script != null) {
			this.script = script;
			valid = true;
		}
	}

	/**
	 * Call by the 
	 */
	@Override
	public Result call() throws SyntaxException, EvaluationException {
		Result r = null;
			if(script != null){
				if(!script.isParsed())
					script.parse_();
				script.evaluate_();
				r = script.finalResult();
			}
		return r;
	}

	/**
	 * Returns true if this is a valid Solver$ object i.e. has a script
	 */
	public boolean isValid(){
		return valid;
	}

	/**
    /**
	 * Attempts to cancel execution of this task.  This attempt will
	 * fail if the task has already completed, has already been cancelled,
	 * or could not be cancelled for some other reason. If successful,
	 * and this task has not started when <tt>cancel</tt> is called,
	 * this task should never run. <br/>
	 *
	 * After this method returns, subsequent calls to {@link #isDone} will
	 * always return true.  Subsequent calls to {@link #isCancelled}
	 * will always return true if this method returned true. <br/>
	 * 
	 * @return false if the task could not be cancelled, typically because 
	 * it has already completed normally; true otherwise
	 */
	public boolean cancel(){
		return result.cancel(true);
	}

	/**
	 * Returns true if this task has completed (for any reason)
	 */
	public boolean isDone(){
		return result.isDone();
	}

	/**
	 * Returns true if this task was cancelled before it completed
	 * normally.
	 */
	public boolean isCancelled(){
		return result.isCancelled();
	}


	// #################################################
	//            BASIC MODE
	// #################################################

	/**
	 * Get the result of the evaluation if it is not ready yet then it 
	 * will block the calling thread until the evaluation is complete.
	 * 
	 * @return the result of the evaluation.
	 */
	public Result get(){
		try {
			return result.get();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
			System.out.println(e.getCause().getMessage());
		}
		return null;
	}


	// #################################################
	//            EXPERT MODE
	// #################################################

	/**
	 * Get the result of the evaluation if it is not ready yet then it 
	 * will block the calling thread until the evaluation is complete.
	 * 
	 * @return the result of the evaluation.
	 * @throws SyntaxException 
	 * @throws EvaluationException 
	 */
	public Result get_() throws SyntaxException, EvaluationException {
		try {
			System.out.println(result.getClass().getSimpleName());
			return result.get();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
			Throwable t = e.getCause();
			if(t instanceof SyntaxException){
				System.out.println("SYNTAX");
				throw (SyntaxException)t;
			}
			else if(t instanceof EvaluationException){
				System.out.println("EVALUATION");
				throw (EvaluationException)t;
			}
			else if(t instanceof Exception){
				System.out.println(e.getCause().getMessage());
			}
		}
		return null;
	}
}
