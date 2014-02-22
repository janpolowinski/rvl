/**
 * 
 */
package org.purl.rvl.java.exception;

/**
 * @author Jan Polowinski
 *
 */
public class UnexpressiveMappingSpecificationException extends Exception {
	
	static String GENERAL_MESSAGE = "Mapping cannot express the mapped values correctly.";

	/**
	 * @param message
	 */
	public UnexpressiveMappingSpecificationException() {
		super(GENERAL_MESSAGE + " E.g. this happens when not enough target values are defined.");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param arg0
	 */
	public UnexpressiveMappingSpecificationException(String arg0) {
		super(GENERAL_MESSAGE + " " + arg0);
		// TODO Auto-generated constructor stub
	}

}
