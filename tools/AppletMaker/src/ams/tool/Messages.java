package ams.tool;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Build strings from a pattern.
 * 
 * @author Peter Lager
 *
 */
class Messages {

	public static String build(String pattern, Object ... arguments){
		return (pattern == null) ? "" : new MessageFormat(pattern, Locale.UK).format(arguments);        
	}
	
	public static String println(String pattern, Object ... arguments){
		String s = build(pattern, arguments);
		System.out.println(s);
		return s;
	}
}
