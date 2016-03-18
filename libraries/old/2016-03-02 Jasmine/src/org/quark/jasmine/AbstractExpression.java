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
 * Base class for both Expression(s) and Algorithm(s). <br>
 * 
 * Holds basic information about the time to build the expression/algorithm
 * class and the time to perform the evaluation. <br>
 * 
 * @author Peter Lager
 *
 */
class AbstractExpression {
	
	protected long __buildTime;
	protected long __evalTime;
	protected Answer __answer;
	
	/**
	 * Default constructor to initialize fields.
	 */
	public AbstractExpression(){
		super();
		__buildTime = 0L;
		__evalTime = 0L;
		__answer = new Answer(null);
	}
	
	/**
	 * Use this with Expression(s) to get the answer.
	 * @return the result of evaluating the expression
	 */
	public Answer answer(){
		return __answer;
	}
	
	/**
	 * Get the length of time it took to build the expression/algorithm
	 * @return build time in nanoseconds
	 */
	public long getBuildTime() {
		return __buildTime;
	}

	/**
	 * Get the length of time it took to evaluate the expression/algorithm
	 * @return evaluation time in nanoseconds
	 */
	public long getEvalTime() {
		return __evalTime;
	}
}
