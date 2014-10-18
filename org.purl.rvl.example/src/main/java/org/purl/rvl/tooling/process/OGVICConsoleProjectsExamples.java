/**
 * 
 */
package org.purl.rvl.tooling.process;

import java.io.IOException;

/**
 * Offers an extended example library with visualisation project covering the use cases from the case studies.
 * 
 * @author Jan Polowinski
 *
 */
public class OGVICConsoleProjectsExamples extends OGVICConsoleProjects {
	
    public static void main(String[] args) throws IOException {
    	
    	OGVICConsoleProjects console =  new OGVICConsoleProjectsExamples();
    	console.runConsole();
        
    }
	
	protected VisProjectLibrary getVisProjectLibrary() {
		VisProjectLibraryExamples library = new VisProjectLibraryExamples();
		return library;
	}

}
