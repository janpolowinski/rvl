package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;

public class UseCaseCIT_1 extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project.registerMappingFile(ExampleFile.get("library-and-publication/cito/example-mappings/CIT_1.ttl"));
		project.registerDataFile(ExampleFile.get("library-and-publication/cito/example-data/cito.owl"));
		project.registerDataFile(ExampleFile.get("library-and-publication/cito/example-data/cito-example-data.ttl"));
		

		
		loadProjectAndRunProcess();
		
	}


}
