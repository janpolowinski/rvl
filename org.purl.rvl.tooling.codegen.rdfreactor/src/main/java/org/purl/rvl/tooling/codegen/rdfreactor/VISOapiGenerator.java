package org.purl.rvl.tooling.codegen.rdfreactor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

	// paths
	final public static String PATH_FOR_GEN_CODE = "src/main/java/";

	public static final String PACKAGE = "org.purl.rvl.java.gen.viso.graphic";

	public static void main(String[] args) throws Exception {

		// create the RDF2GO Model
		Model model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		model.open();
		
		// read VISO_GRAPHIC/graphic into its own model and into the main model
		File file = new File(OntologyFile.VISO_GRAPHIC);
		
		if (file.exists()) {
			try {
				model.readFrom(new FileReader(file), Syntax.Turtle);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// add extra triples useful for code generation
		File fileVisoExtra = new File(OntologyFile.VISO_GRAPHIC_EXTRA);
		
		if (file.exists()) {
			try {
				model.readFrom(new FileReader(fileVisoExtra), Syntax.Turtle);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		CodeGenerator.generate(model, new File(PATH_FOR_GEN_CODE), PACKAGE, Reasoning.rdfs,
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
