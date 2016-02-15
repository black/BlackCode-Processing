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

import java.util.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * An abstract expression. This is the base class for all 
 * operator and operand type.
 * 
 * @author Peter Lager
 */
abstract class Exp implements Opcodes, CompileConstants {

	protected static final String MATH = "java/lang/Math";

	Exp parent = null;
	Exp[] exps;
	int nbrExps = 0, priority;

	// The regex will be calculated only if this Exp is
	// added to the Exp set. It is not needed after that.
	String regex = "";
	boolean rightAssociative = false;
	boolean unaryFollows = false;
	// Line and position in the textual representation
	int lineNo, pos;
	// The symbol that represents 
	String symbol = "";
	
	// Start filling from the end of the array so they are in the
	// correct order for the compiler
	boolean addExp(Exp exp){
		if(nbrExps > 0){
			exp.parent = this;
			exps[--nbrExps] = exp;
			return true;
		}
		return false;
	}
	
	/*
	 * Compile this expression. This method must append to the given code writer
	 * the byte code that evaluates and pushes on the stack the value of this
	 * expression.
	 */
	abstract void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException;
	

	@Override
	public boolean equals(Object obj) {
		Exp other = (Exp) obj;
		return (other == null) ? false : symbol.equals(other.symbol);
	}
	
	@Override
	public int hashCode() {
		return ((symbol == null) ? 0 : symbol.hashCode());
	}

	void prepare(String symbol, int priority){
		prepare(symbol, 0, priority);
	}
	
	/**
	 * Prepare the Exp class with initial vales. <br>
	 * 
	 * @param symbol the symol for this expression
	 * @param nbrExps number of operands needed
	 * @param priority reqd for operator precedence.
	 */
	void prepare(String symbol, int nbrExps, int priority){
		this.symbol = symbol;
		this.priority = priority;
		this.nbrExps = nbrExps;
		exps = new Exp[nbrExps];
		char c = symbol.charAt(symbol.length()-1);
		unaryFollows = "+-/*(=,^<>%&|;".indexOf(c) >= 0;
	}

}


/* ###################################################################################################
 * ###################################################################################################
 * 
 * 			Exp(s) suitable for CompileAlgorithm
 * 
 * ###################################################################################################
 * ###################################################################################################
 */

class Assign extends Exp {
	public Assign() {
		prepare("=", 2, 15);
		rightAssociative = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[1].compile(mv, jls);																				// d0, d1
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);				// D
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "org/quark/jasmine/Algorithm", "__vars", "Ljava/util/HashMap;");			// D, Map
		mv.visitInsn(SWAP);	
		if(exps[0].getClass() != GlobalVar.class)
			throw new JasmineException(ErrorType.LHS_VARIABLE_REQD, this);
		((GlobalVar)exps[0]).compileLHS(mv, jls);																// Map, D, name
		mv.visitInsn(SWAP);	 																					// Map, name, D
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);	// A
		mv.visitInsn(POP);	// ...
	}
}


/**
 * 'if' if conditional statement
 * 
 */
class If extends Exp {

	public If() {
		prepare("if", 1, 14);
		unaryFollows = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// Add IF_ELSE type label to
		JumpLabel jl_else = new JumpLabel(JumpLabel.IF_ELSE);
		jls.push(jl_else);
		// Calculate boolean expression
		exps[0].compile(mv, jls);
		mv.visitInsn(D2I);
		mv.visitJumpInsn(IFEQ, jl_else.label);
	}
}

/**
 * 'then' part of if-else-endif conditional statement
 * 
 */
class Then extends Exp {

	public Then() {
		prepare("then", 0, 14);
		unaryFollows = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		//exps[0].compile(mv, jls);
	}
}

/**
 * 'else' part of if-else-endif conditional statement
 * 
 */
class Else extends Exp {

