package org.purl.rvl.tutorial;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;

public class RunInterpreterCollapsibleTree extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {

		project.registerMappingFile("src/test/resources/tutorial/live/your-mappings.ttl");
		project.registerDataFile("src/test/resources/tutorial/live/your-data.ttl");

		process.setD3Generator(new D3GeneratorTreeJSON());

		loadProjectAndRunProcess();
	}

}
