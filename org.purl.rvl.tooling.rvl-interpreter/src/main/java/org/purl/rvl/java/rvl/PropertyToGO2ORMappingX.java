package org.purl.rvl.java.rvl;

import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.gen.rvl.GraphicObjectToObjectRelation;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;

/**
 * @author Jan Polowinski
 *
 */
public class PropertyToGO2ORMappingX extends PropertyMappingX {

	static final String NL =  System.getProperty("line.separator");
	
	private final static Logger LOGGER = Logger.getLogger(PropertyToGO2ORMappingX.class.getName());

	//private Set<SubMappingRelationX> subMappings;
	
	public PropertyToGO2ORMappingX(Property_to_Graphic_Object_to_Object_RelationMapping delegatee) {
		super(delegatee);
	}

	public String toStringDetailed() {
		
		String s = "";

		s += super.toStringDetailed();
		
		// targetAttribute is specific to P2GAM
		GraphicObjectToObjectRelation tgo2or = getDelegatee().getAllTargetobject_to_objectrelation_as().firstValue();
		String tgrString = tgo2or.getAllLabel_as().count()>0 ? tgo2or.getAllLabel_as().firstValue() : tgo2or.toString();
		s += "     target GOTOR: " + tgrString + NL ;
		
		if (super.hasSubMapping()) {
			// list sub-mappings
			// TODO only first submapping listed here
			SubMappingRelationX smr = new SubMappingRelationX(getDelegatee().getAllSub_mapping_as().firstValue());
			s += "     Submapping relation (only first shown): " + smr + NL;
			s += smr.toStringDetailed(true);
		}
		
		return s;
	}

	public boolean isInvertSourceProperty() {
		if (getDelegatee().hasInvertsourceproperty()) {
			return getDelegatee().getAllInvertsourceproperty_as().firstValue();
		} else return false;
	}


	public Property getTargetGraphicRelation() throws InsufficientMappingSpecificationException {
		if (getDelegatee().hasTargetobject_to_objectrelation()) {
			URI uri = getDelegatee().getAllTargetobject_to_objectrelation_as().firstValue().asURI(); 
			return new Property(getDelegatee().getModel(), uri, false);
		} else throw new InsufficientMappingSpecificationException("Missing target graphic relation for mapping " + this);
	}
	
	protected Property_to_Graphic_Object_to_Object_RelationMapping getDelegatee() {
		return (Property_to_Graphic_Object_to_Object_RelationMapping) delegatee.castTo(Property_to_Graphic_Object_to_Object_RelationMapping.class);
	}

}
