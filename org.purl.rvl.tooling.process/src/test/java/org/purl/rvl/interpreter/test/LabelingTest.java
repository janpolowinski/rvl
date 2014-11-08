package org.purl.rvl.interpreter.test;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;

@SuppressWarnings("unused")
public class LabelingTest extends TestOGVICProcess {

	@Override
	protected String getProjectName() {
		return "labeling-test";
	}
	
}
