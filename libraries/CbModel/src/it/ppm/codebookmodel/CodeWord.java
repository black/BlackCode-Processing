package it.ppm.codebookmodel;

/**
 *@author Federico Bartoli
 *@author Mattia Masoni

*/

/**
 * This class represent a pixel during "Leranig" 'state
 */
public class CodeWord {

	public float vm[];
	public float aux[];
	
	/**
	 * Create a new object CodeWord.
	 * @param new_vm is a vector of 3 elements,represent  the RGB's compnent of pixel's color.	
	 * @param new_aux is a vector of 6 elements that represent:
	 * 							-the min brightness that the codeword accepted
								-the max brightness that the codeword accepted
								-the frequency with which the codeword has occurred
								-the maximun negative run-lenght defined as the longhest interval during the
								  "Learning" state that the codeword has not recurred
								-the first that the codeword has occurred
								-the kast that the codeword has occurred

	 */
	public CodeWord(float new_vm[],float new_aux[]){

		vm=new float[3];
		aux=new float[6];
		for(int i=0;i<3;i++)
			vm[i]=new_vm[i];
		for(int i=0;i<6;i++)
			aux[i]=new_aux[i];

	}

}
