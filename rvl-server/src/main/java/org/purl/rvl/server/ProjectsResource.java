package org.purl.rvl.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.purl.rvl.exception.EmptyGeneratedException;
import org.purl.rvl.exception.JsonExceptionWrapper;
import org.purl.rvl.exception.OGVICProcessException;
import org.purl.rvl.exception.OGVICRepositoryException;
import org.purl.rvl.exception.OGVICSystemInitException;
import org.purl.rvl.tooling.avm2d3.GraphicType;
import org.purl.rvl.tooling.codegen.rdfreactor.OntologyFile;
import org.purl.rvl.tooling.commons.FileRegistry;
import org.purl.rvl.tooling.commons.utils.FileResourceUtils;
import org.purl.rvl.tooling.model.ModelManager;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.process.VisProject;
import org.purl.rvl.tooling.process.VisProjectLibrary;
import org.purl.rvl.tooling.process.VisProjectLibraryExamples;

/**
 * @author Jan Polowinski
 * based on http://www.vogella.com/tutorials/REST/article.html
 */
@Path("/projects")
public class ProjectsResource {
	
	private final static Logger LOGGER = Logger.getLogger(ProjectsResource.class.getName()); 

	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	
	final static Random random = new Random();

	
	// Return the list of projects to a browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public List<VisProject> getVisProjectsBrowser() {
		List<VisProject> projects = new ArrayList<VisProject>();
		try {
			projects.addAll(VisProjectLibraryExamples.getInstance().getProjects());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
		return projects;
	}

	// Return the list of projects for applications
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<VisProject> getProjects() {
		List<VisProject> projects = new ArrayList<VisProject>();
		projects.addAll(VisProjectLibraryExamples.getInstance().getProjects());
		return projects;
	}
	
	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void newVisProject(@FormParam("id") String id, @FormParam("name") String name,
			@FormParam("description") String description, @Context HttpServletResponse servletResponse)
			throws IOException {

		LOGGER.info("Creating new project " + id);

		VisProject project = new VisProject(id);
		if (name != null) {
			project.setName(name);
		}
		if (description != null) {
			project.setDescription(description);
		}
		VisProjectLibraryExamples.getInstance().storeProject(project);

		servletResponse.sendRedirect("http://localhost:8585/semvis/forms/run.html");
		servletResponse.setStatus(HttpServletResponse.SC_OK);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/run")
	public Response runNewVisProject(
			@FormDataParam("id") String id,
			@FormDataParam("graphicType") String graphicType,
			@FormDataParam("data") String data,
			@FormDataParam("mappings") String mappings,
			@Context HttpServletResponse servletResponse
			) {
		
		LOGGER.info("Running new posted project ...");

		try {
			
			if (id == null || id.isEmpty()) {
				LOGGER.warning("Couldn't run process. ID is required.");
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonExceptionWrapper.wrapAsJSONException("Couldn't run process. ID is required.")).build();
			} 

			VisProject project = new VisProject(id);
			
			project.setReasoningDataModel(Reasoning.rdfs);
			
			if (graphicType != null && !graphicType.isEmpty()) {
				project.setDefaultGraphicType(graphicType);
			} else {
				LOGGER.warning("Not setting the graphic type.");
			}
			
			if (data != null && !data.isEmpty()) {
				File newTmpDataFile = saveToTempFile(data);
				project.registerDataFile(newTmpDataFile);
			} else {
				LOGGER.warning("Couldn't run process. Some data is required.");
				return Response.status(Status.BAD_REQUEST).entity(
						JsonExceptionWrapper.wrapAsJSONException("Couldn't run process for project with id " + id 
								+ ". Some data is required.")).build();
			}
			if (mappings != null && !mappings.isEmpty()) {
				File newTmpMappingFile = saveToTempFile(mappings);
				project.registerMappingFile(newTmpMappingFile);
			} else {
				LOGGER.warning("Couldn't run process. At least one mapping is required.");
				return Response.status(Status.BAD_REQUEST).entity(
						JsonExceptionWrapper.wrapAsJSONException("Couldn't run process for project with id " + id 
								+ ". At least one mapping is required.")).build();
			}
			
			VisProjectLibraryExamples.getInstance().storeProject(project);

			String jsonResult = runProject(project.getId());
	
			if (jsonResult.isEmpty()) {
				return Response.status(Status.NO_CONTENT).build();
			} else {
				LOGGER.info("Succesfully run project with id " + id);
				LOGGER.finest(jsonResult);
				return Response.status(Status.OK).entity(jsonResult).build();
			}
		
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Couldn't run process "
					+ "for project with id " + id + ": " + e.getMessage(), e);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(JsonExceptionWrapper.wrapAsJSONException("Couldn't run process "
						+ "for project with id " + id + ": " + e.getMessage()))
					.build();
		}
		
