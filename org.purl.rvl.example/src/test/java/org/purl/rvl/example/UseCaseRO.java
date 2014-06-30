package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;

public class UseCaseRO extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project.registerMappingFile(ExampleFile.get("software/ro/example-mappings/experimental-wip.ttl"));
		//project.registerDataFile(ExampleFile.get("software/ro/example-mappings/extra-data.ttl"));
		project.registerDataFile(ExampleFile.get("software/ro/example-data/ro_v_1_4_1_incl_social_network_example.owl"));
		
		project.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
		
	}


}
