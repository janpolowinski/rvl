/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.SubmappingException;
import org.purl.rvl.java.gen.viso.graphic.Labeling;
import org.purl.rvl.java.gen.viso.graphic.Superimposition;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.java.viso.graphic.ShapeX;
import org.purl.rvl.tooling.commons.utils.ModelUtils;

/**
 * @author Jan Polowinski
 * 
 */
public class MappingToLabelingHandler extends MappingToP2GOTORHandler {

	public MappingToLabelingHandler(ModelSet modelSet, RVLInterpreter rvlInterpreter, Model modelAVM) {
		super(modelSet, rvlInterpreter, modelAVM);
	}

	private final static Logger LOGGER = Logger.getLogger(MappingToLabelingHandler.class.getName());

	@Override
	void encodeStatement(Statement statement) throws InsufficientMappingSpecificationException, SubmappingException {

		Resource subject = statement.getSubject();
		
		// For each statement, create a startNode GO representing the subject (if not exists)
		GraphicObjectX subjectNode = rvlInterpreter.createOrGetGraphicObject(subject);
		LOGGER.finest("Created GO for subject: " + subject.toString());
		
		encodeStatement(statement, this.mapping, subjectNode);

	}
	
	/**
	 * Encode with context.
	 * 
	 * @param statement
	 * @param mapping
	 * @throws InsufficientMappingSpecificationException
	 * @throws SubmappingException 
	 */
	public void encodeStatement(Statement statement, PropertyToGO2ORMappingX mapping, GraphicObjectX graphicObjectToApplyMapping) 
			throws InsufficientMappingSpecificationException, SubmappingException {
		
		// TODO triple part not passed???? 
		// sv = triplePart; // TODO when sp rdf:ID: ID actually only fine when URIs!

		logStatementDetails(LOGGER, statement);

		Node object = statement.getObject();

		// 1. create the label - for each statement, create an endNode GO representing the object (if not exists)
		// create an additional object here, don't reuse existing ones!
		GraphicObjectX label = new GraphicObjectX(modelAVM, "http://purl.org/rvl/example-avm/GO_Label_" 
				+ rvlInterpreter.createNewInternalID(), true);
		LOGGER.finest("Created new Label-GO for (label) object: " + object.toString());
		// TODO when sp rdf:ID: not created for object in all cases?!
		
		// set represented resource and store as a main GraphicObject (will enable automatic labeling, for example)
		URI predicateURI = statement.getPredicate();
		// set represented resource - we may want this > otherwise warnings, when graphic objects without 
		// represented resource exist:
		label.setRepresentedResource(predicateURI);
		//(we probably dont't want this:
		//rvlInterpreter.addToMainGraphicObjectSet(label);

		Labeling rel = new Labeling(modelAVM, "http://purl.org/rvl/example-avm/LabelingRelation_"
				+ rvlInterpreter.createNewInternalID(), true);
		rel.setLabel(ModelUtils.getGoodNodeLabel(mapping.getTargetGraphicRelation(), modelAVM));

		graphicObjectToApplyMapping.addLabeledwith(rel);
		rel.setLabelinglabel(label);
		rel.setLabelingattachedBy(Superimposition.RDFS_CLASS); // passing a node here
		rel.setLabelingbase(graphicObjectToApplyMapping);

		// set default shape of icon labels
		label.setShapenamed(new ShapeX(modelAVM, "http://purl.org/viso/shape/commons/Square", false));

		// 2. call the submapping method with the same unchanged statement to set label text_value or
		// icon_shape etc ...
		if (mapping.hasSubMapping()) {
			rvlInterpreter.applySubmappings(mapping, statement, rel);
		}

	}

}
