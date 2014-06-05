/**
 * 
 */
package org.purl.rvl.exception;

/**
 * @author Jan Polowinski
 *
 */
public class UnsupportedMappingParameterValueException extends Exception {

	static String GENERAL_MESSAGE = "Mapping parameter value not supported: ";

	
	/**
	 * @param message
	 */
	public UnsupportedMappingParameterValueException() {
		super(GENERAL_MESSAGE);
	}

	public UnsupportedMappingParameterValueException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