	public Else() {
		prepare("else", 0, 14);
		unaryFollows = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// Get label off the stack if not right type throw exception
		JumpLabel jl_else = jls.isEmpty() ? null : jls.pop();
		if(jl_else == null || jl_else.type != JumpLabel.IF_ELSE)
			throw new JasmineException(ErrorType.ORPHAN_ELSE, this);
		
		// If we get here it is because we just finished the 'THEN' block
		// so we need to jump to the end
		JumpLabel jl_done = new JumpLabel(JumpLabel.IF_ENDIF);
		jls.push(jl_done);
		mv.visitJumpInsn(GOTO, jl_done.label);
	
		
		// Start of the ELSE block
		mv.visitLabel(jl_else.label);
		// The ELSE block
		//exps[0].compile(mv, jls);
	}
}

/**
 * 'endif' part of if-else-endif conditional statement
 * 
 */
class Endif extends Exp {

	public Endif() {
		prepare("endif", 0, 14);
		unaryFollows = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		JumpLabel jl = jls.isEmpty() ? null : jls.pop();
		if(jl == null || (jl.type != JumpLabel.IF_ELSE && jl.type != JumpLabel.IF_ENDIF))
			throw new JasmineException(ErrorType.ORPHAN_ENDIF, this);

		mv.visitLabel(jl.label);
		//exps[0].compile(mv, jls);
	}
}

/**
 * 'while' part of while()-wend conditional statement
 * 
 */
class While extends Exp {

	public While() {
		prepare("while", 1, 14);
		unaryFollows = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// Add WEND type label - where to go (false)
		JumpLabel jl_wend = new JumpLabel(JumpLabel.WEND);
		jls.push(jl_wend);
		// Return label from loop end - wend statement
		JumpLabel jl_while = new JumpLabel(JumpLabel.WHILE);
		jls.push(jl_while);
		
		// Where to return when we reach end of loop body
		mv.visitLabel(jl_while.label);

		// Calculate boolean expression
		exps[0].compile(mv, jls);
		
		mv.visitInsn(D2I);
		mv.visitJumpInsn(IFEQ, jl_wend.label);
	}
}

/**
 * 'wend' part of while()-wend conditional statement
 * 
 */
class Wend extends Exp {

	public Wend() {
		prepare("wend", 0, 14);
		unaryFollows = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		JumpLabel jl_while = jls.pop();
		if(jl_while.type != JumpLabel.WHILE)
			throw new JasmineException(ErrorType.ORPHAN_WEND, this);
		
		// Go back to start of loop
		mv.visitJumpInsn(GOTO, jl_while.label);
		
		// We come here when while(bool) when bool = false)
		JumpLabel jl_wend = jls.pop();
		if(jl_wend.type != JumpLabel.WEND)
			throw new JasmineException(ErrorType.ORPHAN_WEND, this);
		mv.visitLabel(jl_wend.label);

		//exps[0].compile(mv, jls);
	}
}


/**
 * 'wend' part of while()-wend conditional statement
 * 
 */
class Repeat extends Exp {

	public Repeat() {
		prepare("repeat", 0, 14);
		unaryFollows = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// Add REPAT type label - where to return to if until(false)
		JumpLabel jl_repeat = new JumpLabel(JumpLabel.REPEAT);
		jls.push(jl_repeat);
		
		// Where to return when we reach end of loop body
		mv.visitLabel(jl_repeat.label);

		// Calculate boolean expression
		//exps[0].compile(mv, jls);
	}
}

/**
 * 'wend' part of while()-wend conditional statement
 * 
 */
class Until extends Exp {

	public Until() {
		prepare("until", 1, 14);
		unaryFollows = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);

		JumpLabel jl_repeat = jls.pop();
		if(jl_repeat.type != JumpLabel.REPEAT)
			throw new JasmineException(ErrorType.ORPHAN_UNTIL, this); // Unmatched end
		mv.visitInsn(D2I);
		mv.visitJumpInsn(IFEQ, jl_repeat.label);	
	}
}

/**
 * 'for' part of for(,,)-forend conditional statement
 * Each of the three parameters must be a single statement e.g.
 * for(i = 0; i < 10; i = i + 1);
 * The 
 */
class For extends Exp {

