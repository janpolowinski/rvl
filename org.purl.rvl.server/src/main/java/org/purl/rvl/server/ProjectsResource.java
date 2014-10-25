package org.purl.rvl.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.purl.rvl.tooling.process.VisProject;
import org.purl.rvl.tooling.process.VisProjectLibraryExamples;

/**
 * @author Jan Polowinski
 * based on http://www.vogella.com/tutorials/REST/article.html
 */
@Path("/projects")
public class ProjectsResource {

	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	// Return the list of projects to a browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public List<VisProject> getVisProjectsBrowser() {
		List<VisProject> projects = new ArrayList<VisProject>();
		//System.out.println("before getting projects (browser) ");
		try {
			projects.addAll(VisProjectLibraryExamples.getInstance().getProjects());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		//System.out.println("projects:" + projects);
		return projects;
	}

	// Return the list of projects for applications
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<VisProject> getProjects() {
		//System.out.println("before getting projects (application) ");
		List<VisProject> projects = new ArrayList<VisProject>();
		projects.addAll(VisProjectLibraryExamples.getInstance().getProjects());
		//System.out.println("projects:" + projects);
		return projects;
	}
	
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void newVisProject(@FormParam("id") String id, @FormParam("name") String name,
			@FormParam("description") String description, @Context HttpServletResponse servletResponse)
			throws IOException {

		// System.out.println("hello" + id + description + summary + "");

		VisProject project = new VisProject(id);
		if (name != null) {
			project.setDescription(name);
		}
		if (description != null) {
			project.setDescription(description);
		}
		VisProjectLibraryExamples.getInstance().storeProject(project);

		// servletResponse.sendRedirect("http://localhost:8585/semvis/forms/form.html");
		// servletResponse.sendRedirect("testsincenothingworks");
		servletResponse.setStatus(HttpServletResponse.SC_OK);
	}


	// Defines that the next path parameter after projects is
	// treated as a parameter and passed to the ProjectsResources
	// Allows to type http://localhost:8080/semvis/projects/1
	// 1 will be treaded as parameter project and passed to ProjectResource
	@Path("{project}")
	public ProjectResource getProject(@PathParam("project") String id) {
		return new ProjectResource(uriInfo, request, id);
	}

}