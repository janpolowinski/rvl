/**
 * 
 */
package org.purl.rvl.exception;

/**
 * @author Jan Polowinski
 *
 */
public class InsufficientMappingSpecificationException extends MappingException {

	static String GENERAL_MESSAGE = "Mapping insuffiently specified. Missing source or target property?";

	
	/**
	 * @param message
	 */
	public InsufficientMappingSpecificationException() {
		super(GENERAL_MESSAGE);
	}

	public InsufficientMappingSpecificationException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
