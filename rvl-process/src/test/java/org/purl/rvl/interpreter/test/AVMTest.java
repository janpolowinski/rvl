package org.purl.rvl.interpreter.test;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.exception.EmptyGeneratedException;
import org.purl.rvl.exception.OGVICProcessException;
import org.purl.rvl.tooling.process.VisProjectLibrary;

public class AVMTest extends TestOGVICProcess {

	@Test
	public void testOGVICProcess() throws FileNotFoundException, OGVICProcessException, EmptyGeneratedException {
		
		project = VisProjectLibrary.getInstance().getProject(getProjectName());	
		
		loadProjectAndRunProcess();
		
		assertGeneratedJSONEqualsExpected();

	}

	@Override
	protected String getProjectName() {
		return "avm-test";
	}
}
