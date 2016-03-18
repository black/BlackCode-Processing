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

import java.util.List;

import org.objectweb.asm.Opcodes;

/**
 * This class is the key to using Jasmine by provides several static methods
 * to create Expression and Algorithm objects which can then be evaluated. <br>
 * 
 * 
 * 
 * @author Peter Lager
 *
 */
public abstract class Compile implements Opcodes {

	public static long initTime = 0;

	/**
	 * Create an expression object. If an error is found while compiling the expression
	 * this method will return null and the error message being displayed in the 
	 * console window.
	 * 
	 * @param line the expression to be evaluated
	 * @param profileMe if true then the evaluation time will be recorded.
	 * @return the Expression object or null if an error occurred.
	 */
	public static Expression expression(String line, boolean profileMe){
		try {
			return expression_(line, profileMe);
		}
		catch (JasmineException e) {
			System.out.println(e);
			return null;
		}
	}

	/**
	 * Create an expression object. If an error is found while compiling the expression
	 * an exception is thrown.
	 * 
	 * @param line the expression to be evaluated
	 * @param profileMe if true then the evaluation time will be recorded.
	 * @return the Expression object if no error occurred.
	 */
	public static Expression expression_(String line, boolean profileMe) throws JasmineException {
		CompileExpression expression = CompileExpression.getInstance();
		return expression.getExpression(line, profileMe);
	}



	/**
	 * Create an algorithm object. If an error is found while compiling the algorithm
	 * this method will return null and the error message being displayed in the 
	 * console window.
	 * 
	 * @param line the algorithm to be evaluated
	 * @param profileMe if true then the algorithm time will be recorded.
	 * @return the Algorithm object or null if an error occurred.
	 */
	public static Algorithm algorithm(String line, boolean profileMe){
		try {
			return algorithm_(new String[] { line }, profileMe);
		} catch (JasmineException e) {
			System.out.println(e);
			return null;
		}
	}

	/**
	 * Create an algorithm object. If an error is found while compiling the algorithm
	 * an exception is thrown.
	 * 
	 * @param line the algorithm to be evaluated
	 * @param profileMe if true then the algorithm time will be recorded.
	 * @return the Algorithm object if no error occurred.
	 */
	public static Algorithm algorithm_(String line, boolean profileMe) throws JasmineException {
		return algorithm_(new String[] { line }, profileMe);
	}

	/**
	 * Create an algorithm object. If an error is found while compiling the algorithm
	 * this method will return null and the error message being displayed in the  
	 * console window.
	 * 
	 * @param lines the algorithm to be evaluated
	 * @param profileMe if true then the algorithm time will be recorded.
	 * @return the Algorithm object or null if an error occurred.
	 */
	public static Algorithm algorithm(String[] lines, boolean profileMe){
		try {
			return algorithm_(lines, profileMe);
		} catch (JasmineException e) {
			System.out.println(e);
			return null;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	/**
	 * Create an algorithm object. If an error is found while compiling the algorithm
	 * an exception is thrown.
	 * 
	 * @param lines the algorithm to be evaluated
	 * @param profileMe if true then the algorithm time will be recorded.
	 * @return the Algorithm object if no error occurred.
	 */
	public static Algorithm algorithm_(String[] lines, boolean profileMe) throws JasmineException{
		CompileAlgorithm algorithm = CompileAlgorithm.getInstance();
		return algorithm.getAlgorithm(lines, profileMe);
	}

	/**
	 * This method initializes the parser used for compiling expressions and algorithms. It is recommended 
	 * that you call this method during the startup of your application because it takes some time to 
	 * execute (depends on your hardware but unlikely to exceed 50 milliseconds). <br>
	 * If you don't call this method the initialization will be done on the first attempt to compile an
	 * expression or algorithm. <br>
	 */
	public static void init(){
		long t = System.nanoTime();
		CompileExpression.getInstance();
		CompileAlgorithm.getInstance();
		t = System.nanoTime() - t;
		if(initTime == 0) initTime = t;
		Jasmine.announce();
	}

	/* ###################################################################################################
	 * ###################################################################################################
	 * 
	 * 			PRINT OUTPUT METHODS FOR DEBUGGING
	 * 
	 * ###################################################################################################
	 * ###################################################################################################
	 */

	static void printTraceCode(byte[] b) {
//		TraceClassVisitor cv = new TraceClassVisitor(new PrintWriter(System.out));
//		ClassReader cr = new ClassReader(b);
//		cr.accept(cv, 0);
	}
	
	// Useful for printing the infix list
	static void printExpns(String title, List<Exp> tokens){
		System.out.println("-------------------------------------------------------------------");
		System.out.println(title + "\n========");
		for(Exp token : tokens){
			System.out.println(token.symbol);
		}
		System.out.println("-------------------------------------------------------------------");
	}

	// Prints the abstract syntax tree
	static void printAST(String title, Exp exp){
		System.out.println("-------------------------------------------------------------------");
		System.out.println(title + "\n========");
		printASTimpl(exp, " ");
		System.out.println("-------------------------------------------------------------------");
	}

	// Recursive method for printing AST should only be called from printAST(...)
	private static void printASTimpl(Exp exp, String tab){
		System.out.println(""+exp.lineNo+":"+exp.pos+"\t"+tab + exp.symbol);
		for(int i = 0; i < exp.exps.length; i++){
			if(exp.exps[i] != null)
				printASTimpl(exp.exps[i], tab + ".     ");
		}
	}
}
