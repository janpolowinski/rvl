package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseAA_4 extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {

		project.registerMappingFile(ExampleFile.get("life-sciences/amino-acid/example-mappings/AA_4.ttl"));
		project.registerDataFile(ExampleFile.get("life-sciences/amino-acid/example-data/amino-acid.owl"));
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		//process.setD3Generator(new D3GeneratorTreeJSON());
		project.setD3Generator(new D3GeneratorSimpleJSON());

		loadProjectAndRunProcess();
	}


}
