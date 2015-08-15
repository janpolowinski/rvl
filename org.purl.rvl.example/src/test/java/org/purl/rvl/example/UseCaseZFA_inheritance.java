package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;
import org.purl.rvl.exception.EmptyGeneratedException;
import org.purl.rvl.exception.OGVICProcessException;

public class UseCaseZFA_inheritance extends TestOGVICProcessExamples {
	
	@Ignore
	@Test
	public void testOGVICProcess() throws FileNotFoundException, OGVICProcessException, EmptyGeneratedException {
		
		project = getVisProjectLibrary().getProject(getProjectName());
	
		loadProjectAndRunProcess();
		
		assertGeneratedJSONEqualsExpected();
	}

	@Override
	protected String getProjectName() {
		return "zfa-inheritance";
	}

}
