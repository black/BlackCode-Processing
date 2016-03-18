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

/**
 * This class is used to hold either  <br>
 * <ul>
 * <li>the answer to an expression, or</li>
 * <li>the value of a variable in an algorithm. </li>
 * </ul> <br>
 * Jasmine uses the double data type throughout this class
 * provides utilities methods to convert the answer to other 
 * data types.
 * 
 * @author Peter Lager
 *
 */
public final class Answer {

	public Object value;

	/**
	 * Create a null Answer object
	 */
	Answer() {
		this.value = null;
	}

	/**
	 * Create an Answer object with the given value.
	 * @param value if null will be set 
	 */
	Answer(Object value) {
		this.value = value;
	}

	/**
	 * Used by the generated QuarkExpression class object created 
	 * in the default package.
	 */
	public void setValue(Object value){
		this.value = value;
	}
	
	/**
	 * Get the Double interpretation of the base object if a direct
	 * conversion can take place.
	 * 
	 * @return Double interpretation of the base object.
	 * 
	 * @throws JasmineException if the base object is not a Double or cannot be converted to a Double
	 */
	public double toDouble_() throws JasmineException {
		if(value instanceof Boolean)
			System.out.println(value.getClass().getSimpleName());
		if(value instanceof Double)
			return ((Double) value).doubleValue();
		else if (value instanceof Integer) 
			return ((Integer) value).doubleValue();
		else if (value instanceof Long)
			return ((Long) value).doubleValue();
		else if (value instanceof Float)
			return ((Float) value).doubleValue();
		else if (value instanceof String) {
			try {
				return Double.parseDouble(((String) value));
			} catch (NumberFormatException e) {
				// Allow to pass through to EvaluationException
				e.printStackTrace();
			}
		}
		throw new JasmineException(ErrorType.INVALID_CAST, ErrorType.INVALID_CAST.message + "double");
	}


	/**
	 * Get the Double value of the base object if a direct conversion 
	 * can take place. If the base object cannot be converted to a Double
	 * then display an error message and return zero.
	 * 
	 * @return Double interpretation of the base object.
	 */
	public double toDouble() {
		try {
			return toDouble_();
		} catch (JasmineException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	
	/**
	 * Get the Float interpretation of the base object if a direct
	 * conversion can take place.
	 * 
	 * @return Float interpretation of the base object.
	 * 
	 * @throws JasmineException if the base object is not a Float or cannot be converted to a Float
	 */
	public float toFloat_() throws JasmineException {
		if(value instanceof Double)
			return ((Double) value).floatValue();
		else if (value instanceof Integer) 
			return ((Integer) value).floatValue();
		else if (value instanceof Long)
			return ((Long) value).floatValue();
		else if (value instanceof Float)
			return ((Float) value).floatValue();
		else if (value instanceof String) {
			try {
				return Float.parseFloat(((String) value));
			} catch (NumberFormatException e) {
				// Allow to pass through to nException
			}
		}
		throw new JasmineException(ErrorType.INVALID_CAST, ErrorType.INVALID_CAST.message + "float");
	}


	/**
	 * Get the Float value of the base object if a direct conversion 
	 * can take place. If the base object cannot be converted to a Float
	 * then display an error message and return zero.
	 * 
	 * @return Float interpretation of the base object.
	 */
	public float toFloat() {
		try {
			return toFloat_();
		} catch (JasmineException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	/**
	 * Get an Integer interpretation of the base object if a direct
	 * conversion can take place.
	 * 
	 * @return Integer interpretation of the base object.
	 * 
	 * @throws JasmineException if the base object is not an Integer or cannot be converted to an Integer
	 */
	public int toInteger_() throws JasmineException {
		if (value instanceof Integer)
			return ((Integer) value).intValue();
		else if (value instanceof Long) 
			return ((Long) value).intValue();
		else if (value instanceof Double) 
			return ((Double) value).intValue();
		else if (value instanceof Float) 
			return ((Float) value).intValue();
		else if (value instanceof String) {
			try {
				Double parsed = Double.parseDouble(((String) value));
				return parsed.intValue();
			} catch (NumberFormatException e) {
				// Allow to pass through to INVALID_CAST error
			}
		}
		throw new JasmineException(ErrorType.INVALID_CAST, ErrorType.INVALID_CAST.message + "int");
	}

	/**
	 * Get the Integer value of the base object if a direct conversion 
	 * can take place. If the base object cannot be converted to an 
	 * Integer then display an error message and return zero.
	 * 
	 * @return Double interpretation of the base object.
	 */
	public int toInteger() {
		try {
			return toInteger_();
		} catch (JasmineException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	/**
	 * Get the Boolean interpretation of the base object if a direct
	 * conversion can take place.
	 * 
	 * @return Boolean interpretation of the base object.
	 * 
	 * @throws JasmineException if the base object is not a Boolean or cannot be converted to a Boolean
	 */
	public boolean toBoolean_() throws JasmineException {
		if (value instanceof Boolean)
			return ((Boolean) value).booleanValue();
		else if(value instanceof Double)
			return ((Double) value) != 0;
		else if (value instanceof Integer) 
			return ((Integer) value) != 0;
		else if (value instanceof Long)
			return ((Long) value) != 0;
		else if (value instanceof Float)
			return ((Float) value) != 0;
		else if (value instanceof String) {
			if (((String) value).equalsIgnoreCase("true")) {
				return true;
			} else if (((String) value).equalsIgnoreCase("false")) {
				return false;
			}
		}
		throw new JasmineException(ErrorType.INVALID_CAST, ErrorType.INVALID_CAST.message + "boolean");
	}

	/**
	 * Get the Boolean value of the base object if a direct conversion 
	 * can take place. If the base object cannot be converted to a 
	 * Boolean then display an error message and return false.
	 * 
	 * @return Boolean interpretation of the base object.
	 */
	public boolean toBoolean(){
		try {
			return toBoolean_();
		} catch (JasmineException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

}
