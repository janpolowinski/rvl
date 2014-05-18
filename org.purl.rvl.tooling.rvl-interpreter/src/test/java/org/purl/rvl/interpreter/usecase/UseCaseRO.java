package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseRO extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		project.registerMappingFile(ExampleMapping.RO);
		project.registerDataFile(ExampleData.RO_SEMVIS);
		
		project.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
		
	}


}
