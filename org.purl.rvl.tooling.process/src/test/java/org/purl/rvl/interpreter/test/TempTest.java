package org.purl.rvl.interpreter.test;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.process.VisProjectLibrary;

@SuppressWarnings("unused")
public class TempTest extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project = VisProjectLibrary.getInstance().getProject(getProjectName());	

		loadProjectAndRunProcess();
	}

	@Override
	protected String getProjectName() {
		return "temp-test";
	}

}
