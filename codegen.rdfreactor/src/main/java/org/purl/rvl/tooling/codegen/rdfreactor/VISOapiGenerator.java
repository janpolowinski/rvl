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
public class VISOapiGenerator {

//  now passed as a parameter:
//	final public static String PATH_FOR_GEN_CODE = "target/generated-sources/java/";

	public static final String PACKAGE = "org.purl.rvl.java.gen.viso.graphic";

	public static void main(String[] args) throws Exception {
		
		String pathToGenCodeParam = "";
		try {
			pathToGenCodeParam = args[0];
			System.out.println(pathToGenCodeParam);
		} catch (Exception e) {
			System.out.println("No path-to-gen-code parameter found. No VISO code will be generated.");
			return;
		}

		// create the RDF2GO Model
		Model model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		model.open();
		
		// read VISO/graphic
		InputStream visoStream = new VISOapiGenerator().getClass().getResourceAsStream(OntologyFile.VISO_GRAPHIC);
		// add extra triples useful for code generation
		InputStream visoExtraStream = new VISOapiGenerator().getClass().getResourceAsStream(OntologyFile.VISO_GRAPHIC_EXTRA);

		if (null == visoStream) {
			throw new Exception("VISO ontology not available.");
		} else if (null == visoExtraStream) {
			throw new Exception("VISO extra triples for code generation not available.");
		} else {
			try {
				model.readFrom(visoStream, Syntax.Turtle);
				model.readFrom(visoExtraStream, Syntax.Turtle);
			} catch (IOException e) {
				throw new Exception("Problem reading VISO files: " + e.getMessage());
			}
		}

		CodeGenerator.generate(model, new File(pathToGenCodeParam), PACKAGE, Reasoning.rdfs,
				true, "");

		// CodeGenerator.generate(
		// VISO_GRAPHIC,
		// PATH_FOR_GEN_CODE,
		// PACKAGE,
		// Reasoning.rdfs, // rdfsAndOwl causes errors when rdfs:comment is
		// missing at ontology classes
		// true // deprecated, but better documented:,true
		// );

	}

}
