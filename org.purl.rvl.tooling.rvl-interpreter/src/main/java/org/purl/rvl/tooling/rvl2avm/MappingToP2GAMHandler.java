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
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.exception.NotImplementedMappingFeatureException;
import org.purl.rvl.java.RDF;
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
		
		// validity checking
		
		if (!mapping.hasValuemapping()) {
			throw new InsufficientMappingSpecificationException(
					"P2GA-mappings with no value mappings at all are not supported.");
		}
		
		GraphicAttribute tga = mapping.getTargetAttribute();
		if (null == tga) {
			throw new InsufficientMappingSpecificationException("No target graphic attribute set.");
		}

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

			// (re)calculate the (implicitly) mapped values
			// this will be accessed in the encode method while encoding statements
			// TODO is this done too often now?
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
		
		Resource subject = statement.getSubject();
		Node sourceValue = statement.getObject();
		
		// create a GO for each subject of the statement
	    GraphicObjectX go = rvlInterpreter.createOrGetGraphicObject(statement.getSubject());
	    
	    LOGGER.finest("Created GO for subject: " + subject.toString());

	    encodeStatement(statement, mapping, go, sourceValue);
	}
	


	public void encodeStatement(Statement statement, PropertyToGraphicAttributeMappingX mapping,
			GraphicObjectX go, Node sourceValue) throws InsufficientMappingSpecificationException {
		
		this.mapping = mapping;
		
		GraphicAttribute tga = mapping.getTargetAttribute();
		Property sp = mapping.getSourceProperty();


		LOGGER.finest("trying to find and apply value mapping for sv " + sourceValue.toString());
		
		// get the mapping table SV->TV (the calculation of mapped values from data dependent (!) implicit
		// value mappings must have been triggered before!)
		Map<Node, Node> svUriTVuriMap = mapping.getMappedValues();	
		
		// get the target value for the sv
    	Node tv;
    	
    	if (sp.asURI().equals(RDF.ID)) { // special treatment of rdf:ID TODO: still necessary?

			tv = svUriTVuriMap.get(sourceValue);

		} else { // other source properties than rdf:ID ...
			sourceValue = statement.getObject();
			tv = svUriTVuriMap.get(sourceValue);
		}
    	
    	
    	// if we found a tv for the sv
    	if (null != tv) {
    		
	    	rvlInterpreter.applyGraphicValueToGO(tga, tv, sourceValue, go);	
	    	
	    	rvlInterpreter.applyInheritanceOfTargetValue(mapping, statement.getSubject(), tv);
    	}

		
		/*
		// if we found a tv for the sv
					if (null != tv && null != sv) {
			
						applyGraphicValueToGO(tga, tv, sv, goToApplySubmapping);
						// TODO enable again : applyInheritanceOfTargetValue(mapping, mainStatement.getSubject(), tv);
			
					} else {
						LOGGER.finest("Graphic attribute , source or target value was null, couldn't apply graphic value " + tv
								+ " to the sv " + sv);
					}
					*/
		
	}

}
