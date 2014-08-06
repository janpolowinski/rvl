package org.purl.rvl.java.rvl;

import org.ontoware.rdf2go.model.node.Node;
import org.purl.rvl.java.gen.rvl.Sub_mappingrelation;
import org.purl.rvl.java.gen.rvl.SyntacticRole;

/**
 * @author Jan Polowinski
 *
 */
public class SubMappingRelationX {
	
	static final String NL =  System.getProperty("line.separator");
	
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
	
	public MappingX getSubMapping() {
		// wrong return type and wrong methode name, but seems to work
		return (MappingX)subMappingRelationGen.getAllSub_mapping_as().firstValue().castTo(MappingX.class);
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

	
	public String toStringDetailed(boolean showSubMappingDetails){
		
		String s = "";
		
		if (hasOnRole()) {
			s += "      ... on role: " + getOnRole() + NL ;
		}
		if(hasOnTriplePart()) {
			s += "      ... on triple part: " + getOnTriplePart() + NL;
		}

		if (hasSubMapping()) {
			s += "      ... to mapping: " + getSubMapping() + NL ;
			
			if (showSubMappingDetails) {
				
				MappingX subMapping = getSubMapping();
				
				s += "      ... sub-mapping details: " + NL;
				s += subMapping.toStringAsSpecificAsPossible() + NL ;
			}
		}
		
		return s;
	}
	
	public String toStringDetailed() {
		return toStringDetailed(false);
	}

}
