package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class UseCaseROinstanceData extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		process.setWriteAVM(true);
		
		//process.setUriStart("http://purl.org/ro/ont#");

		project.registerMappingFile(ExampleMapping.RO_SOCIAL_NETWORK);
		project.registerDataFile(ExampleData.RO_SOCIAL_NETWORK);
		project.registerDataFile(ExampleData.RO_SOCIAL_NETWORK_EXTRA_DATA);
		project.setReasoningDataModel(Reasoning.rdfs);
		

		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorSimpleJSON());
		//process.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
		
	}


}
