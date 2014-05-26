package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorSimpleJSON;

public class UseCaseROInstancesSocialNetwork extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		process.setWriteAVM(true);
		
		project.setReasoningDataModel(Reasoning.rdfs);

		project.registerMappingFile(ExampleFile.get("software/ro/example-mappings/ro-instances-social-network.ttl"));
		project.registerDataFile(ExampleFile.get("software/ro/example-mappings/extra-data.ttl"));
		project.registerDataFile(ExampleFile.get("software/ro/example-data/ro_v_1_4_1_incl_social_network_example.owl"));

		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorSimpleJSON());
		//process.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
		
	}


}
