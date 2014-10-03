/**
 * 
 */
package org.purl.rvl.exception;

import org.purl.rvl.java.rvl.MappingX;

/**
 * @author Jan Polowinski
 *
 */
public class InsufficientMappingSpecificationException extends MappingException {

	static String GENERAL_MESSAGE = "Mapping insuffiently specified.";

	
	/**
	 * @param message
	 */
	public InsufficientMappingSpecificationException() {
		super(GENERAL_MESSAGE);
	}

	public InsufficientMappingSpecificationException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}
	
	public InsufficientMappingSpecificationException(MappingX mapping, String string) {
		super(GENERAL_MESSAGE + " " + string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
