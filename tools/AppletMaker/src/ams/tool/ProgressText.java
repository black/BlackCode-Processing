package ams.tool;

/**
 * Simple structure used to transfer progress information from the 
 * worker thread to the main thread during signing.
 * 
 * @author Peter Lager
 *
 */
class ProgressText {
	
	String text;
	boolean replace; // true removes last line before adding this one.
	
	public ProgressText(boolean replace, String text){
		this.replace = replace;
		this.text = text;
	}
	
}
