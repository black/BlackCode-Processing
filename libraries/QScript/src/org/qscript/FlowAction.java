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

/**
 * When a FlowOperator is evaluated it returns a FlowAction object indicating the 
 * next step to be evaluated.
 * 
 * @author Peter Lager
 *
 */
public final class FlowAction extends Argument {
	
	public static final int NEXT = 0;
	public static final int BACK = 1;
	public static final int JUMP = 2;
	public static final int END = 3;
	
	public final int action;
	
	public final Argument endWith;
	
	public FlowAction(int action){
		super(null);
		this.action = action;
		this.endWith = null;
	}
	
	public FlowAction(int action, Argument endWith){
		super(null);
		this.action = action;
		this.endWith = endWith;
	}

	/**
	 * Returns true
	 */
	@Override
	public boolean isFlowAction() {
		return true;
	}

	/**
	 * Returns false
	 */
	@Override
	public boolean isArgument() {
		return false;
	}

}
