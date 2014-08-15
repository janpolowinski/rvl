package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;

public class UseCasePO_9_Containment extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		//process.setUriStart("http://purl.org/obo/owl/");
		
		project.setReasoningDataModel(Reasoning.rdfs); // setting to rdfs makes it necessary to remove the transitive hull (default setting at the moment),
		   											   // but shape is not evaluated otherwise

		project.registerMappingFile(ExampleFile.get("life-sciences/plant-ontology/example-mappings/PO_9_containment.ttl"));
		project.registerDataFile(ExampleFile.get("life-sciences/plant-ontology/example-data/po_anatomy.owl"));
		project.registerDataFile(ExampleFile.get("life-sciences/plant-ontology/example-mappings/extra-data.ttl"));
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorTreeJSON());
		project.setD3GraphicFile("circle-packing-zoomable/index.html");
		
		loadProjectAndRunProcess();
	}


}
