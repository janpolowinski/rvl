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
					OGVICProcess.GRAPH_DATA,
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
						+ mapping.asURI());
			} else if (!stmtSetIterator.hasNext()) {
				LOGGER.severe("Statement iterator was empty, no relations could be interpreted for "
						+ mapping.asURI());
			} else {
		    
			    while (stmtSetIterator.hasNext() 
			    		&& processedGraphicRelations < OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING) {
			    	
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
			
		} catch (InsufficientMappingSpecificationException e) {
			LOGGER.warning("No resources will be affected by mapping " + mapping.asURI() 
					+ " (" + e.getMessage() + ")" );
		} catch (ClassCastException e) {
			LOGGER.warning("No resources will be affected by mapping. Maybe the submapping was " +
					"accidentally applied to literal? " + mapping.asURI() 
					+ " (" + e.getMessage() + ")" );
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
	 * @param mapping
	 * @param go - the graphic object to use as a base for applying the additional visual encoding
	 * @param workNode - the node to work with (use as source value, apply base further mappings on ...)
	 * It is one of the nodes used in the statement.
	 * @throws InsufficientMappingSpecificationException
	 */
	public void encodeStatement(Statement statement, PropertyToGraphicAttributeMappingX mapping,
			GraphicObjectX graphicObjectToApplyMapping, Node workNode) throws InsufficientMappingSpecificationException {
		
		this.mapping = mapping;
		
		GraphicAttribute tga = mapping.getTargetAttribute();
		//Property sp = mapping.getSourceProperty();
		
		// get the mapping table SV->TV (the calculation of mapped values from data dependent (!) implicit
		// value mappings must have been triggered before!)
		
		// mapping table SV->TV
		Map<Node, Node> svUriTVuriMap;

		final Model modelData = modelSet.getModel(OGVICProcess.GRAPH_DATA);
		// TODO: automatically extend by other relations than subClassOf ? subPropertyOf? 
		final Property extensionProperty = new Property(modelData, RDFS.subClassOf, false);

		svUriTVuriMap = mapping.getExtendedMappedValues(modelSet, extensionProperty);
		//svUriTVuriMap = mapping.getMappedValues();	
		
		if (null == svUriTVuriMap || svUriTVuriMap.isEmpty()) {
			LOGGER.severe("Could not apply submappings since no mapped values have been found.");
			return;
		}

    	Node targetValue = null;
    	Node sourceValue = null;

		// special treatment of rdf:ID not necessary anymore since special triples <ID> rdf:ID <ID> are now provided
		sourceValue = workNode;
		
		// get the target value for the sv
		targetValue = svUriTVuriMap.get(sourceValue); 
		
		
		// should now be obsolete since done in handle method
		// delete soon
		/*
		TupleSourceValueTargetValue<Node, Node> svWithItsTv;
		//ClosableIterator<Statement> it = modelSet.findStatements(OGVICProcess.GRAPH_DATA, subjectResource,
		//		sp.asURI(), Variable.ANY);
		
		Iterator<Statement> it = DataQuery.findRelationsOnInstanceOrClassLevel(
				modelSet,
				OGVICProcess.GRAPH_DATA,
				(PropertyMappingX)mapping.castTo(PropertyMappingX.class),
				true,
				workNode.asResource(),
				null)
				.iterator();
		
		LOGGER.severe("Getting Statement iterator for (" + workNode + " " + sp.asURI() + " ANY)");
		
		try {

			// TODO Multiple objects may match a mapped target value. For now only the first match will win!
			svWithItsTv = rvlInterpreter.lookUpTvForSv(it, svUriTVuriMap);
			sourceValue = svWithItsTv.sourceValue; 
			targetValue = svWithItsTv.targetValue;

		} catch (Exception e) {
			LOGGER.fine("Could not get value for source property " + sp + " for subject "
					+ workNode);
			return;
		}
		*/
		

    	// if we found a tv for the sv
    	if (null != targetValue && null != sourceValue) {
    		
	    	rvlInterpreter.applyGraphicValueToGO(tga, targetValue, sourceValue, graphicObjectToApplyMapping);	
	    	
	    	// TODO enable again : 
	    	//rvlInterpreter.applyInheritanceOfTargetValue(mapping, statement.getSubject(), targetValue);
	    	
    	} else {
			LOGGER.fine("Source or target (graphic) value was null, couldn't apply target value " + targetValue
					+ " to the source value " + sourceValue + ".");
		}
    	
    	// TODO call submappings here: problem: we dont have graphic relation here as expected by the applySubmappingsMethod.
    	// create additional method, or a graphic relation to be consistent here?
    	// submappings
		if (mapping.hasSubMapping()) {
			rvlInterpreter.applySubmappings(mapping, statement, graphicObjectToApplyMapping);
		} else {
			LOGGER.warning("Submapping existed, but could not be applied, since no parent graphic relation was provided.");
		}
		
	}

}