	public For() {
		prepare("for", 3, 14);
		unaryFollows = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// Label for start of test
		Label test = new Label();
		
		Label body = new Label();
		JumpLabel jl_endfor = new JumpLabel(JumpLabel.ENDFOR);
		jls.push(jl_endfor);
		
		JumpLabel jl_for = new JumpLabel(JumpLabel.FOR);
		jls.push(jl_for);
	

		
		// Initialise part then jump post loop action
		exps[0].compile(mv, jls);
		mv.visitJumpInsn(GOTO, test);
	


		// Post increment
		mv.visitLabel(jl_for.label);
		exps[2].compile(mv, jls);

		

		// Conditional part
		mv.visitLabel(test);
		exps[1].compile(mv, jls);
		
		mv.visitInsn(D2I);
		mv.visitJumpInsn(IFEQ, jl_endfor.label);
		mv.visitJumpInsn(GOTO, body);
		
		
		mv.visitLabel(body);
	}
}
//class For extends Exp {
//
//	public For() {
//		prepare("for", 3, 14);
//		unaryFollows = true;
//	}
//
//	@Override
//	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
//		// Add WEND type label - where to go (false)
//		Label test = new Label();
//		Label body = new Label();
//		JumpLabel jl_endfor = new JumpLabel(JumpLabel.ENDFOR);
//		jls.push(jl_endfor);
//		JumpLabel jl_for = new JumpLabel(JumpLabel.FOR);
//		jls.push(jl_for);
//	
//		// Initialise part
//		exps[0].compile(mv, jls);
//	
//		
//		// Conditional part
//		mv.visitLabel(test);
//		exps[1].compile(mv, jls);
//		
//		mv.visitInsn(D2I);
//		mv.visitJumpInsn(IFEQ, jl_endfor.label);
//		mv.visitJumpInsn(GOTO, body);
//		
//		// Post increment
//		mv.visitLabel(jl_for.label);
//		exps[2].compile(mv, jls);
//		mv.visitJumpInsn(GOTO, test);
//		
//		mv.visitLabel(body);
//	}
//}

/**
 * 'fend' part of for(,,)-fend conditional statement
 * 
 */
class ForEnd extends Exp {

	public ForEnd() {
		prepare("fend", 0, 14);
		unaryFollows = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// Jump to post increment
		JumpLabel jl_for = jls.pop();
		if(jl_for.type != JumpLabel.FOR)
			throw new JasmineException(ErrorType.ORPHAN_FOREND, this);
		mv.visitJumpInsn(GOTO, jl_for.label);

		// Label to exit the loop
		JumpLabel jl_forend = jls.pop();
		if(jl_forend.type != JumpLabel.ENDFOR)
			throw new JasmineException(ErrorType.ORPHAN_FOREND, this);
		mv.visitLabel(jl_forend.label);
	}
}

/**
 * A global variable reference expression. <br>
 * Although not technically 'global' it is stored in a field
 * called __vars, a hashmap. <br.
 * 
 * 
 */
class GlobalVar extends Operand {

	public GlobalVar(final String id) {
		prepare(id, 0, 40);
	}

	@Override
	void applyUnaryMinus(){
		hasUnaryMinus = true;
	}

	void compileLHS(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException { // Assignment
		mv.visitLdcInsn(symbol);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException { // Retrieving value
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, ALGORITHM_CLASS, "__vars", "Ljava/util/HashMap;");				// Map
		mv.visitLdcInsn(symbol);																					// Map, name
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
		mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
		if(hasUnaryMinus) {
			mv.visitLdcInsn(new Double(-1));
			mv.visitInsn(DMUL);
		}
	}
}

class Print extends Exp {

	public Print() {
		prepare("print", 1, 23);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {	// Retrieving value
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		exps[0].compile(mv, jls);																// Map, name
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V", false);
	}
}



/**
 * ;  used to separate statements
 */
class Separator extends Exp {

	public Separator() {
		prepare(";", 1, -1);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
	}
}


/**
 * A constant expression.
 */
class Stop extends Exp {


