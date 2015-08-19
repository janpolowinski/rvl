/**
 * 
 */
package org.purl.rvl.exception;

import org.purl.rvl.java.rvl.MappingX;

/**
 * @author Jan Polowinski
 *
 */
public class MappingException extends Exception {

	private static final long serialVersionUID = 1L;
	
	static String GENERAL_MESSAGE = "Mapping could not be applied:";
	
	/**
	 * @param message
	 */
	public MappingException() {
		super(GENERAL_MESSAGE);
	}

	public MappingException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}

	public MappingException(MappingX mapping, String string) {
		super("Mapping " + mapping + " could not be applied: " + string);
	}

}
