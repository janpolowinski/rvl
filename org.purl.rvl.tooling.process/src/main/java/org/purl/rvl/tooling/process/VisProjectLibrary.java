package org.purl.rvl.tooling.process;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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
		// "Bootstrapping" the latest generated AVM
		///////////////////////////////////////////////////////////////////
		VisProject avmBootstrap = new VisProject("avm");
		//avmBootstrap.setWriteAVM(false);
		avmBootstrap.registerMappingFile(ExampleMapping.AVM_EXAMPLE_BOOTSTRAP);
		avmBootstrap.registerDataFile(ExampleData.AVM);
		avmBootstrap.registerDataFile(ExampleData.AVM_EXTRA_DATA);
		//avmBootstrap.setRvlInterpreter(new SimpleRVLInterpreter());
		avmBootstrap.setD3Generator(new D3GeneratorDeepLabelsJSON());
		//avmBootstrap.setD3Generator(new D3GeneratorTreeJSON());
		storeProject(avmBootstrap);
				
		//////////////////////////////////////////////////////////////////
		// "Bootstrapping" RVL Classes
		///////////////////////////////////////////////////////////////////
		VisProject useCaseRVLClasses = new VisProject("rvl");
		useCaseRVLClasses.registerMappingFile(ExampleMapping.RVL_EXAMPLE_BOOTSTRAP);
		useCaseRVLClasses.registerDataFile(OntologyFile.RVL);
		useCaseRVLClasses.registerDataFile(ExampleData.RVL_EXTRA_DATA);
		//useCaseRVLClasses.setRvlInterpreter(new SimpleRVLInterpreter());
		useCaseRVLClasses.setD3Generator(new D3GeneratorTreeJSON());
		//useCaseRVLClasses.setD3Generator(new D3GeneratorSimpleJSON());
		storeProject(useCaseRVLClasses);
		
		//////////////////////////////////////////////////////////////////
		// "Bootstrapping" VISO_GRAPHIC Classes
		///////////////////////////////////////////////////////////////////
		VisProject useCaseVISOClasses = new VisProject("viso");
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
		VisProject useCaseRVLExampleData = new VisProject("example");
		useCaseRVLExampleData.registerMappingFile(ExampleMapping.RVL_EXAMPLE);
		//useCaseRVLExampleData.registerMappingFile(ExampleMapping.RVL_EXAMPLE_MINI);
		useCaseRVLExampleData.registerDataFile(ExampleData.RVL_EXAMPLE);
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
		containmentTest.registerMappingFile("/example-mappings/containment-test.ttl");
		containmentTest.registerDataFile(ExampleData.RVL_EXAMPLE);
		containmentTest.setD3Generator(new D3GeneratorTreeJSON());
		containmentTest.setD3GraphicFile("circle-packing-zoomable/index.html");
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
		
//		//////////////////////////////////////////////////////////////////
//		// Amino-Acids
//		///////////////////////////////////////////////////////////////////
//		VisProject useCaseAA = new VisProject("aa");
//		useCaseAA.registerMappingFile("/life-sciences/amino-acid/example-mappings/experimental-wip.ttl");
//		useCaseAA.registerDataFile("/life-sciences/amino-acid/example-data/amino-acid.owl");
//		//useCaseAA.setD3Generator(new D3GeneratorTreeJSON());
//		useCaseAA.setD3Generator(new D3GeneratorDeepLabelsJSON());
//		storeProject(useCaseAA);
//		
//		//////////////////////////////////////////////////////////////////
//		// Zebra-fishs
//		///////////////////////////////////////////////////////////////////
//		VisProject useCaseZFA = new VisProject("zfa");
//		useCaseZFA.registerMappingFile("/life-sciences/zebra-fish-anatomy/example-mappings/experimental-wip.ttl");
//		useCaseZFA.registerDataFile("/life-sciences/zebra-fish-anatomy/example-data/ZFA_subset.ttl");
//		//useCaseAA.setRvlInterpreter(new SimpleRVLInterpreter());
//		useCaseZFA.setD3Generator(new D3GeneratorTreeJSON());
//		//useCaseAA.setD3Generator(new D3GeneratorSimpleJSON());
//		storeProject(useCaseZFA);
		
	}
	
	public void storeProject(VisProject project){
		this.library.put(project.getName(), project);
	}

	public void listProjects() {
		
		System.out.println("Available Visualization Projects: ");	
		
		/*Set<Entry<String,VisProject>> projects = library.entrySet();
		
		for (Iterator<Entry<String,VisProject>> iterator = projects.iterator(); iterator.hasNext();) {
			Entry<String, VisProject> entry = (Entry<String, VisProject>) iterator
					.next();
			
			System.out.println(entry.getValue());	
		}*/
		
		
		Set<String> projectNames = library.keySet();
		
		for (String projectName : projectNames) {
			System.out.println(projectName);	
		}
		
	}

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
	
}
