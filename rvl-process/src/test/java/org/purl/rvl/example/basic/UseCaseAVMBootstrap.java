package org.purl.rvl.example.basic;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.purl.rvl.exception.EmptyGeneratedException;
import org.purl.rvl.exception.OGVICProcessException;
import org.purl.rvl.exception.OGVICRepositoryException;
import org.purl.rvl.exception.OGVICSystemInitException;
import org.purl.rvl.interpreter.test.ContainmentTest;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.process.VisProject;
import org.purl.rvl.tooling.process.VisProjectLibrary;

public class UseCaseAVMBootstrap extends TestOGVICProcess {
	
	private final static Logger LOGGER = Logger.getLogger(UseCaseAVMBootstrap.class.getName()); 
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException, OGVICProcessException, OGVICRepositoryException, OGVICSystemInitException, EmptyGeneratedException {

		// do some vis to have an AVM
		VisProject p = VisProjectLibrary.getInstance().getProject("containment-test");
		OGVICProcess process = OGVICProcess.getInstance();
		process.loadProject(p);
		process.runOGVICProcess();
//		String previousResult = process.getGeneratedD3json();
		
		// vis the AVM
		String result = process.runAVMBootstrappingVis(p);
		LOGGER.log(Level.INFO, "Generated D3-JSON for the AVM bootstrapping: " + result);
	}

	@Override
	protected String getProjectName() {
		return "avm";
	}


}
