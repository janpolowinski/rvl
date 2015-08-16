/**
 * 
 */
package org.purl.rvl.exception;

import java.io.FileNotFoundException;


/**
 * @author Jan Polowinski
 *
 */
public class OGVICRepositoryException extends Exception {

	private static final long serialVersionUID = 1L;
	
	static String GENERAL_MESSAGE = "Repository problem";
	
	/**
	 * @param message
	 */
	public OGVICRepositoryException() {
		super(GENERAL_MESSAGE);
	}
	
	public OGVICRepositoryException(Throwable cause) {
		super(GENERAL_MESSAGE, cause);
	}

	public OGVICRepositoryException(String string) {
		super(GENERAL_MESSAGE + ": " + string);
	}
	
	public OGVICRepositoryException(String string, Throwable cause) {
		super(GENERAL_MESSAGE + ": " + string, cause);
	}

	public OGVICRepositoryException(String repositoryName, String string) {
		super("Repository " + repositoryName + " has a problem: " + string);
	}

	public OGVICRepositoryException(String repositoryName, String string, Throwable cause) {
		super("Repository " + repositoryName + " has a problem: " + string, cause);
	}

}
