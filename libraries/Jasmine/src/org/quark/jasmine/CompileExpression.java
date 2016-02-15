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

package org.quark.jasmine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 * This is a singleton class used to compile Expressions.
 * 
 * @author Peter Lager
 *
 */
final class CompileExpression implements Opcodes, CompileConstants {

	static final boolean SHOW_INFIX = false;
	static final boolean SHOW_AST = false;
	static final boolean SHOW_TRACE = false;

	static final int WHITESPACE = 0;
	static final int FLOAT1 = 1;
	static final int FLOAT2 = 2;
	static final int INT = 3;
	static final int VARIABLE = 4;
	static final int OPERATOR = 5;

	static CompileExpression instance = null;

	OperatorSet operatorSet = null;
	protected Pattern[] patterns;

	/**
	 * Get the singleton instance.
	 */
	static CompileExpression getInstance(){
		if(instance == null){
			instance = new CompileExpression();
		}
		return instance;
	}

	/**
	 * This private constructor gets the operator set to be used and creates the 
	 * regular expressions used during parsing.
	 */
	private CompileExpression(){
		operatorSet = OperatorSet.getOperatorSet(OperatorSet.EXPRESSION);
		patterns = new Pattern[6];
		patterns[WHITESPACE] =	Pattern.compile("^(" + "\\s+" + ")"); 
		patterns[FLOAT1] =		Pattern.compile("^(" + "[0-9]*\\.[0-9]*([Ee][+-]?[0-9]+)?" + ")"); 
		patterns[FLOAT2] =		Pattern.compile("^(" + "[0-9]+([Ee][+-]?[0-9]+)+" + ")"); 
		patterns[INT] =			Pattern.compile("^(" + "[0-9]+" + ")"); 
		patterns[VARIABLE] =	Pattern.compile("^(" + "[a-zA-Z][a-zA-Z0-9_]*" + ")"); 
		patterns[OPERATOR] = 	operatorSet.getPattern();
	}



	/* ###################################################################################################
	 * ###################################################################################################
	 * 
	 * 			COMPILE EXPRESSION
	 * 
	 * ###################################################################################################
	 * ###################################################################################################
	 */

	/**
	 * Create and return the Expression.
	 * 
	 * @param lines the Expression to compile
	 * @param profileMe if true record the evaluation time
	 * @return the Expression object or null
	 * @throws JasmineException
	 */
	Expression getExpression(String line, boolean profileMe) throws JasmineException {
		List<Exp> expList;
		Exp exp = null;
		long buildTime = System.nanoTime();

		// Parse line and build AST
		try {
			expList = parse(line);
			if(SHOW_INFIX)
				Compile.printExpns("Infix order", expList);
			exp = convertToAST(expList);
			if(SHOW_AST)
				Compile.printAST("Expression", exp);
		} catch (JasmineException e) {
			System.out.println(e);
			return null;
		}
		// Create a class than extends Expression
		// >>> class header
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		//                          class to make       parent class           Interfaces
		cw.visit(V1_6, ACC_PUBLIC, DUMMY_EXPRESSION_CLASS, null, EXPRESSION_CLASS, null );

		cw.visitField(ACC_PUBLIC, "m", "D",  null,  null).visitEnd();
		// CREATE FIELDS

		// >>> Default public constructor
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		// Call super()
		mv.visitMethodInsn(INVOKESPECIAL, EXPRESSION_CLASS, "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();

		// >>> eval() method 
		mv = cw.visitMethod(ACC_PUBLIC, "eval", "([D)"+EXPRESSION_CLASS_ID, null, null);
		// Initialize timer if we are profiling
		if(profileMe){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
			mv.visitFieldInsn(PUTFIELD, EXPRESSION_CLASS, "__evalTime", "J");
		}

		// Compile code for eval()
		try {
			if(exp != null)
				exp.compile(mv, null);
		}
		catch(JasmineException e){
			throw e;
		}

		// Store evaluation time if profiling
		if(profileMe){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(DUP);
			mv.visitFieldInsn(GETFIELD, EXPRESSION_CLASS, "__evalTime", "J");
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
			mv.visitInsn(LSUB);
			mv.visitInsn(LNEG);
			mv.visitFieldInsn(PUTFIELD, EXPRESSION_CLASS, "__evalTime", "J");
		}
		// Convert double to Double
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
		// Get the answer field
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, EXPRESSION_CLASS, "__answer", ANSWER_CLASS_ID);
		// Change stack to ... o, v
		mv.visitInsn(SWAP);
		// Save result in answer
		mv.visitMethodInsn(INVOKEVIRTUAL, ANSWER_CLASS, "setValue", "(Ljava/lang/Object;)V", false);

		// Setup and return the answer
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ARETURN);

