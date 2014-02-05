package org.purl.rvl.tooling.process;

public class ExampleMapping {
	
	public static final String RVL_EXAMPLE_MINI = "../org.purl.rvl.vocabulary/rvl-example-mappings-mini.ttl";
	// problem (no root nodes found) here probably because subClassOf etc. are reflexiv and our method for finding root nodes does not work here! -> exlude direct "loop" relations
	public static final String RVL_EXAMPLE_BOOTSTRAP = "../org.purl.rvl.vocabulary/rvl-example-mappings-bootstrap.ttl";
	public static final String RO = "/Users/Jan/Projekte/Beruf/Promotion/Recherche/CaseStudies/Requirements/stRO/MappingExample/ro-rvl-mapping-example.ttl";
	public static final String RO_SOCIAL_NETWORK = "/Users/Jan/Projekte/Beruf/Promotion/Recherche/CaseStudies/Requirements/stRO/MappingExample/ro-rvl-mapping-example-social-network.ttl";
	public static final String PO = "/Users/Jan/Projekte/Beruf/Promotion/Recherche/CaseStudies/Bio/PO/MappingExample/po-rvl-mapping-example.ttl";
	public static final String RVL_EXAMPLE = "../org.purl.rvl.vocabulary/rvl-example-mappings.ttl";
	public static final String ZFO = "/Users/Jan/Projekte/Beruf/Promotion/Recherche/CaseStudies/Bio/Zebrafish/MappingExample/zfa-rvl-mapping-example.n3";
	public static final String ZFO_X = "/Users/Jan/Projekte/Beruf/Promotion/Recherche/CaseStudies/Bio/Zebrafish/MappingExample/ZFO-X.n3";
	public static final String ZFO_Y = "/Users/Jan/Projekte/Beruf/Promotion/Recherche/CaseStudies/Bio/Zebrafish/MappingExample/ZFO-Y.n3";

}
