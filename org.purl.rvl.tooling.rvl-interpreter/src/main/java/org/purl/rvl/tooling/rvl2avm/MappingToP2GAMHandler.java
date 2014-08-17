/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.exception.NotImplementedMappingFeatureException;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.query.data.DataQuery;

/**
 * @author Jan Polowinski
 * 
 */
public class MappingToP2GAMHandler extends MappingHandlerBase {

	private final static Logger LOGGER = Logger
			.getLogger(MappingToP2GAMHandler.class.getName());

	protected PropertyToGraphicAttributeMappingX mapping;
	
	public MappingToP2GAMHandler(ModelSet modelSet,
			RVLInterpreter rvlInterpreter, Model modelAvm) {
		super(modelSet, rvlInterpreter, modelAvm);
	}

	public void handleP2GAMMapping(PropertyToGraphicAttributeMappingX mapping) throws MappingException {
		
		this.mapping = mapping;

	    try {

			 // get a statement set 
			Set<Statement> stmtSet = DataQuery.findRelationsOnInstanceOrClassLevel(
					modelSet,
					OGVICProcess.GRAPH_DATA,
					(PropertyMappingX) mapping.castTo(PropertyMappingX.class),
					true,
					null,
					null
					);

			// recalculate the mapping table SV->TV
			// this will be accessed in the encode method while encoding statements
			mapping.calculateValues(stmtSet);	
		    
			stmtSetIterator = stmtSet.iterator(); // TODO why stored as a member?
			
			if (null == stmtSetIterator) {
				LOGGER.severe("Statement iterator was null, no relations could be interpreted for "
						+ mapping.asURI());
			} else if (!stmtSetIterator.hasNext()) {
				LOGGER.severe("Statement iterator was empty, no relations could be interpreted for "
						+ mapping.asURI());
			} else {
		    
			    // for all statements check whether there is a tv for the sv
			    for (Iterator<Statement> stmtSetIt = stmtSetIterator; stmtSetIt.hasNext() 
			    		&& processedGraphicRelations < OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING;) {
			    	
			    	Statement statement = (Statement) stmtSetIt.next();
			    	
			    	encodeStatement(statement);
		    	
			    	processedGraphicRelations++;
			    	
				}
		    
			}
			
		} catch (InsufficientMappingSpecificationException e) {
			LOGGER.warning("No resources will be affected by mapping " + mapping.asURI() 
					+ " (" + e.getMessage() + ")" );
		} 

	}

	@Override
	void encodeStatement(Statement statement) throws InsufficientMappingSpecificationException,
			NotImplementedMappingFeatureException, MappingException {
		
		// create a GO for each subject of the statement
	    GraphicObjectX go = rvlInterpreter.createOrGetGraphicObject(statement.getSubject());
	    
		GraphicAttribute tga = mapping.getTargetAttribute();

    	Node sv = statement.getObject();

		LOGGER.finest("trying to find and apply value mapping for sv " + sv.toString());
		
		// get the mapping table SV->TV (the calculation must have been triggered before!)
		Map<Node, Node> svUriTVuriMap = mapping.getCalculatedValues();	
		
		// get the target value for the sv
    	Node tv = svUriTVuriMap.get(sv);
    	
    	// if we found a tv for the sv
    	if (null != tv) {
    		
	    	rvlInterpreter.applyGraphicValueToGO(tga, tv, sv, go);	
	    	
	    	rvlInterpreter.applyInheritanceOfTargetValue(mapping, statement.getSubject(), tv);
    	}

	}

}
