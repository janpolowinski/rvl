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
	
	static final String NL = System.getProperty("line.separator");

	private VisProjectLibrary library;
	
    public OGVICConsoleProjects(VisProjectLibrary library) {
    	super();
    	this.library =  library;
	}

	public static void main(String[] args) throws IOException {
    	
    	OGVICConsoleProjects console =  new OGVICConsoleProjects(new VisProjectLibrary());
    	console.runConsole();
        
    }

	private boolean firstRun = true;


	/**
	 * @throws IOException
	 */
	public void runConsole() throws IOException {
    	
    	// set up interactive process
    	OGVICProcess interactiveProcess = OGVICProcess.getInstance();
    	
    	boolean stop = false;
    	String inputString;
    	String projectName;
    	
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	
    	onInit();
    	onStart();
    	
    	while (stop == false) {
    		
    		library.listProjects();
    		System.out.println();
    		
    		printLegend();
    		
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
    	onExit();
	}

	private void printLegend() {
		
		String legend = "";
		
		String manualModeSelection = "Enter a visualisation project name from the above list. ";
		String manualQuit = "Entering (q) will take you to the main menu.";
		String hl = "###########################################" + NL;
		
		if (firstRun) {
			legend += hl;
			legend += "Visualisation Library mode" + NL;
			legend += "(for selecting from the shipped tests and use-case examples)" + NL;
			legend += hl;
			// start getting user input
	        legend += manualModeSelection + " " + manualQuit + ":" + NL;
    	} else {
	        // offer to stop program or run again
	        legend += manualModeSelection + " " + manualQuit + ":" + NL;
    	}
		
		System.out.print(legend);
	}

	private void onInit() {

	}

	private void onStart() {

	}
	
	private void onExit() {

	}
    
}