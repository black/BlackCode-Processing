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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * A singleton class to manage all available operators.
 * 
 * @author Peter Lager
 */

public class OperatorSet {

	private static OperatorSet operators = null;
	
	/**
	 * Get the standard set of operators included.
	 * This method is called 
	 * 
	 * @return the standard set of operators
	 */
	public static OperatorSet get() {
		if (operators == null) 
			makeSet();
		return operators;
	}

	/**
	 * This allows you to use your own operator set. The best way to do this is use
	 * OperatorSet.get() to get the standard set then add / remove operators to
	 * make your own custom set. Then use this method to set it as the default
	 * set to use. <br>
	 * Creating and using custom operator sets is advanced stuff, do this at your 
	 * own risk.
	 * 
	 * @param opset the set to use, null values are ignored.
	 */
	public static void set(OperatorSet opset){
		if(opset != null)
			operators = opset;
	}
	
	private static void makeSet(){
		operators = new OperatorSet();

		// Standard Operators
		operators.addOperator(new AdditionSymbol("+", 2, 24, Operator.INFIX_SYMBOL));
		operators.addOperator(new SubtractionSymbol("-", 2, 24, Operator.INFIX_SYMBOL));
		operators.addOperator(new MultiplicationSymbol("*", 2, 25, Operator.INFIX_SYMBOL));
		operators.addOperator(new DivisionSymbol("/", 2, 25, Operator.INFIX_SYMBOL));
		operators.addOperator(new ModuloSymbol("%", 2, 25, Operator.INFIX_SYMBOL));
		operators.addOperator(new PowerFunction("^", 2, 27, Operator.INFIX_SYMBOL));

		// Logical operators
		operators.addOperator(new AndSymbol("AND", 2, 19, Operator.INFIX_SYMBOL));
		operators.addOperator(new AndSymbol("&&", 2, 19, Operator.INFIX_SYMBOL));
		operators.addOperator(new OrSymbol("OR", 2, 17, Operator.INFIX_SYMBOL));
		operators.addOperator(new OrSymbol("||", 2, 17, Operator.INFIX_SYMBOL));
		operators.addOperator(new XorSymbol("XOR", 2, 18, Operator.INFIX_SYMBOL));
		operators.addOperator(new NotFuntion("NOT", 1, 30, Operator.FUNCTION));

		// Math functions
		operators.addOperator(new RandomFunction("rnd", 2, 30, Operator.FUNCTION));
		operators.addOperator(new MaxFunction("max", 2, 30, Operator.FUNCTION));
		operators.addOperator(new MinFunction("min", 2, 30, Operator.FUNCTION));
		operators.addOperator(new FloorFuction("floor", 1, 30, Operator.FUNCTION));
		operators.addOperator(new CeilFunction("ceil", 1, 30, Operator.FUNCTION));
		operators.addOperator(new IntFunction("int", 1, 30, Operator.FUNCTION));
		operators.addOperator(new RoundFunction("round", 1, 30, Operator.FUNCTION));
		operators.addOperator(new NegFunction("neg", 1, 30, Operator.FUNCTION));
		operators.addOperator(new AbsFunction("abs", 1, 30, Operator.FUNCTION));
		operators.addOperator(new PowerFunction("pow", 2, 30, Operator.FUNCTION));
		operators.addOperator(new SqrtFunction("sqrt", 1, 30, Operator.FUNCTION));
		operators.addOperator(new SqrtzFunction("sqrtz", 1, 30, Operator.FUNCTION));
		operators.addOperator(new Log_Function("log", 2, 30, Operator.FUNCTION));
		operators.addOperator(new LogE_Function("logE", 1, 30, Operator.FUNCTION));
		operators.addOperator(new Log10_Function("log10", 1, 30, Operator.FUNCTION));
		operators.addOperator(new ExpFunction("exp", 1, 30, Operator.FUNCTION));
		operators.addOperator(new SignumFunction("signum", 1, 30, Operator.FUNCTION));

		// Trignometric functions
		operators.addOperator(new CosFunction("cos", 1, 30, Operator.FUNCTION));
		operators.addOperator(new SinFunction("sin", 1, 30, Operator.FUNCTION));
		operators.addOperator(new TanFunction("tan", 1, 30, Operator.FUNCTION));
		operators.addOperator(new CoshFunction("cosh", 1, 30, Operator.FUNCTION));
		operators.addOperator(new SinhFunction("sinh", 1, 30, Operator.FUNCTION));
		operators.addOperator(new TanhFunction("tanh", 1, 30, Operator.FUNCTION));
		operators.addOperator(new AcosFunction("acos", 1, 30, Operator.FUNCTION));
		operators.addOperator(new AsinFunction("asin", 1, 30, Operator.FUNCTION));
		operators.addOperator(new AtanFunction("atan", 1, 30, Operator.FUNCTION));
		operators.addOperator(new Atan2Function("atan2", 2, 30, Operator.FUNCTION));
		operators.addOperator(new DegreesToRadians("radians", 1, 30, Operator.FUNCTION));
		operators.addOperator(new RadiansToDegrees("degrees", 1, 30, Operator.FUNCTION));

		// Comparison
		operators.addOperator(new EqualSymbol("==", 2, 20, Operator.INFIX_SYMBOL));
		operators.addOperator(new NotEqualSymbol("!=", 2, 20, Operator.INFIX_SYMBOL));
		operators.addOperator(new GreaterThanOrEqualSymbol(">=", 2, 21, Operator.INFIX_SYMBOL));
		operators.addOperator(new GreaterThanSymbol(">", 2, 21, Operator.INFIX_SYMBOL));
		operators.addOperator(new LessThanOrEqualSymbol("<=", 2, 21, Operator.INFIX_SYMBOL));
		operators.addOperator(new LessThanSymbol("<", 2, 21, Operator.INFIX_SYMBOL));

		// Constants must have a priority >= 40
		operators.addOperator(new PI_constant("PI", 0, 40, Operator.CONSTANT));
		operators.addOperator(new E_constant("E", 0, 40, Operator.CONSTANT));
		operators.addOperator(new EOL_constant("EOL", 0, 40, Operator.CONSTANT));
		operators.addOperator(new True_constant("TRUE", 0, 40, Operator.CONSTANT));
		operators.addOperator(new False_constant("FALSE", 0, 40, Operator.CONSTANT));
		operators.addOperator(new True_constant("true", 0, 40, Operator.CONSTANT));
		operators.addOperator(new False_constant("false", 0, 40, Operator.CONSTANT));

		// Variables and assignment 
		operators.addOperator(new AssignmentSymbol("=", 2, 15, Operator.INFIX_SYMBOL));
		operators.addOperator(new PrintFunction("print", 1, 30, Operator.FUNCTION));
		operators.addOperator(new PrintlnFunction("println", 1, 30, Operator.FUNCTION));

		// Program flow constructs
		operators.addOperator(new IF_operator("IF", 1, 20, Operator.FUNCTION)); 
		operators.addOperator(new ELSE_operator("ELSE", 0, 3, Operator.MARKER));
		operators.addOperator(new ENDIF_operator("ENDIF", 0, 2, Operator.MARKER));

		operators.addOperator(new REPEAT_operator("REPEAT", 0, 14, Operator.MARKER)); 
		operators.addOperator(new UNTIL_operator("UNTIL", 1, 12, Operator.FUNCTION)); 

		operators.addOperator(new WHILE_operator("WHILE", 1, 14, Operator.FUNCTION)); 
		operators.addOperator(new WEND_operator("WEND", 0, 2, Operator.MARKER));

		// Precedence controls
		operators.addOperator(new WAIT_operator("WAIT", 1, 30, Operator.FUNCTION));
		operators.addOperator(new END_operator("END", 1, 30, Operator.FUNCTION));
		operators.addOperator(new LeftParenSymbol("(", 0, 12, Operator.MARKER)); // was 12
		operators.addOperator(new RightParenSymbol(")", 0, 11, Operator.MARKER)); // was 11
		operators.addOperator(new CommaSymbol(",", 0, 9, Operator.MARKER));
		operators.addOperator(new ExpressionSeperatorSymbol(";", 0, 9, Operator.MARKER));

		// MIsc
		operators.addOperator(new RuntimeFunction("runtime", 0, 30, Operator.FUNCTION));

		// String
		
		// Vector
		operators.addOperator(new VectorFunction("V", 3, 30, Operator.FUNCTION));
		operators.addOperator(new VectorFunction("vector", 3, 30, Operator.FUNCTION));
		operators.addOperator(new MagFunction("mag", 1, 30, Operator.FUNCTION));
		operators.addOperator(new DotFunction("dot", 2, 30, Operator.FUNCTION));
		operators.addOperator(new CrossFunction("cross", 2, 30, Operator.FUNCTION));
		operators.addOperator(new NormalizeFunction("norm", 1, 30, Operator.FUNCTION));
		operators.addOperator(new LerpFunction("lerp", 3, 30, Operator.FUNCTION));
		operators.addOperator(new AngleBetweenFunction("angleBetween", 3, 30, Operator.FUNCTION));
		operators.addOperator(new RandomVectorFunction("rndV", 1, 30, Operator.FUNCTION));
		operators.addOperator(new V000_constant("V000", 0, 40, Operator.CONSTANT));
		operators.addOperator(new V100_constant("V100", 0, 40, Operator.CONSTANT));
		operators.addOperator(new V010_constant("V010", 0, 40, Operator.CONSTANT));
		operators.addOperator(new V001_constant("V001", 0, 40, Operator.CONSTANT));
		operators.addOperator(new VectorXFunction("vX", 1, 40, Operator.FUNCTION));
		operators.addOperator(new VectorYFunction("vY", 1, 40, Operator.FUNCTION));
		operators.addOperator(new VectorZFunction("vZ", 1, 40, Operator.FUNCTION));

		// Complex
		operators.addOperator(new ComplexFunction("C", 2, 30, Operator.FUNCTION));
		operators.addOperator(new ComplexFunction("complex", 2, 30, Operator.FUNCTION));
		operators.addOperator(new RealFunction("real", 1, 30, Operator.FUNCTION));
		operators.addOperator(new ImagFunction("imag", 1, 30, Operator.FUNCTION));
		operators.addOperator(new ConjugateFunction("conj", 1, 30, Operator.FUNCTION));
		operators.addOperator(new RandomComplexFunction("rndC", 1, 30, Operator.FUNCTION));
	
	}

