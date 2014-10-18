package org.purl.rvl.interpreter.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.purl.rvl.exception.OGVICModelsException;
import org.purl.rvl.exception.OGVICRepositoryException;
import org.purl.rvl.exception.d3.D3GeneratorException;
import org.purl.rvl.tooling.avm2d3.D3GeneratorDeepLabelsJSON;
import org.purl.rvl.tooling.codegen.rdfreactor.OntologyFile;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.process.VisProject;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Jan Polowinski
 *
 */
public abstract class TestOGVICProcess {
	
	private static final String RESULT_JSON_FOLDER = "d3-json";
	
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

	
	public void loadProjectAndRunProcess() {

		try {
			process.loadProject(project);
		} catch (OGVICRepositoryException e1) {
			e1.printStackTrace();
			Assert.fail("Project could not be loaded: " + e1.getMessage());
		}
		
		try {
			process.runOGVICProcess();
			//process.runOGVICProcessForTesting();
		} catch (D3GeneratorException e) {
			e.printStackTrace();
			Assert.fail("OGVIC Process could not be run: " + e.getMessage());
		} catch (OGVICModelsException e) {
			e.printStackTrace();
			Assert.fail("OGVIC Process could not be run: " + e.getMessage());
		}
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
	
	protected String getGeneratedD3json(){
		return process.getGeneratedD3json();
	}

	protected String getExpectedD3json(String fileNameWithoutPath) throws IOException {
		
		String combinedResourceName = "/" + RESULT_JSON_FOLDER + "/" + fileNameWithoutPath;
		
		// this won't work!!
//		String jsonTestResultFile = this.getClass().getResource(combined).toExternalForm(); 
//		File file = new File(jsonTestResultFile);
//		return FileUtils.readFileToString(file);
		
		InputStream htmlFileStream = this.getClass().getResourceAsStream(combinedResourceName);
		return IOUtils.toString(htmlFileStream, "utf-8");
	}

	/**
	 * Checks if the generated D3 JSON equals the expected JSON from the test result file
	 */
	protected void assertGeneratedJSONEqualsExpected() {
		
		try {
			
			String actual = getGeneratedD3json();
			String expected = getExpectedD3json(getExpectedD3JSONFileName());
			
			// length seems not always to be the same ?!
			//Assert.assertEquals(null, expected, actual);
			//Assert.assertEquals(null, expected.length(), actual.length());
			
			try {
				
				// this works fine (when non-strict), except for sub-mapping test case which is very large atm
				JSONAssert.assertEquals(expected, actual, false);
				
			}  catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
			
		} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Assert.fail(e.getMessage());
		}
	}

	protected abstract String getExpectedD3JSONFileName();
	
}
