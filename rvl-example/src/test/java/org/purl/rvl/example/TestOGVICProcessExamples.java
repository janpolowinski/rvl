package org.purl.rvl.example;

import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.process.VisProjectLibrary;
import org.purl.rvl.tooling.process.VisProjectLibraryExamples;

public abstract class TestOGVICProcessExamples extends TestOGVICProcess {

	/* (non-Javadoc)
	 * @see org.purl.rvl.interpreter.test.TestOGVICProcess#getVisProjectLibrary()
	 */
	@Override
	protected VisProjectLibrary getVisProjectLibrary() {
		return VisProjectLibraryExamples.getInstance();
	}
	
	protected String getExpectedD3JSONFileName() {
		return "use-case-" + getProjectName() + ".json"; 
	}
	
}
