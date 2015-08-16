package org.purl.rvl.server;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.purl.rvl.exception.EmptyGeneratedException;
import org.purl.rvl.tooling.codegen.rdfreactor.OntologyFile;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.process.VisProject;
import org.purl.rvl.tooling.process.VisProjectLibraryExamples;

/**
 * Root resource (exposed at "gend3" path)
 * Returns the generated D3 from the "rvl" visualisation project for testing purposes.
 */
@Path("gend3")
public class GeneratedD3Resource {
	
	private final static Logger LOGGER = Logger.getLogger(GeneratedD3Resource.class.getName()); 

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
    	
		String json = "";
    	OGVICProcess process;
    	
    	try {
    	
			process = OGVICProcess.getInstance();
			
			process.registerOntologyFile(OntologyFile.VISO_GRAPHIC);
			process.registerOntologyFile(OntologyFile.RVL);

			VisProject project = VisProjectLibraryExamples.getInstance().getProject("rvl");
			
			process.loadProject(project);
			process.runOGVICProcess();

			try {
				json = process.getGeneratedD3json();
			} catch (EmptyGeneratedException empty) {
				LOGGER.warning(empty.getMessage() + " Proceeding anyway.");
			}
    	
	    } catch (Exception e) {
	    	json = e.getMessage();
	    	e.printStackTrace();
	    }
    	
		return json;
    }
    
}
