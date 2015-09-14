/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.exception.NotImplementedMappingFeatureException;
import org.purl.rvl.exception.SubmappingException;
import org.purl.rvl.exception.UnsupportedMappingParameterValueException;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.commons.Graph;
import org.purl.rvl.tooling.commons.Settings;
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

	/**
	 * Apply mapping on the whole data set without considering any context.
	 * 
	 * @param mapping
	 * @throws MappingException
	 */
	public void handleP2GAMMapping(PropertyToGraphicAttributeMappingX mapping) throws MappingException {
		
		handleP2GAMMapping(mapping, null, null);
		
	}
	
/**
 * Apply mapping starting using the resource workResource as a subject 
 * for filtering statements and applying the result to the graphic object go.
 * 
 * @param mapping
 * @param graphicObjectToApplyMapping - the graphic object to use as a base for applying the additional visual encoding
 * @param workNode - the node to work with (use as source value, apply base further mappings on ...) 
 * @throws MappingException
 */
public void handleP2GAMMapping(PropertyToGraphicAttributeMappingX mapping, 
		GraphicObjectX graphicObjectToApplyMapping, Resource workResource) throws MappingException {
		
		this.mapping = mapping;

	    try {
	    	
	    	// validity checking
			
			if (!mapping.hasValuemapping()) {
				throw new InsufficientMappingSpecificationException(
						"P2GA-mappings with no value mappings at all are not supported.");
			}

			// get a statement set 
			Set<Statement> stmtSet = DataQuery.findRelationsOnInstanceOrClassLevel(
					modelSet,
					Graph.GRAPH_DATA,
					mapping,
					true, // only most specific relations (e.g. only A citesCritical B, not A cites B if both exist)
					workResource,
					null
					);

			// (re)calculate the (implicitly) mapped values
			// this will be accessed in the encode method while encoding statements
			// TODO is this done too often now?
			mapping.calculateValues(stmtSet);
		    
			stmtSetIterator = stmtSet.iterator(); // TODO why stored as a member?
			
			if (null == stmtSetIterator) {
				LOGGER.severe("Statement iterator was null, no relations could be interpreted for "
						+ mapping);
			} else if (!stmtSetIterator.hasNext()) {
				LOGGER.severe("Statement iterator was empty, no relations could be interpreted for "
						+ mapping);
			} else {
		    
			    while (stmtSetIterator.hasNext() 
			    		&& processedGraphicRelations < Settings.MAX_GRAPHIC_RELATIONS_PER_MAPPING) {
			    	
			    	Statement statement = stmtSetIterator.next();
			    	Node valueNode = statement.getObject();
			    	
			    	if (null == graphicObjectToApplyMapping) {
			    		encodeStatement(statement);
			    	} else {
			    		encodeStatement(statement, mapping, graphicObjectToApplyMapping, valueNode);
			    	}
		    	
			    	processedGraphicRelations++;
			    	
				}
		    
			}
			
//		} catch (InsufficientMappingSpecificationException e) {
//			LOGGER.warning("No resources will be affected by mapping " + mapping 
//					+ " (" + e.getMessage() + ")" );
		} catch (ClassCastException e) {
			throw new UnsupportedMappingParameterValueException(mapping, "Maybe the submapping was " +
					"accidentally applied to a literal? :" + e.getMessage(), e);
		}

	}

	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.rvl2avm.MappingHandlerBase#encodeStatement(org.ontoware.rdf2go.model.Statement)
	 */
	@Override
	void encodeStatement(Statement statement) throws InsufficientMappingSpecificationException,
			NotImplementedMappingFeatureException, MappingException {
		
		Resource subject = statement.getSubject();
		Node object = statement.getObject();
		
		// create a GO for the subject of the statement
	    GraphicObjectX go = rvlInterpreter.createOrGetGraphicObject(subject);
	    
	    LOGGER.finest("Created GO for subject: " + subject);

	    encodeStatement(statement, mapping, go, object);
	}
	


	/**
	 * @param statement - the statement to visually encode
	 * @param mapping - a mapping describing the visual encoding to a graphic attribute
	 * @param go - the graphic object whose graphic attributes have to be changed
	 * @param sourceValueWorkNode - the node to work with (use as source value, apply base further mappings on ...)
	 * It is one of the nodes used in the statement.
	 * @throws MappingException 
	 */
	public void encodeStatement(Statement statement, PropertyToGraphicAttributeMappingX mapping,
			GraphicObjectX graphicObjectToApplyMapping, Node sourceValueWorkNode) throws MappingException {
		
		// checks ...
		if (null == sourceValueWorkNode) {
			throw new MappingException("Work node may not be null. (Statement: " + statement + ")");
		}
		if (null == graphicObjectToApplyMapping) {
			throw new MappingException("Graphic object to apply the mapping may not be null. (Statement: " + statement + ", work node: " + sourceValueWorkNode + ")");
		}
		
		this.mapping = mapping;
		
		Property tga = mapping.getTargetGraphicRelation();
		//Property sp = mapping.getSourceProperty();
		
		// get the mapping table SV->TV (the calculation of mapped values from data dependent (!) implicit
		// value mappings must have been triggered before!)
		
		// mapping table SV->TV

		final Model modelData = modelSet.getModel(Graph.GRAPH_DATA);
		
		// TODO: automatically extend by other relations than subClassOf ? subPropertyOf? 
		final Property extensionProperty = new Property(modelData, RDFS.subClassOf, false);

		Map<Node, Node> svUriTVuriMap = mapping.getExtendedMappedValues(modelSet, extensionProperty);
		//svUriTVuriMap = mapping.getMappedValues();	
		
		if (null == svUriTVuriMap) {
			throw new MappingException("Could not apply mapping, since no mapped values have been found. Source-to-target-value-map was null");
		} else if (svUriTVuriMap.isEmpty()) {
			throw new MappingException("Could not apply mapping, since no mapped values have been found. Source-to-target-value-map is empty.");
		}

		LOGGER.fine("Encoding statement " + statement);
		
		// special treatment of rdf:ID not necessary anymore since special triples <ID> rdf:ID <ID> are now provided
		
		// get the target value for the sv
		Node targetValue = svUriTVuriMap.get(sourceValueWorkNode); 

    	// if we found a tv for the sv
    	if (null != targetValue) {
    		
	    	rvlInterpreter.applyGraphicValueToGO(tga, targetValue, sourceValueWorkNode, graphicObjectToApplyMapping);	
	    	
	    	// TODO enable again : 
	    	//rvlInterpreter.applyInheritanceOfTargetValue(mapping, statement.getSubject(), targetValue);
	    	
	    	// TODO call submappings here: problem: we dont have graphic relation here as expected by the applySubmappingsMethod.
	    	// create additional method, or a graphic relation to be consistent here?
	    	// submappings
			if (mapping.hasSubMapping()) {
				LOGGER.finest("Applying submapping ... ");
				rvlInterpreter.applySubmappings(mapping, statement, graphicObjectToApplyMapping);
			}
	    	
    	} else {
			LOGGER.fine("No target value found for source value " + sourceValueWorkNode + ".");
		}	
	}
}
