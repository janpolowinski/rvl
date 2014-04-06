package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.avm.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.rvl2avm.SimpleRVLInterpreter;

public class UseCaseSLUB extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		process.setWriteAVM(true);
		
		//process.setUriStart("http://data.slub-dresden.de/datamodels/22/records/");
	
		process.registerDataFile(ExampleData.SLUB_TEST);
		process.registerDataFile(ExampleData.SLUB_EXTRA_DATA);
		process.registerMappingFile(ExampleMapping.SLUB);
		
		process.setRvlInterpreter(new SimpleRVLInterpreter());
		//process.setD3Generator(new D3GeneratorTreeJSON());
		process.setD3Generator(new D3GeneratorSimpleJSON());
		
		process.runOGVICProcess();
	}


}
