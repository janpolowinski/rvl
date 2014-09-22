package org.purl.rvl.java.rvl;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdfreactor.runtime.ReactorRuntimeEntity;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.gen.rvl.GraphicObjectToObjectRelation;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;
import org.purl.rvl.java.gen.rvl.Sub_mappingrelation;

import com.hp.hpl.jena.rdf.arp.states.HasSubjectFrameI;

/**
 * @author Jan Polowinski
 *
 */
public class PropertyToGO2ORMappingX extends PropertyMappingX {
	
	private static final long serialVersionUID = 1L;

	static final String NL =  System.getProperty("line.separator");
	
	private final static Logger LOGGER = Logger.getLogger(PropertyToGO2ORMappingX.class.getName());

	private Set<SubMappingRelationX> subMappings;

	Property_to_Graphic_Object_to_Object_RelationMapping delegatee; 
	
	public String toStringDetailed(){
		
		String s = "";
		
		// try to get the string description from the (manual) PropertyMappingX class, which is not in the super-class hierarchy
		PropertyMappingX pm = (PropertyMappingX) delegatee.castTo(PropertyMappingX.class);
		s += pm.toStringDetailed();
		
		// targetAttribute is specific to P2GAM
		GraphicObjectToObjectRelation tgo2or = delegatee.getAllTargetobject_to_objectrelation_as().firstValue();
		String tgrString = tgo2or.getAllLabel_as().count()>0 ? tgo2or.getAllLabel_as().firstValue() : tgo2or.toString();
		s += "     target GOTOR: " + tgrString + NL ;
		
		if (pm.hasSubmapping()) {
			// list sub-mappings
			// TODO only first submapping listed here
			SubMappingRelationX smr = new SubMappingRelationX(delegatee.getAllSub_mapping_as().firstValue());
			s += "     Submapping relation (only first shown): " + smr + NL;
			s += smr.toStringDetailed(true);
		}
		
		return s;
	}

	public boolean isInvertSourceProperty() {
		if (delegatee.hasInvertsourceproperty()) {
			return delegatee.getAllInvertsourceproperty_as().firstValue();
		} else return false;
	}
	
	public Property getSourceProperty() throws InsufficientMappingSpecificationException {
		return ((PropertyMappingX) delegatee.castTo(PropertyMappingX.class)).getSourceProperty();
	}

	public GraphicObjectToObjectRelation getTargetGraphicRelation() throws InsufficientMappingSpecificationException {
		if (delegatee.hasTargetobject_to_objectrelation()) {
			return delegatee.getAllTargetobject_to_objectrelation_as().firstValue();
		} else throw new InsufficientMappingSpecificationException("Missing target graphic relation.");
	}

	public Property getInheritedBy() {
		if (delegatee.hasInvertsourceproperty()) {
			return (Property)delegatee.getAllInheritedby_as().firstValue().castTo(Property.class);
		} else return null;
	}

	public Set<SubMappingRelationX> getSubMappings() {
		
		if (null != subMappings) {
			return subMappings;
		}
		
		Set<SubMappingRelationX> subMappingRelationsX = new HashSet<SubMappingRelationX>();
		if (hasSubmapping()) {
			ClosableIterator<Sub_mappingrelation> subMappingRelations =  delegatee.getAllSub_mapping_as().asClosableIterator();
			while (subMappingRelations.hasNext()) {
				
				Sub_mappingrelation rel = (Sub_mappingrelation) subMappingRelations
						.next();
				
				SubMappingRelationX relX = new SubMappingRelationX(rel);
				
				if (!relX.hasSubMapping() || !relX.hasOnRole()) {
					LOGGER.warning("Ignored incomplete submapping " + relX.toString() + ", since no submapping was found or onRole is not specified.");
					continue;
				}
				
				subMappingRelationsX.add(relX);
			}
			
			subMappings = subMappingRelationsX;
			
			return subMappings;
			
		} else  {
			return null;
		}	
	}

}
