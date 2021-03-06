/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.exception.NotImplementedMappingFeatureException;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.tooling.commons.Graph;
import org.purl.rvl.tooling.commons.Settings;
import org.purl.rvl.tooling.query.data.DataQuery;

/**
 * @author Jan Polowinski
 * 
 */
public abstract class MappingToP2GOTORHandler extends MappingHandlerBase {

	private final static Logger LOGGER = Logger
			.getLogger(MappingToP2GOTORHandler.class.getName());

	protected PropertyToGO2ORMappingX mapping;
	
	public MappingToP2GOTORHandler(ModelSet modelSet,
			RVLInterpreter rvlInterpreter, Model modelAvm) {
		super(modelSet, rvlInterpreter, modelAvm);
	}

	public void handleP2GOTORMapping(PropertyToGO2ORMappingX mapping) throws MappingException {
		
		this.mapping = mapping;
		
		try {
			stmtSetIterator = DataQuery.findRelationsOnInstanceOrClassLevel(
					modelSet, Graph.GRAPH_DATA,
					mapping,
					true, null, null).iterator();
			
		} catch (InsufficientMappingSpecificationException e) {
			throw new MappingException(mapping, "Problem getting mapping-statements " +
					"for P2GOTOR mapping: " + e.getMessage());
		}

		if (null == stmtSetIterator) {
			throw new MappingException(mapping, "Statement iterator was null. ");
		} else if (!stmtSetIterator.hasNext()) {
			LOGGER.warning("Statement iterator was empty for " + mapping);
		} else {

			while (stmtSetIterator.hasNext()
					&& processedGraphicRelations < Settings.MAX_GRAPHIC_RELATIONS_PER_MAPPING) {

				Statement statement = (Statement) stmtSetIterator.next();

					try {
						encodeStatement(statement);
					} catch (InsufficientMappingSpecificationException e) {
						throw new MappingException("Problem encoding statement " 
							+ statement.toString() + ": " + e.getMessage());
					} catch (NotImplementedMappingFeatureException e) {
						LOGGER.warning("Problem encoding statement, skipped " 
								+ statement.toString() + ": " + e.getMessage());
					}

				processedGraphicRelations++;

			}

		}

	}

}
