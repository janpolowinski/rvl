package org.purl.rvl.interpreter.test;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.tooling.process.ExampleData;

public class SubMappingTest extends TestOGVICProcess {
	
	@Override
	protected String getExpectedD3JSONFileName() {
		return "sub-mapping.json";
	}
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project.setReasoningDataModel(Reasoning.rdfs);

		// TODO combine tests when the 2nd one works:
		//project.registerMappingFile(ExampleMapping.RVL_TEST_SUBMAPPING);
		project.registerMappingFile("../org.purl.rvl.vocabulary/example-mappings/submapping-on-p2gam-test.ttl");
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		
		//project.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
		
		//assertGeneratedJSONEqualsExpected();

	}
}
