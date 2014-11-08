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
		// "Bootstrapping" the latest generated AVM (if there is one already)
		///////////////////////////////////////////////////////////////////
		initAVMBootstrappingProject();
				
		//////////////////////////////////////////////////////////////////
		// "Bootstrapping" RVL Classes
		///////////////////////////////////////////////////////////////////
		VisProject useCaseRVLClasses = new VisProject("rvl");
		useCaseRVLClasses.setReasoningDataModel(Reasoning.rdfs);
		useCaseRVLClasses.registerMappingFile(ExampleMapping.RVL_EXAMPLE_BOOTSTRAP);
		useCaseRVLClasses.registerDataFile(OntologyFile.RVL);
		useCaseRVLClasses.registerDataFile(ExampleData.RVL_EXTRA_DATA);
		//useCaseRVLClasses.setRvlInterpreter(new SimpleRVLInterpreter());
		useCaseRVLClasses.setD3Generator(new D3GeneratorTreeJSON());
		//useCaseRVLClasses.setD3Generator(new D3GeneratorSimpleJSON());
		useCaseRVLClasses.setDefaultGraphicType("circle-packing-zoomable");
		storeProject(useCaseRVLClasses);
		
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
		
		project = storeProject("temp-test");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile(ExampleMapping.RVL_TEST_TEMP);
		project.registerDataFile(ExampleData.RVL_EXAMPLE);
		project.registerDataFile(ExampleData.RVL_EXAMPLE_INFERRED_TRIPLES);
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		//project.setD3Generator(new D3GeneratorTreeJSON());

		

		
	}
	
	public void storeProject(VisProject project) {
		this.library.put(project.getName(), project);
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

	public VisProject getProject(String useCaseName) {
		if (useCaseName.equals("avm")) {
			try {
				initAVMBootstrappingProject();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return library.get(useCaseName);
	}
	
	private void initAVMBootstrappingProject() throws FileNotFoundException {
		
		VisProject avmBootstrap = new VisProject("avm");
		//avmBootstrap.setWriteAVM(false);
		avmBootstrap.registerMappingFile(ExampleMapping.AVM_EXAMPLE_BOOTSTRAP);
		try {
			avmBootstrap.registerDataFile(ExampleData.AVM);
			avmBootstrap.setDescription("available");
		} catch (FileNotFoundException e) {
			LOGGER.fine("AVM file was not not found, probably because this is the first run.");
			avmBootstrap.setDescription("Make sure to run some other project first.");
		}
		avmBootstrap.registerDataFile(ExampleData.AVM_EXTRA_DATA);
		//avmBootstrap.setRvlInterpreter(new SimpleRVLInterpreter());
		avmBootstrap.setD3Generator(new D3GeneratorDeepLabelsJSON());
		//avmBootstrap.setD3Generator(new D3GeneratorTreeJSON());
		storeProject(avmBootstrap);
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
