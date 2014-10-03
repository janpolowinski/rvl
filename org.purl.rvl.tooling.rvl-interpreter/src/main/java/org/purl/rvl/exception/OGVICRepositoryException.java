/**
 * 
 */
package org.purl.rvl.exception;


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

	public OGVICRepositoryException(String string) {
		super(GENERAL_MESSAGE + ": " + string);
	}

	public OGVICRepositoryException(String repositoryName, String string) {
		super("Repository " + repositoryName + " has a problem: " + string);
	}

}
