package org.purl.rvl.tooling.process;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.ontoware.rdf2go.Reasoning;
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
		
		LOGGER.fine("Initializing example visualization projects library");
		
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
		useCaseZFA_inheritance.setReasoningDataModel(Reasoning.rdfs);
		useCaseZFA_inheritance.registerMappingFile("/life-sciences/zebra-fish-anatomy/example-mappings/ZFA-inheritance.ttl");
		useCaseZFA_inheritance.registerDataFile("/life-sciences/zebra-fish-anatomy/example-data/ZFA_subset.ttl");
		useCaseZFA_inheritance.setD3Generator(new D3GeneratorTreeJSON());
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		// requires better filtering!
		//project.setD3Generator(new D3GeneratorTreeJSON());
		storeProject(useCaseZFA_inheritance);
		
		//////////////////////////////////////////////////////////////////
		// Plants
		///////////////////////////////////////////////////////////////////
		VisProject useCasePO_8 = new VisProject("po-8");
		useCasePO_8.setReasoningDataModel(Reasoning.rdfs); // setting to rdfs makes it necessary to remove the transitive hull (default setting at the moment),
		   											   // but shape is not evaluated otherwise
		useCasePO_8.registerMappingFile("/life-sciences/plant-ontology/example-mappings/PO_8.ttl");
		useCasePO_8.registerDataFile("/life-sciences/plant-ontology/example-data/po_anatomy.owl");
		useCasePO_8.registerDataFile("/life-sciences/plant-ontology/example-mappings/extra-data.ttl");
		useCasePO_8.setD3Generator(new D3GeneratorTreeJSON());
		storeProject(useCasePO_8);
		
		VisProject useCasePO_9 = new VisProject("po-9");
		useCasePO_9.setReasoningDataModel(Reasoning.rdfs); // setting to rdfs makes it necessary to remove the transitive hull (default setting at the moment),
		   // but shape is not evaluated otherwise
		useCasePO_9.registerMappingFile("/life-sciences/plant-ontology/example-mappings/PO_9.ttl");
		useCasePO_9.registerDataFile("/life-sciences/plant-ontology/example-data/po_anatomy.owl");
		useCasePO_9.registerDataFile("/life-sciences/plant-ontology/example-mappings/extra-data.ttl");
		useCasePO_9.setD3Generator(new D3GeneratorTreeJSON());
		useCasePO_9.setDefaultGraphicType("collapsible-tree");
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
		useCaseCIT_blog.setD3Generator(new D3GeneratorTreeJSON());
		storeProject(useCaseCIT_blog);
		
		VisProject project;
		
		project = storeProject("ro");
		project.registerMappingFile("/software/ro/example-mappings/experimental-wip.ttl");
		//project.registerDataFile("/software/ro/example-mappings/extra-data.ttl"));
		project.registerDataFile("/software/ro/example-data/ro_v_1_4_1_incl_social_network_example.owl");
		project.setD3Generator(new D3GeneratorTreeJSON());

		project = storeProject("ro-4b");
		project.registerMappingFile("/software/ro/example-mappings/RO_4b.ttl");
		project.registerDataFile("/software/ro/example-mappings/extra-data.ttl");
		project.registerDataFile("/software/ro/example-data/ro_v_1_4_1_incl_social_network_example.owl");

		project = storeProject("ro-5");
		project.registerMappingFile("/software/ro/example-mappings/RO_5.ttl");
		project.registerDataFile("/software/ro/example-mappings/extra-data.ttl");
		project.registerDataFile("/software/ro/example-data/ro_v_1_4_1_incl_social_network_example.owl");

		project = storeProject("ro-6");
		project.registerMappingFile("/software/ro/example-mappings/RO_6.ttl");
		//project.registerMappingFile("/software/ro/example-mappings/RO_type_shape_test.ttl"));
		project.registerDataFile("/software/ro/example-mappings/extra-data.ttl");
		project.registerDataFile("/software/ro/example-data/ro_v_1_4_1_incl_social_network_example.owl");		
		//project.setD3Generator(new D3GeneratorSimpleJSON());
		project.setD3Generator(new D3GeneratorDeepLabelsJSON());
		
		project = storeProject("ro-7");
		project.registerMappingFile("/software/ro/example-mappings/RO_7.ttl");
		//project.registerMappingFile("/software/ro/example-mappings/RO_type_shape_test.ttl"));
		project.registerDataFile("/software/ro/example-mappings/extra-data.ttl");
		project.registerDataFile("/software/ro/example-data/ro_v_1_4_1_incl_social_network_example.owl");		
		//project.setD3Generator(new D3GeneratorSimpleJSON());
		//project.setD3Generator(new D3GeneratorDeepLabelsJSON());
		project.setD3Generator(new D3GeneratorTreeJSON());
		project.setDefaultGraphicType("circle-packing-zoomable");
		
		project = storeProject("ro-instances-social-network");
		project.setReasoningDataModel(Reasoning.rdfs);
		project.registerMappingFile("/software/ro/example-mappings/ro-instances-social-network.ttl");
		project.registerDataFile("/software/ro/example-mappings/extra-data.ttl");
		project.registerDataFile("/software/ro/example-data/ro_v_1_4_1_incl_social_network_example.owl");
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorDeepLabelsJSON());
		
		project = storeProject("ogvic-requirements");
		project.registerMappingFile("/software/ro/example-mappings/ro-ogvic-requirements.ttl");
		//project.registerDataFile("/software/ro/example-mappings/extra-data.ttl"));
		project.registerDataFile("/software/ro/example-data/ogvic-requirements.ttl");
		project.registerDataFile("/software/ro/example-data/ro_v_1_4_1_incl_social_network_example.owl");
		project.setD3Generator(new D3GeneratorTreeJSON());
		
		project = storeProject("po");
		project.registerMappingFile("/life-sciences/plant-ontology/example-mappings/experimental-wip.ttl");
		project.registerDataFile("/life-sciences/plant-ontology/example-data/po_anatomy.owl");
		project.registerDataFile("/life-sciences/plant-ontology/example-mappings/extra-data.ttl");
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		project.setD3Generator(new D3GeneratorTreeJSON());
		//process.setD3Generator(new D3GeneratorSimpleJSON());

		project = storeProject("po-9-containment");
		project.setReasoningDataModel(Reasoning.rdfs); // setting to rdfs makes it necessary to remove the transitive hull (default setting at the moment),
		// but shape is not evaluated otherwise
		project.registerMappingFile("/life-sciences/plant-ontology/example-mappings/PO_9_containment.ttl");
		project.registerDataFile("/life-sciences/plant-ontology/example-data/po_anatomy.owl");
		project.registerDataFile("/life-sciences/plant-ontology/example-mappings/extra-data.ttl");
		project.setD3Generator(new D3GeneratorTreeJSON());
		project.setDefaultGraphicType("circle-packing-zoomable");
		
		project = storeProject("aa-3-and-4");
		project.registerMappingFile("/life-sciences/amino-acid/example-mappings/AA_4.ttl");
		project.registerMappingFile("/life-sciences/amino-acid/example-mappings/AA_3.ttl");
		project.registerDataFile("/life-sciences/amino-acid/example-data/amino-acid.owl");

		project = storeProject("tolkien");
		project.registerDataFile("/library-and-publication/tolkien/example-data/tolkien-example-data.ttl");
		project.registerMappingFile("/library-and-publication/tolkien/example-mappings/tolkien-example-mappings.ttl");
		project.setD3Generator(new D3GeneratorTreeJSON());
		
		//project.registerDataFile(ExampleData.LLD_TEST);
		//project.registerDataFile(ExampleData.LLD_EXTRA_DATA);
		//project.registerMappingFile(ExampleMapping.LLD);
		//project.setRvlInterpreter(new SimpleRVLInterpreter());
		//project.setD3Generator(new D3GeneratorTreeJSON());
		
	}
	
	public static VisProjectLibraryExamples getInstance() {
		if (instance == null) {
	        instance = new VisProjectLibraryExamples();
	    }
	    return instance;
	}

}
