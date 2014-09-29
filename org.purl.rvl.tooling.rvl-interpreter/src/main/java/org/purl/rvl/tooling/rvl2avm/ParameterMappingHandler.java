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
import org.purl.rvl.exception.UnsupportedMappingParameterValueException;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.gen.viso.graphic.Object_to_ObjectRelation;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.query.data.DataQuery;

/**
 * Handles mappings on the "parameters" of complex (n-ary) relations. 
 * For example this can be used to set the labeling type of a labeling relation depending on 
 * different source values.
 * 
 * Since this is very similar to setting the graphic attributes of a GraphicObject, currently P2GAMappings are 
 * used for this purpose and hence this handler extends the {@link MappingToP2GAMHandler}.
 * 
 * @author Jan Polowinski
 *
 */
public class ParameterMappingHandler extends MappingToP2GAMHandler {
	
	private final static Logger LOGGER = Logger
			.getLogger(ParameterMappingHandler.class.getName());

	public ParameterMappingHandler(ModelSet modelSet, RVLInterpreterBase rvlInterpreterBase, Model modelAVM) {
		super(modelSet, rvlInterpreterBase, modelAVM);
	}

	public void handleParameterMapping(final PropertyToGraphicAttributeMappingX parameterMapping,
			final Object_to_ObjectRelation rel, final Resource workResource) throws NotImplementedMappingFeatureException, MappingException {
		
    	if (null == rel) {
    		throw new MappingException("A graphic relation to apply the parameter settings " +
    				"to must be provided (relation was null).");
    	}
    	if (null == workResource) {
    		throw new MappingException("A resource to work with must be provided " +
    				" (workResource was null).");
    	}
		
		// copied from MappingToP2GAMHandler

		this.mapping = parameterMapping; // CHANGE

	    try {
	    	
	    	// validity checking
			
			if (!mapping.hasValuemapping()) {
				throw new InsufficientMappingSpecificationException(
						"P2GA-mappings with no value mappings at all are not supported.");
			}

			// get a statement set 
			final Set<Statement> stmtSet = DataQuery.findRelationsOnInstanceOrClassLevel(
					modelSet,
					OGVICProcess.GRAPH_DATA,
					mapping,
					true, // only most specific relations (e.g. only A citesCritical B, not A cites B if both exist)
					workResource,
					null
					);

			// (re)calculate the (implicitly) mapped values
			// this will be accessed in the encode method while encoding statements
			// TODO is this done too often?
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
			    		&& processedGraphicRelations < OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING) {
			    	
			    	Statement statement = stmtSetIterator.next();
			    	Node valueNode = statement.getObject();
			    	
			    	encodeStatement(statement, mapping, rel, valueNode); // CHANGE
		    	
			    	processedGraphicRelations++;
			    	
				}
		    
			}
			
//		} catch (InsufficientMappingSpecificationException e) {
//			LOGGER.warning("No resources will be affected by mapping " + mapping 
//					+ " (" + e.getMessage() + ")" );
		} catch (ClassCastException e) {
			throw new UnsupportedMappingParameterValueException(mapping, "Maybe the submapping was " +
					"accidentally applied to a literal? :" + e.getMessage());
		}
		
	}

	private void encodeStatement(Statement statement, PropertyToGraphicAttributeMappingX mapping,
			Object_to_ObjectRelation rel, Node workNode) throws MappingException {
		
		// copied from MappingToP2GAMHandler

		this.mapping = mapping;
		
		Property tga = mapping.getTargetGraphicRelation();
		//Property sp = mapping.getSourceProperty();
		
		// get the mapping table SV->TV (the calculation of mapped values from data dependent (!) implicit
		// value mappings must have been triggered before!)
		
		// mapping table SV->TV
		Map<Node, Node> valueMap;

		final Model modelData = modelSet.getModel(OGVICProcess.GRAPH_DATA);
		// TODO: automatically extend by other relations than subClassOf ? subPropertyOf? 
		final Property extensionProperty = new Property(modelData, RDFS.subClassOf, false);

		valueMap = mapping.getExtendedMappedValues(modelSet, extensionProperty);   // CHANGE? TODO only explicitly mapped values?
		//valueMap = mapping.getMappedValues();	
		//valueMap = mapping.getExplicitlyMappedValues()
		
		if (null == valueMap || valueMap.isEmpty()) {
			throw new MappingException("Could not apply submappings since mapped values were null or empty.");
		} else {
			LOGGER.finest("Parameter mapping table: " + PropertyToGraphicAttributeMappingX.valueMapToString(valueMap));
		}

		// special treatment of rdf:ID not necessary anymore since special triples <ID> rdf:ID <ID> are now provided
		final Node sourceValue = workNode;
		
		// get the target value for the sv
		final Node targetValue = valueMap.get(sourceValue); 

    	// if we found a tv for the sv
    	if (null != targetValue && null != sourceValue) {
    		
	    	rvlInterpreter.applyParameterToGraphicRelation(tga, targetValue, sourceValue, rel);	// CHANGED
	    	
	    	// TODO enable again : 
	    	//rvlInterpreter.applyInheritanceOfTargetValue(mapping, statement.getSubject(), targetValue);
	    	
    	} else {
			LOGGER.fine("Source or target (graphic) value was null, couldn't apply target value " + targetValue
					+ " to the source value " + sourceValue + ".");
		}
		
	}

}
