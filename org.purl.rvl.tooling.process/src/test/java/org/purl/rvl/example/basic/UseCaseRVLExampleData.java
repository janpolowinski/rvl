package org.purl.rvl.example.basic;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.process.VisProjectLibrary;

public class UseCaseRVLExampleData extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project = VisProjectLibrary.getInstance().getProject(getProjectName());

		loadProjectAndRunProcess();
	}

	@Override
	protected String getProjectName() {
		return "rvl-example-data";
	}

}
