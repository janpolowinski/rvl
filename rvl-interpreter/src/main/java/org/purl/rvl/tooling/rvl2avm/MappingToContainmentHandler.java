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
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.exception.NotImplementedMappingFeatureException;
import org.purl.rvl.exception.SubmappingException;
import org.purl.rvl.java.gen.viso.graphic.Containment;
import org.purl.rvl.java.gen.viso.graphic.Object_to_ObjectRelation;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.commons.utils.ModelUtils;

/**
 * @author Jan Polowinski TODO cloned from linking, much duplicated code
 */
public class MappingToContainmentHandler extends MappingToP2GOTORHandler {

	public MappingToContainmentHandler(ModelSet modelSet,
			RVLInterpreter rvlInterpreter, Model modelAVM) {
		super(modelSet, rvlInterpreter, modelAVM);
	}

	private final static Logger LOGGER = Logger
			.getLogger(MappingToContainmentHandler.class.getName());

	public void encodeStatement(Statement statement) throws InsufficientMappingSpecificationException,
		NotImplementedMappingFeatureException, SubmappingException {
		
		try {
			statement.getObject().asResource();
		} catch (ClassCastException e) {
			throw new NotImplementedMappingFeatureException("Can only handle linking relations where all " +
					"objects are resources, but " + statement.getObject() + " is probably a Literal.");
		}
		
		logStatementDetails(LOGGER,statement);

		Resource subject = statement.getSubject();
		Resource object = statement.getObject().asResource();

		// For each statement, create a container GO representing the subject
		// (if not exists)
		GraphicObjectX subjectContainer = rvlInterpreter
				.createOrGetGraphicObject(subject);

		// For each statement, create a containee GO representing the object (if
		// not exists)
		GraphicObjectX objectContainee = rvlInterpreter
				.createOrGetGraphicObject(object);

		LOGGER.finest("Created GO for subject: " + subject.toString());
		LOGGER.finest("Created GO for object: " + object.toString());

		// generic graphic relation needed for submappings
		// (could also be some super class of containment ,...)
		Object_to_ObjectRelation rel = null;

		// create the containment relation
		// Containment dlRel = new Containment(modelAVM, true);
		Containment containmentRel = new Containment(modelAVM,
				"http://purl.org/rvl/example-avm/ContainmentRel_"
						+ rvlInterpreter.createNewInternalID(), true);
//		containmentRel.setLabel(ModelUtils.getGoodNodeLabel(mapping.getTargetGraphicRelation(), modelAVM));

		// configure the relation
		if (!mapping.isInvertSourceProperty()) {
			containmentRel.setContainmentcontainer(subjectContainer);
			containmentRel.setContainmentcontainee(objectContainee);
			subjectContainer.addContains(containmentRel);
			objectContainee.addContainedby(containmentRel);
		} else {
			containmentRel.setContainmentcontainee(subjectContainer);
			containmentRel.setContainmentcontainer(objectContainee);
			subjectContainer.addContainedby(containmentRel);
			objectContainee.addContains(containmentRel);
		}

		rel = containmentRel;

		// submappings
		if (mapping.hasSubMapping()) {
			
			// Containment etc need to be subclasses of (n-ary)
			// GraphicRelation
			rvlInterpreter.applySubmappings(mapping, statement, rel);
		}

	}

}
