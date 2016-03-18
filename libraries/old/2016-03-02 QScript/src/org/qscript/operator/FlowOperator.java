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

/**
 * A flow operator is one which controls the flow of operations 
 * found in conditional and loop constructs.
 * 
 * @author Peter Lager
 *
 */
public abstract class FlowOperator extends Operator {

	public final FlowOperatorType fop_type;

	public FlowOperator(FlowOperatorType fop_type, String symbol, int nbrArgs, int priority, int type) {
		super(symbol, nbrArgs, priority, type);
		this.fop_type = fop_type;
	}

	/**
	 * Determines whether the supplied flow construct is a valid end construct for this one.
	 * @param possibleEnd flow construct to test
	 * @return true if valid
	 */
	public boolean isMatchingStartFor(FlowOperator possibleEnd){	
		return fop_type.isMatchingStartFor(possibleEnd.fop_type);
	}

	/**
	 * Determines whether the supplied flow construct is a valid start construct for this one.
	 * @param possibleStart flow construct to test
	 * @return true if valid
	 */
	public boolean isMatchingEndFor(FlowOperator possibleStart){	
		return fop_type.isMatchingStartFor(possibleStart.fop_type);
	}

	/**
	 * @return the type
	 */
	public FlowOperatorType getFopType() {
		return fop_type;
	}
	
	/**
	 * Returns true
	 */
	@Override
	public boolean isFlowOperator() {
		return true;
	}

	public String toString(){
		return symbol;
	}
}