	// Gives fast access based on operator symbol
	private HashMap<String, Operator> operatorMap = new HashMap<String, Operator>();
	private  List<Operator> operatorList = new ArrayList<Operator>();
	private boolean changed = true;
	private String regex;

	// Enforce singleton pattern
	private OperatorSet(){}

	/**
	 * Allows user defined operators to be added to the set. This enables customising the
	 * script language. <br>
	 * 
	 * @param op
	 */
	public void addOperator(Operator op){
		Operator oldOp = operatorMap.put(op.getSymbol(), op);
		// Remove older version from list
		if(oldOp != null)
			operatorList.remove(oldOp);
		operatorList.add(op);
		changed = true;
	}

	/**
	 * Remove the operator with the specified symbol.
	 * @param symbol operator symbol
	 */
	public void removeOperator(String symbol){
		if(operatorMap.containsKey(symbol)){
			Operator oldOp = operatorMap.remove(symbol);
			operatorList.remove(oldOp);
			changed = true;
		}
	}

	/**
	 * Get the regex to match all operators. If the list of operators has changed since the 
	 * last time this method was called the regex will be recalculated.
	 */
	public String getRegex(){
		if(changed){
			regex = RegexMaker.regexFromOperators(operatorList);
			changed = false;
		}
		return regex;
	}
	
	/**
	 * Returns true if the operator set has changed
	 */
	public boolean hasChanged(){
		return changed;
	}
	
	/**
	 * This creates an instance of the operator with the symbol passed as
	 * a parameter value. <br>
	 * Effectively this method provides a copy of the operator held in 
	 * this operator set.
	 *  
	 * @param symbol the symbol to search for.
	 * @return an operator object that matches the symbol or null if no match.
	 */
	public Operator getOperator(String symbol){
		Operator op = operatorMap.get(symbol);
		Class<? extends Operator> c = op.getClass();
		try {
			Constructor<? extends Operator> con = c.getConstructor(String.class, int.class, int.class, int.class);
			Operator o = (Operator) con.newInstance(op.getSymbol(), op.getNbrArgs(), op.getPriority(), op.getOpType());
			return o;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * See if the operator set recognizes a symbol.
	 * 
	 * @param symbol the operator symbol to look for
	 * @return true if found else false
	 */
	public boolean hasOperator(String symbol){
		return operatorMap.containsKey(symbol);
	}


	/**
	 * Get a list of the symbols for this operator set. Changes in the list are not
	 * reflected in the operator set which remains unchanged.
	 */
	public Set<String> getSymbols(){
		return operatorMap.keySet();
	}
}
