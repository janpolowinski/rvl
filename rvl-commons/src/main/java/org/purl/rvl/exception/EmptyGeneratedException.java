/**
 * 
 */
package org.purl.rvl.exception;


/**
 * @author Jan Polowinski
 *
 */
public class EmptyGeneratedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	static String GENERAL_MESSAGE = "Warning - the generated model/text was empty!";
	
	public EmptyGeneratedException() {
		super(GENERAL_MESSAGE);
	}

	public EmptyGeneratedException(Throwable cause) {
		super(GENERAL_MESSAGE, cause);
	}

	public EmptyGeneratedException(String message) {
		super(GENERAL_MESSAGE + ": " + message);
	}

	public EmptyGeneratedException(String message, Throwable cause) {
		super(GENERAL_MESSAGE + ": " + message, cause);
	}

}
