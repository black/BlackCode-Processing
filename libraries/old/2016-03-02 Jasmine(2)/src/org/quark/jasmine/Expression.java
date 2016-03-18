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
 * Abstract class for all Expressions.
 * 
 * @author Peter Lager
 *
 */
public abstract class Expression extends AbstractExpression {

	/**
	 * This method is overridden in the generated class. <p>
	 * 
	 * This method accepts zero or more values to be used in the expression. They 
	 * are matched, in order, to the variables found in the expression. For example if the expression is <br>
	 * <pre>"4 + h * a / sin(a * b)"</pre>
	 * then evaluating the method with <br>
	 * <pre>expression.eval(2.1, 6.2, -7.2);</pre>
	 * will initialise the variables as 
	 * <pre>h = 2.1;  a = 6.2;  b = -7.2;</pre>
	 * 
	 *  This method returns 'this' so for example you can chain like this:
	 *  <pre>expression.eval(2.1, 6.2, -7.2).answer().toDouble(); </pre>
	 *  to get the answer as a float.
	 *  
	 * @param vars the initial values for variables
	 * @return this Expression
	 */
    public abstract Expression eval(double... vars);
}
