package org.purl.rvl.tooling.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OGVICConsole {
	
    public static void main(String[] args) throws IOException { 
    	
    	boolean stop = false;
    	
    	while (stop == false) {
    	
	    	// set up interactive process
	    	
	    	OGVICProcess interactiveProcess = OGVICProcess.getInstance();
	    	interactiveProcess.registerOntologyFile(OGVICProcess.VISO_LOCAL_REL);
	    	interactiveProcess.registerOntologyFile(OGVICProcess.RVL_LOCAL_REL);
	    	
	    	VisProjectLibrary library = new VisProjectLibrary();
	    	
	    	library.listProjects();
	    	
	    	System.out.println();
	
	    	// start getting user input
	    	
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        
	        // use case name
	        System.out.print("Enter use case name: ");
	        String useCaseName = br.readLine();
	        
	        
	        
	
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
	        }
	        
	        // offer to stop program
	        System.out.print("Stop program (y) or run further visualisation project (ENTER): ");
	        String stopString = br.readLine();
	        if(stopString.equals("y")) {
	        	stop = true;
	        }
    	}
        
    }
    
}