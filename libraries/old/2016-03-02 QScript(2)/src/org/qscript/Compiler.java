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

import java.util.LinkedList;
import java.util.Stack;

import org.qscript.errors.ErrorType;
import org.qscript.errors.SyntaxException;
import org.qscript.events.SyntaxErrorEvent;
import org.qscript.operator.FlowOperator;
import org.qscript.operator.Operator;

/**
 * This class is used to create an 'executable' list of steps (tokens) that 
 * can be evaluated by the Script class.
 * 
 * @author Peter Lager
 *
 */
class Compiler {

	/**
	 * Take a list of tokens in postfix order and create a list of ScriptStep(s)
	 * to be evaluated. It examines all the flow operators and creates appropriate 
	 * jump links.<br>
	 * 
	 * @param script the script object to be evaluated
	 * @param postfix tokens in postfix order.
	 * @return list of ScriptStep(s) for evaluation
	 * @throws SyntaxException  if the script is empty (no tokens) or unable to match all the flow operators
	 */
	static LinkedList<ScriptStep> compile(Script script, LinkedList<Token> postfix) throws SyntaxException {
		LinkedList<ScriptStep> steps = new LinkedList<ScriptStep>();
		if(postfix.isEmpty()) {
			script.fireEvent(SyntaxErrorEvent.class,  ErrorType.EMPTY_SCRIPT, Token.DUMMY);
			script.throwSyntaxException(ErrorType.EMPTY_SCRIPT, Token.DUMMY);
			// method will exit by throwing exception
		}

		// Create runtime step list for further processing
		steps = new LinkedList<ScriptStep>();
		for(Token token : postfix){
			ScriptStep step = new ScriptStep(token);
			if(!steps.isEmpty()){
				step.back = steps.peekLast();
				step.back.next = step;
			}
			steps.addLast(step);
		}

		// Find matching flow operators
		boolean error = false;
		Stack<ScriptStep> flowStack = new Stack<ScriptStep>();
		Stack<ScriptStep> endStatementStack = new Stack<ScriptStep>();

		FlowOperator currFop;
		ScriptStep currStep = steps.get(0);
		Token currToken;

		for(int i = 0; i < steps.size(); i++) {
			currStep = steps.get(i);
			currToken = currStep.token;
			currStep.pos = i;
			// It it is not an operator then process next step
			if(!currToken.isOperator())
				continue;

			// If is is an end of statement operator remember it.
			if(((Operator) currToken).getSymbol().equals(";")){
				endStatementStack.push(currStep);
				continue;
			}

			// It it is not a flow operator then process next step
			if(!currToken.isFlowOperator())
				continue;

			// To get here it must be a flow operator
			currFop = (FlowOperator) currStep.token;
			if(currFop.fop_type.isALONE()) { // START
				continue;
			}
			if(currFop.fop_type.isSTART()) { // START
				// Remember on stack
				flowStack.push(currStep);
				// See if we have a conditional test if yes then change previous to start of test
				currStep.back = endStatementStack.isEmpty() ? steps.getFirst() : endStatementStack.pop().next;
				continue;
			}
			// INTER or END
			// If the stack is empty then we have an error
			if(flowStack.isEmpty()) {
				error = true;
				break;
			} 
			// Take the last flow operator off the stack
			ScriptStep backStep = flowStack.pop();
			FlowOperator backFop = (FlowOperator) backStep.token;

			// If we don't have a matching START then we have an error
			if(!backFop.isMatchingStartFor(currFop)) {
				error = true;
				break;
			}
			backStep.forward = currStep.next;
			currStep.back = (backFop.fop_type.hasConditionalTest()) ? backStep.back : backStep.next;
			if(currFop.fop_type.isINTER()){
				flowStack.push(currStep);
			}
		} // End of steps loop

		if(error || !flowStack.isEmpty()){
			script.fireEvent(SyntaxErrorEvent.class,  ErrorType.UNMATCHED_FLOW_OPERATOR, currStep.token);
			script.throwSyntaxException(ErrorType.UNMATCHED_FLOW_OPERATOR, currStep.token);
		}
		return steps;
	}


}
