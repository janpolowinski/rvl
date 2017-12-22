/**
 * 
 */
package org.purl.rvl.exception;


/**
 * @author Jan Polowinski
 *
 */
public class OGVICSystemInitException extends Exception {

	private static final long serialVersionUID = 1L;
	
	static String GENERAL_MESSAGE = "OGVIC system init exception, something went wrong when starting up OGVIC";
	
	public OGVICSystemInitException() {
		super(GENERAL_MESSAGE);
	}
	
	public OGVICSystemInitException(Throwable cause) {
		super(GENERAL_MESSAGE, cause);
	}

	public OGVICSystemInitException(String message) {
		super(GENERAL_MESSAGE + ": " + message);
	}
	
	public OGVICSystemInitException(String message, Throwable cause) {
		super(GENERAL_MESSAGE + ": " + message, cause);
	}

}
