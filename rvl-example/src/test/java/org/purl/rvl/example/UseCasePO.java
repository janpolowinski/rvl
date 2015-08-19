package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;

public class UseCasePO extends TestOGVICProcessExamples {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		simplyRunWithoutTesting();
	}

	@Override
	protected String getProjectName() {
		return "po";
	}

}
