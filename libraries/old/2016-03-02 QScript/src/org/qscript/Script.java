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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.swing.Timer;

import org.qscript.errors.ErrorType;
import org.qscript.errors.EvaluationException;
import org.qscript.errors.SyntaxException;
import org.qscript.events.EvaluationErrorEvent;
import org.qscript.events.HaltExecutionEvent;
import org.qscript.events.ResumeEvent;
import org.qscript.events.ScriptEvent;
import org.qscript.events.ScriptFinishedEvent;
import org.qscript.events.StoreUpdateEvent;
import org.qscript.events.TraceEvent;
import org.qscript.eventsonfire.Events;
import org.qscript.operator.FlowOperator;
import org.qscript.operator.Operator;

/**
 * This class is responsible for managing all the actions necessary to evaluate
 * an expression or algorithm. It will use the Tokenizer, Postix and Compiler 
 * classes to create a list of ScriptStep(s) which it will then try to evaluate.
 * 
 * @author Peter Lager
 *
 */
public class Script {

	private static final int STOPPED = 1;
	private static final int RUNNING = 2;
	// By default a script has a maximum of 5 seconds to evaluate a script
	private static float DEFAULT_TIME_LIMIT = 5;

	/**
	 * Set the default time limit (seconds) for all subsequent scripts. This is used 
	 * to stop the script evaluation taking longer than expected. It is useful in the
	 * event of an infinite loop. 
	 * @param defaultTimeLimit the maximum time in seconds
	 */
	public static void defaultTimeLimit(float defaultTimeLimit){
		if(defaultTimeLimit > 1)
			DEFAULT_TIME_LIMIT = defaultTimeLimit;	
	}

	/**
	 * Get the default script evaluation time limit.
	 */
	public static double defaultTimeLimit(){
		return DEFAULT_TIME_LIMIT;
	}

	private String[] code = null;
	private LinkedList<ScriptStep> steps;
	private Stack<Argument> ongoing = new Stack<Argument>();
	private Argument[] args = new Argument[256];

	private ScriptStep pc;
	private int status = STOPPED;

	// Time limit in seconds
	private float timeLimit = DEFAULT_TIME_LIMIT;
	private int traceDelay = 200;

	private ResumeTimer resumeTimer;
	private ScriptTimer scriptTimer;

	// Modes
	private boolean traceOn = false;

	private int nbrListeners = 0;

	private Result result = null;

	private float parseTime;
	private float evalTime;

	// Used to indicate whether the script is ready for evaluation.
	private boolean parsed = false;
	private boolean paused = false;

	public float getRunTime(){
		return evalTime;
	}

	/**
	 * See if the script has been successfully parsed
	 */
	public boolean isParsed() {
		return parsed;
	}

	/**
	 * Get the delay time (in milliseconds) after token has 
	 * been parsed.
	 * @return the traceDelay
	 */
	public int traceDelay() {
		return traceDelay;
	}

	/**
	 * Set the delay (milliseconds) between processing tokens It does not
	 * determine if the trace mode is on or off, this should be done with
	 * <pre>
	 * traceModeOn()
	 * traceModeOff()
	 * </pre> 
	 * 
	 * @param delay the traceDelay (in milliseconds) to set
	 */
	public void traceDelay(int delay) {
		if(delay > 0)
			traceDelay = delay;
	}

	/**
	 * Determines whether this script should fire TraceEvents or not. <br>
	 * If you switch tracing on then the time limit is ignored because 
	 * it is assumed that you are taking control and will end evaluation 
	 * with the stop() method when needed. <br>
	 * If you are switching off tracing then the timer is reset and the
	 * time limit set equal to the default time limit. <br> 
	 * You can adjust the trace speed the with traceDelay(int) method.
	 * 
	 * @param activate use true to turn on trace events
	 */

	/**
	 * Find out whether the trace is on or not.
	 * 
	 */
	public boolean isTraceModeOn(){
		return traceOn;
	}

