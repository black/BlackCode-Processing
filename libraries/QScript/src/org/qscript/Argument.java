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

import org.qscript.errors.ErrorType;
import org.qscript.errors.EvaluationException;

/**
 * 
 * An argument represents a dynamically typed un-named value and is used to store 
 * the intermediate and final results of an expression evaluation. <br>
 * 
 * Internally a value will either be null or one of these data types :- <br>
 * <ul>
 * <li>Integer</li>
 * <li>Double</li>
 * <li>Boolean</li>
 * <li>String</li>
 * </ul>
 * 
 * When creating an Argument or setting its value, the (new) value to be used
 * will be converted (if necessary) into one of the supported types. <br>
 * <ul>
 * <li>byte / Byte </li>
 * <li>short / Short </li>
 * <li>long / Long </li>
 * </ul>
 * will be converted to Integer <br><br>
 * <ul>
 * <li>float / Float </li>
 * </ul>
 * will be converted to Double <br><br>
 * <li>char / Character </li>
 * </ul>
 * will be converted to String <br><br>
 * 
 * Any other object type will result in the value being null. 
 * 
 * 
 * @author Peter Lager
 */
public class Argument extends Token {

	private final Object value;

	public final boolean isNull;
	public final boolean isString;
	public final boolean isInteger;
	public final boolean isDouble;
	public final boolean isBoolean;
	public final boolean isVector;
	public final boolean isComplex;
	public final boolean isThing;
	public final boolean isNumeric;


