package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseAA_4 extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		//process.setUriStart("http://www.co-ode.org/ontologies/amino-acid/2006/05/18/amino-acid.owl#");
		
		project.registerMappingFile(ExampleMapping.AA_4);
		project.registerDataFile(ExampleData.AA);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		//process.setD3Generator(new D3GeneratorTreeJSON());
		project.setD3Generator(new D3GeneratorSimpleJSON());

		loadProjectAndRunProcess();
	}


}
