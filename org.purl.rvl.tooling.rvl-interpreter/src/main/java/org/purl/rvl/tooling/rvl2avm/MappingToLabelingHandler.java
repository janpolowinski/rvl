/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.gen.viso.graphic.Labeling;
import org.purl.rvl.java.gen.viso.graphic.Superimposition;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.java.viso.graphic.ShapeX;
import org.purl.rvl.tooling.util.AVMUtils;

/**
 * @author Jan Polowinski
 * 
 */
public class MappingToLabelingHandler extends MappingToP2GOTORHandler {

	public MappingToLabelingHandler(ModelSet modelSet,
			RVLInterpreter rvlInterpreter, Model modelAVM) {
		super(modelSet, rvlInterpreter, modelAVM);
	}

	private final static Logger LOGGER = Logger
			.getLogger(MappingToLabelingHandler.class.getName());

	@Override
	public void encodeStatement(Statement statement)
			throws InsufficientMappingSpecificationException {

		Resource subject = statement.getSubject();
		Resource object = statement.getObject().asResource();

		LOGGER.finest("Subject label "
				+ AVMUtils.getGoodLabel(subject, modelAVM));
		LOGGER.finest("Object label " + AVMUtils.getGoodLabel(object, modelAVM));

		LOGGER.fine("Statement to be mapped : " + statement);

		// For each statement, create a startNode GO representing the subject
		// (if not exists)
		GraphicObjectX subjectNode = rvlInterpreter
				.createOrGetGraphicObject(subject);
		LOGGER.finest("Created GO for subject: " + subject.toString());

		// For each statement, create an endNode GO representing the object (if
		// not exists)
		// create an additional object here, don't reuse existing ones!
		GraphicObjectX label = new GraphicObjectX(modelAVM, false); 
		LOGGER.finest("Created new Label-GO for object: " + object.toString());

		Labeling rel = new Labeling(modelAVM,
				"http://purl.org/rvl/example-avm/GR_"
						+ rvlInterpreter.createNewInternalID(), true);
		rel.setLabel(AVMUtils.getGoodLabel(mapping.getTargetGraphicRelation(),
				modelAVM));

		subjectNode.setLabeledwith(rel);
		rel.setLabelinglabel(label);
		rel.setLabelingattachedBy(Superimposition.RDFS_CLASS); // passing a node
																// here
		rel.setLabelingbase(subjectNode);

		// set default shape of icon labels
		label.setShapenamed(new ShapeX(modelAVM,
				"http://purl.org/viso/shape/commons/Square", false));

		// submappings
		if (mapping.hasSub_mapping()) {
			rvlInterpreter.applySubmappings(mapping, statement, rel);
		}

	}

}
