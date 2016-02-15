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

import java.util.Collection;
import java.util.HashMap;

/**
 * An algorithm is a series of statements on one or more lines that can be evaluated. <br>
 * 
 * Algorithm extends the capabilities of the Expression class by providing support for 
 * variables, selection and iteration constructs. Unlike expression, algorithms does not 
 * return a single value, rather you can examine any of the variables after evaluation. <br>
 * 
 * @author Peter Lager
 *
 */
public abstract class Algorithm extends AbstractExpression {

	protected HashMap<String, Double> __vars = new HashMap<String, Double>();
	
	
	/**
	 * Initialize all the variables to zero. The variables are not cleared before or after
	 * an evaluation so that variable data persists between evaluations. If you don't want
	 * the data to persist call this method before calling eval(...)
	 */
	public void clearVariables(){
		Collection<String> names = __vars.keySet();
			for(String name : names)
				__vars.put(name,  new Double(0));
	}

	/**
	 * Display all the variables and their values in the console window.
	 */
	public void showVariables(){
		Collection<String> names = __vars.keySet();
			for(String name : names)
				System.out.println("> " + name + "\t" + __vars.get(name));
	}

	/**
	 * This method is called for you when the algorithm is evaluated so it is
	 * recommended that you do this by passing initial variable state in the
	 * parameters for eval(initial_variable_state)
	 */
	public void initVariables(Object... params){
		if(params != null){
			for(int n = 1; n < params.length; n += 2){
				if(__vars.containsKey(params[n-1]))
						__vars.put((String)params[n-1], toDouble(params[n]));		
			}
		}
	}


	/**
	 * This is used to convert any value passed by the user to a double. It accepts data of
	 * any type and attempt to convert it to a double. If it's not possible it returns 0.0 
	 */
	private double toDouble(Object value) {
		if(value instanceof Double)
			return ((Double) value).doubleValue();
		if (value instanceof Float)
			return ((Float) value).doubleValue();
		if (value instanceof Integer) 
			return ((Integer) value).doubleValue();
		if (value instanceof Long)
			return ((Long) value).doubleValue();
		if(value instanceof Boolean)
			return (Boolean) value ? 1.0 : 0.0;
		if (value instanceof String) {
			try {
				return Double.parseDouble(((String) value));
			} catch (NumberFormatException e) {
				return 0.0;
			}
		}
		if (value instanceof Short)
			return ((Short) value).doubleValue();
		if (value instanceof Byte)
			return ((Byte) value).doubleValue();
		return 0.0;
	}

	/**
	 * Use this to get the value stored in a variable after evaluation has finished. <br>
	 * 
	 * @param name the name of the variable
	 * @return the value of a variable wrapped in an answer object or null if the variable doen't exist.
	 */	
	public Answer answer(String name){
		return new Answer(__vars.get(name));
	}

	/**
	 * Jasmine using ASM to generate the code for this method. <br>
	 * 
	 *  The parameters represent the initial values to be used for the evaluation
	 *  and are expressed as pairs "variable _name", variable+value. For instnace <br>
	 *  <pre>("m", 4.6, "pi2", 2 * Math.PI)</pre><br>
	 *  will initialise the variables m and pi2 to 4.6 and 6.283185307 <br>
	 *  
	 *  This method returns 'this' so for example you can chain like this:
	 *  <pre>algorithm.eval("m", 4.6, "pi2", 2 * Math.PI).answer("m").toFloat(); </pre>
	 *  to get the value stored in 'm' as a float.
	 *  
	 * @param vars the initial values to use in this evaluation
	 * @returns this Algorithm 
	 */
	public abstract Algorithm eval(Object... vars);
}
