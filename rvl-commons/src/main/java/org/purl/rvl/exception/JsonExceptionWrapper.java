package org.purl.rvl.exception;

public final class JsonExceptionWrapper {
	
	public static final String wrapAsJSONException(String message) {
		return 	"{\"message\" : \"" + message + "\"}";
	}

}
