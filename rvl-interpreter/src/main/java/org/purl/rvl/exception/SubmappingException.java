package org.purl.rvl.exception;

public class SubmappingException extends MappingException {

	private static final long serialVersionUID = 1L;
	
	static String GENERAL_MESSAGE = "Submapping could not be applied. ";

	public SubmappingException() {
		super(GENERAL_MESSAGE);
	}
	
	public SubmappingException(Throwable cause) {
		super(GENERAL_MESSAGE, cause);
	}
	
	public SubmappingException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}
	
	public SubmappingException(String string, Throwable cause) {
		super(GENERAL_MESSAGE + " " + string, cause);
	}

}
