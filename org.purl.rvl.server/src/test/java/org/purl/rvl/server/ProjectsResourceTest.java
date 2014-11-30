package org.purl.rvl.server;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.purl.rvl.tooling.commons.utils.FileResourceUtils;
import org.purl.rvl.tooling.process.VisProject;

public class ProjectsResourceTest {
	
	@Test
	public void testCreatingTempFile() throws IOException {
		
		String data = "@prefix :        <http://purl.org/rvl/example-mappings/> .";
		
		File file = ProjectsResource.saveToTempFile(data);
		
		System.out.println(file);
	}
	
	@Test
	public void testRunningProjectFromDataAndMappingStrings() throws IOException, URISyntaxException {
		
		FileReader dataReader = new FileReader("src/test/data.ttl");
		FileReader mappingReader = new FileReader("src/test/mapping.ttl");
		
		String data = IOUtils.toString(dataReader);
		String mappings = IOUtils.toString(mappingReader);
		
		ProjectsResource res = new ProjectsResource();
		Response response = res.runNewVisProject("test", data, mappings, "on", null);
		
		System.out.println(response);
	}
	
	@Test
	public void testExampleDataProvision() throws IOException, URISyntaxException {
		String data = FileResourceUtils.getFromExampleData("rvl-example-data.ttl");
		ProjectsResource res = new ProjectsResource();
		String result = res.getExampleData();
		assertEquals(data, result);
	}
	
	@Test
	public void testExampleMappingProvision() throws IOException, URISyntaxException {
		String mappings = FileResourceUtils.getFromExampleMappings("rvl-example-mappings.ttl");
		ProjectsResource res = new ProjectsResource();
		String result = res.getExampleMappings();
		assertEquals(mappings, result);
	}
	
	@Test
	public void testProjectsList() throws IOException, URISyntaxException {
		ProjectsResource res = new ProjectsResource();
		List<VisProject> result = res.getProjects();
	}

}
