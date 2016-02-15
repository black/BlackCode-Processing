package org.qscript;

/**
 * A Result object is created when an expression or algorithm has been evaluated. <br>
 * 
 * To be safe when using the result it is best to see if it is valid with the isValid() method.
 * 
 * 
 * @author Peter Lager
 *
 */
public class Result {

	public final static Result INVALID = new Result(null);
	
	/** The answer to the script */
	public final Argument answer;
	/** Line number where the error (if any) occurred */
	public final int lineNo;
	/** Error description */
	public final String errorMessage;

	/**
	 * Fail result.
	 * 
	 * @param lineNo line number where the error occurred
	 * @param errorMessage textual description of the error
	 */
	public Result(int lineNo, String errorMessage) {
		this.answer = null;
		this.lineNo = lineNo;
		this.errorMessage = errorMessage;
	}

	/**
	 * Success result.
	 * 
	 * @param answer
	 */
	public Result(Argument answer) {
		this.answer = answer;
		this.lineNo = -1;
		if(answer == null)
			errorMessage = "INAVLID";
		else
			errorMessage = "";
	}

	/**
	 * Is the answer valid
	 * @return true if the answer and the answer value is not null
	 */
	public boolean isValid(){
		return answer != null && !answer.isNull;
	}

	/**
	 * return error message if argument is invalid else return String representation
	 * of the argument.
	 */
	public String toString(){
		if(answer == null)
			return errorMessage;
		else
			return answer.toString();
	}
}
