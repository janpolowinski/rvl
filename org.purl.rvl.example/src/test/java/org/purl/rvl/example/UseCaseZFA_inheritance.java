package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;

public class UseCaseZFA_inheritance extends TestOGVICProcessExamples {
	
	@Ignore
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project = getVisProjectLibrary().getProject(getProjectName());
	
		loadProjectAndRunProcess();
		
		assertGeneratedJSONEqualsExpected();
	}

	@Override
	protected String getProjectName() {
		return "zfa-inheritance";
	}

}
