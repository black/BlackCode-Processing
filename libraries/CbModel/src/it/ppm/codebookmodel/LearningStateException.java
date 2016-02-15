package it.ppm.codebookmodel;

/**
 *@author Federico Bartoli
 *@author Mattia Masoni

*/

/**
  This class represent the exception on the class CbModel state
 */
@SuppressWarnings("serial")
public class LearningStateException extends Exception{

	/**
	 * 	Create a new object LearningstateException
	 *
	 */
	LearningStateException(){
	    
		super("Impossible to pass the state 'test' to the state of 'learning'");
	
	}
}
