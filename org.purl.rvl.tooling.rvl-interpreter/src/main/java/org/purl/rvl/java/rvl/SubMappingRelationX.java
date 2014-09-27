package org.purl.rvl.java.rvl;

import java.util.List;

import org.ontoware.rdf2go.model.node.Node;
import org.purl.rvl.exception.SubmappingException;
import org.purl.rvl.java.gen.rvl.Identitymapping;
import org.purl.rvl.java.gen.rvl.Mapping;
import org.purl.rvl.java.gen.rvl.PropertyMapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;
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
	
	public PropertyMappingX getSubMapping() throws SubmappingException {
		
		if (hasSubMapping()) {
			
			// wrong methode name, but seems to work
			Mapping subMapping = subMappingRelationGen.getAllSub_mapping_as().firstValue();
			
			//List<org.ontoware.rdfreactor.schema.rdfs.Class> types = subMapping.getAllType_as().asList();
			
			if (subMapping.isInstanceof(Property_to_Graphic_AttributeMapping.RDFS_CLASS)) {
				return new PropertyToGraphicAttributeMappingX((Property_to_Graphic_AttributeMapping)subMapping.castTo(Property_to_Graphic_AttributeMapping.class));
			} 
			else if (subMapping.isInstanceof(Property_to_Graphic_Object_to_Object_RelationMapping.RDFS_CLASS)) {
				return new PropertyToGO2ORMappingX((Property_to_Graphic_Object_to_Object_RelationMapping)subMapping.castTo(Property_to_Graphic_Object_to_Object_RelationMapping.class));
			}
			else if (subMapping.isInstanceof(Identitymapping.RDFS_CLASS)) {
				return new IdentityMappingX((Identitymapping)subMapping.castTo(Identitymapping.class));
			} else {
				throw new SubmappingException("Submappings may only be one of P2GAM, P2GOTOR or Identity mapping. ");
			}
		} else 
			return null;
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

	
	public String toStringDetailed(boolean showSubMappingDetails) {

		String s = "";

		if (hasOnRole()) {
			s += "      ... on role: " + getOnRole() + NL;
		}
		if (hasOnTriplePart()) {
			s += "      ... on triple part: " + getOnTriplePart() + NL;
		}

		if (hasSubMapping()) {
			try {
				s += "      ... to mapping: " + getSubMapping() + NL;

				if (showSubMappingDetails) {

					MappingX subMapping = getSubMapping();

					s += "      ... sub-mapping details: " + NL;
					s += subMapping.toStringAsSpecificAsPossible() + NL;
				}

			} catch (SubmappingException e) {
				s += "      ... sub-mapping could not be printed." + NL;
			}
		}

		return s;
	}
	
	public String toStringDetailed() {
		return toStringDetailed(false);
	}

}
