package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.interpreter.test.TestOGVICProcess;

public class UseCaseZFA_inheritance extends TestOGVICProcess {
	
	@Ignore
	@Test
	public void testOGVICProcess() throws FileNotFoundException {

		project.setReasoningDataModel(Reasoning.rdfs);
		
		project.registerMappingFile(ExampleFile.get("life-sciences/zebra-fish-anatomy/example-mappings/ZFA-inheritance.ttl"));
		project.registerDataFile(ExampleFile.get("life-sciences/zebra-fish-anatomy/example-data/ZFA_subset.ttl")); // original file causes heap space problems
		
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		// requires better filtering!
		//project.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
		
		assertGeneratedJSONEqualsExpected();
	}

	@Override
	protected String getExpectedD3JSONFileName() {
		return "use-case-zfa-inheritance.json";
	}

}
