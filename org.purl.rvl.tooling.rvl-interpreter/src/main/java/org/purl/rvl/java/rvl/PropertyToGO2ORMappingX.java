package org.purl.rvl.java.rvl;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.gen.rvl.GraphicObjectToObjectRelation;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;
import org.purl.rvl.java.gen.rvl.Sub_mappingrelation;

/**
 * @author Jan Polowinski
 *
 */
public class PropertyToGO2ORMappingX extends
		Property_to_Graphic_Object_to_Object_RelationMapping implements MappingIF {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final String NL =  System.getProperty("line.separator");
	
	private final static Logger LOGGER = Logger.getLogger(PropertyToGO2ORMappingX.class.getName());

	private Set<SubMappingRelationX> subMappings; 
	
	public PropertyToGO2ORMappingX(Model model, URI classURI,
			Resource instanceIdentifier, boolean write) {
		super(model, classURI, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyToGO2ORMappingX(Model model, Resource instanceIdentifier,
			boolean write) {
		super(model, instanceIdentifier, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyToGO2ORMappingX(Model model, String uriString, boolean write)
			throws ModelRuntimeException {
		super(model, uriString, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyToGO2ORMappingX(Model model, BlankNode bnode, boolean write) {
		super(model, bnode, write);
		// TODO Auto-generated constructor stub
	}

	public PropertyToGO2ORMappingX(Model model, boolean write) {
		super(model, write);
		// TODO Auto-generated constructor stub
	}
	
	public String toStringDetailed(){
		
		String s = "";
		
		// try to get the string description from the (manual) PropertyMappingX class, which is not in the super-class hierarchy
		PropertyMappingX pm = (PropertyMappingX) this.castTo(PropertyMappingX.class);
		s += pm.toStringDetailed();
		
		// targetAttribute is specific to P2GAM
		GraphicObjectToObjectRelation tgo2or = this.getAllTargetobject_to_objectrelation_as().firstValue();
		String tgrString = tgo2or.getAllLabel_as().count()>0 ? tgo2or.getAllLabel_as().firstValue() : tgo2or.toString();
		s += "     target GOTOR: " + tgrString + NL ;
		
		if (pm.hasSub_mapping()) {
			// list sub-mappings
			// TODO only first submapping listed here
			SubMappingRelationX smr = new SubMappingRelationX(this.getAllSub_mapping_as().firstValue());
			s += "     Submapping relation (only first shown): " + smr + NL;
			s += smr.toStringDetailed(true);
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
	
	public Property getSourceProperty() throws InsufficientMappingSpecificationException {
		return ((PropertyMappingX) this.castTo(PropertyMappingX.class)).getSourceProperty();
	}

	public GraphicObjectToObjectRelation getTargetGraphicRelation() throws InsufficientMappingSpecificationException {
		if (this.hasTargetobject_to_objectrelation()) {
			return this.getAllTargetobject_to_objectrelation_as().firstValue();
		} else throw new InsufficientMappingSpecificationException("Missing target graphic relation.");
	}

	public Property getInheritedBy() {
		if (this.hasInvertsourceproperty()) {
			return (Property)getAllInheritedby_as().firstValue().castTo(Property.class);
		} else return null;
	}

	public Set<SubMappingRelationX> getSubMappings() {
		
		if (null != subMappings) {
			return subMappings;
		}
		
		Set<SubMappingRelationX> subMappingRelationsX = new HashSet<SubMappingRelationX>();
		if (this.hasSub_mapping()) {
			ClosableIterator<Sub_mappingrelation> subMappingRelations =  getAllSub_mapping_as().asClosableIterator();
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
