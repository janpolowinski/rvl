package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.rvl2avm.SimpleRVLInterpreter;

public class UseCasePO extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		process.setUriStart("http://purl.org/obo/owl/");
		process.setJsonFileNameRel("../org.purl.rvl.tooling.d3vis/examples/collapsible_tree/data.json");
		
		process.registerMappingFile(ExampleMapping.PO);
		process.registerDataFile(ExampleData.PO_SIMPLIFIED);
		
		process.setRvlInterpreter(new SimpleRVLInterpreter());
		process.setD3Generator(new D3GeneratorTreeJSON());
		
		process.runOGVICProcess();
	}


}
