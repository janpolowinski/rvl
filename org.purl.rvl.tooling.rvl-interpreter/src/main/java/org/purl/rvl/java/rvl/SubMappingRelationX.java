package org.purl.rvl.java.rvl;

import org.ontoware.rdf2go.model.node.Node;
import org.purl.rvl.java.gen.rvl.Sub_mappingrelation;
import org.purl.rvl.java.gen.rvl.SyntacticRole;
import org.purl.rvl.tooling.util.RVLUtils;

public class SubMappingRelationX {
	
	private Sub_mappingrelation subMappingRelationGen;

	/**
	 * @param subMappingRelationGen
	 */
	public SubMappingRelationX(Sub_mappingrelation subMappingRelationGen) {
		super();
		this.subMappingRelationGen = subMappingRelationGen;
	}

	
	/**
	 * @return the subMappingRelationGen
	 */
	public Sub_mappingrelation getSubMappingRelationGen() {
		return subMappingRelationGen;
	}
	
	
	public SyntacticRole getOnRole() {
		return subMappingRelationGen.getAllOnrole_as().firstValue() ;
	}
	
	public Node getOnTriplePart() {
		return subMappingRelationGen.getAllOntriplepart_as().firstValue();
	}
	
	public Mapping getSubMapping() {
		// wrong return type and wrong methode name, but seems to work
		return (Mapping)subMappingRelationGen.getAllSub_mapping_as().firstValue().castTo(Mapping.class);
	}
	
	public boolean hasOnRole(){
		return subMappingRelationGen.hasOnrole();
	}


	public boolean hasOnTriplePart() {
		return subMappingRelationGen.hasOnrole();
	}


	public boolean hasSubMapping() {
		return subMappingRelationGen.hasSub_mapping();
	}

	
	public String toStringSummary(){
		
		String label = "";

		if (hasSubMapping() && hasOnRole()) {
			
			label += " ... to mapping: " + getSubMapping() ; // wrong return type and wrong methode name, but seems to work
			label += " ... on role: " + getOnRole() ;
			label += RVLUtils.mappingToStringAsSpecificAsPossible((org.purl.rvl.java.rvl.Mapping)getSubMapping().castTo(org.purl.rvl.java.rvl.Mapping.class)) ;

		}
		if(hasOnTriplePart()) {
			label += " ... on triple part: " + getOnTriplePart() ;
		}
		
		return label;
		
	}


}
