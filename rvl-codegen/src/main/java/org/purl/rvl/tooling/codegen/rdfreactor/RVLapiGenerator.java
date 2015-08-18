package org.purl.rvl.tooling.codegen.rdfreactor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdfreactor.generator.CodeGenerator;


/**
 * @author Jan Polowinski
 *
 */
public class RVLapiGenerator {

//  now passed as a parameter:
//	final public static String PATH_FOR_GEN_CODE = "target/generated-sources/java/";
	
	public static final String PACKAGE = "org.purl.rvl.java.gen.rvl";
//	public static final String PACKAGE = "org.purl.rvl.test.pizza";
//	public static final String PACKAGE = "org.purl.rvl.test.peopelpets";

	
	public static void main(String[] args) throws Exception {
		
		String pathToGenCodeParam = "";
		try {
			pathToGenCodeParam = args[0];
			System.out.println(pathToGenCodeParam);
		} catch (Exception e) {
			System.out.println("No path-to-gen-code parameter found. No RVL code will be generated.");
			return;
		}
		
		// create the RDF2GO Model
		Model model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		model.open();
		
		// read RVL vocabulary
		InputStream rvlStream = new RVLapiGenerator().getClass().getResourceAsStream(OntologyFile.RVL);
		// add extra triples useful for code generation
		InputStream rvlExtraStream = new RVLapiGenerator().getClass().getResourceAsStream(OntologyFile.RVL_EXTRA);

		if (null == rvlStream) {
			throw new Exception("RVL ontology not available.");
		} else if (null == rvlExtraStream) {
			throw new Exception("RVL extra triples for code generation not available.");
		} else {
			try {
				model.readFrom(rvlStream, Syntax.Turtle);
				model.readFrom(rvlExtraStream, Syntax.Turtle);
			} catch (IOException e) {
				throw new Exception("Problem reading RVL files: " + e.getMessage());
			}
		}

		CodeGenerator.generate(model, new File(pathToGenCodeParam), PACKAGE, Reasoning.rdfs, true,"");
		
//		CodeGenerator.generate(
//				ONTOLOGY_URL,
//				PATH_FOR_GEN_CODE,
//				PACKAGE,
//				Reasoning.rdfs,
//				true // deprecated, but better documented:,true
//				);
		
	}


}
