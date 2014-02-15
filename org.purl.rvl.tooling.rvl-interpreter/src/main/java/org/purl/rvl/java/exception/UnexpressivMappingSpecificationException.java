/**
 * 
 */
package org.purl.rvl.java.exception;

/**
 * @author Jan Polowinski
 *
 */
public class UnexpressivMappingSpecificationException extends Exception {

	/**
	 * @param message
	 */
	public UnexpressivMappingSpecificationException() {
		super("Mapping cannot express the mapped values correctly. E.g. this happens when not enough target values are defined.");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
