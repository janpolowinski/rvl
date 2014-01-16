/**
 * 
 */
package org.purl.rvl.java.exception;

/**
 * @author Jan Polowinski
 *
 */
public class InsufficientMappingSpecificationExecption extends Exception {

	/**
	 * @param message
	 */
	public InsufficientMappingSpecificationExecption() {
		super("Mapping insuffiently specified. Missing source or target property?");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