	/**
	 * Create an Argument to act as the operand for the various operators
	 * 
	 * @param v the value to store in this argument.
	 */
	public Argument(Object v) {
		super();
		Object temp = null;
		if(v instanceof String || v instanceof Integer || v instanceof Double || v instanceof Boolean )
			temp = v;
		else if(v instanceof Float)
			temp = new Double(((Number)v).doubleValue());
		else if(v instanceof Character)
			temp = new String(v.toString());
		else if(v instanceof Long || v instanceof Short || v instanceof Byte)
			temp = new Integer(((Number)v).intValue());
		else if(v instanceof Vector || v instanceof Complex || v instanceof Thing)
			temp = v;
		value = temp;

		isNull = value == null;
		isString = value instanceof String;
		isInteger = value instanceof Integer;
		isDouble = value instanceof Double;
		isBoolean = value instanceof Boolean;
		isVector = value instanceof Vector;
		isComplex = value instanceof Complex;
		isThing = value instanceof Thing;
		isNumeric = isInteger | isDouble;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Argument) {
			Argument a = (Argument) o;
			if(isString || a.isString) {
				return toString().equals(a.toString());
			} else {
				return value.equals(a.getValue());
			}
		}
		return false;
	}

	/**
	 * Get the value stored in this Argument.
	 * 
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns true
	 */
	@Override
	public boolean isArgument() {
		return true;
	}

	/**
	 * If the value is of type Double and it is Not a Number (NaN) then return true.
	 *  In all other circumstances return false.
	 */
	public boolean isNan(){
		return value instanceof Double && Double.isNaN((Double)value);
	}

	/**
	 * Get the Vector value of the base object. If the base object 
	 * is not of type Vector then throw an exception
	 * 
	 * @return vector value of the base object.
	 * 
	 * @throws EvaluationException if the base object is not a Vector
	 */
	public Vector toVector_() 
			throws EvaluationException {
		if (isVector)
			return ((Vector) value);
		throw new EvaluationException(ErrorType.INVALID_CAST, this);
	}

	/**
	 * Get the Vector value of the base object. If the base object 
	 * is not of type Vector then display an error message and 
	 * return false.
	 * 
	 * @return vector value of the base object.
	 */
	public Vector toVector(){
		try {
			return toVector_();
		} catch (EvaluationException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	/**
	 * Get the Complex number value of the base object. If the base object 
	 * is not of type Complex then throw an exception
	 * 
	 * @return complex number value of the base object.
	 * 
	 * @throws EvaluationException if the base object is not a Complex
	 */
	public Complex toComplex_() 
			throws EvaluationException {
		if (isComplex)
			return ((Complex) value);
		throw new EvaluationException(ErrorType.INVALID_CAST, this);
	}

	/**
	 * Get the Complex number value of the base object. If the base object 
	 * is not of type Complex then display an error message and 
	 * return false.
	 * 
	 * @return complex number value of the base object.
	 */
	public Complex toComplex(){
		try {
			return toComplex_();
		} catch (EvaluationException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}


	/**
	 * Get the Boolean interpretation of the base object if a direct
	 * conversion can take place.
	 * 
	 * @return Boolean interpretation of the base object.
	 * 
	 * @throws EvaluationException if the base object is not a Boolean or 
	 * cannot be converted to a Boolean
	 */
	public boolean toBoolean_() 
			throws EvaluationException {
		if (value instanceof Boolean)
			return ((Boolean) value).booleanValue();
		else if (value instanceof String) {
			if (((String) value).equalsIgnoreCase("true")) {
				return true;
			} else if (((String) value).equalsIgnoreCase("false")) {
				return false;
			}
		}
		throw new EvaluationException(ErrorType.INVALID_CAST, this);
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
		} catch (EvaluationException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/**
	 * Attempt to convert the base object to a Boolean.
	 * 
	 * @throws EvaluationException if the base object cannot be 
	 * converted to a Boolean
	 */
	//	public void castToBoolean_() 
	//			throws EvaluationException{
	//		if (value instanceof Boolean) 
	//			return;
	//		value = toBoolean_();
	//	}

	/**
	 * Get the Double interpretation of the base object if a direct
	 * conversion can take place.
	 * 
	 * @return Double interpretation of the base object.
	 * 
	 * @throws EvaluationException if the base object is not a Double or cannot be converted to a Double
	 */
	public double toDouble_() 
			throws EvaluationException {
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
		throw new EvaluationException(ErrorType.INVALID_CAST, this);
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
		} catch (EvaluationException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	/**
	 * Attempt to convert the base object to a Double.
	 * 
	 * @throws EvaluationException if the base object cannot be 
	 * converted to a Double
	 */
	//	public void castToDouble_() 
	//			throws EvaluationException {
	//		if(value instanceof Double)
	//			return;
	//		value = toDouble_();
	//	}

	/**
	 * Get the Float interpretation of the base object if a direct
	 * conversion can take place.
	 * 
	 * @return Float interpretation of the base object.
	 * 
	 * @throws EvaluationException if the base object is not a Float or cannot be converted to a Float
	 */
	public float toFloat_() 
			throws EvaluationException {
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
				// Allow to pass through to EvaluationException
			}
		}
		throw new EvaluationException(ErrorType.INVALID_CAST, this);
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
		} catch (EvaluationException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	/**
	 * Attempt to convert the base object to a Float.
	 * 
	 * @throws EvaluationException if the base object cannot be 
	 * converted to a Float
	 */
	//	public void castToFloat_() 
	//			throws EvaluationException {
	//		if(value instanceof Float)
	//			return;
	//		value = toFloat_();
	//	}


	/**
	 * Get an Integer interpretation of the base object if a direct
	 * conversion can take place.
	 * 
	 * @return Integer interpretation of the base object.
	 * 
	 * @throws EvaluationException if the base object is not an Integer 
	 * or cannot be converted to an Integer
	 */
	public int toInteger_() 
			throws EvaluationException {
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
		throw new EvaluationException(ErrorType.INVALID_CAST, this);
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
		} catch (EvaluationException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	/**
	 * Attempt to convert the base object to an Integer.
	 * 
	 * @throws EvaluationException if the base object cannot be 
	 * converted to an Integer
	 */
	//	public void castToInteger_() throws EvaluationException {
	//		if (value instanceof Integer)
	//			return;
	//		value = toInteger_();
	//	}

	/**
	 * Convert the base object to a String.
	 * 
	 */
	//	public void castToString() {
	//		if (value instanceof String)
	//			return;
	//		value = toString();
	//	}

	@Override
	public String toString() {
		if(value != null)
			return value.toString();
		else
			return "INVALID";
	}

//	/**
//	 * Does this Argument have a Boolean value.
//	 * 
//	 * @return true if Boolean else false.
//	 */
//	public boolean isBoolean() {
//		return isBoolean;
//	}
//
//	/**
//	 * Does this Argument have a Double value.
//	 * 
//	 * @return true if Double else false.
//	 */
//	public boolean isDouble() {
//		return isDouble;
//	}
//
//	/**
//	 * Does this Argument have an Integer value.
//	 * 
//	 * @return true if Integer else false.
//	 */
//	public boolean isInteger() {
//		return isInteger;
//	}
//
//	/**
//	 * Determine if the base object is a literal string.
//	 * 
//	 * @return whether or not the argument is a literal string
//	 */
//	public boolean isString() {
//		return isString;
//	}
//
//	/**
//	 * Determine if the base object is numeric i.e. a double or an integer
//	 * @return true if this argument is numeric (i.e. integer or double)
//	 */
//	public boolean isNumeric(){
//		return isNumeric;
//	}
//
//	/**
//	 * Determine if the base object is a Vector
//	 * @return true if this argument is a Vector
//	 */
//	public boolean isVector(){
//		return isVector;
//	}
//
//	/**
//	 * Determine if the base object is a complex number.
//	 * @return true if this argument is a complex number
//	 */
//	public boolean isComplex(){
//		return isComplex;
//	}
//
//	/**
//	 * Determine if the base object is null.
//	 * 
//	 * @return whether or not the argument is null
//	 */
//	public boolean isNull() {
//		return isNull;
//	}
}

