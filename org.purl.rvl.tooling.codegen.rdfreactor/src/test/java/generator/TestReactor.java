package generator;

import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdfreactor.generator.CodeGenerator;



public class TestReactor {

	// paths
	final public static String PATH_TO_PROJECT_ONTOLOGIES = "src/test/resources/ontologies/";
	final public static String PATH_FOR_GEN_CODE = "src/main/java/";
	
	// ontologies
//	final public static String RVL_LOCAL_ABSOLUTE = "file:///Users/Jan/VersionControlRepositories/git/rvl/org.purl.rvl.vocabulary/rvl.owl"; // does not work somehow
	final public static String RVL_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl.owl"; // HACK: references the rvl.owl in the vocabularies project
	public static final String ONTOLOGY_URL = RVL_LOCAL_REL;
//	public static final String ONTOLOGY_URL = PATH_TO_PROJECT_ONTOLOGIES + "rvl.owl";
//	public static final String ONTOLOGY_URL = PATH_TO_PROJECT_ONTOLOGIES + "pizza.owl";
	
	public static final String PACKAGE = "org.purl.rvl.interpreter.rvl";
//	public static final String PACKAGE = "org.purl.rvl.test.pizza";

	public static void main(String[] args) throws Exception {
		CodeGenerator.generate(
				ONTOLOGY_URL,
				PATH_FOR_GEN_CODE,
				PACKAGE,
				Reasoning.rdfs,
				true // deprecated, but better documented:,true
				);
	}


}
