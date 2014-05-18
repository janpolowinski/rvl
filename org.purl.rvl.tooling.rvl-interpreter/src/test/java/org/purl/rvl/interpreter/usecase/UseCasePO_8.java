package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCasePO_8 extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		//process.setUriStart("http://purl.org/obo/owl/");

		project.registerMappingFile(ExampleMapping.PO_8);
		project.registerDataFile(ExampleData.PO);
		project.registerDataFile(ExampleData.PO_EXTRA_DATA);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorTreeJSON());
		//process.setD3Generator(new D3GeneratorSimpleJSON());
		
		loadProjectAndRunProcess();
	}


}
