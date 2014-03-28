package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseRO extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		process.setUriStart("http://purl.org/ro/semvis-example/");

		process.registerMappingFile(ExampleMapping.RO);
		process.registerDataFile(ExampleData.RO_SEMVIS);
		
		process.setD3Generator(new D3GeneratorTreeJSON());
		
		process.runOGVICProcess();
		
	}


}
