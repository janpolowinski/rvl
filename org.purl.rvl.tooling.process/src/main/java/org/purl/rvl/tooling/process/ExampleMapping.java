package org.purl.rvl.tooling.process;

/**
 * @author Jan Polowinski
 *
 */
public class ExampleMapping {
	
//	public static final String RO = OGVICProcess.USE_CASE_FOLDER + "/Requirements/stRO/MappingExample/ro-rvl-mapping-example.ttl";
//	public static final String RO_4b = OGVICProcess.USE_CASE_FOLDER + "/Requirements/stRO/MappingExample/RO_4b.ttl";
//	public static final String RO_5 = OGVICProcess.USE_CASE_FOLDER + "/Requirements/stRO/MappingExample/RO_5.ttl";
//	public static final String RO_6 = OGVICProcess.USE_CASE_FOLDER + "/Requirements/stRO/MappingExample/RO_6.ttl";
//	public static final String RO_7 = OGVICProcess.USE_CASE_FOLDER + "/Requirements/stRO/MappingExample/RO_7.ttl";
//	public static final String RO_SOCIAL_NETWORK = OGVICProcess.USE_CASE_FOLDER + "/Requirements/stRO/MappingExample/ro-rvl-mapping-example-social-network.ttl";
//	public static final String PO = OGVICProcess.USE_CASE_FOLDER + "/Bio/PO/MappingExample/po-rvl-mapping-example.ttl";
//	public static final String PO_7 = OGVICProcess.USE_CASE_FOLDER + "/Bio/PO/MappingExample/PO_7.ttl";
//	public static final String PO_8 = OGVICProcess.USE_CASE_FOLDER + "/Bio/PO/MappingExample/PO_8.ttl";
//	public static final String PO_9 = OGVICProcess.USE_CASE_FOLDER + "/Bio/PO/MappingExample/PO_9.ttl";

//	public static final String ZFO = OGVICProcess.USE_CASE_FOLDER + "/Bio/Zebrafish/MappingExample/zfa-rvl-mapping-example.n3";
//	public static final String ZFO_X = OGVICProcess.USE_CASE_FOLDER + "/Bio/Zebrafish/MappingExample/ZFO-X.n3";
//	public static final String ZFO_Y = OGVICProcess.USE_CASE_FOLDER + "/Bio/Zebrafish/MappingExample/ZFO-Y.n3";
//	public static final String ZFO_inheritance = OGVICProcess.USE_CASE_FOLDER + "/Bio/Zebrafish/MappingExample/ZFO-inheritance.ttl";
//	public static final String AA = OGVICProcess.USE_CASE_FOLDER + "/Bio/AminoAcid/MappingExample/amino-acids-rvl-mapping-example.ttl";
//	public static final String AA_3 = OGVICProcess.USE_CASE_FOLDER + "/Bio/AminoAcid/MappingExample/AA_3.ttl";
//	public static final String AA_4 = OGVICProcess.USE_CASE_FOLDER + "/Bio/AminoAcid/MappingExample/AA_4.ttl";
//	public static final String CIT_1 = OGVICProcess.USE_CASE_FOLDER + "/Publication/SPAR/MappingExample/CIT_1.ttl";
//	public static final String CIT_5 = OGVICProcess.USE_CASE_FOLDER + "/Publication/SPAR/MappingExample/CIT_5.ttl";
//	public static final String CIT_blog = OGVICProcess.USE_CASE_FOLDER + "/Publication/SPAR/MappingExample/CIT_blog.ttl";
//	public static final String LLD = OGVICProcess.USE_CASE_FOLDER + "/Publication/LLD/lld-data-mapping-example.ttl";
	
	public static final String RVL_TEST_TEMP = "/example-mappings/temp-test.ttl";
	public static final String RVL_TEST_RDF_ID = "/example-mappings/rdf-id-test.ttl";
	public static final String RVL_TEST_SUBMAPPING = "/example-mappings/submapping-test.ttl";
	public static final String RVL_EXAMPLE = "/example-mappings/rvl-example-mappings.ttl";
	public static final String RVL_EXAMPLE_FILTERING = "/experiments/example-mappings/rvl-example-filtering.ttl";
	public static final String RVL_EXAMPLE_LABELING = "/example-mappings/labeling-test.ttl";
	public static final String RVL_EXAMPLE_IDENTITY = "/example-mappings/identity-mapping-test.ttl";
	public static final String RVL_EXAMPLE_AUTOMATIC_VM = "/example-mappings/auto-value-mapping-test.ttl";
	public static final String RVL_EXAMPLE_CONTAINMENT = "/example-mappings/containment-test.ttl";
	public static final String RVL_EXAMPLE_LINKING = "/example-mappings/linking-test.ttl";
	
	// problem (no root nodes found) here probably because subClassOf etc. are reflexiv and our method for finding root nodes does not work here! -> exlude direct "loop" relations
	public static final String RVL_EXAMPLE_BOOTSTRAP = "/example-mappings/rvl-bootstrap.ttl";
	public static final String AVM_EXAMPLE_BOOTSTRAP = "/example-mappings/avm-bootstrap.ttl";
	public static final String VISO_EXAMPLE_BOOTSTRAP = "/example-mappings/viso-bootstrap.ttl";


}
