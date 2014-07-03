/**
 * 
 */
package org.purl.rvl.exception;

/**
 * @author Jan Polowinski
 *
 */
public class NotImplementedMappingFeatureException extends Exception {

	static String GENERAL_MESSAGE = "Mapping feature not implemented: ";

	
	/**
	 * @param message
	 */
	public NotImplementedMappingFeatureException() {
		super(GENERAL_MESSAGE);
	}

	public NotImplementedMappingFeatureException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
