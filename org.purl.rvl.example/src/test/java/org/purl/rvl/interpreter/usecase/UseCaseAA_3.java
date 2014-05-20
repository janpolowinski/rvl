package org.purl.rvl.interpreter.usecase;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseAA_3 extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		//process.setUriStart("http://www.co-ode.org/ontologies/amino-acid/2006/05/18/amino-acid.owl#");
		
		project.registerMappingFile(ExampleMapping.AA_3);
		project.registerDataFile(ExampleData.AA);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		//process.setD3Generator(new D3GeneratorTreeJSON());
		project.setD3Generator(new D3GeneratorSimpleJSON());

		loadProjectAndRunProcess();
	}


}
