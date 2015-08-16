package org.purl.rvl.example.example_data_not_yet_available_here;

import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;

public class UseCaseLLD extends TestOGVICProcess {
	
	@Test @Ignore
	public void testOGVICProcess() throws FileNotFoundException {

		simplyRunWithoutTesting();
	}


	@Override
	protected String getProjectName() {
		return "lld";
	}

}
