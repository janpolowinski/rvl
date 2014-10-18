package org.purl.rvl.interpreter.test;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.tooling.process.VisProjectLibrary;

public class SubMappingTest extends TestOGVICProcess {

	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project = VisProjectLibrary.getInstance().getProject(getProjectName());	
		
		loadProjectAndRunProcess();
		
		//assertGeneratedJSONEqualsExpected();

	}

	@Override
	protected String getProjectName() {
		return "sub-mapping-test";
	}
}