	public Stop(){
		prepare("stop", 0, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		JumpLabel jl_stop = (jls.isEmpty()) ?  null : ((ArrayDeque<JumpLabel>)jls).pollLast();
		if(jl_stop == null || jl_stop.type != JumpLabel.STOP)
			throw new JasmineException(ErrorType.ORPHAN_ELSE, this);
		mv.visitJumpInsn(GOTO, jl_stop.label);
	}
}


/* ###################################################################################################
 * ###################################################################################################
 * 
 * 			Exp(s) suitable for CompileExpression
 * 
 * ###################################################################################################
 * ###################################################################################################
 */

/**
 * The 'abs' method
 */
class Abs extends Exp {

	public Abs() {
		super();
		prepare("abs", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "abs", "(D)D", false);
	}
}


/**
 * The 'acos' method
 */
class Acos extends Exp {

	public Acos() {
		super();
		prepare("acos", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "acos", "(D)D", false);
	}
}

/**
 * An addition expression.
 */
class Add extends Exp {

	public Add() {
		super();
		prepare("+", 2, 24);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds an instruction to add the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);
		mv.visitInsn(DADD);
	}
}

/**
 * A logical "and" expression.
 */
class And extends Exp {

	public And() {
		super();
		prepare("&&", 2, 19);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		Label end = new Label();
		exps[0].compile(mv, jls); 			// ... e1
		mv.visitInsn(DUP2); 			// ... e1, e1
		mv.visitInsn(D2I);   			// ... e1, i1
		// tests if e1 is false
		mv.visitJumpInsn(IFEQ, end); 	// ... e1
		// case where e1 is true : e1 && e2 is equal to e2
		mv.visitInsn(POP2);				// ...
		exps[1].compile(mv, jls);			// ... e2
		// if e1 is false, e1 && e2 is equal to e1:
		// we jump directly to this label, without evaluating e2
		mv.visitLabel(end);
	}
}

/**
 * The 'asin' method
 */
class Asin extends Exp {

	public Asin() {
		super();
		prepare("asin", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "asin", "(D)D", false);
	}
}

/**
 * The 'atan' method
 */
class Atan extends Exp {

	public Atan() {
		super();
		prepare("atan", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "atan", "(D)D", false);
	}
}

/**
 * The 'atan2' method
 */
class Atan2 extends Exp {

	public Atan2() {
		super();
		prepare("atan2", 2, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds an instruction to add the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "atan2", "(DD)D", false);
	}
}

/**
 * The 'cube root' method
 */
class Cbrt extends Exp {

	public Cbrt() {
		super();
		prepare("cbrt", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "cbrt", "(D)D", false);
	}
}

/**
 * The 'ceil' method
 */
class Ceil extends Exp {

	public Ceil() {
		super();
		prepare("ceil", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "ceil", "(D)D", false);
	}
}

/**
 * A RParen - right parenthesis.
 */
class Comma extends Exp {

	public Comma() {
		prepare(",", 100);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// Do nothing
	}
}

/**
 * The 'cos' method
 */
class Cos extends Exp {

	public Cos() {
		super();
		prepare("cos", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "cos", "(D)D", false);
	}
}

/**
 * The 'cosh' method
 */
class Cosh extends Exp {

	public Cosh() {
		super();
		prepare("cosh", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "cosh", "(D)D", false);
	}
}

/**
 * A number literal.
 */
class Cst extends Operand {

	double value;

	public Cst(){
		prepare("$~CONST ", 40);
	}

	public Cst(final double value) {
		prepare("" + value, 40);
		this.value = value;
	}

	@Override
	void applyUnaryMinus(){
		value *= -1;
		symbol = "" + value;
	}
	
	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// pushes the constant's value onto the stack
		mv.visitLdcInsn(new Double(value));
	}
}

/**
 * Equivalent to boolean true 1.0
 */
class True extends Exp {
	
	public True(){
		prepare("TRUE", 0, 40);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		mv.visitInsn(DCONST_1);
	}

}

/**
 * Equivalent to boolean false 0.0
 */
class False extends Exp {
	
	public False(){
		prepare("FALSE", 0, 40);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		mv.visitInsn(DCONST_0);
	}

}


/**
 * The constant Pi = 3.14159265358979323846
 */
class Pi extends Exp {
	
