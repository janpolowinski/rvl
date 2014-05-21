package org.purl.rvl.tooling.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * @author Jan Polowinski
 *
 */
public class OGVICConsole {
	
	private final static Logger LOGGER = Logger.getLogger(OGVICConsole.class.getName()); 
	
    public static void main(String[] args) throws IOException { 
    	
    	VisProjectLibrary library = new VisProjectLibrary();
    	
    	// set up interactive process
    	OGVICProcess interactiveProcess = OGVICProcess.getInstance();
    	
    	boolean stop = false;
    	boolean firstRun = true;
    	String inputString;
    	String useCaseName;
    	
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	
    	while (stop == false) {
    		
    		library.listProjects();
    		System.out.println();
    		
    		if (firstRun) {
    			// start getting user input
		        System.out.print("Enter use case name: ");
	    	} else {
		        // offer to stop program or run again
		        System.out.print("Stop program (q) or run further visualisation project (use case name)? : ");
	    	}
    		
    		inputString = br.readLine();
    		
	        if(inputString.equals("q")) {
	        	stop = true;
	        	continue;
	        } else {
	        	useCaseName = inputString;
	        	if (!library.contains(useCaseName)) {
	        		System.err.println("Couldn't find project '" + useCaseName + "'. Typo?");
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

	        // load the project (use case)
	        try {
	        	interactiveProcess.loadProject(library.getProject(useCaseName));
	        	System.out.println("Loaded project " + useCaseName);

	        } catch(Exception e){
	        	System.err.println("Could not load the project " + useCaseName);
	        }

	        // run the process
	        try {
	        	interactiveProcess.runOGVICProcess();
	        } catch(Exception e){
	        	System.err.println("Could not run the project " + useCaseName);
	        	LOGGER.severe("Reason: " + e.getMessage());
	        }
	        
	        firstRun = false;
	        	
    	}
        
    }
    
}