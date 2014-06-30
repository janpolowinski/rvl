package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;

public class UseCaseAA extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project.registerMappingFile(ExampleFile.get("life-sciences/amino-acid/example-mappings/experimental-wip.ttl"));
		project.registerDataFile(ExampleFile.get("life-sciences/amino-acid/example-data/amino-acid.owl"));
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		//process.setD3Generator(new D3GeneratorTreeJSON());


		loadProjectAndRunProcess();
	}


}
