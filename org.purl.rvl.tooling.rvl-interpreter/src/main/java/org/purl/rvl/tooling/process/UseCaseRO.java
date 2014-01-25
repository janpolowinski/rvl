package org.purl.rvl.tooling.process;

import org.junit.Test;
import org.purl.rvl.tooling.avm.D3GeneratorTreeJSON;

public class UseCaseRO extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		process.setUriStart("http://purl.org/ro/semvis-example/");
		process.setJsonFileNameRel("../org.purl.rvl.tooling.d3vis/examples/collapsible_tree/data.json");
				
		process.registerMappingFile(ExampleMapping.RO);
		process.registerDataFile(ExampleData.RO_SEMVIS);
		
		process.setD3Generator(new D3GeneratorTreeJSON());
		
		process.runOGVICProcess();
		
	}


}
