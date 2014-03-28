package org.purl.rvl.interpreter.usecase;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.avm.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.rvl2avm.SimpleRVLInterpreter;

public class UseCaseVISOClasses extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() {
		
		process.setUriStart("http://purl.org/viso/graphic/");
	
		process.registerMappingFile(ExampleMapping.RVL_EXAMPLE_BOOTSTRAP);
		
		process.setRvlInterpreter(new SimpleRVLInterpreter());
		process.setD3Generator(new D3GeneratorTreeJSON());
		//process.setD3Generator(new D3GeneratorSimpleJSON());
		
		process.runOGVICProcess();
	}


}
