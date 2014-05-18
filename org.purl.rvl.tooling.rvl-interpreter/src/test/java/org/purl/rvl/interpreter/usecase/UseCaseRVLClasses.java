package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.process.OGVICProcess;

public class UseCaseRVLClasses extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		project.setReasoningDataModel(Reasoning.rdfs);

		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE_BOOTSTRAP);
		project.registerDataFile(OGVICProcess.RVL_LOCAL_REL);
		project.registerDataFile(ExampleData.RVL_EXTRA_DATA);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorTreeJSON());
		//project.setD3Generator(new D3GeneratorSimpleJSON());
		
		loadProjectAndRunProcess();
	}


}
