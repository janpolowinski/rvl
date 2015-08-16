package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;

public class UseCaseRO extends TestOGVICProcessExamples {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project = getVisProjectLibrary().getProject(getProjectName());
				
		loadProjectAndRunProcess();
		
	}

	@Override
	protected String getProjectName() {
		return "ro";
	}

}
