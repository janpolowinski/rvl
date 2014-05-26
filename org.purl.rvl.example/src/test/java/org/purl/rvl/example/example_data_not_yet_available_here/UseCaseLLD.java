package org.purl.rvl.example.example_data_not_yet_available_here;

import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseLLD extends TestOGVICProcess {
	
	@Test @Ignore
	public void testOGVICProcess() throws FileNotFoundException {
		
		//project.registerDataFile(ExampleData.LLD_TEST);
		//project.registerDataFile(ExampleData.LLD_EXTRA_DATA);
		//project.registerMappingFile(ExampleMapping.LLD);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		//project.setD3Generator(new D3GeneratorTreeJSON());
		project.setD3Generator(new D3GeneratorSimpleJSON());
		
		loadProjectAndRunProcess();
	}


}
