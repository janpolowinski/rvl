package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

@SuppressWarnings("unused")
public class UseCaseZFA_inheritance extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {

		project.setReasoningDataModel(Reasoning.rdfs);
		
		project.registerMappingFile(ExampleFile.get("life-sciences/zebra-fish-anatomy/example-mappings/ZFA-inheritance.ttl"));
		project.registerDataFile(ExampleFile.get("life-sciences/zebra-fish-anatomy/example-data/ZFA_subset.ttl")); // original file causes heap space problems
		
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorSimpleJSON()); // requires better filtering!
		//project.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
	}


}
