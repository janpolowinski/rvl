package org.purl.rvl.example;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.interpreter.test.TestOGVICProcess;
import org.purl.rvl.tooling.avm2d3.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

@SuppressWarnings("unused")
public class UseCasePO_8 extends TestOGVICProcess {

	@Test
	public void testOGVICProcess() throws FileNotFoundException {

		// process.setUriStart("http://purl.org/obo/owl/");

		project.setReasoningDataModel(Reasoning.rdfs); // setting to rdfs makes it necessary to remove the transitive hull (default setting at the moment),
													   // but shape is not evaluated otherwise

		project.registerMappingFile(ExampleMapping.PO_8);
		project.registerDataFile(ExampleData.PO);
		project.registerDataFile(ExampleData.PO_EXTRA_DATA);

		// project.setRvlInterpreter(new SimpleRVLInterpreter());
		 project.setD3Generator(new D3GeneratorTreeJSON());
		//process.setD3Generator(new D3GeneratorSimpleJSON());

		loadProjectAndRunProcess();
	}

}
