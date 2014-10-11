package org.purl.rvl.interpreter.test;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

@SuppressWarnings("unused")
public class TempTest extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project.setReasoningDataModel(Reasoning.rdfs);

		project.registerMappingFile(ExampleMapping.RVL_TEST_TEMP);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		
		//project.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
	}

	@Override
	protected String getExpectedD3JSONFileName() {
		return null;
	}


}
