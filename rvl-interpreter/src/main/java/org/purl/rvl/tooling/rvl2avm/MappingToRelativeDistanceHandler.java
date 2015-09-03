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
import org.purl.rvl.exception.NotImplementedMappingFeatureException;
import org.purl.rvl.exception.SubmappingException;
import org.purl.rvl.java.gen.viso.graphic.Object_to_ObjectRelation;
import org.purl.rvl.java.gen.viso.graphic.RelativeDistance;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.commons.utils.ModelUtils;

/**
 * @author Jan Polowinski
 * 
 */
public class MappingToRelativeDistanceHandler extends MappingToP2GOTORHandler {

	public MappingToRelativeDistanceHandler(ModelSet modelSet, RVLInterpreter rvlInterpreter, Model modelAVM) {
		super(modelSet, rvlInterpreter, modelAVM);
	}

	private final static Logger LOGGER = Logger.getLogger(MappingToRelativeDistanceHandler.class.getName());

	@Override
	public void encodeStatement(Statement statement) throws InsufficientMappingSpecificationException,
		NotImplementedMappingFeatureException, SubmappingException {
		
		try {
			statement.getObject().asResource();
		} catch (ClassCastException e) {
			throw new NotImplementedMappingFeatureException("Can only handle relative distance relations where all " +
					"objects are resources, but " + statement.getObject() + " is probably a Literal.");
		}
		
		logStatementDetails(LOGGER,statement);

		Resource subject = statement.getSubject();
		Resource object = statement.getObject().asResource();
		
		// For each statement, create a "graphic:relatively_distant_object" GO representing the subject
		// (if not exists)
		GraphicObjectX subjectNode = rvlInterpreter.createOrGetGraphicObject(subject);
		LOGGER.finest("Created GO for subject: " + subject);

		// For each statement, create an "graphic:relatively_distant_object" GO representing the object 
		// (if not exists)
		GraphicObjectX objectNode = rvlInterpreter.createOrGetGraphicObject(object);
		LOGGER.finest("Created GO for object: " + object);

		//URI predicateURI = statement.getPredicate();

		Object_to_ObjectRelation rel = null;

		// create the relative distance relation
		RelativeDistance rdRel = new RelativeDistance(modelAVM, "http://purl.org/rvl/example-avm/RelativeDistanceRel_"
				+ rvlInterpreter.createNewInternalID(), true);
		rdRel.setLabel(ModelUtils.getGoodNodeLabel(mapping.getTargetGraphicRelation(), modelAVM));

		// configure the relation
		rdRel.addRelativelydistantobject(subjectNode);
		rdRel.addRelativelydistantobject(objectNode);
		subjectNode.addRelativedistanceto(rdRel);
		objectNode.addRelativedistanceto(rdRel);

		// set default shape of undirected connectors
//		connector.setShapenamed(new ShapeX(modelAVM, "http://purl.org/viso/shape/commons/Line", false));

		// set the default here // TODO: should be done in VISO
		rdRel.setRelativeDistancevalue(new Float(1));
		rel = rdRel;


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
