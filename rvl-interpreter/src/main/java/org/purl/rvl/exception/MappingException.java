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
	
	public MappingException() {
		super(GENERAL_MESSAGE);
	}
	
	public MappingException(Throwable cause) {
		super(GENERAL_MESSAGE, cause);
	}

	public MappingException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}
	
	public MappingException(String string, Throwable cause) {
		super(GENERAL_MESSAGE + " " + string, cause);
	}

	public MappingException(MappingX mapping, String string) {
		super("Mapping " + mapping + " could not be applied: " + string);
	}
	
	public MappingException(MappingX mapping, String string, Throwable cause) {
		super("Mapping " + mapping + " could not be applied: " + string, cause);
	}

}
