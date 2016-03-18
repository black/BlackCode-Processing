package org.quark.jasmine;



public class Jasmine {

	public static final double TRUE = 1.0;
	public static final double FALSE = 0.0;
	
	/**
	 * return the pretty version of the library.
	 */
	public static String getPrettyVersion() {
		return "1.0.1";
	}

	/**
	 * return the version of the library used by Processing
	 */
	public static String getVersion() {
		return "2";
	}

	public static void announce(){
		System.out.println("===================================================");
		System.out.println("   Jasmine V1.0.1 created by Peter Lager");
		System.out.println("===================================================");
	}

}
