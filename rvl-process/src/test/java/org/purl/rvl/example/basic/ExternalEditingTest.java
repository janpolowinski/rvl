package org.purl.rvl.example.basic;

import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.process.VisProjectLibrary;

public class ExternalEditingTest extends TestOGVICProcess {
	
	@Test
	@Ignore
	public void testOGVICProcess() throws FileNotFoundException {
		
		project = VisProjectLibrary.getInstance().getProject(getProjectName());
		
		loadProjectAndRunProcess();
	}

	@Override
	protected String getProjectName() {
		return "external-editing-test";
	}

}
