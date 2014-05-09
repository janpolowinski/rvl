package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.avm.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseCIT_1 extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		project.registerMappingFile(ExampleMapping.CIT_1);
		project.registerDataFile(ExampleData.CIT);
		project.registerDataFile(ExampleData.CITO);
		
		project.setD3Generator(new D3GeneratorSimpleJSON());
		
		loadProjectAndRunProcess();
		
	}


}
