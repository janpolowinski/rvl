package org.purl.rvl.interpreter.test;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

public class FilteringTest extends TestOGVICProcess {

	@Test
	public final void testOGVICProcess() throws FileNotFoundException {

		//project.setReasoningDataModel(Reasoning.none); // does this actually work? still a lot of resource and owl thing types

		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE_FILTERING);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);

		//project.setRvlInterpreter(new SimpleRVLInterpreter());

		//project.setD3Generator(new D3GeneratorTreeJSON());

		loadProjectAndRunProcess();
		
		assertGeneratedJSONEqualsExpected();
	}
	

	@Override
	protected String getExpectedD3JSONFileName() {
		return "filtering.json";
	}

}