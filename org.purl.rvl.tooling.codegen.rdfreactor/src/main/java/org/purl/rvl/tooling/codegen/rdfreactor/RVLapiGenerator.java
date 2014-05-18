package org.purl.rvl.tooling.codegen.rdfreactor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdfreactor.generator.CodeGenerator;


public class RVLapiGenerator {

	// paths
	final public static String PATH_TO_TEST_ONTOLOGIES = "src/test/resources/ontologies/";
	final public static String PATH_FOR_GEN_CODE = "src/main/java/";
	
	// ontologies
	final public static String RVL_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl.owl";
	public static final String RVL_EXTRA_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-extra-for-codegen.ttl"; // extra statements required for correct code generation
	public static final String ONTOLOGY_URL = RVL_LOCAL_REL;
//	public static final String ONTOLOGY_URL = PATH_TO_TEST_ONTOLOGIES + "pizza.owl";
//	public static final String ONTOLOGY_URL = PATH_TO_TEST_ONTOLOGIES + "people+pets.owl";
	
	public static final String PACKAGE = "org.purl.rvl.java.gen.rvl";
//	public static final String PACKAGE = "org.purl.rvl.test.pizza";
//	public static final String PACKAGE = "org.purl.rvl.test.peopelpets";
	
	


	public static void main(String[] args) throws Exception {
		
		// create the RDF2GO Model
		Model model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		model.open();
		File file = new File(ONTOLOGY_URL);
		if (file.exists()) {
			try {
				model.readFrom(new FileReader(file),
						Syntax.RdfXml);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// add etxra triples only for code generation which are wrong with respect to RDFS or OWL semantics
		File fileExtra = new File(RVL_EXTRA_LOCAL_REL);
		if (fileExtra.exists()) {
			try {
				model.readFrom(new FileReader(fileExtra),
						Syntax.Turtle);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File pathToGenCode = new File(PATH_FOR_GEN_CODE);
		CodeGenerator.generate(model, pathToGenCode, PACKAGE, Reasoning.rdfs, true,"");
		
//		CodeGenerator.generate(
//				ONTOLOGY_URL,
//				PATH_FOR_GEN_CODE,
//				PACKAGE,
//				Reasoning.rdfs,
//				true // deprecated, but better documented:,true
//				);
		
	}


}
