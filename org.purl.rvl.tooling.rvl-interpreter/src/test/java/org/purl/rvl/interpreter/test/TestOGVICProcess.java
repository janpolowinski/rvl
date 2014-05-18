package org.purl.rvl.interpreter.test;

import org.junit.Before;
import org.junit.Test;
import org.purl.rvl.tooling.avm2d3.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.process.VisProject;

public class TestOGVICProcess {
	
	protected OGVICProcess process;
	protected VisProject project = new VisProject("test");

	@Before
	public void setUp() throws Exception {
		
		process = OGVICProcess.getInstance();
		
		process.registerOntologyFile(OGVICProcess.VISO_LOCAL_REL);
		process.registerOntologyFile(OGVICProcess.RVL_LOCAL_REL);

	}

	@Test
	public void testOGVICProcess() {
		
		//public static final String REXD_URI = "http://purl.org/rvl/example-data";
		//public static final String REXD_URI = "http://purl.org/rvl";
		//public static final String REXD_URI = "http://purl.org/ro/semvis-example/";
		//public static final String REXD_URI = "http://purl.org/obo/owl/";

		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorSimpleJSON());
		
		loadProjectAndRunProcess();
	}

	
	public void loadProjectAndRunProcess(){

		process.loadProject(project);
		process.runOGVICProcess();
	}
	/*
	@Test
	public void testWriteAVMToFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegisterMappingFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegisterOntologyFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegisterDataFile() {
		fail("Not yet implemented");
	}
	*/

}