	public Pi(){
		prepare("PI", 0, 40);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		mv.visitLdcInsn(Math.PI);
	}

}

/**
 * The constant E = 2.7182818284590452354
 */

class E extends Exp {
	
	public E(){
		prepare("E", 0, 40);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		mv.visitLdcInsn(Math.E);
	}

}



/**
 * The 'degrees' method
 */
class Deg extends Exp {

	public Deg() {
		super();
		prepare("degrees", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// computes e
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "toDegrees", "(D)D", false);
	}
}

/**
 * A division expression.
 */
class Div extends Exp {

	public Div() {
		super();
		prepare("/", 2, 25);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);
		mv.visitInsn(DDIV);
	}
}

/**
 * The 'exponential' method
 */
class EpowerX extends Exp {

	public EpowerX() {
		super();
		prepare("exp", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "exp", "(D)D", false);
	}
}

/**
 * A "equal" expression.
 */
class EQ extends Exp {

	public EQ() {
		super();
		prepare("==", 2, 20);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds the instructions to compare the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);
		Label ifTrue = new Label();
		Label end = new Label();
		mv.visitInsn(DCMPL);
		mv.visitJumpInsn(IFEQ, ifTrue);
		mv.visitInsn(DCONST_0);
		mv.visitJumpInsn(GOTO, end);
		mv.visitLabel(ifTrue);
		mv.visitInsn(DCONST_1);
		mv.visitLabel(end);	
	}
}


class Field extends Operand {

	String name;
	
	public Field(final String name) {
		this.name = name;
		prepare("FIELD: " + name + " ["+ name + "]", 40);
	}
	
	@Override
	void applyUnaryMinus(){
		hasUnaryMinus = true;
	}
	
	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// Push the value into the array element position
		// specified in index
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "org/quark/evaluate/ASMdummy", name, "D");
	}
}

/**
 * The 'floor' method
 */
class Floor extends Exp {

	public Floor() {
		super();
		prepare("floor", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "floor", "(D)D", false);
	}
}


/**
 * A "greater than" expression.
 */
class GT extends Exp {

	public GT() {
		super();
		prepare(">", 2, 21);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds the instructions to compare the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);

		Label ifTrue = new Label();
		Label end = new Label();

		mv.visitInsn(DCMPL);
		mv.visitJumpInsn(IFGT, ifTrue);
		mv.visitInsn(DCONST_0);
		mv.visitJumpInsn(GOTO, end);
		mv.visitLabel(ifTrue);
		mv.visitInsn(DCONST_1);
		mv.visitLabel(end);
	}
}


/**
 * A "greater than or equal to" expression.
 */
class GTE extends Exp {

	public GTE() {
		super();
		prepare(">=", 2, 21);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds the instructions to compare the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);

		Label ifTrue = new Label();
		Label end = new Label();

		mv.visitInsn(DCMPL);
		mv.visitJumpInsn(IFGE, ifTrue);
		mv.visitInsn(DCONST_0);
		mv.visitJumpInsn(GOTO, end);
		mv.visitLabel(ifTrue);
		mv.visitInsn(DCONST_1);
		mv.visitLabel(end);
	}
}

/**
 * A local variable reference expression. <br>
 * A local variable is created for each unique variable name 
 * used in the expression.
 * 
 */
class LocalVar extends Operand {

	int index;
	
	public LocalVar(final int index) {
		prepare("$~VAR ["+ index + "]", 0, 40);
		this.index = index;
	}
	
	public LocalVar(final String id, final int index) {
		prepare(id + " ["+ index + "]", 0, 40);
		this.index = index;
	}
	
	@Override
	void applyUnaryMinus(){
		hasUnaryMinus = true;
	}
	
	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// Push the value into the array element position  specified in index
		mv.visitVarInsn(ALOAD, 1);				// load the array
		mv.visitLdcInsn(new Integer(index));    // load the index
		mv.visitInsn(DALOAD);
		if(hasUnaryMinus) {
			mv.visitLdcInsn(new Double(-1));
			mv.visitInsn(DMUL);
		}
	}
}

