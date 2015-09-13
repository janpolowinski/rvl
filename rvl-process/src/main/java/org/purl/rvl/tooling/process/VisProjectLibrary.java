package org.purl.rvl.tooling.process;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.Reasoning;
import org.purl.rvl.tooling.avm2d3.D3GeneratorDeepLabelsJSON;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;
import org.purl.rvl.tooling.codegen.rdfreactor.OntologyFile;

/**
 * @author Jan Polowinski
 *
 */
public class VisProjectLibrary {
	
	private static VisProjectLibrary instance = null;
	
	Map<String,VisProject> library = new HashMap<String,VisProject>();
	
	private final static Logger LOGGER = Logger.getLogger(VisProjectLibrary.class.getName()); 

	/**
	 * @throws FileNotFoundException 
	 * 
	 * TODO setting this protected is against the singleton pattern, avoid inheritance between the concrete libraries  
	 */
	protected VisProjectLibrary() {
		super();
		try {
			initWithUseCaseTestProjects();
		} catch (FileNotFoundException e) {
			LOGGER.severe("One of the files could not be read into the visualisation project library:");
			LOGGER.severe(e.getMessage());
			System.exit(0);
		}
	}
	
	public void initWithUseCaseTestProjects() throws FileNotFoundException {
		
		//////////////////////////////////////////////////////////////////
		// "Bootstrapping" VISO_GRAPHIC Classes
		///////////////////////////////////////////////////////////////////
		VisProject useCaseVISOClasses = new VisProject("viso");
		useCaseVISOClasses.setReasoningDataModel(Reasoning.rdfs);
		useCaseVISOClasses.registerMappingFile(ExampleMapping.VISO_EXAMPLE_BOOTSTRAP);
		useCaseVISOClasses.registerDataFile(OntologyFile.VISO_GRAPHIC);
		useCaseVISOClasses.registerDataFile(ExampleData.VISO_EXTRA_DATA);
		//useCaseVISOClasses.setRvlInterpreter(new SimpleRVLInterpreter());
		useCaseVISOClasses.setD3Generator(new D3GeneratorTreeJSON());
		//useCaseVISOClasses.setD3Generator(new D3GeneratorSimpleJSON());
		storeProject(useCaseVISOClasses);
		
		//////////////////////////////////////////////////////////////////
		// RVL Example Data
		///////////////////////////////////////////////////////////////////
		VisProject useCaseRVLExampleData = new VisProject("rvl-example-data");
		useCaseRVLExampleData.setReasoningDataModel(Reasoning.rdfs);
		useCaseRVLExampleData.registerMappingFile(ExampleMapping.RVL_EXAMPLE);
		//useCaseRVLExampleData.registerMappingFile(ExampleMapping.RVL_EXAMPLE_MINI);
		useCaseRVLExampleData.registerDataFile(ExampleData.RVL_EXAMPLE);
		useCaseRVLExampleData.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		//useCaseRVLExampleData.registerMappingFile(ExampleMapping.RVL_EXAMPLE_OLD);
		//useCaseRVLExampleData.registerDataFile(ExampleData.RVL_EXAMPLE_OLD);
		//useCaseRVLExampleData.setRvlInterpreter(new SimpleRVLInterpreter());
		useCaseRVLExampleData.setD3Generator(new D3GeneratorDeepLabelsJSON());
		//useCaseRVLExampleData.setD3Generator(new D3GeneratorTreeJSON());
		storeProject(useCaseRVLExampleData);
		
		//////////////////////////////////////////////////////////////////
		// Containment Test
		///////////////////////////////////////////////////////////////////
		VisProject containmentTest = new VisProject("containment-test");
		containmentTest.setReasoningDataModel(Reasoning.rdfs);
		containmentTest.registerMappingFile("/example-mappings/containment-test.ttl");
		containmentTest.registerDataFile(ExampleData.RVL_EXAMPLE);
		containmentTest.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		containmentTest.setD3Generator(new D3GeneratorTreeJSON());
		containmentTest.setDefaultGraphicType("circle-packing-zoomable");
		storeProject(containmentTest);
		
		//////////////////////////////////////////////////////////////////
		// Labeling Test
		///////////////////////////////////////////////////////////////////
		VisProject labelingTest = new VisProject("labeling-test");
		labelingTest.registerMappingFile("/example-mappings/labeling-test.ttl");
		labelingTest.registerDataFile(ExampleData.RVL_EXAMPLE);
		labelingTest.setD3Generator(new D3GeneratorDeepLabelsJSON());
		//labelingTest.setD3GraphicFile("circle-packing-zoomable/index.html");
		storeProject(labelingTest);
		
		//////////////////////////////////////////////////////////////////
		// Automatic Value Mapping Test
		///////////////////////////////////////////////////////////////////
		VisProject automaticValueMappingTest = new VisProject("automatic-value-mapping-test");
		automaticValueMappingTest.setReasoningDataModel(Reasoning.rdfs);
		automaticValueMappingTest.registerMappingFile(ExampleMapping.RVL_EXAMPLE_AUTOMATIC_VM);
		automaticValueMappingTest.registerDataFile(ExampleData.RVL_EXAMPLE);
		automaticValueMappingTest.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		storeProject(automaticValueMappingTest);
		
		// Alternative, simplified project creation and storage used from here on ...
		
		VisProject project;
		
		project = storeProject("filtering-test");
		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE_FILTERING);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		
		project = storeProject("identity-mapping-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE_IDENTITY);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		project.setD3Generator(new D3GeneratorDeepLabelsJSON());

		project = storeProject("labeling-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE_LABELING);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);

		project = storeProject("linking-directed-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE_LINKING);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		project.setD3Generator(new D3GeneratorTreeJSON());
		
		project = storeProject("linking-undirected-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE_LINKING_UNDIRECTED);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		project.setD3Generator(new D3GeneratorDeepLabelsJSON());
		
		project = storeProject("relative-distance-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile(ExampleMapping.RVL_EXAMPLE_RELATIVE_DISTANCE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		project.setD3Generator(new D3GeneratorDeepLabelsJSON());
		
		project = storeProject("rdf-id-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile(ExampleMapping.RVL_TEST_RDF_ID);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		
		project = storeProject("sub-mapping-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		// TODO combine tests when the 2nd one works:
		//project.registerMappingFile(ExampleMapping.RVL_TEST_SUBMAPPING);
		project.registerMappingFile("/example-mappings/submapping-on-p2gam-test.ttl");
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		//project.setD3Generator(new D3GeneratorTreeJSON());
		
		project = storeProject("tbox-domain-range-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile("/example-mappings/tbox-domain-range-test.ttl");
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		
		project = storeProject("temp-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile(ExampleMapping.RVL_TEST_TEMP);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		//project.setD3Generator(new D3GeneratorTreeJSON());
		
		project = storeProject("area-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile("/example-mappings/area-test.ttl");
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		
		//////////////////////////////////////////////////////////////////
		// "Bootstrapping" RVL Classes
		///////////////////////////////////////////////////////////////////
		project = storeProject("rvl");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile("/example-mappings/rvl-bootstrap.ttl");
		project.registerDataFile(OntologyFile.RVL);
		project.registerDataFile(ExampleData.RVL_EXTRA_DATA);
		project.setD3Generator(new D3GeneratorTreeJSON());
		project.setDefaultGraphicType("collapsible-tree");
		
		project = storeProject("rvl-circle-packing");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile("/example-mappings/rvl-bootstrap-containment.ttl");
		project.registerDataFile(OntologyFile.RVL);
		project.registerDataFile(ExampleData.RVL_EXTRA_DATA);
		project.setD3Generator(new D3GeneratorTreeJSON());
		project.setDefaultGraphicType("circle-packing-zoomable");
		
		//////////////////////////////////////////////////////////////////
		// "Bootstrapping" AVM (example)
		///////////////////////////////////////////////////////////////////
		project = storeProject("avm-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile("/example-mappings/avm-bootstrap.ttl");
		project.registerDataFile("/example-data/avm-example-data.ttl");
		project.registerDataFile(ExampleData.AVM_EXTRA_DATA);
		project.setD3Generator(new D3GeneratorDeepLabelsJSON());
		
	}
	
	public void storeProject(VisProject project) {
		this.library.put(project.getName(), project);
		LOGGER.info("Stored new project " + project.getId());
	}
	
	public VisProject storeProject(String nameOfNewProject) {
		VisProject project = new VisProject(nameOfNewProject);
		storeProject(project);
		return project;
	}

	public void listProjects() {
		
		System.out.println("Available Visualization Projects: ");
		
		Set<Entry<String, VisProject>> projects = library.entrySet();
		
		for (Entry<String, VisProject> project : projects) {
			
			String line = project.getKey();
			
			String description = project.getValue().getDescription();
			
			if (null != description) {
				line += " - " + description;
			}
			
			System.out.println(line);	
		}
		
	}

	// TODO throw some "project-not-found-exception"
	public VisProject getProject(String useCaseName) {
		return library.get(useCaseName);
	}

	public boolean contains(String projectName){
		return library.containsKey(projectName);
	}

	public static VisProjectLibrary getInstance() {
		if (instance == null) {
	        instance = new VisProjectLibrary();
	    }
	    return instance;
	}
	
	public Collection<? extends VisProject> getProjects() {
		return library.values();
	}
	
}
