package org.purl.rvl.tooling.rvl2avm;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
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

	public abstract void applySubmappings(PropertyToGO2ORMappingX p2go2orm, Statement mainStatement,
			Resource dlRel);

	public abstract void applyGraphicValueToGOsRepresentingNodesRelatedVia(
			GraphicAttribute tga, Node tv, Resource mappedNode, Property inheritedBy);

	public abstract void applyGraphicValueToGO(GraphicAttribute tga, Node tv,
			Node sv, GraphicObjectX go);

	public abstract String createNewInternalID();

}