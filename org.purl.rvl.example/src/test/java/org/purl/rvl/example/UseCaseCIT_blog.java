package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;

public class UseCaseCIT_blog extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project.registerMappingFile(ExampleFile.get("library-and-publication/cito/example-mappings/CIT_blog.ttl"));
		project.registerDataFile(ExampleFile.get("library-and-publication/cito/example-data/cito.owl"));
		project.registerDataFile(ExampleFile.get("library-and-publication/cito/example-data/cito-example-data.ttl"));
		
		project.setD3Generator(new D3GeneratorTreeJSON());
		
		loadProjectAndRunProcess();
		
		assertGeneratedJSONEqualsExpected();
		
	}

	@Override
	protected String getExpectedD3JSONFileName() {
		return "use-case-cit-blog.json";
	}

}
