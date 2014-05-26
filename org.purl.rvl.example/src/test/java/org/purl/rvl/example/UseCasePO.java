package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCasePO extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		//process.setUriStart("http://purl.org/obo/owl/");

		project.registerMappingFile(ExampleFile.get("life-sciences/plant-ontology/example-mappings/experimental-wip.ttl"));
		project.registerDataFile(ExampleFile.get("life-sciences/plant-ontology/example-data/po_anatomy.owl"));
		project.registerDataFile(ExampleFile.get("life-sciences/plant-ontology/example-mappings/extra-data.ttl"));

		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorTreeJSON());
		//process.setD3Generator(new D3GeneratorSimpleJSON());
		
		loadProjectAndRunProcess();
	}


}
