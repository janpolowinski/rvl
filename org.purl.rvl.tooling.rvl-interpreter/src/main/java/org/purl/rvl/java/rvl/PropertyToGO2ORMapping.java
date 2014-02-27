package org.purl.rvl.java.rvl;

import java.util.HashSet;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.java.exception.InsufficientMappingSpecificationExecption;
import org.purl.rvl.java.gen.rvl.GraphicObjectToObjectRelation;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;
import org.purl.rvl.java.gen.rvl.Sub_mappingrelation;
import org.purl.rvl.tooling.util.RVLUtils;

public class PropertyToGO2ORMapping extends
		Property_to_Graphic_Object_to_Object_RelationMapping implements MappingIF {
	
	static final String NL =  System.getProperty("line.separator");
	
	public PropertyToGO2ORMapping(Model model, URI classURI,
			Resource instanceIdentifier, boolean write) {
		super(model, classURI, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyToGO2ORMapping(Model model, Resource instanceIdentifier,
			boolean write) {
		super(model, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyToGO2ORMapping(Model model, String uriString, boolean write)
			throws ModelRuntimeException {
		super(model, uriString, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyToGO2ORMapping(Model model, BlankNode bnode, boolean write) {
		super(model, bnode, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyToGO2ORMapping(Model model, boolean write) {
		super(model, write);
		// TODO Auto-generated constructor stub
	}
	
	public String toString(){
		
		String s = "";
		
		// try to get the string description from the (manual) PropertyMapping class, which is not in the super-class hierarchy
		PropertyMapping pm = (PropertyMapping) this.castTo(PropertyMapping.class);
		s += pm.toString();
		
		// targetAttribute is specific to P2GAM
		GraphicObjectToObjectRelation tgo2or = this.getAllTargetobject_to_objectrelation_as().firstValue();
		String tgrString = tgo2or.getAllLabel_as().count()>0 ? tgo2or.getAllLabel_as().firstValue() : tgo2or.toString();
		s += "     target GOTOR: " + tgrString + NL ;
		
		if (pm.hasSub_mapping()) {
			// list sub-mappings
			Sub_mappingrelation smr = this.getAllSub_mapping_as().firstValue();
			s += "     Submappig relation: " + smr + NL;
			//s += "          type: " + smr.getAllType_as().firstValue() + NL ;
			if(smr.hasOnrole())
				s += "          ... on role: " + smr.getAllOnrole_as().firstValue() + NL ;
			if(smr.hasOntriplepart())
				s += "          ... on triple part: " + smr.getAllOntriplepart_as().firstValue() + NL ;
			if(smr.hasSub_mapping()) {
				org.purl.rvl.java.gen.rvl.Mapping mapping = smr.getAllSub_mapping_as().firstValue();
				s += "          ... to mapping: " + mapping + NL ; // wrong return type and wrong methode name, but seems to work
				s += "              ... Sub-Mapping-Details: " + NL;
				s += NL;
				s += RVLUtils.mappingToStringAsSpecificAsPossible((Mapping)mapping.castTo(Mapping.class)) + NL ;
			}
	
		}
		
		return s;
	}


	public boolean isDisabled() {
		if (this.hasDisabled()) {
			return this.getAllDisabled_as().firstValue();
		} else return false;
	}

	public boolean isInvertSourceProperty() {
		if (this.hasInvertsourceproperty()) {
			return this.getAllInvertsourceproperty_as().firstValue();
		} else return false;
	}
	
	public Property getSourceProperty() throws InsufficientMappingSpecificationExecption {
		return ((PropertyMapping) this.castTo(PropertyMapping.class)).getSourceProperty();
	}

	public GraphicObjectToObjectRelation getTargetGraphicRelation() throws InsufficientMappingSpecificationExecption {
		if (this.hasTargetobject_to_objectrelation()) {
			return this.getAllTargetobject_to_objectrelation_as().firstValue();
		} else throw new InsufficientMappingSpecificationExecption("Missing target graphic relation.");
	}

	public Property getInheritedBy() {
		if (this.hasInvertsourceproperty()) {
			return (Property)getAllInheritedby_as().firstValue().castTo(Property.class);
		} else return null;
	}

	public Set<SubMappingRelationX> getSubMappings() {
		Set<SubMappingRelationX> subMappingRelationsX = new HashSet<SubMappingRelationX>();
		if (this.hasSub_mapping()) {
			ClosableIterator<Sub_mappingrelation> subMappingRelations =  getAllSub_mapping_as().asClosableIterator();
			while (subMappingRelations.hasNext()) {
				
				Sub_mappingrelation rel = (Sub_mappingrelation) subMappingRelations
						.next();
				
				subMappingRelationsX.add(new SubMappingRelationX(rel));
			}
			return subMappingRelationsX;
		} else return null;
	}

}
