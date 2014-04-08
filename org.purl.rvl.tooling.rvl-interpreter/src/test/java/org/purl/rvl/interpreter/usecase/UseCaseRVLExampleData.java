package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.avm.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.rvl2avm.SimpleRVLInterpreter;

public class UseCaseRVLExampleData extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {

		//process.setUriStart("http://purl.org/rvl/example-data");

		process.registerMappingFile(ExampleMapping.RVL_EXAMPLE);
		//process.registerMappingFile(ExampleMapping.RVL_EXAMPLE_MINI);
		process.registerDataFile(ExampleData.RVL_EXAMPLE);
		//process.registerMappingFile(ExampleMapping.RVL_EXAMPLE_OLD);
		//process.registerDataFile(ExampleData.RVL_EXAMPLE_OLD);
		
		process.setRvlInterpreter(new SimpleRVLInterpreter());
		
		process.setD3Generator(new D3GeneratorSimpleJSON());
		//process.setD3Generator(new D3GeneratorTreeJSON());
		
		process.runOGVICProcess();
	}


}
