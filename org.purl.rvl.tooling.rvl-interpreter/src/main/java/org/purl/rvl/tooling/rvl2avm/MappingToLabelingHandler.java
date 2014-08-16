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
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.gen.viso.graphic.Labeling;
import org.purl.rvl.java.gen.viso.graphic.Superimposition;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.java.viso.graphic.ShapeX;
import org.purl.rvl.tooling.util.AVMUtils;

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
	void encodeStatement(Statement statement) throws InsufficientMappingSpecificationException {

		Resource subject = statement.getSubject();
		
		// For each statement, create a startNode GO representing the subject (if not exists)
		GraphicObjectX subjectNode = rvlInterpreter.createOrGetGraphicObject(subject);
		LOGGER.finest("Created GO for subject: " + subject.toString());
		
		encodeStatement(statement, this.mapping, subjectNode);

	}
	
	/**
	 * Temp. solution - pass mapping to handler in public encodeStatement method. Check if this is necessary.
	 * 
	 * @param statement
	 * @param mappingAttribute
	 * @throws InsufficientMappingSpecificationException
	 */
	public void encodeStatement(Statement statement, PropertyToGO2ORMappingX mappingAttribute, GraphicObjectX subjectNode) throws InsufficientMappingSpecificationException {

		logStatementDetails(LOGGER, statement);

		Node object = statement.getObject();


		// For each statement, create an endNode GO representing the object (if not exists)
		// create an additional object here, don't reuse existing ones!
		GraphicObjectX label = new GraphicObjectX(modelAVM, "http://purl.org/rvl/example-avm/GO_Label_" 
				+ rvlInterpreter.createNewInternalID(), false);
		LOGGER.finest("Created new Label-GO for (Label) object: " + object.toString());

		Labeling rel = new Labeling(modelAVM, "http://purl.org/rvl/example-avm/LabelingRelation_"
				+ rvlInterpreter.createNewInternalID(), true);
		rel.setLabel(AVMUtils.getGoodNodeLabel(mappingAttribute.getTargetGraphicRelation(), modelAVM));

		subjectNode.addLabeledwith(rel);
		rel.setLabelinglabel(label);
		rel.setLabelingattachedBy(Superimposition.RDFS_CLASS); // passing a node here
		rel.setLabelingbase(subjectNode);

		// set default shape of icon labels
		label.setShapenamed(new ShapeX(modelAVM, "http://purl.org/viso/shape/commons/Square", false));

		// submappings
		if (mappingAttribute.hasSub_mapping()) {
			rvlInterpreter.applySubmappings(mappingAttribute, statement, rel);
		}

	}

}
