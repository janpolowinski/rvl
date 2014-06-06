/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import java.util.Iterator;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.util.RVLUtils;

/**
 * @author Jan Polowinski
 * 
 */
public abstract class MappingToP2GOTORHandler extends MappingHandlerBase {

	private final static Logger LOGGER = Logger
			.getLogger(MappingToP2GOTORHandler.class.getName());

	protected PropertyToGO2ORMappingX mapping;
	Iterator<Statement> stmtSetIterator;

	public MappingToP2GOTORHandler(ModelSet modelSet,
			RVLInterpreter rvlInterpreter, Model modelAvm) {
		super(modelSet, rvlInterpreter, modelAvm);
	}

	public void handleP2GOTORMapping(PropertyToGO2ORMappingX mapping)
			throws InsufficientMappingSpecificationException {

		this.mapping = mapping;

		stmtSetIterator = RVLUtils.findRelationsOnInstanceOrClassLevel(
				modelSet, OGVICProcess.GRAPH_DATA,
				(PropertyMappingX) mapping.castTo(PropertyMappingX.class),
				true, null, null).iterator();

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

				} catch (Exception e) {
					LOGGER.warning("Problem creating GOs: " + e.getMessage());
				}

				processedGraphicRelations++;

			}

		}

	}

	protected abstract void encodeStatement(Statement statement) throws InsufficientMappingSpecificationException;

}
