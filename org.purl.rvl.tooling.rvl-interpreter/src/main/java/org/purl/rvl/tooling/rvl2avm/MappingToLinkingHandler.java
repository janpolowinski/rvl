/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.NotImplementedMappingFeatureException;
import org.purl.rvl.exception.SubmappingException;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.gen.viso.graphic.Object_to_ObjectRelation;
import org.purl.rvl.java.gen.viso.graphic.UndirectedLinking;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.java.viso.graphic.ShapeX;
import org.purl.rvl.tooling.util.AVMUtils;

/**
 * @author Jan Polowinski
 * 
 */
public class MappingToLinkingHandler extends MappingToP2GOTORHandler {

	public MappingToLinkingHandler(ModelSet modelSet, RVLInterpreter rvlInterpreter, Model modelAVM) {
		super(modelSet, rvlInterpreter, modelAVM);
	}

	private final static Logger LOGGER = Logger.getLogger(MappingToLinkingHandler.class.getName());

	@Override
	public void encodeStatement(Statement statement) throws InsufficientMappingSpecificationException, NotImplementedMappingFeatureException, SubmappingException {
		
		try {
			statement.getObject().asResource();
		} catch (ClassCastException e) {
			throw new NotImplementedMappingFeatureException("Can only handle linking relations where all " +
					"objects are resources, but " + statement.getObject() + " is probably a Literal.");
		}
		
		logStatementDetails(LOGGER,statement);

		Resource subject = statement.getSubject();
		Resource object = statement.getObject().asResource();
		
		// For each statement, create a startNode GO representing the subject
		// (if not exists)
		GraphicObjectX subjectNode = rvlInterpreter.createOrGetGraphicObject(subject);
		LOGGER.finest("Created GO for subject: " + subject);

		// For each statement, create an endNode GO representing the object (if
		// not exists)
		// Node object = statement.getObject();

		GraphicObjectX objectNode = rvlInterpreter.createOrGetGraphicObject(object);
		LOGGER.finest("Created GO for object: " + object);

		// create a connector object
		GraphicObjectX connector = new GraphicObjectX(modelAVM, "http://purl.org/rvl/example-avm/GO_Connector_"
				+ rvlInterpreter.createNewInternalID(), true);

		// set represented resource and store as a main GraphicObject (will enable automatic labeling, for example)
		URI predicateURI = statement.getPredicate();
		connector.setRepresents(predicateURI);
		rvlInterpreter.addToMainGraphicObjectSet(connector);

		// label the GraphicObject itself (only used for bootstrapping purposes,
		// when looking at the generated Abstract Visual Model)
		connector.setLabel("Connector ( " + AVMUtils.getGoodNodeLabel(predicateURI, modelAVM) + " or sub-property)");

		// generic graphic relation needed for submappings
		// (could also be some super class of directed linking, undirected
		// linking, containment ,...)
		Object_to_ObjectRelation rel = null;

		// directed linking
		if (mapping.getTargetGraphicRelation().equals(DirectedLinking.RDFS_CLASS)) {

			// create the directed linking relation
			// DirectedLinking dlRel = new DirectedLinking(modelAVM, true);
			DirectedLinking dlRel = new DirectedLinking(modelAVM, "http://purl.org/rvl/example-avm/GR_"
					+ rvlInterpreter.createNewInternalID(), true);
			dlRel.setLabel(AVMUtils.getGoodNodeLabel(mapping.getTargetGraphicRelation(), modelAVM));

			// configure the relation
			if (mapping.isInvertSourceProperty()) {
				dlRel.setEndnode(subjectNode);
				dlRel.setStartnode(objectNode);
				subjectNode.addLinkedfrom(dlRel);
				objectNode.addLinkedto(dlRel);
			} else {
				dlRel.setStartnode(subjectNode);
				dlRel.setEndnode(objectNode);
				subjectNode.addLinkedto(dlRel);
				objectNode.addLinkedfrom(dlRel);
			}

			// set default shape of directed connectors
			connector.setShapenamed(new ShapeX(modelAVM, "http://purl.org/viso/shape/commons/UMLAssociation", false));

			dlRel.setLinkingconnector(connector);
			rel = dlRel;

		} else { // undirected linking

			// create the undirected linking relation
			// UndirectedLinking udlRel = new UndirectedLinking(modelAVM, true);
			UndirectedLinking udlRel = new UndirectedLinking(modelAVM, "http://purl.org/rvl/example-avm/GR_"
					+ rvlInterpreter.createNewInternalID(), true);
			udlRel.setLabel(AVMUtils.getGoodNodeLabel(mapping.getTargetGraphicRelation(), modelAVM));

			// configure the relation
			udlRel.addLinkingnode(subjectNode);
			udlRel.addLinkingnode(objectNode);
			subjectNode.addLinkedwith(udlRel);
			objectNode.addLinkedwith(udlRel);

			// set default shape of undirected connectors
			connector.setShapenamed(new ShapeX(modelAVM, "http://purl.org/viso/shape/commons/Line", false));

			udlRel.setLinkingconnector(connector);
			rel = udlRel;
		}

		// submappings
		if (mapping.hasSubMapping()) {

			if (null != rel) {
				rvlInterpreter.applySubmappings(mapping, statement, rel);
			} else {
				LOGGER.warning("Submapping existed, but could not be applied, since no parent graphic relation was provided.");
			}
		}

	}

}
