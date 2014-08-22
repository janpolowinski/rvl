/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.exception.NotImplementedMappingFeatureException;
import org.purl.rvl.java.RDF;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;
import org.purl.rvl.java.rvl.mapping.TupleSourceValueTargetValue;
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
	    	
	    	// validity checking
			
			if (!mapping.hasValuemapping()) {
				throw new InsufficientMappingSpecificationException(
						"P2GA-mappings with no value mappings at all are not supported.");
			}

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
		
		// create a GO for the subject of the statement
	    GraphicObjectX go = rvlInterpreter.createOrGetGraphicObject(subject);
	    
	    LOGGER.finest("Created GO for subject: " + subject);

	    encodeStatement(statement, mapping, go, subject);
	}
	


	public void encodeStatement(Statement statement, PropertyToGraphicAttributeMappingX mapping,
			GraphicObjectX go, Resource subjectResource) throws InsufficientMappingSpecificationException {
		
		this.mapping = mapping;
		
		GraphicAttribute tga = mapping.getTargetAttribute();
		Property sp = mapping.getSourceProperty();
		
		// get the mapping table SV->TV (the calculation of mapped values from data dependent (!) implicit
		// value mappings must have been triggered before!)
		
		// mapping table SV->TV
		Map<Node, Node> svUriTVuriMap;

		Model modelData = modelSet.getModel(OGVICProcess.getInstance().GRAPH_DATA);
		Property extensionProperty = new Property(modelData, RDFS.subClassOf, false);

		svUriTVuriMap = mapping.getExtendedMappedValues(modelSet, extensionProperty);
		//svUriTVuriMap = mapping.getMappedValues();	
		
		if (null == svUriTVuriMap || svUriTVuriMap.isEmpty()) {
			LOGGER.severe("Could not apply submappings since no mapped values have been found.");
			return;
		}
		
		// get the target value for the sv
    	Node targetValue;
    	Node sourceValue = null;
    	
    	if (sp.asURI().equals(RDF.ID)) { // special treatment of rdf:ID TODO: still necessary?

			sourceValue = svUriTVuriMap.get(subjectResource);
			targetValue = sourceValue; 

		} else { // other source properties than rdf:ID ...
			
			//sourceValue = statement.getObject();
			//targetValue = svUriTVuriMap.get(sourceValue); 
			
			
			
			// this does not work for cases like mapping the shape in AA-4 were a relation on class level is mapped!
			// but it seems to be necessary for RO-6 etc ...
			// TODO the necessary extension mechanism needs to be refactored and be able to handle class-level relations
			// hint: use sparql query instead!

			TupleSourceValueTargetValue<Node, Node> svWithItsTv;
			//ClosableIterator<Statement> it = modelSet.findStatements(OGVICProcess.GRAPH_DATA, subjectResource,
			//		sp.asURI(), Variable.ANY);
			
			Iterator<Statement> it = DataQuery.findRelationsOnInstanceOrClassLevel(
					modelSet,
					OGVICProcess.GRAPH_DATA,
					(PropertyMappingX)mapping.castTo(PropertyMappingX.class),
					false,
					subjectResource,
					null)
					.iterator();
			
			LOGGER.severe("Getting Statement iterator for (" + subjectResource + " " + sp.asURI() + " ANY)");
			
			try {

				// TODO Multiple objects may match a mapped target value. For now only the first match will win!
				svWithItsTv = rvlInterpreter.lookUpTvForSv(it, svUriTVuriMap);
				sourceValue = svWithItsTv.sourceValue; 
				targetValue = svWithItsTv.targetValue;

			} catch (Exception e) {
				LOGGER.fine("Could not get value for source property " + sp + " for object "
						+ statement.getObject());
				return;
			}
	
		}

    	// if we found a tv for the sv
    	if (null != targetValue && null != sourceValue) {
    		
	    	rvlInterpreter.applyGraphicValueToGO(tga, targetValue, sourceValue, go);	
	    	
	    	// TODO enable again : 
	    	rvlInterpreter.applyInheritanceOfTargetValue(mapping, statement.getSubject(), targetValue);
	    	
    	} else {
			LOGGER.fine("Source or target (graphic) value was null, couldn't apply target value " + targetValue
					+ " to the source value " + sourceValue + ".");
		}
		
	}

}
