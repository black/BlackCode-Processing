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
 * This is a singleton class used to compile Algorithms.
 * 
 * @author Peter Lager
 *
 */
final class CompileAlgorithm implements Opcodes, CompileConstants {

	static final boolean SHOW_INFIX = false;
	static final boolean SHOW_AST = false;
	static final boolean SHOW_TRACE = false;

	static final int COMMENT	 	= 0;
	static final int WHITESPACE 	= 1;
	static final int FLOAT1 		= 2;
	static final int FLOAT2 		= 3;
	static final int INT 			= 4;
	static final int VARIABLE 		= 5;
	static final int OPERATOR 		= 6;

	static CompileAlgorithm instance = null;

	OperatorSet operatorSet = null;
	Pattern[] patterns;

	/**
	 * Get the singleton instance.
	 */
	static CompileAlgorithm getInstance(){
		if(instance == null){
			instance = new CompileAlgorithm();
		}
		return instance;
	}

	/**
	 * This private constructor gets the operator set to be used and creates the 
	 * regular expressions used during parsing.
	 */
	private CompileAlgorithm(){
		operatorSet = OperatorSet.getOperatorSet(OperatorSet.ALGORITHM);
		patterns = new Pattern[7];
		// 		COMMENT 		( "[#]+.*" ),

		patterns[COMMENT] 		=	Pattern.compile("^(" + "[#]+.*" + ")"); 
		patterns[WHITESPACE] 	=	Pattern.compile("^(" + "\\s+" + ")"); 
		patterns[FLOAT1] 		=	Pattern.compile("^(" + "[0-9]*\\.[0-9]*([Ee][+-]?[0-9]+)?" + ")"); 
		patterns[FLOAT2] 		=	Pattern.compile("^(" + "[0-9]+([Ee][+-]?[0-9]+)+" + ")"); 
		patterns[INT] 			=	Pattern.compile("^(" + "[0-9]+" + ")"); 
		patterns[VARIABLE] 		=	Pattern.compile("^(" + "[a-zA-Z][a-zA-Z0-9_]*" + ")"); 
		patterns[OPERATOR] 		= 	operatorSet.getPattern();
	}



	/* ###################################################################################################
	 * ###################################################################################################
	 * 
	 * 			COMPILE ALGORITHM
	 * 
	 * ###################################################################################################
	 * ###################################################################################################
	 */


	String join(String[] lines, String s){
		StringBuilder sb = new StringBuilder(lines[0]);
		for(int i = 1; i < lines.length; i++){
			sb.append(s);
			sb.append(lines[i]);
		}
		return sb.toString();
	}
	/**
	 * Create and return the Algorithm.
	 * 
	 * @param lines the Algorithm to compile
	 * @param profileMe if true record the evaluation time
	 * @return the Algorithm object or null
	 * @throws JasmineException
	 */
	Algorithm getAlgorithm(String[] lines, boolean profileMe) throws JasmineException {
		long buildTime = System.nanoTime();

		HashMap<String, Double> vars = new HashMap<String, Double>(32);
		Deque<JumpLabel> jumpStack = new ArrayDeque<JumpLabel>(128);
		List<Exp> infix = new ArrayList<Exp>(1024);

		// Parse to infix
		for(int i = 0; i < lines.length; i++){
			infix.addAll(parse(i,lines[i],vars));
			infix.add(operatorSet.getExp(";"));
		}
		if(SHOW_INFIX)
			Compile.printExpns("Infix order", infix);

		// Convert to AST
		Deque<Exp> astExp = convertToAST(infix);

		if(SHOW_AST){
			int n = 0;
			for(Exp exp : astExp){
				Compile.printAST("Statement " + n, exp);
				n++;
			}
		}
		// Create a class than extends Algorithm
		// >>> class header
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		//                          class to make       parent class           Interfaces
		cw.visit(V1_6, ACC_PUBLIC, DUMMY_ALGORITHM_CLASS, null, ALGORITHM_CLASS, null );

		// >>> Default public constructor
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		// Call super()
		mv.visitMethodInsn(INVOKESPECIAL, ALGORITHM_CLASS, "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// >>> eval() method 
		mv = cw.visitMethod(ACC_PUBLIC, "eval", "([Ljava/lang/Object;)" + ALGORITHM_CLASS_ID, null, null);
		// Initialize timer if we are profiling
		if(profileMe){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
			mv.visitFieldInsn(PUTFIELD, ALGORITHM_CLASS, "__evalTime", "J");
		}
		// Parse parameters
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, ALGORITHM_CLASS, "initVariables", "([Ljava/lang/Object;)V", false);

		JumpLabel jl_stop = new JumpLabel(JumpLabel.STOP);
		jumpStack.addLast(jl_stop);

		// Compile the AST 
		try {
			for(Exp exp : astExp)
				exp.compile(mv, jumpStack);
		} catch (JasmineException e) {
			throw e;
		}

		// Location to come to if the 'stop' instruction is executed
		mv.visitLabel(jl_stop.label);

		// Store evaluation time if profiling
		if(profileMe){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(DUP);
			mv.visitFieldInsn(GETFIELD, ALGORITHM_CLASS, "__evalTime", "J");
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
			mv.visitInsn(LSUB);
			mv.visitInsn(LNEG);
			mv.visitFieldInsn(PUTFIELD, ALGORITHM_CLASS, "__evalTime", "J");
		}

		// Return this
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ARETURN);