		//		code below only works when servlets are available:
		//		"When deploying a JAX-RS application using servlet then ServletConfig, ServletContext, HttpServletRequest and HttpServletResponse are available using @Context. " taken from: 
		//		https://jersey.java.net/documentation/latest/user-guide.html#d0e5169
		//
		//		servletResponse.sendRedirect("http://localhost:8080/semvis/projects");
		//		servletResponse.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		//		servletResponse.setHeader("Location", "http://localhost:8080/semvis/projects");
		//		servletResponse.setContentType(MediaType.APPLICATION_XML);
		//		servletResponse.setStatus(HttpServletResponse.SC_OK);
		//		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		//		servletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
	}
	
	public static File saveToTempFile(String data) throws IOException {
		
		File tmpFile = new File(System.getProperty("java.io.tmpdir") + "/semvis-tmp-data-or-mapping-" + random.nextDouble() + System.currentTimeMillis()  + ".tmp");
		FileWriter writer;
		writer = new FileWriter(tmpFile);
		writer.write(data);
		writer.close();
		return tmpFile;
	}

	@GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Path("/run/{id}")
    public Response runVisProject(@PathParam("id") String id, @Context HttpServletResponse servletResponse) {
		
		try {
			
			if (id == null || id.isEmpty()) {
				return Response.status(Status.BAD_REQUEST)
						.entity(JsonExceptionWrapper.wrapAsJSONException("ID is required")).build();
			} 
			
			String jsonResult = "";
			
			jsonResult = runProject(id);
			
			if (jsonResult.isEmpty()) {
				return Response.status(Status.NO_CONTENT).build();
			} else {
				LOGGER.finest(jsonResult);
				return Response.status(Status.OK).entity(jsonResult).build();
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(JsonExceptionWrapper.wrapAsJSONException("Couldn't run project: " + e.getMessage())).build();
		}

    }
	
	@GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Path("/run/external")
    public String runExternalEditingProject(@Context HttpServletResponse servletResponse) throws OGVICSystemInitException {

		String jsonResult;
		try {
			jsonResult = runExternalEditingProject(GraphicType.FORCE_DIRECTED_GRAPH);
		} catch (OGVICRepositoryException e) {
			jsonResult =  e.getMessage();
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			jsonResult =  e.getMessage();
			e.printStackTrace();
		}
		
//		if (null == servletResponse) {
//			LOGGER.warning("servlet response was null");
//		}
		
		return jsonResult;
    }
	
	@GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Path("/run/external/{graphicType}")
    public String runExternalEditingProject(@PathParam("graphicType") String graphicType, @Context HttpServletResponse servletResponse) {

		String jsonResult;
		try {
			jsonResult = runExternalEditingProject(graphicType);
		} catch (Exception e) {
			jsonResult =  e.getMessage();
			e.printStackTrace();
		}
		
		return jsonResult;
    }
	
	
	@GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Path("/latest")
    public String getLatestGeneratedD3() {
		
		LOGGER.finer("/run/latest");
		
		String jsonResult;
		try {
			jsonResult = OGVICProcess.getInstance().getGeneratedD3json();
		} catch (Exception e) {
			jsonResult =  "";
			e.printStackTrace();
		}
		
		LOGGER.finest(jsonResult);
		
		return jsonResult;
    }
	
	@GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Path("/bootstrap/avm/{projectID}")
    public Response runBootstrapAVM(@PathParam("projectID") String projectID) {
		
		try {
			
			String jsonResult = "";
			VisProject project = VisProjectLibraryExamples.getInstance().getProject(projectID);
			jsonResult = OGVICProcess.getInstance().runAVMBootstrappingVis(project);
			
			if (jsonResult.isEmpty()) {
				return Response.status(Status.NO_CONTENT).build();
			} else {
				LOGGER.finest(jsonResult);
				return Response.status(Status.OK).entity(jsonResult).build();
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(JsonExceptionWrapper.wrapAsJSONException("Couldn't meta-visualize the AVM: " + e.getMessage())).build();
		}

    }
	
	@GET
    @Produces({MediaType.TEXT_PLAIN})
	@Path("/example/data")
    public String getExampleData() {
		try {
			return FileResourceUtils.getFromExampleData("rvl-example-data.ttl");
		} catch (IOException e) {
			e.printStackTrace();
			return "# example data could not be found.";
		}
    }
	
	@GET
    @Produces({MediaType.TEXT_PLAIN})
	@Path("/example/mappings")
    public String getExampleMappings() {
		try {
			return FileResourceUtils.getFromExampleMappings("rvl-example-mappings.ttl");
		} catch (IOException e) {
			e.printStackTrace();
			return "# example mappings could not be found.";
		}
    }
	
	@GET
    @Produces({MediaType.TEXT_PLAIN})
	@Path("/mappingmodel/{id}")
    public String getMappingModel(@PathParam("id") String id) throws OGVICRepositoryException {
		
		ModelManager modelManager = ModelManager.getInstance();
		VisProject project = VisProjectLibraryExamples.getInstance().getProject(id);
		
		//FileRegistry mfr = project.getMappingFileRegistry();

		modelManager.initInternalModels(); // TODO refaktor? actually only RVL model is required here
		modelManager.initMappingsModel(project.getMappingFileRegistry());
		
		Model model = modelManager.getMappingsModel();
		
		return model.serialize(Syntax.Turtle);
		//return "requested mapping model for project " + id;
    }
	
	// TODO, HACK: we assume here, there is only one file!
	@GET
    @Produces({MediaType.TEXT_PLAIN})
	@Path("/mappingfile/{id}")
    public String getMappingFile(@PathParam("id") String id) throws OGVICRepositoryException, IOException {
		
		VisProject project = VisProjectLibraryExamples.getInstance().getProject(id);
		FileRegistry mfr = project.getMappingFileRegistry();
		File mappingFile = mfr.getFiles().iterator().next();

		if (null==mappingFile) {
			throw new OGVICRepositoryException();
		}
		
		final String mappingFilePath = mappingFile.getPath();
		final String mappingFileContent = FileResourceUtils.getFromWithinJarsAsString(mappingFilePath);
		
		return mappingFileContent
				+ System.lineSeparator() + System.lineSeparator() 
				+ "# this mapping file was loaded from: " 
				+ mappingFilePath + "(" + mappingFile.getAbsolutePath() + ")";
	    }
	
	@GET
    @Produces({MediaType.TEXT_PLAIN})
	@Path("/datamodel/{id}")
    public String getDataModel(@PathParam("id") String id) throws OGVICRepositoryException {
		
		ModelManager modelManager = ModelManager.getInstance();
		VisProject project = VisProjectLibraryExamples.getInstance().getProject(id);

		modelManager.initDataModel(project.getDataFileRegistry(), project.getReasoningDataModel());
		
		Model model = modelManager.getDataModel();
		
		return model.serialize(Syntax.Turtle);
    }



	// Defines that the next path parameter after projects is
	// treated as a parameter and passed to the ProjectsResources
	// Allows to type http://localhost:8080/semvis/projects/1
	// 1 will be treaded as parameter project and passed to ProjectResource
	@Path("{project}")
	public ProjectResource getProject(@PathParam("project") String id) {
		return new ProjectResource(uriInfo, request, id);
	}
	
	
	private String runProject(String id) throws OGVICRepositoryException, OGVICProcessException, OGVICSystemInitException {
	
		String json = "";
		OGVICProcess process = OGVICProcess.getInstance();
		VisProject project = VisProjectLibraryExamples.getInstance().getProject(id);
		
		if (project.isGenFromAvmDirty()) {
			process.loadProject(project);
			process.runOGVICProcess();
		} else {
			LOGGER.info("Returning old JSON generated from the AVM without running the transformations, since no changes could be detected.");
		}

		try {
			json = project.getGeneratedD3json();
		} catch (EmptyGeneratedException e) {
			LOGGER.warning(JsonExceptionWrapper.wrapAsJSONException(e.getMessage() + " Proceeding anyway"));
		}
	
		return json;
	}

	/**
	TODO: Paths need to be fixed.
	 * @throws OGVICSystemInitException 
	 */
	private String runExternalEditingProject(String defaultGraphicType) throws FileNotFoundException, OGVICRepositoryException, OGVICSystemInitException {
	
		String json;
		OGVICProcess process = OGVICProcess.getInstance();
	
		process.registerOntologyFile(OntologyFile.VISO_GRAPHIC);
		process.registerOntologyFile(OntologyFile.RVL);
		
		VisProject project = new VisProject("external-editing-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerDataFile("editing/data.ttl");
		project.registerMappingFile("editing/mapping.ttl");
		project.setDefaultGraphicType(defaultGraphicType);
		
		// load from optional files
		try {
			project.registerDataFile("editing/ontology.ttl");
		} catch (FileNotFoundException e) {}
		try {
			project.registerMappingFile("/example-commons/rvl-example-commons.ttl");
		} catch (FileNotFoundException e) {}
		
		try {
			process.loadProject(project);
		} catch (OGVICRepositoryException e) {
			json = e.getMessage();
			e.printStackTrace();
		}
	
		try {
			process.runOGVICProcess();
		} catch (OGVICProcessException e) {
			json = e.getMessage();
			e.printStackTrace();
		}

		try {
			json = process.getGeneratedD3json();
		} catch (Exception e) {
			json = e.getMessage();
			e.printStackTrace();
		}
	
		return json;
	}

}