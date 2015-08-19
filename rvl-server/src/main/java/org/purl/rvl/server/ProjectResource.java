package org.purl.rvl.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.purl.rvl.tooling.process.VisProject;
import org.purl.rvl.tooling.process.VisProjectLibraryExamples;

/**
 * @author Jan Polowinski
 * based on http://www.vogella.com/tutorials/REST/article.html
 */
public class ProjectResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;

	public ProjectResource(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}

	// Application integration
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public VisProject getVisProject() {
		VisProject project = VisProjectLibraryExamples.getInstance().getProject(id);
		if (project == null)
			throw new RuntimeException("Get: VisProject with " + id + " not found");
		return project;
	}

	// for the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public VisProject getVisProjectHTML() {
		VisProject project = VisProjectLibraryExamples.getInstance().getProject(id);
		if (project == null)
			throw new RuntimeException("Get: VisProject with " + id + " not found");
		return project;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putVisProject(JAXBElement<VisProject> project) {
		VisProject c = project.getValue();
		return putAndGetResponse(c);
	}

	private Response putAndGetResponse(VisProject project) {
		Response res;
		if (VisProjectLibraryExamples.getInstance().contains(id)) {
			res = Response.noContent().build();
		} else {
			res = Response.created(uriInfo.getAbsolutePath()).build();
		}
		VisProjectLibraryExamples.getInstance().storeProject(project);
		return res;
	}

}