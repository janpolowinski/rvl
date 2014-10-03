package org.purl.rvl.tooling.process;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Allows for using the OGVIC process with a data and a mapping file as input.
 * Both files will be requested by a dialogue.
 * 
 * @author Jan Polowinski
 *
 */
public class OGVICConsoleFiles {
	
	private final static Logger LOGGER = Logger.getLogger(OGVICConsoleFiles.class.getName()); 
	
    public static void main(String[] args) throws IOException { 

    	// set up interactive process
    	OGVICProcess interactiveProcess = OGVICProcess.getInstance();
    	
    	boolean stop = false;
    	boolean firstRun = true;
    	String inputString;
    	String projectName = "adhoc project";

    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	
    	while (stop == false) {
    		
    		System.out.println();
    		
    		if (!firstRun) {
		        // offer to stop program or run (again)
		        System.out.print("Stop program (q) or run (further) visualization process (v)? : ");
		        
		    	inputString = br.readLine();
	    		
		        if (inputString.equals("q")) {
		        	stop = true;
		        	continue;
		        }
	    	}
    		
        	VisProject project = new VisProject(projectName);
	        
	        System.out.print("Enter data file name: ");
    		inputString = br.readLine();
	        
	        try {
	        	project.registerDataFile(inputString);
	        } catch (FileNotFoundException e) {
	        	System.err.println("Couldn't find data file '" + inputString + "'. Typo?");
	        }
	        
	        System.out.print("Enter mapping file name: ");
    		inputString = br.readLine();
	        
	        try {
	        	project.registerMappingFile(inputString);
	        } catch (FileNotFoundException e) {
	        	System.err.println("Couldn't find mapping file '" + inputString + "'. Typo?");
	        }

	        // configure adhoc project
	        try {
	        	//project.setD3Generator(new D3GeneratorTreeJSON());
	        	interactiveProcess.loadProject(project);

	        } catch(Exception e){
	        	LOGGER.severe("Could not load the project " + projectName);
	        }

	        // run the process
	        try {
	        	interactiveProcess.runOGVICProcess();
	        } catch(Exception e){
	        	LOGGER.severe("Could not run the project " + projectName + " Reason: " + e.getMessage());
	        }
	        
	        firstRun = false;
	        	
    	}
        
    }
    
}