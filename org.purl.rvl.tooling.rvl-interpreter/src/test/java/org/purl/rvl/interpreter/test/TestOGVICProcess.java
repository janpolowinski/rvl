package org.purl.rvl.interpreter.test;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.purl.rvl.tooling.avm2d3.D3GeneratorDeepLabelsJSON;
import org.purl.rvl.tooling.codegen.rdfreactor.OntologyFile;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.process.VisProject;

/**
 * @author Jan Polowinski
 *
 */
public class TestOGVICProcess {
	
	protected OGVICProcess process;
	protected VisProject project = new VisProject("test");

	@Before
	public void setUp() throws Exception {
		
		process = OGVICProcess.getInstance();
		
		process.registerOntologyFile(OntologyFile.VISO_GRAPHIC);
		process.registerOntologyFile(OntologyFile.RVL);

	}

	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		//public static final String REXD_URI = "http://purl.org/rvl/example-data";
		//public static final String REXD_URI = "http://purl.org/rvl";
		//public static final String REXD_URI = "http://purl.org/ro/semvis-example/";
		//public static final String REXD_URI = "http://purl.org/obo/owl/";

		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorDeepLabelsJSON());
		
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
