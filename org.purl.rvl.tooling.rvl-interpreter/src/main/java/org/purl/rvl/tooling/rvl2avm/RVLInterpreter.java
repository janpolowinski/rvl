package org.purl.rvl.tooling.rvl2avm;

import java.util.Set;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.gen.viso.graphic.Object_to_ObjectRelation;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;

/**
 * @author Jan Polowinski
 * 
 */
public interface RVLInterpreter {

	public abstract void init(Model modelAVM, ModelSet modelSet);

	/**
	 * Interpret all supported RVL mappings
	 */
	public abstract void interpretMappings();

	public abstract GraphicObjectX createOrGetGraphicObject(org.ontoware.rdf2go.model.node.Resource resource);

	/**
	 * The set of "main" graphic objects, e.g. excluding those only created to represent labels, and not directly an rdfs:Resource. Connector objects
	 * are included, because the represent properties/predicates.
	 * 
	 * Unlike the Resource-GraphicObject-map, this set may store multiple GOs for the same resource (as it may be desired in some cases. The map is
	 * not sufficient, since currently GraphicObjects are NOT reused in the context of labeling, for example, though this could be changed in future
	 * versions). Also GraphicObjects used as connectors are not reused.
	 * 
	 * @param newGraphicObject
	 * @return
	 */
	abstract void addToMainGraphicObjectSet(GraphicObjectX newGraphicObject);

	/**
	 * @return - the set of all "main" GraphicObjectX, i.e. excluding those only introduced for labels, but including connectors.
	 */
	abstract Set<GraphicObjectX> getMainGraphicObjectSet();

	public abstract void applySubmappings(PropertyToGO2ORMappingX p2go2orm, Statement mainStatement, Object_to_ObjectRelation graphicRelation);

	public abstract void applyGraphicValueToGOsRepresentingNodesRelatedVia(GraphicAttribute tga, Node tv, Resource mappedNode, Property inheritedBy);

	public abstract void applyGraphicValueToGO(GraphicAttribute tga, Node tv, Node sv, GraphicObjectX go);
	
	/**
	 * Similar to applyGraphicValueToGO(...), but sets parameters of graphic relations rather than attributes of graphic objects
	 * @param parameterProperty
	 * @param parameterValue
	 * @param sourceValue
	 * @param graphicRelation
	 */
	public abstract void applyParameterToGraphicRelation(Resource parameterProperty, Node parameterValue, Node sourceValue, Object_to_ObjectRelation graphicRelation);

	public abstract String createNewInternalID();

	public abstract void applyInheritanceOfTargetValue(PropertyToGraphicAttributeMappingX p2gam, Resource baseResource, Node tv)
			throws InsufficientMappingSpecificationException;


}