/**
 * The 'logarithm base 10' method
 */
class Log10 extends Exp {

	public Log10() {
		super();
		prepare("log10", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "log10", "(D)D", false);
	}
}

/**
 * The 'natural logarithm' method
 */
class LogE extends Exp {

	public LogE() {
		super();
		prepare("log", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "log", "(D)D", false);
	}
}


/**
 * A LParen - left parenthesis.
 */
class LParen extends Exp {

	public LParen() {
		prepare("(", 12);
	}

	@Override
	public void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// Do nothing
	}
}

/**
 * A "less than" expression.
 */
class LT extends Exp {

	public LT() {
		super();
		prepare("<", 2, 21);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds the instructions to compare the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);

		Label ifTrue = new Label();
		Label end = new Label();

		mv.visitInsn(DCMPL);
		mv.visitJumpInsn(IFLT, ifTrue);
		mv.visitInsn(DCONST_0);
		mv.visitJumpInsn(GOTO, end);
		mv.visitLabel(ifTrue);
		mv.visitInsn(DCONST_1);
		mv.visitLabel(end);
	}
}

/**
 * A "less than or equal to" expression.
 */
class LTE extends Exp {

	public LTE() {
		super();
		prepare("<=", 2, 21);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds the instructions to compare the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);

		Label ifTrue = new Label();
		Label end = new Label();

		mv.visitInsn(DCMPL);
		mv.visitJumpInsn(IFLE, ifTrue);
		mv.visitInsn(DCONST_0);
		mv.visitJumpInsn(GOTO, end);
		mv.visitLabel(ifTrue);
		mv.visitInsn(DCONST_1);
		mv.visitLabel(end);
	}
}

/**
 * The 'max' method
 */
class Max extends Exp {

	public Max() {
		super();
		prepare("max", 2, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds an instruction to add the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "max", "(DD)D", false);
	}
}

/**
 * The 'max' method
 */
class Min extends Exp {

	public Min() {
		super();
		prepare("min", 2, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds an instruction to add the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "min", "(DD)D", false);
	}
}

/**
 * An modulus expression.
 */
class Mod extends Exp {

	public Mod() {
		super();
		prepare("%", 2, 25);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds an instruction to add the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);
		mv.visitInsn(DREM);
	}
}

/**
 * A multiplication expression.
 */
class Mul extends Exp {

	public Mul() {
		super();
		prepare("*", 2, 25);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);
		mv.visitInsn(DMUL);
	}
}

/**
 * A "not equal" expression.
 */
class NEQ extends Exp {

	public NEQ() {
		super();
		prepare("!=", 2, 20);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds the instructions to compare the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);
		Label ifTrue = new Label();
		Label end = new Label();
		mv.visitInsn(DCMPL);
		mv.visitJumpInsn(IFNE, ifTrue);
		mv.visitInsn(DCONST_0);
		mv.visitJumpInsn(GOTO, end);
		mv.visitLabel(ifTrue);
		mv.visitInsn(DCONST_1);
		mv.visitLabel(end);	
	}
}

/**
 * A logical "not" expression.
 */
class NOP extends Exp {

	public NOP() {
		super();
		prepare("NOP", 40);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
	}
}

/**
 * A logical "not" expression.
 */
class Not extends Exp {

	public Not() {
		super();
		prepare("not", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// computes !e1 by evaluating 1 - e1
		mv.visitLdcInsn(new Double(1));
		exps[0].compile(mv, jls);
		mv.visitInsn(DSUB);
	}
}

abstract class Operand extends Exp {

	boolean hasUnaryMinus = false;
	
	abstract void applyUnaryMinus();

}

/**
 * A logical "or" expression.
 */
class Or extends Exp {

