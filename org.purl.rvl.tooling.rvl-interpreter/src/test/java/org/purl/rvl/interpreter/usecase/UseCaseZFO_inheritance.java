package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.avm.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.rvl2avm.SimpleRVLInterpreter;

public class UseCaseZFO_inheritance extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {

		project.registerMappingFile(ExampleMapping.ZFO_inheritance);
		project.registerDataFile(ExampleData.ZFO_SUBSET);
		//project.registerDataFile(ExampleData.ZFO);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		//project.setD3Generator(new D3GeneratorSimpleJSON()); // requires better filtering!
		project.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
	}


}