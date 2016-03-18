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
import java.util.List;
import java.util.Stack;

import org.qscript.errors.ErrorType;
import org.qscript.errors.SyntaxException;
import org.qscript.events.SyntaxErrorEvent;
import org.qscript.operator.Operator;

/**
 * This class is used to convert a list of tokens in infix order to postfix order 
 * and validate it.
 * 
 * @author Peter Lager
 *
 */
class Postfix {

	private static int parenthesisDepth = 0;

	/** Prevent instantiation */
	private Postfix() {	}


	/**
	 * Convert the tokens from infix to postfix order
	 * 
	 * @param script the script object to be evaluated
	 * @param infix token in infix order
	 * @return tokens in postfix order
	 * @throws SyntaxException
	 */
	static LinkedList<Token> convert(Script script, LinkedList<Token> infix) throws SyntaxException {
		if(infix.isEmpty())
			return infix;
		LinkedList<Token> postfix = new LinkedList<Token>();
		Stack<Operator> stack = new Stack<Operator>();
		Token token = null;;
		int tn = 0;
		parenthesisDepth = 0;

		for(tn = 0; tn < infix.size(); tn++){
			token = infix.get(tn);
			if(token.isArgument() || token.isVariable()) {
				postfix.addLast(token);
				continue;
			}
			if(token.isOperator()){
				Operator op = (Operator) token;
				String symbol = op.getSymbol();
				// Left parenthesis
				if(symbol.equals("(")){
					mpf_LEFT_PARENTHESIS(script, op, postfix, stack);
					continue;
				}
				// Right parenthesis
				if(symbol.equals(")")){
					mpf_RIGHT_PARENTHESIS(script, op, postfix, stack);
					continue;
				}
				// Comma separator ,
				if(symbol.equals(",")){
					mpf_COMMA(script, op, postfix, stack);
					continue;
				}
				// Statement end ;
				if(symbol.equals(";")){
					mpf_SEMI_COLON(script, op, postfix, stack);
					continue;
				}
				// All other operators with higher priority
				int tokenPr = op.getPriority();
				while(!stack.isEmpty() && tokenPr <= stack.peek().getPriority()){
					postfix.addLast(stack.pop());
				}
				stack.push(op);
			}			
		}
		// All tokens processed add all other operators from stack
		while(!stack.isEmpty()){
			postfix.addLast(stack.pop());
		}
		if(parenthesisDepth != 0){
			script.fireEvent(SyntaxErrorEvent.class,  ErrorType.LEFT_PAREN_EXPECTED, token);
			script.throwSyntaxException(ErrorType.LEFT_PAREN_EXPECTED, token);
			//script.processError(SyntaxErrorEvent.class, SyntaxException.class, ErrorType.MISSING_PARENTHESIS, token);
		}
		//printTokens("Postfix before validation", postfix);
		
		validatePostFix(script, postfix);	
		return postfix;
	}

