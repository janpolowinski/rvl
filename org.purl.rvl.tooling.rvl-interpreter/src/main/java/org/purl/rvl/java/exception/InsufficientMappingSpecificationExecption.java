/**
 * 
 */
package org.purl.rvl.java.exception;

/**
 * @author Jan Polowinski
 *
 */
public class InsufficientMappingSpecificationExecption extends Exception {

	static String GENERAL_MESSAGE = "Mapping insuffiently specified. Missing source or target property?";

	
	/**
	 * @param message
	 */
	public InsufficientMappingSpecificationExecption() {
		super(GENERAL_MESSAGE);
	}

	public InsufficientMappingSpecificationExecption(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
