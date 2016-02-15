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

import org.objectweb.asm.Label;

/**
 * This defines a go-to-label when compiling an algorithm.
 * 
 * @author Peter Lager
 *
 */
final class JumpLabel {

	public static int IF			= 0x10001;
	public static int IF_ELSE		= 0x10002;
	public static int IF_ENDIF		= 0x10004;
	
	public static int WHILE			= 0x20001;
	public static int WEND			= 0x20002;
	
	public static int REPEAT		= 0x30001;
	public static int UNTIL			= 0x30002;
	
	public static int FOR			= 0x40001;
	public static int ENDFOR		= 0x40002;
	
	public static int STOP			= 0x80001;
	
	final int type;	
	final Label label;

	public JumpLabel(int type) {
		super();
		this.type = type;
		this.label = new Label();
	}

	
	
}
