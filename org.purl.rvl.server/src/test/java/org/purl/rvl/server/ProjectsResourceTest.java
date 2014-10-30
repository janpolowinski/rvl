package org.purl.rvl.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

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

}
