package org.purl.rvl.interpreter.usecase;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseSLUB extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project.registerDataFile(ExampleData.SLUB_TEST);
		project.registerDataFile(ExampleData.SLUB_EXTRA_DATA);
		project.registerMappingFile(ExampleMapping.SLUB);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		//project.setD3Generator(new D3GeneratorTreeJSON());
		project.setD3Generator(new D3GeneratorSimpleJSON());
		
		loadProjectAndRunProcess();
	}


}
