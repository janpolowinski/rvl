package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.avm.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.rvl2avm.SimpleRVLInterpreter;

public class UseCaseZFO extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		//process.setUriStart("http://purl.org/obo/owl/ZFA");
		
		//process.registerMappingFile(ExampleMapping.ZFO_X);
		process.registerMappingFile(ExampleMapping.ZFO_Y);
		//process.registerDataFile(ExampleData.ZFO_SUBSET);
		process.registerDataFile(ExampleData.ZFO);
		
		process.setRvlInterpreter(new SimpleRVLInterpreter());
		process.setD3Generator(new D3GeneratorTreeJSON());
		//process.setD3Generator(new D3GeneratorSimpleJSON());
		
		process.runOGVICProcess();
	}


}