	/**
	 * Switches the trace mode on. This causes a delay between evaluation of 
	 * token. The delay time is not included in the script runtime so will
	 * not cause the script to time out.
	 */
	public void traceModeOn(){
		traceOn = true;
	}

	/**
	 * Switches trace mode off
	 */
	public void traceModeOff(){
		traceOn = false;
	}

	/**
	 * Sets the time limit for this script. The script will be stopped if 
	 * this time is reached.
	 * @param limit in seconds
	 */
	public void setTimeLimit(float limit){
		if(limit >= 0.5f)
			timeLimit = limit;
	}

	/**
	 * Return true of the script has stopped evaluation.
	 */
	public boolean isStopped(){
		return status == STOPPED;
	}

	/**
	 * Return true of the script has been paused during evaluation.
	 */
	public boolean isPaused(){
		return paused;
	}

	/**
	 * Is the evaluation still going on?
	 * 
	 * @return false if the script has been stopped or paused.
	 */
	public boolean isRunning() {
		return !(status == STOPPED || paused);
	}

	/**
	 * Stop the evaluation - result is undefined.
	 */
	public synchronized void stop() {
		resume();
		status = STOPPED;
	}

	/**
	 * Perform any trace delay. Only do this is the token can be highlighted.
	 */
	synchronized void performTraceDelay(Token token){
		if(token.charWidth > 0){
			fireEvent(TraceEvent.class, null, token);
			try {
				scriptTimer.pause();
				Thread.sleep(traceDelay);
				scriptTimer.resume();
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * If paused then wait for the script to be resumed.
	 */
	synchronized void performWaiting(){
		while(paused){
			try {
				scriptTimer.pause();
				wait();
				scriptTimer.resume();
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Will cause the script to pause for the specified time in milliseconds or
	 * until resume() method is called, whichever is the sooner. If the
	 * parameter time is zero then the script must be resumed by the user. <br>
	 * 
	 * The wait time is not included in the runtime so will not cause the script to timeout.
	 * 
	 * If the script is already in a paused state or has stopped then this method 
	 * does nothing. <br>
	 * It is also called by the script WAAIT operator
	 * 
	 * @param time timeout in milliseconds
	 */
	public synchronized void waitFor(int time){
		if(!paused && status == RUNNING){
			time = Math.abs(time);
			paused = true;
			if(time > 0)
				resumeTimer = new ResumeTimer(this, time);
		}
	}

	/**
	 * This will resume a paused script. Use this to resume a script from the wait state
	 */
	public synchronized void resume(){
		if(resumeTimer != null){
			resumeTimer.stop();
			resumeTimer = null;
		}
		paused = false;
		notifyAll();
	}

	public Result evaluate(){
		try {
			result = evaluate_();
		} catch (EvaluationException e) {
			System.out.println(e.getMessage());
			return new Result(e.lineNo, e.getMessage());
		}
		return result;
	}

	public Result evaluate_() throws EvaluationException {
		if(!parsed){
			fireEvent(EvaluationErrorEvent.class,  ErrorType.SCRIPT_UNPARSED, Token.DUMMY);
			throwEvaluationException(ErrorType.SCRIPT_UNPARSED, Token.DUMMY);
		}
		Argument answer = null;
		pc = steps.get(0);
		ongoing.clear();
		status = RUNNING;

		// Start the runtime timer
		scriptTimer = new ScriptTimer(timeLimit, ScriptTimer.SECONDS);
		scriptTimer.start();


		while(pc != null && status == RUNNING){
			// Get the token we are processing
			Token token = pc.token;

			// Check for timeout
			if(scriptTimer.hasTimedOut()){
				status = STOPPED;
				fireEvent(HaltExecutionEvent.class,  ErrorType.MAX_TIME_EXCEEDED, token);
				throwEvaluationException(ErrorType.MAX_TIME_EXCEEDED, token);
			}

			// Perform any waiting
			performWaiting();

			// Trace mode ???
			if(traceOn)
				performTraceDelay(token);

			if(token.isArgument()){
				ongoing.push((Argument) token);
				pc = pc.next;
				continue;
			}
			else if(token.isVariable()){
				String identifier  = ((Variable)token).getIdentifier();
				Variable var = getVariable(identifier);
				if(var == null)
					ongoing.push((Argument) token);
				else
					ongoing.push(var);
				pc = pc.next;
				continue;				
			}
			else if(token.isFlowOperator()){
				FlowOperator fop = (FlowOperator) token;
				getArgs(fop);
				FlowAction fa = (FlowAction) fop.resolve(this, fop, args);
				switch(fa.action){
				case FlowAction.BACK:
					pc = pc.back;
					break;
				case FlowAction.JUMP:
					pc = pc.forward;
					break;
				case FlowAction.NEXT:
					pc = pc.next;
					break;
				case FlowAction.END:
					pc = null;
					ongoing.push(fa.endWith);
				}
				continue;
			}
			else if(token.isOperator()){
				Operator op = (Operator)token;
				// If end of statement then reduce ongoing to the last one
				if(op.getSymbol().equals(";")){
					if(ongoing.size() > 0){
						Argument top = ongoing.pop();
						ongoing.clear();
						ongoing.push(top);
					}
					pc = pc.next;
					continue;
				}
				// All other operators end up here
				getArgs(op);
				answer = op.resolve(this, op, args);
				if(answer != null)
					ongoing.push(answer);
				pc = pc.next;
				continue;
			}
			else {
				// Should not be able to get here.
				System.out.println("Shit happens - unrecognised token");
				break;
			}

		}
		evalTime = scriptTimer.getRunTime();
		answer = ongoing.isEmpty() ? new Argument(null) : ongoing.pop();
		result = new Result(answer);

		if(status != STOPPED) {
			// script evaluation ended normally
			status = STOPPED;
			fireEvent(ScriptFinishedEvent.class, null , null);
		}
		else {
			// Script halted by user - result undefined
			fireEvent(HaltExecutionEvent.class,  ErrorType.STOPPED_BY_USER, pc.token);
			throwEvaluationException(ErrorType.STOPPED_BY_USER, pc.token);
		}

		return new Result(answer);
	}

	public Result finalResult() {
		return result;
	}

	private void getArgs(Operator op) throws EvaluationException {
		int nbr = op.getNbrArgs();
		if(nbr >= 0 && nbr <= ongoing.size()){
			while(nbr > 0){
				args[--nbr] = ongoing.pop();
			}
		}
		else {
			// If we get here then we could not get the correct number of arguments
			// This should no longer be possible because the Postfix class checks 
			// the number of operands
			System.out.println("Script getArgs() method invalid number of argumenst)");
			fireEvent(EvaluationErrorEvent.class,  ErrorType.INVALID_ARGUMENTS, op);
			throwEvaluationException(ErrorType.INVALID_ARGUMENTS, op);
		}
	}

	/**
	 * Focal point for all syntax exception throwing.
	 * 
	 * @param etype the error type
	 * @param token token where the exception occured
	 * @throws SyntaxException
	 */
	public void throwSyntaxException(ErrorType etype, Token token) throws SyntaxException { 
		Token t  = (token == null) ? Token.DUMMY : token;
		throw new SyntaxException(etype, t);
	}

	/**
	 * Focal point for all evaluation exception throwing.
	 * 
	 * @param etype the error type
	 * @param token token where the exception occured
	 * @throws SyntaxException
	 */
	public void throwEvaluationException(ErrorType etype, Token token) throws EvaluationException { 
		Token t  = (token == null) ? Token.DUMMY : token;
		throw new EvaluationException(etype, t);
	}

	// ####################################################################################################################################
	// ####################################################################################################################################
	//    EVENT FIRING & EVENT LISTENERS 
	// ####################################################################################################################################
	// ####################################################################################################################################

	/**
	 * Worker method to create and forward script events to listeners.
	 * 
	 * @param eventClass the event class we want to create
	 * @param etype the error type (if the event is caused by an error)
	 * @param token the token that is the source of the event
	 */
	public void fireEvent(Class<? extends ScriptEvent> eventClass, ErrorType etype, Token token){
		fireEvent(eventClass, etype, token, null);
	}

	/**
	 * Worker method to create and forward script events to listeners.
	 * 
	 * @param eventClass the event class we want to create
	 * @param etype the error type (if the event is caused by an error)
	 * @param token the token that is the source of the event
	 * @param extra one additional object depending on event and error types
	 */
	public void fireEvent(Class<? extends ScriptEvent> eventClass, ErrorType etype, Token token, Object extra){
		fireEvent(eventClass, etype, token, new Object[] { extra } );
	}
	
	/**
	 * Worker method to create and forward script events to listeners.
	 * 
	 * @param eventClass the event class we want to create
	 * @param etype the error type (if the event is caused by an error)
	 * @param token the token that is the source of the event
	 * @param extra additional objects depending on event and error types
	 */
	public void fireEvent(Class<? extends ScriptEvent> eventClass, ErrorType etype, Token token, Object[] extra){
		if(nbrListeners > 0){
			int line = token == null ? -1 : token.line;
			int charStart = token == null ? -1 : token.charStart;
			int charWidth = token == null ? -1 : token.charWidth;
			Constructor<? extends ScriptEvent> con;
			ScriptEvent event = null;
			try {
				if(extra == null){
					con = eventClass.getConstructor(Script.class, ErrorType.class, int.class, int.class, int.class);
					event =  con.newInstance(this, etype, line, charStart, charWidth);
				}
				else {
					con = eventClass.getConstructor(Script.class, ErrorType.class, int.class, int.class, int.class, Object[].class);
					event = con.newInstance(this, etype, line, charStart, charWidth, extra);
				}
			} catch (NoSuchMethodException e) {
				System.out.println("1 fire event error " + e.getMessage());
			} catch (SecurityException e) {
				System.out.println("2  fire event error " + e.getMessage());
			} catch (InstantiationException e) {
				System.out.println("3  fire event error " + e.getMessage());
			} catch (IllegalAccessException e) {
				System.out.println("4  fire event error " + e.getMessage());
			} catch (IllegalArgumentException e) {
				System.out.println("5  fire event error " + e.getMessage());
			} catch (InvocationTargetException e) {
				System.out.println("6  fire event error " + e.getTargetException());
			}
			// Fire asynchronous event
			Events.fire(this, event);
		}
	}


	/**
	 * Add an event listener to this script. <br>
	 * 
	 * The listener must have a method annotated with  @EventHandler this method
	 * must have a single parameter of type ScriptEvent.
	 * 
	 * @param listener the event receiver
	 * @return true if a listener has been successfully added else false.
	 */
	public boolean addListener(Object listener){
		try {
			Events.bind(this, listener);
			nbrListeners++;
			return true;
		}
		catch (IllegalArgumentException e){
			System.out.println("Unable to add listener");
		}
		return false;
	}

	/**
	 * Remove an event listener to this script. <br>
	 * 
	 * @param listener the event receiver
	 * @return true if a listener has been successfully removed else false.
	 */
	public boolean removeListener(Object listener){
		try {
			Events.unbind(this, listener);
			nbrListeners--;
			return true;
		}
		catch (IllegalArgumentException e){
			System.out.println("Unable to remove listener");
		}
		return false;
	}

	// ####################################################################################################################################
	// ####################################################################################################################################
	//    DATA STORE STUFF
	// ####################################################################################################################################
	// ####################################################################################################################################

	// The data store
	private ConcurrentSkipListMap<String, Variable> store = new ConcurrentSkipListMap<String, Variable>();

	private String lastIdentifier = "";

	public String getLastIdentifier(){
		return lastIdentifier;
	}

	/**
	 * Save a variable to the current score. Only fire an event if the script is running and
	 * returns a copy of the variable to avoid concurrency issues
	 * @param var
	 */
	public void storeVariable(Variable var){
		if(var != null){
			store.put(var.getIdentifier(), var);
			fireEvent(StoreUpdateEvent.class, null, null, new Object[] { new Variable(var) });
		}
	}

	/**
	 * Load the store with a new variable based on the provided identifier and value.
	 * The identifier will be prepended with $ if necessary.
	 * 
	 * @param identifier the identifier e.g. $x
	 * @param value the value.
	 */
	public void storeVariable(String identifier, Object value){
		Variable var = new Variable(identifier, value);
		store.put(var.getIdentifier(), var);
	}

	/**
	 * Get an array of all the variables stored in the data store. <br>
	 * This is slow because it has to be synchronised with the script. 
	 * It is better to create your own data store in the main sketch 
	 * and listen for DataStoreEvents.
	 * 
	 * @return a copy of the data store as an array
	 */
	public synchronized Variable[] getStoredVariables(){
		return store.values().toArray(new Variable[store.size()]);
	}

	/**
	 * Remove all variables from the data store
	 */
	public void clearVariables(){
		store.clear();
	}

	/**
	 * Get a simple variable based on the identifier.
	 * 
	 * @param identifier
	 * @return null if variable is not in the store
	 */
	public Variable getVariable(String identifier){
		lastIdentifier = identifier;
		return store.get(identifier);
	}

	/**
	 * Get a simple variable from the store.
	 * 
	 * @param var
	 * @return null if variable is not in the store
	 */
	public Variable getVariable(Variable var){
		lastIdentifier = var.getIdentifier();
		return store.get(lastIdentifier);
	}

	// ####################################################################################################################################
	// ####################################################################################################################################
	//   CONSTRUCTORS
	// ####################################################################################################################################
	// ####################################################################################################################################

	@SuppressWarnings("unused")
	private Script(){}

	public Script(String line) {
		setCode(line);
	}

	public Script(String[] lines) {
		setCode(lines);
	}

	public Script(List<String> lines) {
		setCode(lines);
	}

	public void parse(){
		try {
			parse_();
			parsed = true;
		} catch (SyntaxException e) {
			System.out.println("Syntax error    : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unknown error : " + e.getMessage());
		}
	}

	public void parse_() throws SyntaxException{
		parsed = false;
		long time = System.currentTimeMillis();
		LinkedList<Token> tokens = Tokenizer.tokenize(this, code);
		tokens = Postfix.convert(this, tokens);
		steps = Compiler.compile(this, tokens);
		parsed = true;
		time = System.currentTimeMillis() - time;
		parseTime = 1E-9f * time;
		//printProgram("Program", steps);
	}

	/**
	 * Get the time it took parse the script ready for evaluation
	 * @return the parse time in seconds
	 */
	public float getParseTime(){
		return parseTime;
	}

	/**
	 * Set the script lines for this script. <br>
	 * The script will need to be initialized before evaluating.
	 * 
	 * @param lines the code as a list of strings
	 */
	public void setCode(List<String> lines) {
		code = (String[]) lines.toArray(new String[lines.size()]);
		parsed = false;
	}

	/**
	 * Set the script lines for this script. <br>
	 * The script will need to be initialized before evaluating.
	 * 
	 * @param lines the code as an array of strings
	 */
	public void setCode(String[] lines) {
		code = new String[lines.length];
		for(int i = 0; i < code.length; i++)
			code[i] = lines[i];
		parsed = false;
	}

	/**
	 * Set the script line for this script. <br>
	 * The script will need to be initialized before evaluating.
	 * 
	 * @param line single line of code
	 */
	public void setCode(String line) {
		code = new String[] { line };
		parsed = false;
	}

	/**
	 * Get the lines of code for this script.
	 */
	public String[] getCode(){
		return code;
	}
	
	// ####################################################################################################################################
	// ####################################################################################################################################
	//    TIMER STUFF
	// ####################################################################################################################################
	// ####################################################################################################################################

	/**
	 * Used to time how long a script has been running.
	 * 
	 * @author Peter Lager
	 *
	 */
	public class ScriptTimer {

		static final int SECONDS = 1000000000;
		static final int MILLI = 1000000;
		static final int NANO = 1;

		private long timeLimit;

		private long usedTime;
		private long startTime;
		private boolean ticking;

		public ScriptTimer(float timeLimit, int timeUnit){
			switch(timeUnit){
			case NANO:
			case MILLI:
			case SECONDS:
				this.timeLimit =  (long) (timeLimit * timeUnit);
				break;
			default:
				this.timeLimit = Integer.MAX_VALUE;
			}
			startTime =	usedTime = 0;
			ticking = false;
		}

		/**
		 * Reset the timer
		 */
		void start(){
			startTime = System.nanoTime();
			usedTime = 0;
			ticking = true;
		}

		void pause(){
			if(ticking){
				usedTime = System.nanoTime() - startTime;
				ticking = false;
			}
		}

		void resume(){
			if(!ticking){
				startTime = System.nanoTime() - usedTime;
				usedTime = 0;
				ticking = true;
			}
		}

		/**
		 * See if the timelimit has been reached
		 */
		boolean hasTimedOut(){
			return System.nanoTime() - startTime - timeLimit >= 0;
		}

		/**
		 * Stop the timer
		 */
		void stop(){
			ticking = false;
		}

		/**
		 * Get the run time in seconds
		 */
		float getRunTime(){
			return (float)(1.0E-9 * (System.nanoTime() - startTime));
		}


	}

	private class ResumeTimer {

		private Timer timer = null;
		private Script script = null;

		public ResumeTimer(Script script, int interval){
			if(script != null && interval > 0){
				this.script = script;
				timer = new Timer(interval, new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						fireEvent();
					}

				});	
				timer.setRepeats(false);
				timer.start();
			}
		}

		public void stop(){
			if(timer != null)
				timer.stop();
		}

		private void fireEvent() {
			script.scriptRequestsResume();
			timer.stop();
		}

	}

	/**
	 * Performs the same action as resume but also fires a ResumeEvent event
	 */
	private synchronized void scriptRequestsResume(){
		if(resumeTimer != null){
			resumeTimer.stop();
			resumeTimer = null;
		}
		paused = false;
		fireEvent(ResumeEvent.class, null, null);
		notifyAll();
	}


	// ####################################################################################################################################
	// ####################################################################################################################################
	//    DEBUG STUFF
	// ####################################################################################################################################
	// ####################################################################################################################################

	public void printTokens(String title, List<Token> tokens){
		System.out.println("-------------------------------------------------------------------");
		System.out.println(title);
		System.out.println("Line    Pos     Width    Token");
		for(Token token : tokens){
			System.out.println(token.getLine() + "\t" + token.getCharStart() + "\t" + token.getCharWidth() + "\t" + token.forListing());
		}
		System.out.println("-------------------------------------------------------------------");
	}

	public void printProgram(String title, List<ScriptStep> steps){
		System.out.println("-------------------------------------------------------------------");
		System.out.println(title);
		System.out.println("S	N	B	J	Token");
		for(ScriptStep step : steps){
			System.out.print(step.pos + "\t");
			System.out.print( (step.next == null ? "-" : step.next.pos) + "\t");
			System.out.print( (step.back == null ? "-" : step.back.pos) + "\t");
			System.out.print( (step.forward == null ? "-" : step.forward.pos) + "\t");
			System.out.println(step.token.getLine() + "\t" + step.token.getCharStart() + "\t" + step.token.getCharWidth() + "\t" + step.token.forListing());
		}
		System.out.println("-------------------------------------------------------------------");
	}

}
