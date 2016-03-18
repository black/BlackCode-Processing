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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Defines the operators that are used when compiling expressions and algorithms. <br>
 * 
 * This  class allows to to create two 'operating set singletons' one for
 * expressions and another for algorithms.
 * 
 * @author Peter Lager
 *
 */
final class OperatorSet {
	
	public static final int EXPRESSION = 0;
	public static final int ALGORITHM = 1;

	private static OperatorSet opSetExprn = null;
	private static OperatorSet opSetAlgor = null;
	
	/**
	 * The parameter type determines which operator set to return. The actual
	 * operator set is created on first use.
	 * 
	 * @param type EXPRESSION or ALGORITHM
	 * @return the appropriate operator set.
	 */
	public static OperatorSet getOperatorSet(int type){
		if(type == EXPRESSION){
			if(opSetExprn == null){
				opSetExprn = new OperatorSet();
				opSetExprn.makeRegex();
			}
			return opSetExprn;
		}
		if(type == ALGORITHM){
			if(opSetExprn == null){
				opSetExprn = new OperatorSet();
				opSetExprn.makeRegex();
			}
			opSetAlgor = new OperatorSet(opSetExprn);
			// add extra operators for algorithms
			opSetAlgor.addAlgorithmOperators();
			opSetAlgor.makeRegex();
			return opSetAlgor;
		}
		return null;
	}
	
	// Instance fields and methods
	private TreeMap<String, Exp> exprs;
	private String regex = null;
	private Pattern pattern = Pattern.compile("^(" + regex + ")"); 
	
	/**
	 * Private constructor will create the expression set
	 */
	private OperatorSet(){
		exprs = new TreeMap<String, Exp>();
		addOperators();
	}
	
	/**
	 * Private constructor will create the an operator set from
	 * another set. Extra operators can then be added.
	 */
	private OperatorSet(OperatorSet set){
		exprs = new TreeMap<String, Exp>();
		exprs.putAll(set.exprs);
	}
	
	/**
	 * Once all the operators have been added this will create 
	 * all the regular expressions for each operator and the whole 
	 * set.
	 */
	private void makeRegex(){
		regex = regexFromExpressions();
		pattern = Pattern.compile("^(" + regex + ")");
	}

	/**
	 * Get the operatorset regular expression
	 * @return
	 */
	String getRegex(){
		return regex;
	}

	/**
	 * Get the pattern for this set for the parser.
	 * @return
	 */
	Pattern getPattern(){
		return pattern;
	}
	
	/**
	 * Add the extra operators needed for parsing algorithms.
	 */
	private void addAlgorithmOperators(){
		// Unary operator
		addExp(new Assign());
		addExp(new Separator());
		addExp(new NOP());
		addExp(new If());
		addExp(new Then());
		addExp(new Else());
		addExp(new Endif());
		addExp(new While());
		addExp(new Wend());
		addExp(new Repeat());
		addExp(new Until());
		addExp(new For());
		addExp(new ForEnd());
		addExp(new Stop());
		addExp(new Print());
	}
	
	/**
	 * Add the operators needed for parsing expressions.
	 */
	private void addOperators(){
		// Unary operator
		addExp(new UMinus());
		// Structural	
		addExp(new LParen());
		addExp(new RParen());
		addExp(new Comma());
		// Basic math symbols
		addExp(new Add());
		addExp(new Sub());
		addExp(new Div());
		addExp(new Mul());
		addExp(new Pow());
		addExp(new Mod());
		// Logical comparison
		addExp(new And());
		addExp(new Or());
		addExp(new Xor());
		addExp(new Not());
		addExp(new EQ());
		addExp(new NEQ());
		addExp(new GT());
		addExp(new GTE());
		addExp(new LT());
		addExp(new LTE());
		// Trigonometric functions
		addExp(new Sin());
		addExp(new Cos());
		addExp(new Tan());
		addExp(new Asin());
		addExp(new Acos());
		addExp(new Atan());
		addExp(new Sinh());
		addExp(new Cosh());
		addExp(new Tanh());
		addExp(new Atan2());
		addExp(new Deg());
		addExp(new Rad());
		// Math functions
		addExp(new Abs());
		addExp(new Ceil());
		addExp(new Floor());
		addExp(new Round());
		addExp(new Max());
		addExp(new Min());
		addExp(new Sqrt());
		addExp(new Cbrt());
		addExp(new Signum());
		addExp(new Rand());
		// Power / log functions
		addExp(new EpowerX());
		addExp(new LogE());
		addExp(new Log10());
		// Constants
		addExp(new True());
		addExp(new False());
		addExp(new Pi());

	}
	
	/**
	 * See if an operator exists based on its symbol
	 */
	boolean expExists(String symbol){
		return exprs.get(symbol) != null;
	}

	/**
	 * Create and return an Exp object based on its symbol. If the symbol
	 * can't be found it returns null
	 */
	Exp getExp(String symbol){
		Exp op = exprs.get(symbol);
		Class<? extends Exp> c = op.getClass();
		try {
			Constructor<? extends Exp> con = c.getConstructor();
			Exp o = (Exp) con.newInstance();
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
		} catch (Exception e) {
			System.out.println("New exp Exception " + e.getClass().getSimpleName());
		}
		return null;
	}


	/**
	 * Add an expression to this set.
	 * 
	 * @param exp
	 */
	private void addExp(Exp exp){
		exp.regex = regexFromSymbol(exp.symbol);
		exprs.put(exp.symbol, exp);
	}
	
	/**
	 * Create a regular expression from a symbol. Basically prefixes
	 * reserved characters with a backslash.
	 */
	private String regexFromSymbol(String symbol){
		String r = "<([{^-=$!|]})?*+.>";
		String bs = "\\";
		char[] c = symbol.toCharArray();
		StringBuilder sb = new StringBuilder();	
		for(int i = 0; i < c.length; i++){
			if(r.indexOf(c[i]) >= 0)
				sb.append(bs + c[i]);
			else
				sb.append("" + c[i]);
		}
		return sb.toString();
	}

	/**
	 * Calculates the regex for all expression symbols to be used
	 * when parsing
	 */
	private String regexFromExpressions(){
		Collection<Exp> values = exprs.values();
		Exp[] earray = (Exp[]) values.toArray(new Exp[values.size()]);
		int ptr = earray.length;
		StringBuilder sb = new StringBuilder(earray[--ptr].regex);
		do {
			sb.append("|");
			sb.append(earray[--ptr].regex);
		} while(ptr > 0);
		return sb.toString();
	}

}

