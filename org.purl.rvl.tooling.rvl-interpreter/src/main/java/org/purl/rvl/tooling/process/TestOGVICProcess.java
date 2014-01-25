package org.purl.rvl.tooling.process;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.purl.rvl.tooling.avm.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.rvl2avm.SimpleRVLInterpreter;

public class TestOGVICProcess {
	
	OGVICProcess process;

	@Before
	public void setUp() throws Exception {
		
		process = OGVICProcess.getInstance();
		
		process.registerOntologyFile(OGVICProcess.VISO_LOCAL_REL);
		process.registerOntologyFile(OGVICProcess.RVL_LOCAL_REL);

		// set default place to store JSON
		process.setJsonFileNameRel("../org.purl.rvl.tooling.d3vis/examples/force-directed-graph/data.json");

	}

	@Test
	public void testOGVICProcess() {
		
		process.setUriStart("http://purl.org/rvl");
		process.setJsonFileNameRel("../org.purl.rvl.tooling.d3vis/examples/force-directed-graph/data.json");
		//public static final String REXD_URI = "http://purl.org/rvl/example-data";
		//public static final String REXD_URI = "http://purl.org/rvl";
		//public static final String REXD_URI = "http://purl.org/ro/semvis-example/";
		//public static final String REXD_URI = "http://purl.org/obo/owl/";

		process.registerMappingFile(ExampleMapping.RVL_EXAMPLE_MINI);
		process.registerDataFile(ExampleData.RVL_EXAMPLE);
		
		process.setRvlInterpreter(new SimpleRVLInterpreter());
		process.setD3Generator(new D3GeneratorSimpleJSON());
		
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
