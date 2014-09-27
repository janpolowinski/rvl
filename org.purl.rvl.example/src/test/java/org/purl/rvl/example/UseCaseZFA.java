package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;

public class UseCaseZFA extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project.registerMappingFile(ExampleFile.get("life-sciences/zebra-fish-anatomy/example-mappings/experimental-wip.ttl"));
		project.registerDataFile(ExampleFile.get("life-sciences/zebra-fish-anatomy/example-data/ZFA_subset.ttl")); // TODO: original file causes heap space problems
		
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorTreeJSON());
		//project.setD3Generator(new D3GeneratorSimpleJSON());
		
		loadProjectAndRunProcess();
		
		assertGeneratedJSONEqualsExpected();
	}

	@Override
	protected String getExpectedD3JSONFileName() {
		return "use-case-zfa.json";
	}

}
