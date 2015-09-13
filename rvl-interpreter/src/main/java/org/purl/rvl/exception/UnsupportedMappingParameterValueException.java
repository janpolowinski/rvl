/**
 * 
 */
package org.purl.rvl.exception;

import org.purl.rvl.java.rvl.MappingX;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;

/**
 * @author Jan Polowinski
 *
 */
public class UnsupportedMappingParameterValueException extends MappingException {

	static String GENERAL_MESSAGE = "Mapping parameter value not supported: ";

	private static final long serialVersionUID = 1L;
	
	/**
	 * @param message
	 */
	public UnsupportedMappingParameterValueException() {
		super(GENERAL_MESSAGE);
	}

	public UnsupportedMappingParameterValueException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}

	public UnsupportedMappingParameterValueException(MappingX mapping, String string) {
		super(mapping, GENERAL_MESSAGE + " " + string);
	}

	public UnsupportedMappingParameterValueException(MappingX mapping, String string, Throwable cause) {
		super(mapping, GENERAL_MESSAGE + " " + string, cause);
	}
}
