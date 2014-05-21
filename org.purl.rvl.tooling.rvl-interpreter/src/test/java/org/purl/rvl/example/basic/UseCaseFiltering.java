package org.purl.rvl.example.basic;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseFiltering extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		//project.setReasoningDataModel(Reasoning.none); // does this actually work? still a lot of resource and owl thing types

		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE_FILTERING);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);

		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		
		project.setD3Generator(new D3GeneratorSimpleJSON());
		//project.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
	}


}