		Algorithm algorithm = null;	
		try {
			// max stack and max locals automatically computed
			mv.visitMaxs(0, 0);
			mv.visitEnd();
			// >>> Get byte Code
			byte[] b = cw.toByteArray();			

			if(SHOW_TRACE)
				Compile.printTraceCode(b);

			// Define and instantiate an Expression class
			MyClassLoader loader = new MyClassLoader();
			Class<?> algorClass = loader.defineClass(DUMMY_ALGORITHM_CLASS, b);
			algorithm = (Algorithm) algorClass.newInstance();
			// Store build time
			algorithm.__buildTime = System.nanoTime() - buildTime;
			algorithm.__vars = vars;
		} catch (InstantiationException e) {
			throw new JasmineException(ErrorType.UNKNOWN_ERROR);
		} catch (IllegalAccessException e) {
			throw new JasmineException(ErrorType.UNKNOWN_ERROR);
		} catch (Exception e) {
			throw new JasmineException(e);
		}
		return algorithm;
	}


	/* ###################################################################################################
	 * ###################################################################################################
	 * 
	 * 			PARSE LINE TO INFIX
	 * 
	 * ###################################################################################################
	 * ###################################################################################################
	 */

	/**
	 * Parse an expression and return a list of tokens in infix order
	 */
	private List<Exp> parse(int lineNo, String line, HashMap<String, Double> vars) throws JasmineException {
		List<Exp> expList = new ArrayList<Exp>(512);
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
					curr = getExp(operatorSet, type, fragment, vars);
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
						curr.lineNo = lineNo;
						curr.pos = pos;
						expList.add(curr);
						prev = curr;
					}
					pos += fragment.length();
				}
			}
			if (!match) {
				throw new JasmineException(ErrorType.INVALID_CHARACTERS, fragment, lineNo, pos);
			}
		}
		return expList;
	}

	/**
	 * Create a token from a matched sequence of characters. If the characters are 'spaces' then return null.
	 */
	private Exp getExp(OperatorSet exprs, int type, String sequence, HashMap<String, Double> vars){
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
				vars.put(sequence, new Double(0));
				exp = new GlobalVar(sequence);
			}
			break;
		case WHITESPACE:
		case COMMENT:
		}
		return exp;
	}


	/* ###################################################################################################
	 * ###################################################################################################
	 * 
	 * 			CONVERT INFIX TO AST
	 * 
	 * ###################################################################################################
	 * ###################################################################################################
	 */

	/**
	 * Convert the infix list to AST (abstract syntax tree)
	 */
	private Deque<Exp> convertToAST(List<Exp> infix) throws JasmineException{
		Deque<Exp> operatorStack = new ArrayDeque<Exp>(16);
		Deque<Exp> operandStack = new ArrayDeque<Exp>(16);

		//List<Exp> astExp =  new ArrayList<Exp>();
		Deque<Exp> astExp =  new ArrayDeque<Exp>(infix.size());

		Exp exp, popped;
		Exp expError;

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
				if(expClass == Separator.class || expClass == Then.class){
					while(!operatorStack.isEmpty()){
						addNode(operandStack, operatorStack.pop());
					}
					if(!operandStack.isEmpty()){
						astExp.add(operandStack.pop());
					}
					if(operandStack.size() > 0) {
						expError = astExp.peekLast();
						if(expError.exps.length > 0)
							expError = expError.exps[expError.exps.length-1];
						throw new JasmineException(ErrorType.MALFORMED_STATEMENT,  expError);
					}
					continue main;
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
		if(!operandStack.isEmpty()){
			astExp.add(operandStack.pop());
		}
		return astExp;
	}

	/**
	 * Add a token to the AST
	 */
	private void addNode(Deque<Exp> operandStack, Exp operator) throws JasmineException{
		int n = operator.nbrExps;
		while(n > 0 && !operandStack.isEmpty()){
			operator.addExp(operandStack.pop());
			n--;
		}
		while(n-- > 0){
			operator.addExp(new NOP());
		}
		operandStack.push(operator);
	}

}
