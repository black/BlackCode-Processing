package it.ppm.codebookmodel;

/**
 *@author Federico Bartoli
 *@author Mattia Masoni

*/

/**
 * This class represent the exception on the class CbModel size.
 */
@SuppressWarnings("serial")
public class WrongSizeException extends Exception{
	WrongSizeException(){
	    
		super("Dimension of input's image not equal to the size set in the model");
	
	}

}
