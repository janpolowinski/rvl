package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;

public class UseCaseROInstancesSocialNetwork extends TestOGVICProcessExamples {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		process.setWriteAVM(true);

		simplyRunWithoutTesting();

	}

	@Override
	protected String getProjectName() {
		return "ro-instances-social-network";
	}

}
