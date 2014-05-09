package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseRO_4b extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		project.registerMappingFile(ExampleMapping.RO_4b);
		project.registerDataFile(ExampleData.RO_SOCIAL_NETWORK);
		project.registerDataFile(ExampleData.RO_SOCIAL_NETWORK_EXTRA_DATA);
		
		project.setD3Generator(new D3GeneratorSimpleJSON());
		
		loadProjectAndRunProcess();
		
	}


}
