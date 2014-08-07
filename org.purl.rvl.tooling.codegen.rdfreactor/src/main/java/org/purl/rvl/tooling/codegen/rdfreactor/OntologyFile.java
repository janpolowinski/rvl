/**
 * 
 */
package org.purl.rvl.tooling.codegen.rdfreactor;

/**
 * @author Jan Polowinski
 *
 */
public class OntologyFile {

	// RVL FILES
	public static final String RVL = "../org.purl.rvl.vocabulary/rvl.owl";
	public static final String RVL_EXTRA = "../org.purl.rvl.vocabulary/rvl-extra-for-codegen.ttl"; // extra statements required for correct code generation
	
	// VISO_GRAPHIC FILES
	final public static String VISO_GRAPHIC = "../../viso-ontology/modules/graphic/viso-graphic.ttl";
	final public static String VISO_GRAPHIC_EXTRA = "../../viso-ontology/modules/graphic-for-rvl-codegen/viso-graphic-extra-for-codegen.ttl"; // extra statements required for correct code generation

		
	// PATHS
	final public static String PATH_TO_TEST_ONTOLOGIES = "src/test/resources/ontologies/";
	

	// TEST ONTOLOGIES
	//	public static final String TEST_ONTOLOGY = PATH_TO_TEST_ONTOLOGIES + "pizza.owl";
	//	public static final String TEST_ONTOLOGY = PATH_TO_TEST_ONTOLOGIES + "people+pets.owl";

}
