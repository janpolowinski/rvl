/**
 * 
 */
package org.purl.rvl.exception;

/**
 * @author Jan Polowinski
 *
 */
public class MappingException extends Exception {

	static String GENERAL_MESSAGE = "Mapping could not be applied:";

	
	/**
	 * @param message
	 */
	public MappingException() {
		super(GENERAL_MESSAGE);
	}

	public MappingException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