	/**
	 * Validate the postfix order to ensure that every operator, function etc
	 * has the correct number of arguments.
	 * @param script the script object to be evaluated
	 * @param postfix token in postfix order
	 * @throws SyntaxException
	 */
	private static void validatePostFix(Script script, LinkedList<Token> postfix) throws SyntaxException {
		LinkedList<Token> stack = new LinkedList<Token>();
		ErrorType etype = ErrorType.INVALID_ARGUMENTS;
		int nbrOperands = 0, nbrParameters = 0;
		Token errorOnToken = null, dummy;
		Operator op;
		for(Token token : postfix){
			// See if argument or variable
			if(token.isArgument() || token.isVariable()){
				nbrOperands++;
				stack.addLast(token);
				continue;
			}
			// Now sort out any operators
			if(token.isOperator() || token.isFlowOperator()){
				op = (Operator)token;
				// Check for end of statement operator
				if(op.getSymbol().equals(";")){
					// Should be just one argument/variable left on the stack
					if(stack.size() > 1 || nbrOperands > nbrParameters){
						errorOnToken = op; 
						etype = ErrorType.OPERATOR_EXPECTED; 
						//System.out.println("Postfix : 148  Op " + op);
						break;
					}
					else {
						nbrOperands = nbrParameters = 0;
						stack.clear();
						continue;
					}
				}
				int op_type = op.getOpType();
				int nbr_args_needed = op.getNbrArgs();
				nbrParameters += nbr_args_needed;
				int op_expr_pos = op.getCharStart();
				if(op_type == Operator.MARKER){
					continue;
				}
				if(op_type == Operator.CONSTANT){
					dummy = new Argument(new Integer(0));
					dummy.setTextPosition(op_expr_pos, 1);
					nbrOperands++;
					stack.addLast(dummy);
					continue;
				}
				// Infix and function types will have a number of arguments
				if(op_type == Operator.INFIX_SYMBOL || op_type == Operator.FUNCTION){
					// Check to see if we have enough args before we examine them
					// to prevent unwanted exceptions accessing the linked list
					if(nbr_args_needed > stack.size()){
						errorOnToken = op; 
						etype = ErrorType.ARGUMENT_EXPECTED; 
						//System.out.println("Postfix : 178  Op " + op); 
						break;						
					}
					//ParseToken arg;
					if(nbr_args_needed > 0){
						Token arg = stack.removeLast();
						// In the expression the last argument should appear after 
						// the operator whatever the operator type 
						if(arg.getCharStart() < op_expr_pos){
							errorOnToken = op; 
							etype = ErrorType.ARGUMENT_EXPECTED; 
							//System.out.println("Postfix : 189  Op " + op + "  Arg " + arg); 
							break;						
						}

						// Check position of arguments depending on operator type
						switch(op_type){
						case Operator.INFIX_SYMBOL:
							// in the expression arguments should precede the operator
							for(int i = 1; i < nbr_args_needed; i++){
								arg = stack.removeLast();
								if(arg.getCharStart() > op_expr_pos){
									errorOnToken = op; 
									etype = ErrorType.OPERATOR_EXPECTED; 
									//System.out.println("Postfix : 202  Op " + op + "  Arg " + arg); 
									break;						
								}							
							}
							break;
						case Operator.FUNCTION:
							// in the expression arguments should be after the operator
							for(int i = 1; i < nbr_args_needed; i++){
								arg = stack.removeLast();
								if(arg.getCharStart() < op_expr_pos){
									errorOnToken = op; 
									etype = ErrorType.INVALID_ARGUMENTS; 
									//System.out.println("Postfix : 214  Op " + op + "  Arg " + arg); 
									break;						
								}							
							}
							break;
						} // End of switch
					} // end nbr of args test
					// If we found an error stop
					if(errorOnToken != null) break; // stop on error

					// Anything else on the stack should come before the operator
					for(Token t : stack)
						if(t.getCharStart() > op_expr_pos){ 
							errorOnToken = op; 
							etype = ErrorType.OPERATOR_EXPECTED; 
							//System.out.println("Postfix : 229  Op " + op);
						}
					// If we found an error stop
					if(errorOnToken != null) break; // stop on error

					// If we get here then the operator is OK and need a dummy result
					dummy = new Argument(new Integer(0));
					dummy.setTextPosition(op_expr_pos, 1);
					stack.addLast(dummy);
					continue;
				}		
			}
		}
		if(errorOnToken != null){
			script.fireEvent(SyntaxErrorEvent.class, etype, errorOnToken);
			script.throwSyntaxException(etype, errorOnToken);
		}
		// Finally there should be just zero/one arg/var on the stack left
		if(stack.size() > 1 || nbrOperands > nbrParameters){
			script.fireEvent(SyntaxErrorEvent.class,  ErrorType.INVALID_ARGUMENTS, Token.DUMMY);
			script.throwSyntaxException(ErrorType.INVALID_ARGUMENTS, Token.DUMMY);
		}
	}
	
	private static void mpf_COMMA(Script script, Token token, LinkedList<Token> postfix, Stack<Operator> stack) throws SyntaxException {
		if(parenthesisDepth < 1){
			script.fireEvent(SyntaxErrorEvent.class,  ErrorType.INVALID_COMMA_SEP, token);
			script.throwSyntaxException(ErrorType.INVALID_COMMA_SEP, token);
		}
		while(!stack.isEmpty()){
			Operator stackOp = stack.peek();
			if(!stackOp.getSymbol().equals("("))
				postfix.addLast(stack.pop());
			else
				break;
		}
	}

	private static void mpf_LEFT_PARENTHESIS(Script script, Token token, LinkedList<Token> postfix, Stack<Operator> stack){
		parenthesisDepth++;
		stack.push((Operator)token);		
	}

	private static void mpf_RIGHT_PARENTHESIS(Script script, Token token, LinkedList<Token> postfix, Stack<Operator> stack) throws SyntaxException {
		if(--parenthesisDepth < 0){
			script.fireEvent(SyntaxErrorEvent.class,  ErrorType.UNMATCHED_PARENTHESIS, token);
			script.throwSyntaxException(ErrorType.UNMATCHED_PARENTHESIS, token);
		}
		while(!stack.isEmpty()){
			Operator stackOp = stack.pop();
			if(!stackOp.getSymbol().equals("("))
				postfix.addLast(stackOp);
			else
				break;
		}
	}

	private static void mpf_SEMI_COLON(Script script, Token token, LinkedList<Token> postfix, Stack<Operator> stack) throws SyntaxException {
		if(parenthesisDepth != 0){
			script.fireEvent(SyntaxErrorEvent.class,  ErrorType.LEFT_PAREN_EXPECTED, token);
			script.throwSyntaxException(ErrorType.LEFT_PAREN_EXPECTED, token);
		}
		while(!stack.isEmpty())
			postfix.addLast(stack.pop());
		postfix.addLast(token);
	}

	static void printTokens(String title, List<Token> tokens){
		System.out.println("-------------------------------------------------------------------");
		System.out.println(title);
		System.out.println("Line    Pos     Width    Token");
		for(Token token : tokens){
			System.out.println(token.getLine() + "\t" + token.getCharStart() + "\t" + token.getCharWidth() + "\t" + token.forListing());
		}
		System.out.println("-------------------------------------------------------------------");
	}


}



