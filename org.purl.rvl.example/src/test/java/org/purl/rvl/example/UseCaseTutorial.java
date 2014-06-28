package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.VisProject;

public class UseCaseTutorial extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project = new VisProject("test");
		
		project.registerMappingFile(ExampleFile.get("tutorial/live/your-mappings.ttl"));
		project.registerDataFile(ExampleFile.get("tutorial/live/your-data.ttl"));

		//process.setD3Generator(new D3GeneratorTreeJSON());

		loadProjectAndRunProcess();
	}
	
	@Test
	public void testOGVICProcessCT() throws FileNotFoundException {
		
		project = new VisProject("test");
		
		project.registerMappingFile(ExampleFile.get("tutorial/live/your-mappings.ttl"));
		project.registerDataFile(ExampleFile.get("tutorial/live/your-data.ttl"));

		process.setD3Generator(new D3GeneratorTreeJSON());

		loadProjectAndRunProcess();
	}


}
