package org.purl.rvl.server;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.purl.rvl.exception.OGVICRepositoryException;
import org.purl.rvl.tooling.avm2d3.GraphicType;
import org.purl.rvl.tooling.commons.utils.FileResourceUtils;
import org.purl.rvl.tooling.process.VisProject;

public class ProjectsResourceTest extends JerseyTest {
	
    @Override
    protected URI getBaseUri() {
        return UriBuilder.fromUri(super.getBaseUri()).path("").build();
    }

    @Override
    protected Application configure() {
        return new RVLServerResourceConfiguration();
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }
	
    @Test
    public void testFormDataMultiPart() {
    	
    	//URI uri = getBaseUri();
    	
        final WebTarget target = target().path("projects/run");

        final FormDataMultiPart mp = new FormDataMultiPart();
        final FormDataBodyPart p = new FormDataBodyPart(FormDataContentDisposition.name("id").build(), "test-project-id");
        mp.bodyPart(p);

        final String s = target.request().post(Entity.entity(mp, MediaType.MULTIPART_FORM_DATA_TYPE), String.class);
        assertEquals("{\"some message\" : \"test\"}", s);
    }
	
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
		Response response = res.runNewVisProject("test", GraphicType.FORCE_DIRECTED_GRAPH, data, mappings, null);
		
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
		System.out.println(result);
	}

	@Test
	public void testGetMappingModel() throws OGVICRepositoryException {
		ProjectsResource res = new ProjectsResource();
		String serializedMappingModel = res.getMappingModel("aa-3");
		System.out.println(serializedMappingModel);
	}
	
	@Test
	public void testGetDataModel() throws OGVICRepositoryException {
		ProjectsResource res = new ProjectsResource();
		String serializedDataModel = res.getDataModel("aa-3");
		System.out.println(serializedDataModel);
	}
	
	@Test
	public void testGetMappingFile() throws IOException, URISyntaxException, OGVICRepositoryException {
		String mappings = FileResourceUtils.getFromExampleMappings("linking-test.ttl");
		ProjectsResource res = new ProjectsResource();
		String result = res.getMappingFile("linking-directed-test");
		assertEquals(mappings, result);
	}
	
}
