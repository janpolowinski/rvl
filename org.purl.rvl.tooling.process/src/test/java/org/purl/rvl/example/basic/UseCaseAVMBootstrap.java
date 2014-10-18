package org.purl.rvl.example.basic;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseAVMBootstrap extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {

		process.setWriteAVM(false);
		
		project.registerMappingFile(ExampleMapping.AVM_EXAMPLE_BOOTSTRAP);
		project.registerDataFile(ExampleData.AVM);
		project.registerDataFile(ExampleData.AVM_EXTRA_DATA);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		//project.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
	}

	@Override
	protected String getProjectName() {
		return "avm";
	}


}
