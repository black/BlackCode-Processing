package it.ppm.codebookmodel;

import java.util.LinkedList;

/**
 *@author Federico Bartoli
 *@author Mattia Masoni

*/

/**
 * This class represent a pixel during learnig's state.This class is codeword's list.
 */
public class CodeBook{

	private LinkedList<CodeWord> CbList; 

	/**
	 * 	Construct a new object CodeBook.	
	 *
	 */
	public CodeBook(){

		CbList=new LinkedList<CodeWord>();

	}
	/**
	 * Add a new element  with the specified parameters(codeword).
	 * @param new_vm is a vector of 3 elements,represent  the RGB's compnent of pixel's color.	
	 * @param new_aux is a vector of 6 elements that represent:
	  								-the min brightness that the codeword accepted
									-the max brightness that the codeword accepted
									-the frequency with which the codeword has occurred
									-the maximun negative run-lenght defined as the longhest interval during the
								   		"Learning" state that the codeword has not recurred
									-the first that the codeword has occurred
									-the last that the codeword has occurred

	 */
	public void add(float new_vm[],float new_aux[]){
	
		CbList.add(new CodeWord(new_vm,new_aux));

	}
	/**
	 * 	Update a codeword.
	 * @param index index at which the specified codeword is to be modified.
	 * @param vm is a vector of 3 elements,represent  the RGB's compnent of pixel's color.	
	 * @param aux is a vector of 6 elements that represent:
	 * 							-the min brightness that the codeword accepted
								-the max brightness that the codeword accepted
								-the frequency with which the codeword has occurred
								-the maximun negative run-lenght defined as the longhest interval during the
								   "Learning" state that the codeword has not recurred
								-the first that the codeword has occurred
								-the last that the codeword has occurred

	 */
	public void update(int index,float [] vm,float aux[]){
		
		CbList.set(index,new CodeWord(vm,aux));
	
	}
	/**
	 * 	Returns the number of point belonging to the list.

	 * @return the number of point belonging to the list(is is greater than zero)
	 */
	public int size(){
	
		return CbList.size();
	
	}
	/**
	 * 	Returns the object codeword at the specified index in the list
	 * @param index index at which the specified codeword is to be returned
	 * @return he object codeword at the specified index in the list
	 */
	public CodeWord get(int index){
		
		return CbList.get(index);
		
	}
	/**
	 * 	Delete the codeword at the specified index 		
	 * @param index index at which the specified codeword is to be deleted
	 */
	public void remove(int index){
		
		CbList.remove(index);
		
	}
}
