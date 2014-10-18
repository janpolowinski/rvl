package org.purl.rvl.tooling.process;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.purl.rvl.tooling.avm2d3.D3GeneratorDeepLabelsJSON;
import org.purl.rvl.tooling.avm2d3.D3GeneratorTreeJSON;

/**
 * @author Jan Polowinski
 *
 */
public class VisProjectLibraryExamples extends VisProjectLibrary {
	
	private static VisProjectLibraryExamples instance;

	Map<String,VisProject> library = new HashMap<String,VisProject>();
	
	private final static Logger LOGGER = Logger.getLogger(VisProjectLibraryExamples.class.getName()); 
	
	/**
	 * @throws FileNotFoundException 
	 * 
	 */
	private VisProjectLibraryExamples() {
		super();
	}
	
	public void initWithUseCaseTestProjects() throws FileNotFoundException {
		
		// init basic test projects defined in other maven projects
		super.initWithUseCaseTestProjects();
		
		//////////////////////////////////////////////////////////////////
		// Amino-Acids
		///////////////////////////////////////////////////////////////////
		VisProject useCaseAA = new VisProject("aa");
		useCaseAA.registerMappingFile("/life-sciences/amino-acid/example-mappings/experimental-wip.ttl");
		useCaseAA.registerDataFile("/life-sciences/amino-acid/example-data/amino-acid.owl");
		//useCaseAA.setD3Generator(new D3GeneratorTreeJSON());
		useCaseAA.setD3Generator(new D3GeneratorDeepLabelsJSON());
		storeProject(useCaseAA);
		
		VisProject useCaseAA_3 = new VisProject("aa-3");
		useCaseAA_3.registerMappingFile("/life-sciences/amino-acid/example-mappings/AA_3.ttl");
		useCaseAA_3.registerDataFile("/life-sciences/amino-acid/example-data/amino-acid.owl");
		useCaseAA_3.setD3Generator(new D3GeneratorDeepLabelsJSON());
		storeProject(useCaseAA_3);
		
		VisProject useCaseAA_4 = new VisProject("aa-4");
		useCaseAA_4.registerMappingFile("/life-sciences/amino-acid/example-mappings/AA_4.ttl");
		useCaseAA_4.registerDataFile("/life-sciences/amino-acid/example-data/amino-acid.owl");
		useCaseAA_4.setD3Generator(new D3GeneratorDeepLabelsJSON());
		storeProject(useCaseAA_4);
		
		//////////////////////////////////////////////////////////////////
		// Zebra-fishs
		///////////////////////////////////////////////////////////////////
		VisProject useCaseZFA = new VisProject("zfa");
		useCaseZFA.registerMappingFile("/life-sciences/zebra-fish-anatomy/example-mappings/experimental-wip.ttl");
		useCaseZFA.registerDataFile("/life-sciences/zebra-fish-anatomy/example-data/ZFA_subset.ttl");
		//useCaseAA.setRvlInterpreter(new SimpleRVLInterpreter());
		useCaseZFA.setD3Generator(new D3GeneratorTreeJSON());
		storeProject(useCaseZFA);
		
		VisProject useCaseZFA_inheritance = new VisProject("zfa-inheritance");
		useCaseZFA_inheritance.registerMappingFile("/life-sciences/zebra-fish-anatomy/example-mappings/ZFA-inheritance.ttl");
		useCaseZFA_inheritance.registerDataFile("/life-sciences/zebra-fish-anatomy/example-data/ZFA_subset.ttl");
		useCaseZFA_inheritance.setD3Generator(new D3GeneratorTreeJSON());
		storeProject(useCaseZFA_inheritance);
		
		//////////////////////////////////////////////////////////////////
		// Plants
		///////////////////////////////////////////////////////////////////
		VisProject useCasePO_8 = new VisProject("po-8");
		useCasePO_8.registerMappingFile("/life-sciences/plant-ontology/example-mappings/PO_8.ttl");
		useCasePO_8.registerDataFile("/life-sciences/plant-ontology/example-data/po_anatomy.owl");
		useCasePO_8.setD3Generator(new D3GeneratorTreeJSON());
		storeProject(useCasePO_8);
		
		VisProject useCasePO_9 = new VisProject("po-9");
		useCasePO_9.registerMappingFile("/life-sciences/plant-ontology/example-mappings/PO_9.ttl");
		useCasePO_9.registerDataFile("/life-sciences/plant-ontology/example-data/po_anatomy.owl");
		useCasePO_9.setD3Generator(new D3GeneratorTreeJSON());
		storeProject(useCasePO_9);
		
		//////////////////////////////////////////////////////////////////
		// CiTO
		///////////////////////////////////////////////////////////////////
		VisProject useCaseCIT_1 = new VisProject("cit-1");
		useCaseCIT_1.registerMappingFile("/library-and-publication/cito/example-mappings/CIT_1.ttl");
		useCaseCIT_1.registerDataFile("/library-and-publication/cito/example-data/cito.owl");
		useCaseCIT_1.registerDataFile("/library-and-publication/cito/example-data/cito-example-data.ttl");
		useCaseCIT_1.setD3Generator(new D3GeneratorDeepLabelsJSON());
		storeProject(useCaseCIT_1);
		
		VisProject useCaseCIT_5 = new VisProject("cit-5");
		useCaseCIT_5.registerMappingFile("/library-and-publication/cito/example-mappings/CIT_5.ttl");
		useCaseCIT_5.registerDataFile("/library-and-publication/cito/example-data/cito.owl");
		useCaseCIT_5.registerDataFile("/library-and-publication/cito/example-data/cito-example-data.ttl");
		useCaseCIT_5.setD3Generator(new D3GeneratorDeepLabelsJSON());
		storeProject(useCaseCIT_5);
		
		VisProject useCaseCIT_blog = new VisProject("cit-blog");
		useCaseCIT_blog.registerMappingFile("/library-and-publication/cito/example-mappings/CIT_blog.ttl");
		useCaseCIT_blog.registerDataFile("/library-and-publication/cito/example-data/cito.owl");
		useCaseCIT_blog.registerDataFile("/library-and-publication/cito/example-data/cito-example-data.ttl");
		useCaseCIT_blog.setD3Generator(new D3GeneratorDeepLabelsJSON());
		storeProject(useCaseCIT_blog);
		
	}
	
	public static VisProjectLibraryExamples getInstance() {
		if (instance == null) {
	        instance = new VisProjectLibraryExamples();
	    }
	    return instance;
	}

}
