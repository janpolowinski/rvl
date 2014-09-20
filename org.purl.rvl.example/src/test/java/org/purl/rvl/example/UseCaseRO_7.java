package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorDeepLabelsJSON;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;

public class UseCaseRO_7 extends TestOGVICProcess {
	
	@Test
	public void testOGVICProcess() throws FileNotFoundException {
		
		project.registerMappingFile(ExampleFile.get("software/ro/example-mappings/RO_7.ttl"));
		//project.registerMappingFile(ExampleFile.get("software/ro/example-mappings/RO_type_shape_test.ttl"));
		project.registerDataFile(ExampleFile.get("software/ro/example-mappings/extra-data.ttl"));
		project.registerDataFile(ExampleFile.get("software/ro/example-data/ro_v_1_4_1_incl_social_network_example.owl"));		
		//project.setD3Generator(new D3GeneratorSimpleJSON());
		//project.setD3Generator(new D3GeneratorDeepLabelsJSON());
		project.setD3Generator(new D3GeneratorTreeJSON());
		project.setD3GraphicFile("circle-packing-zoomable/index.html");
		loadProjectAndRunProcess();
		
		assertGeneratedJSONEqualsExpected();
	}
	
	
	@Override
	protected String getExpectedD3JSONFileName() {
		return "use-case-ro-7.json";
	}

}
