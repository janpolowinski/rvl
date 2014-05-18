/**
 * 
 */
package org.purl.rvl.exception;

/**
 * @author Jan Polowinski
 *
 */
public class IncompleteColorValuesException extends Exception {

	/**
	 * @param message
	 */
	public IncompleteColorValuesException() {
		super("Color values are incomplete or not given at all. Checked for RGB (hex string) and single r,g,b values.");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
