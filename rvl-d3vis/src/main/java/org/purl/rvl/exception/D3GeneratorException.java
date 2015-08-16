/**
 * 
 */
package org.purl.rvl.exception;


/**
 * @author Jan Polowinski
 *
 */
public class D3GeneratorException extends Exception {

	private static final long serialVersionUID = 1L;
	
	static String GENERAL_MESSAGE = "D3 Code could not be generated:";
	
	/**
	 * @param message
	 */
	public D3GeneratorException() {
		super(GENERAL_MESSAGE);
	}

	public D3GeneratorException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}


}
