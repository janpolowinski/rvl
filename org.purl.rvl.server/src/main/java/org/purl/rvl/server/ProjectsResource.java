package org.purl.rvl.server;

import java.io.FileNotFoundException;
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

import org.purl.rvl.exception.D3GeneratorException;
import org.purl.rvl.exception.OGVICModelsException;
import org.purl.rvl.exception.OGVICRepositoryException;
import org.purl.rvl.tooling.codegen.rdfreactor.OntologyFile;
import org.purl.rvl.tooling.process.OGVICProcess;
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
			project.setName(name);
		}
		if (description != null) {
			project.setDescription(description);
		}
		VisProjectLibraryExamples.getInstance().storeProject(project);

		// servletResponse.sendRedirect("http://localhost:8585/semvis/forms/form.html");
		// servletResponse.sendRedirect("testsincenothingworks");
		servletResponse.setStatus(HttpServletResponse.SC_OK);
	}
	
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/run")
	public void runNewVisProject(@FormParam("id") String id, @FormParam("data") String data,
			@FormParam("mappings") String mappings, @Context HttpServletResponse servletResponse)
			throws IOException {

		System.out.println("posted" + id + data + mappings + "");

		VisProject project = new VisProject(id);
		if (data != null) {
			//project.setName(name);
			System.out.println(data);
		}
		if (mappings != null) {
			//project.setDescription(description);
			System.out.println(mappings);
		}
		VisProjectLibraryExamples.getInstance().storeProject(project);
		
		runProject(id);

		// servletResponse.sendRedirect("http://localhost:8585/semvis/forms/form.html");
		// servletResponse.sendRedirect("testsincenothingworks");
		servletResponse.setStatus(HttpServletResponse.SC_OK);
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		servletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
	}
	
	@GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Path("/run/{id}")
    public String runVisProject(@PathParam("id") String id, @Context HttpServletResponse servletResponse) {
		
		System.out.println("/run/" + id);
		
		String jsonResult = runProject(id);
		
		if (null!=servletResponse) {
		
		//servletResponse.setStatus(HttpServletResponse.SC_OK);
		servletResponse.addHeader("Access-Control-Allow-Origin", "*");
		servletResponse.addHeader("Access-Control-Allow-Methods", "GET");
		} else {
			System.out.println("servlet response was null");
		}
		
		return jsonResult;
    }
	
	
	@GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Path("/latest")
    public String getLatestGeneratedD3() {
		
		System.out.println("/run/latest");
		
		String jsonResult = OGVICProcess.getInstance().getGeneratedD3json();
		
		System.out.println(jsonResult);
		
		return jsonResult;
    }


	// Defines that the next path parameter after projects is
	// treated as a parameter and passed to the ProjectsResources
	// Allows to type http://localhost:8080/semvis/projects/1
	// 1 will be treaded as parameter project and passed to ProjectResource
	@Path("{project}")
	public ProjectResource getProject(@PathParam("project") String id) {
		return new ProjectResource(uriInfo, request, id);
	}
	
	
	private String runProject(String id) {
	
		OGVICProcess process = OGVICProcess.getInstance();
	
		try {
			process.registerOntologyFile(OntologyFile.VISO_GRAPHIC);
			process.registerOntologyFile(OntologyFile.RVL);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		VisProject project = VisProjectLibraryExamples.getInstance().getProject(id);
		
		try {
			process.loadProject(project);
		} catch (OGVICRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		try {
			process.runOGVICProcess();
		} catch (D3GeneratorException | OGVICModelsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String json = "could not be generated";
		
		try {
			json = process.getGeneratedD3json();
		} catch (Exception e) {
			// TODO: handle exception
		}
	
		return json;
	}

}