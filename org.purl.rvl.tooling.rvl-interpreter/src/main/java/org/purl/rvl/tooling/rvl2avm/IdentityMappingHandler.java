package org.purl.rvl.tooling.rvl2avm;

import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.gen.viso.graphic.GraphicObject;
import org.purl.rvl.java.rvl.IdentityMappingX;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.query.data.DataQuery;

public class IdentityMappingHandler extends MappingHandlerBase {
	
	private final static Logger LOGGER = Logger
			.getLogger(IdentityMappingHandler.class.getName());
	
	protected IdentityMappingX mapping;

	public IdentityMappingHandler(ModelSet modelSet,
			RVLInterpreter rvlInterpreter, Model modelAvm) {
		super(modelSet, rvlInterpreter, modelAvm);
	}

	@Override
	protected void encodeStatement(Statement statement)
			throws InsufficientMappingSpecificationException {
		
		logStatementDetails(LOGGER,statement);

		Resource subject = statement.getSubject();
		Node object = statement.getObject();
		
		// For each statement, create a startNode GO representing the subject
		// (if not exists)
		GraphicObjectX subjectNode = rvlInterpreter
				.createOrGetGraphicObject(subject);
		LOGGER.finest("Created GO for subject: " + subject.toString());
		
		// TODO hack: we are always settings textvalue here
		// (as will be the case in 99%), 
		// while also color values may have been passed!
		
		Node sv = object.asLiteral();
		Node tv = sv;
		GraphicAttribute tga = GraphicAttribute.getInstance(OGVICProcess.getInstance().getModelAVM(), GraphicObject.TEXTVALUE);
		rvlInterpreter.applyGraphicValueToGO(tga, tv, sv, subjectNode);
		
		// remove existing shapes (incl. default shape) - these are now overridden by the text-shape
		subjectNode.removeAllShapenamed();
		
	}

	/**
	 * // TODO implement special treatment of rvl:label here (for now only implemented when used in a sub-mapping!)
	 * 
	 * @param mapping
	 * @throws MappingException
	 */
	public void handleIdentityMapping(IdentityMappingX mapping) throws MappingException {

		this.mapping = mapping;
		
		try {
			stmtSetIterator = DataQuery.findRelationsOnInstanceOrClassLevel(
					modelSet, OGVICProcess.GRAPH_DATA,
					(PropertyMappingX) mapping.castTo(PropertyMappingX.class),
					true, null, null).iterator();
			
		} catch (InsufficientMappingSpecificationException e) {
			throw new MappingException("Problem getting Identity-mapping-statements " +
					"for " + mapping.asURI() + ": " + e.getMessage());
		}


		if (null == stmtSetIterator) {
			LOGGER.severe("Statement iterator was null, no relations could be interpreted for "
					+ mapping.asURI());
		} else if (!stmtSetIterator.hasNext()) {
			LOGGER.severe("Statement iterator was empty, no relations could be interpreted for "
					+ mapping.asURI());
		} else {

			while (stmtSetIterator.hasNext()
					&& processedGraphicRelations < OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING) {

				Statement statement = (Statement) stmtSetIterator.next();

					try {
						encodeStatement(statement);
					} catch (InsufficientMappingSpecificationException e) {
						throw new MappingException("Problem encoding statement " 
							+ statement.toString() + ": " + e.getMessage());
					}

				processedGraphicRelations++;

			}

		}
		
	}


}
