package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;

public class UseCaseTutorial extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project.registerMappingFile(ExampleFile.get("tutorial/rvl-mappings.ttl"));
		project.registerDataFile(ExampleFile.get("tutorial/example-data/world-fact-book.rdf"));

		//process.setD3Generator(new D3GeneratorTreeJSON());

		loadProjectAndRunProcess();
	}


}
