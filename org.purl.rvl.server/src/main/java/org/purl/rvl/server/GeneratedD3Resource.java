package org.purl.rvl.server;

import java.io.FileNotFoundException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.purl.rvl.exception.D3GeneratorException;
import org.purl.rvl.exception.OGVICModelsException;
import org.purl.rvl.exception.OGVICRepositoryException;
import org.purl.rvl.tooling.codegen.rdfreactor.OntologyFile;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.process.VisProject;
import org.purl.rvl.tooling.process.VisProjectLibrary;
import org.purl.rvl.tooling.process.VisProjectLibraryExamples;

/**
 * Root resource (exposed at "gend3" path)
 */
@Path("gend3")
public class GeneratedD3Resource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        
    	OGVICProcess process = OGVICProcess.getInstance();

		try {
			process.registerOntologyFile(OntologyFile.VISO_GRAPHIC);
			process.registerOntologyFile(OntologyFile.RVL);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		VisProject project = VisProjectLibraryExamples.getInstance().getProject("rvl");
		
		try {
			process.loadProject(project);
		} catch (OGVICRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			process.runOGVICProcess();
		} catch (D3GeneratorException | OGVICModelsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String json = "could not be generated";
		
		try {
			json = process.getGeneratedD3json();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return json;
    	
    }
}