	public Or() {
		super();
		prepare("||", 2, 17);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		Label end = new Label();
		exps[0].compile(mv, jls); 		// ... e1  			(0 = false   1= true)
		mv.visitInsn(DUP2); 			// ... e1, e1
		mv.visitInsn(D2I);   			// ... e1, i1
		// tests if e1 is true
		mv.visitJumpInsn(IFNE, end); 	// ... e1
		// case where e1 is false : e1 || e2 is equal to e2
		mv.visitInsn(POP2);				// ...
		exps[1].compile(mv, jls);		// ... e2
		// if e1 is true, e1 || e2 is equal to e1:
		// we jump directly to this label, without evaluating e2
		mv.visitLabel(end);
	}
}


/**
 * The 'power' method
 */
class Pow extends Exp {

	public Pow() {
		super();
		prepare("^", 2, 27);
		rightAssociative = true;
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1, e2, and adds an instruction to add the two values
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "pow", "(DD)D", false);
	}
}


/**
 * The 'rad' method
 */
class Rad extends Exp {

	public Rad() {
		super();
		prepare("radians", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// computes e
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "toRadians", "(D)D", false);
	}
}

/**
 * The random number in range >=0 and < 1 method
 */
class Rand extends Exp {

	public Rand() {
		super();
		prepare("random", 2, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);		// a
		mv.visitInsn(DUP2);				// a a
		exps[1].compile(mv, jls);		// a a b
		mv.visitInsn(DUP2_X2);				// a b a
		mv.visitInsn(POP2);				// a b a
		mv.visitInsn(DSUB);				// a b-a	
		mv.visitMethodInsn(INVOKESTATIC, MATH, "random", "()D", false);
		mv.visitInsn(DMUL);				// a (b-a)*r	
		mv.visitInsn(DADD);				// a + (b-a)*r	
	}
}

/**
 * The random number in range >=0 and < 1 method
 */
class Round extends Exp {

	public Round() {
		super();
		prepare("round", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "round", "(D)J", false);
		mv.visitInsn(L2D);
	}
}

/**
 * A RParen - right parenthesis.
 */
class RParen extends Exp {

	public RParen() {
		prepare(")", 11);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// Do nothing
	}
}


/**
 * The 'signum' method
 */
class Signum extends Exp {

	public Signum() {
		super();
		prepare("signum", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "signum", "(D)D", false);
	}
}


/**
 * The 'sin' method
 */
class Sin extends Exp {

	public Sin() {
		super();
		prepare("sin", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "sin", "(D)D", false);
	}
}


/**
 * The 'sinh' method
 */
class Sinh extends Exp {

	public Sinh() {
		super();
		prepare("sinh", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "sinh", "(D)D", false);
	}
}

/**
 * The 'square root' method
 */
class Sqrt extends Exp {

	public Sqrt() {
		super();
		prepare("sqrt", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "sqrt", "(D)D", false);
	}
}

/**
 * An subtraction expression.
 */
class Sub extends Exp {

	public Sub() {
		super();
		prepare("-", 2, 24);
	}


	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		exps[1].compile(mv, jls);
		mv.visitInsn(DSUB);
	}
}

/**
 * The 'tan' method
 */
class Tan extends Exp {

	public Tan() {
		super();
		prepare("tan", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "tan", "(D)D", false);
	}
}


/**
 * The 'tanh' method
 */
class Tanh extends Exp {

	public Tanh() {
		super();
		prepare("tanh", 1, 30);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		exps[0].compile(mv, jls);
		mv.visitMethodInsn(INVOKESTATIC, MATH, "tanh", "(D)D", false);
	}
}

/**
 * Unary minus expression.
 */
class UMinus extends Exp {

	public UMinus() {
		super();
		prepare("$~U-", 1, 24);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		mv.visitLdcInsn(new Double(-1));
		mv.visitInsn(DMUL);
	}
}

/**
 * A logical "or" expression.
 */
class Xor extends Exp {

	public Xor() {
		super();
		prepare("^^", 2, 18);
	}

	@Override
	void compile(final MethodVisitor mv, final Deque<JumpLabel> jls) throws JasmineException {
		// compiles e1
		exps[0].compile(mv, jls);
		mv.visitInsn(D2L);
		exps[1].compile(mv, jls);
		mv.visitInsn(D2L);
		// tests if e1 is true
		mv.visitInsn(LXOR); // -1 or 1 if different
		mv.visitInsn(L2D); // convert to double
	}
}



