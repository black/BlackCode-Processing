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

import org.objectweb.asm.Type;

/**
 * Constants used in the Jasmine library
 * 
 * @author Peter Lager
 *
 */
public interface CompileConstants {
	

	// The name of the class to hold the expression (something unique)
	String DUMMY_ALGORITHM_CLASS = "QuarkAlgorithm";
	// The super class that holds the core functionality
	static String ALGORITHM_CLASS = Type.getInternalName(Algorithm.class);
	static String ALGORITHM_CLASS_ID = Type.getDescriptor(Algorithm.class);

	// The name of the class to hold the expression (something unique)
	String DUMMY_EXPRESSION_CLASS = "QuarkExpression";
	// The super class that holds the core functionality
	String EXPRESSION_CLASS = Type.getInternalName(Expression.class);
	String EXPRESSION_CLASS_ID = Type.getDescriptor(Expression.class);
	
	// The answer class
	String ANSWER_CLASS = Type.getInternalName(Answer.class);
	String ANSWER_CLASS_ID = Type.getDescriptor(Answer.class);

}
