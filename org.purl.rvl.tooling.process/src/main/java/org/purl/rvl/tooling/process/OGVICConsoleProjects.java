package org.purl.rvl.tooling.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * @author Jan Polowinski
 *
 */
public class OGVICConsoleProjects {
	
	private final static Logger LOGGER = Logger.getLogger(OGVICConsoleProjects.class.getName()); 
	
    public static void main(String[] args) throws IOException { 
    	
    	runConsole();
        
    }

	/**
	 * @throws IOException
	 */
	public static void runConsole() throws IOException {
		VisProjectLibrary library = new VisProjectLibrary();
    	
    	// set up interactive process
    	OGVICProcess interactiveProcess = OGVICProcess.getInstance();
    	
    	boolean stop = false;
    	boolean firstRun = true;
    	String inputString;
    	String projectName;
    	
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	
    	while (stop == false) {
    		
    		library.listProjects();
    		System.out.println();
    		
    		if (firstRun) {
    			// start getting user input
		        System.out.print("Enter project name: ");
	    	} else {
		        // offer to stop program or run again
		        System.out.print("Stop program (q) or run further visualisation project (p)? : ");
	    	}
    		
    		inputString = br.readLine();
    		
	        if (inputString.equals("q")) {
	        	stop = true;
	        	continue;
	        } else {
	        	projectName = inputString;
	        	if (!library.contains(projectName)) {
	        		System.err.println("Couldn't find project '" + projectName + "'. Typo?");
	        		continue;
	        	}
	        }

	        /*
		        // max relations
		        System.out.print("Enter number of max relations:");
		        try {
		            int i = Integer.parseInt(br.readLine());
		            System.out.print("Will ignore other than the first " + i + " relations.");
		        } catch(NumberFormatException nfe){
		            System.err.println("Invalid Format!");
		        }*/

	        // load the project
	        try {
	        	interactiveProcess.loadProject(library.getProject(projectName));
	        	LOGGER.info("Loaded project " + projectName);

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