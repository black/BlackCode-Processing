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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Creates the regular expression for each operator and the composite one
 * for the tokeniser.
 * 
 * @author Peter Lager
 *
 */
class RegexMaker {

	private static String r = "<([{^-=$!|]})?*+.>";
	private static String bs = "\\";
	
	public static String regexFromSymbol(String symbol){
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
	
	public static String regexFromOperators(List<Operator> ops){
		Collections.sort(ops, new OrderOps());
		int nbr = ops.size();
		StringBuilder sb = new StringBuilder(ops.get(0).regex);
		for(int i = 1; i < nbr; i++){
			sb.append("|");
			sb.append(ops.get(i).regex);
		}
		return sb.toString();
	}
	
	private static class OrderOps implements Comparator<Operator> {

		@Override
		public int compare(Operator o1, Operator o2) {
			return o2.symbol.compareTo(o1.symbol);
		}
		
	}
}
