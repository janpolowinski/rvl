package org.purl.rvl.exception;

public class OGVICModelsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	static String GENERAL_MESSAGE = "Problem with the OGVIC models:";
	
	/**
	 * @param message
	 */
	public OGVICModelsException() {
		super(GENERAL_MESSAGE);
	}

	public OGVICModelsException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}

}
