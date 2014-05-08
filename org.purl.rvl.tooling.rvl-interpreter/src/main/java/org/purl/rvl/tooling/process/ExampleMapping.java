package org.purl.rvl.tooling.process;

public class ExampleMapping {
	
	public static final String RVL_EXAMPLE_MINI = "../org.purl.rvl.vocabulary/rvl-example-mappings-mini.ttl";
	// problem (no root nodes found) here probably because subClassOf etc. are reflexiv and our method for finding root nodes does not work here! -> exlude direct "loop" relations
	public static final String RVL_EXAMPLE_BOOTSTRAP = "../org.purl.rvl.vocabulary/rvl-example-mappings-bootstrap.ttl";
	public static final String RO = OGVICProcess.USE_CASE_FOLDER + "/Requirements/stRO/MappingExample/ro-rvl-mapping-example.ttl";
	public static final String RO_4b = OGVICProcess.USE_CASE_FOLDER + "/Requirements/stRO/MappingExample/RO_4b.ttl";
	public static final String RO_5 = OGVICProcess.USE_CASE_FOLDER + "/Requirements/stRO/MappingExample/RO_5.ttl";
	public static final String RO_SOCIAL_NETWORK = OGVICProcess.USE_CASE_FOLDER + "/Requirements/stRO/MappingExample/ro-rvl-mapping-example-social-network.ttl";
	public static final String PO = OGVICProcess.USE_CASE_FOLDER + "/Bio/PO/MappingExample/po-rvl-mapping-example.ttl";
	public static final String PO_8 = OGVICProcess.USE_CASE_FOLDER + "/Bio/PO/MappingExample/PO_8.ttl";
	public static final String PO_9 = OGVICProcess.USE_CASE_FOLDER + "/Bio/PO/MappingExample/PO_9.ttl";
	public static final String RVL_EXAMPLE_OLD = "../org.purl.rvl.vocabulary/rvl-example-mappings-old.ttl";
	public static final String RVL_EXAMPLE = "../org.purl.rvl.vocabulary/rvl-example-mappings.ttl";
	public static final String ZFO = OGVICProcess.USE_CASE_FOLDER + "/Bio/Zebrafish/MappingExample/zfa-rvl-mapping-example.n3";
	public static final String ZFO_X = OGVICProcess.USE_CASE_FOLDER + "/Bio/Zebrafish/MappingExample/ZFO-X.n3";
	public static final String ZFO_Y = OGVICProcess.USE_CASE_FOLDER + "/Bio/Zebrafish/MappingExample/ZFO-Y.n3";
	public static final String AA = OGVICProcess.USE_CASE_FOLDER + "/Bio/AminoAcid/MappingExample/amino-acids-rvl-mapping-example.ttl";
	public static final String AA_4 = OGVICProcess.USE_CASE_FOLDER + "/Bio/AminoAcid/MappingExample/AA_4.ttl";
	
	public static final String SLUB = OGVICProcess.USE_CASE_FOLDER + "/Publication/SLUB/slub-data-mapping-example.ttl";
	public static final String AVM_EXAMPLE_BOOTSTRAP = "../org.purl.rvl.vocabulary/avm-example-mappings-bootstrap.ttl";

}
