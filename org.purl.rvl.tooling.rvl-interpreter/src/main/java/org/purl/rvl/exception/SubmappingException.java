package org.purl.rvl.exception;

public class SubmappingException extends MappingException {

	private static final long serialVersionUID = 1L;
	
	static String GENERAL_MESSAGE = "Mapping cannot express the mapped values correctly.";

	public SubmappingException() {
		super(GENERAL_MESSAGE);
	}
	
	public SubmappingException(String string) {
		super(GENERAL_MESSAGE + " " + string);
	}

}