		Expression expression = null;
		try{ 
			// max stack and max locals automatically computed
			mv.visitMaxs(0, 0);
			mv.visitEnd();
			// >>> Get byte Code
			byte[] b = cw.toByteArray();			

			if(SHOW_TRACE)
				Compile.printTraceCode(b);

			// Define and instantiate an Expression class
			MyClassLoader loader = new MyClassLoader();
			Class<?> expClass = loader.defineClass(DUMMY_EXPRESSION_CLASS, b);
			expression = (Expression) expClass.newInstance();
			// Store builtime
			expression.__buildTime = System.nanoTime() - buildTime;
		} catch (InstantiationException e) {
			throw new JasmineException(ErrorType.UNKNOWN_ERROR);
		} catch (IllegalAccessException e) {
			throw new JasmineException(ErrorType.UNKNOWN_ERROR);
		} catch (Exception e) {
			throw new JasmineException(e);
		}
		return expression;
	}


	/* ###################################################################################################
	 * ###################################################################################################
	 * 
	 * 			PARSER
	 * 
	 * ###################################################################################################
	 * ###################################################################################################
	 */

	/**
	 * Parse an expression and return a list of tokens in infix order
	 */
	private List<Exp> parse(String line) throws JasmineException {
		List<Exp> expList = new ArrayList<Exp>(512);
		HashMap<String, Integer> indices = new HashMap<String, Integer>();

		String fragment = "";
		Exp prev = null, curr = null;
		Class<? extends Exp> currClass;
		boolean lastIsUnary = false;
		int pos = 0;

		while (!line.equals("")) {
			boolean match = false;
			for (int type = 0; type < patterns.length; type++) {
				Matcher m = patterns[type].matcher(line);
				if (m.find()) {
					match = true;
					fragment = m.group();
					line = m.replaceFirst("");
					curr = getExp(operatorSet, type, fragment, indices);
					if(curr != null) {
						currClass = curr.getClass();
						if(currClass == Add.class || currClass == Sub.class){
							lastIsUnary = prev == null || prev.unaryFollows;
						}
						else if(lastIsUnary && (currClass == Cst.class || currClass == LocalVar.class)){
							// Unary operator exists so remove it
							prev = expList.remove(expList.size() - 1);
							// Apply if unary
							if(prev.getClass() == Sub.class)
								((Operand)curr).applyUnaryMinus();
							// Clear flag
							lastIsUnary = false;
						}
						curr.pos = pos;
						expList.add(curr);
						prev = curr;
					}
					pos += fragment.length();					
				}
			}
			if (!match) {
				throw new JasmineException(ErrorType.INVALID_CHARACTERS, fragment, 0, pos);
			}
		}
		return expList;
	}

	/**
	 * Create a token from a matched sequence of characters. If the characters are 'spaces' then return null.
	 */
	private Exp getExp(OperatorSet exprs, int type, String sequence, HashMap<String, Integer> indices){
		Exp exp = null;
		switch(type){
		case OPERATOR:
			exp = exprs.getExp(sequence);
			break;
		case FLOAT1:
		case FLOAT2:
		case INT:
			exp = new Cst(Double.parseDouble(sequence));
			break;
		case VARIABLE:
			// If the variable name starts with the name of an expression  then
			// check that it is not an operator 
			if(exprs.expExists(sequence))
				exp = exprs.getExp(sequence);
			else {
				Integer arrayIndex = indices.get(sequence);
				if(arrayIndex == null){
					arrayIndex = indices.size();
					indices.put(sequence, arrayIndex);
				}
				exp = new LocalVar(sequence, arrayIndex);
			}
			break;
		case WHITESPACE:
		}
		return exp;
	}


	/* ###################################################################################################
	 * ###################################################################################################
	 * 
	 * 			CONVERT TO AST
	 * 
	 * ###################################################################################################
	 * ###################################################################################################
	 */

	/**
	 * Convert the infix list to AST (abstract syntax tree)
	 */
	private Exp convertToAST(List<Exp> infix) throws JasmineException{
		Deque<Exp> operatorStack = new ArrayDeque<Exp>(128);
		Deque<Exp> operandStack = new ArrayDeque<Exp>(128);

		Exp exp, popped;;
		Class<? extends Exp> expClass;

		main:
			for(int i = 0; i < infix.size(); i++){
				exp = infix.get(i);
				expClass = exp.getClass();
				if(expClass == LParen.class){
					operatorStack.push(exp);
					continue;
				}
				if(expClass == RParen.class){
					while(!operatorStack.isEmpty()){
						popped = operatorStack.pop();
						if(popped.getClass() == LParen.class){
							continue main;
						}
						else {
							addNode(operandStack, popped);
						}
					}
					throw new JasmineException(ErrorType.UNMATCHED_PARENTHESIS, exp); // no matching left parenthesis
				}
				if(expClass == Comma.class){
					while(!operatorStack.isEmpty()){
						popped = operatorStack.pop();
						if(popped.getClass() == LParen.class){
							operatorStack.push(popped);
							continue main;
						}
						else if(popped.getClass() == Comma.class){
							continue main;
						}
						else {
							addNode(operandStack, popped);
						}
					}
					throw new JasmineException(ErrorType.INVALID_COMMA_SEP, exp); // comma outside ( )
				}
				// Operand (constant or variable)
				if(exp instanceof Operand){
					operandStack.push(exp);
					continue;
				}
				// All other operators
				Exp exp2;
				while(!operatorStack.isEmpty()){
					exp2 = operatorStack.peek();
					if((!exp.rightAssociative && exp.priority == exp2.priority) || exp.priority < exp2.priority){
						operatorStack.pop();
						addNode(operandStack, exp2);
					}
					else {
						break;
					}
				} // end while
				operatorStack.push(exp);
			}  // End for loop

		// Empty the operator stack
		while(!operatorStack.isEmpty()){
			addNode(operandStack, operatorStack.pop());
		}
		return operandStack.pop();
	}

	/**
	 * Add a token to the AST
	 */
	private void addNode(Deque<Exp> operandStack, Exp operator) throws JasmineException{
		int n = operator.nbrExps;
		if(operandStack.size() >= n){
			while(n-- > 0){
				operator.addExp(operandStack.pop());
			}
			operandStack.push(operator);
		}
		else {
			throw new JasmineException(ErrorType.INVALID_NBR_OPERANDS, operator);
		}
	}

}

