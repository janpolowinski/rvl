/**
 * 
 */
package org.purl.rvl.exception;


/**
 * @author Jan Polowinski
 *
 */
public class OGVICProcessException extends Exception {

	private static final long serialVersionUID = 1L;
	
	static String GENERAL_MESSAGE = "OGVIC process exception";
	
	public OGVICProcessException() {
		super(GENERAL_MESSAGE);
	}
	
	public OGVICProcessException(Throwable cause) {
		super(GENERAL_MESSAGE, cause);
	}

	public OGVICProcessException(String message) {
		super(GENERAL_MESSAGE + ": " + message);
	}
	
	public OGVICProcessException(String message, Throwable cause) {
		super(GENERAL_MESSAGE + ": " + message, cause);
	}

}
