 package org.purl.rvl.tooling.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.purl.rvl.exception.OGVICRepositoryException;
import org.purl.rvl.tooling.d3vis.embeddedserver.context.ContextUtils;
import org.purl.rvl.tooling.d3vis.embeddedserver.server.JettyServer;

/**
 * @author Jan Polowinski
 *
 */
public class OGVICConsole {
	
	private final static Logger LOGGER = Logger.getLogger(OGVICConsole.class.getName()); 
	static final String NL = System.getProperty("line.separator");
	
	private JettyServer jettyServer;
	private boolean firstRun = true;
	private VisProjectLibrary library;
	
    public static void main(String[] args) throws IOException, OGVICRepositoryException {
    	
    	OGVICConsole console =  new OGVICConsole();
    	console.runConsole();
        
    }


	/**
	 * @throws IOException
	 * @throws OGVICRepositoryException 
	 */
	public void runConsole() throws IOException, OGVICRepositoryException {
    	
    	boolean stop = false;
    	String inputString;
    	
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	
    	onInit();
    	onStart();
    	
    	while (stop == false) {
    		
    		System.out.println();
    		
    		printLegend();
    		
    		inputString = br.readLine();
    		
	        firstRun = false;
    		
    		if (inputString.equals("q")) {
	        	stop = true;
	        	onExit();
	        	continue;
	        } else if (inputString.equals("l")) {
	        	new OGVICConsoleProjects(library).runConsole();
	        	continue;
	        } else if (inputString.equals("f")) {
	        	new OGVICConsoleFiles().runConsole();
	        	continue;
	        } else {
        		System.err.println("'" + inputString + "' is not a valid option. Typo?");
        		continue;
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
	        	
    	}
	}

	private void printLegend() {
		
		String legend = "";
		
		String manualModeSelection = "Select visualisation project library mode (l) or file-based mode (f).";
		String manualQuit = "Entering (q) will quit the programm.";
		String hl = "###########################################" + NL;
		
		if (firstRun) {
			legend += hl;
			legend += "RVL-Tooling" + NL;
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
		ContextHandlerCollection contexts = ContextUtils.getContexts();
		jettyServer = new JettyServer();
		jettyServer.setHandler(contexts);
		
		library = getVisProjectLibrary();
	}

	private void onStart() {
		try {
			startFrontEndServer();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private void onExit() {
		try {
			stopFrontEndServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startFrontEndServer() throws Exception {
		LOGGER.info("Starting Frontend-Server ...");
		jettyServer.start();
		LOGGER.info("Started Frontend-Server");
	}
	
	private void stopFrontEndServer() throws Exception {
		if (jettyServer.isStarted()) {
			LOGGER.info("Stopping Frontend-Server ...");
			jettyServer.stop();
			LOGGER.info("Stopped Frontend-Server");
		}
	}
	
	protected VisProjectLibrary getVisProjectLibrary() {
		VisProjectLibrary library = VisProjectLibrary.getInstance();
		return library;
	}
    
}