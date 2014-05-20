package org.purl.rvl.interpreter.usecase;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseRVLExampleData extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {

		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE);
		//project.registerMappingFile(ExampleMapping.RVL_EXAMPLE_MINI);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		//project.registerMappingFile(ExampleMapping.RVL_EXAMPLE_OLD);
		//project.registerDataFile(ExampleData.RVL_EXAMPLE_OLD);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		
		//project.setD3Generator(new D3GeneratorSimpleJSON());
		project.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
	}


}
