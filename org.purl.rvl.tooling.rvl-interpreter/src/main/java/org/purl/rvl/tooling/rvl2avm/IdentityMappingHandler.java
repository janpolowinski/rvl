package org.purl.rvl.tooling.rvl2avm;

import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.exception.NotImplementedMappingFeatureException;
import org.purl.rvl.java.RDF;
import org.purl.rvl.java.RVL;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.gen.viso.graphic.GraphicObject;
import org.purl.rvl.java.rvl.IdentityMappingX;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.query.data.DataQuery;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.ModelUtils;

public class IdentityMappingHandler extends MappingHandlerBase {
	
	private final static Logger LOGGER = Logger
			.getLogger(IdentityMappingHandler.class.getName());
	
	protected IdentityMappingX mapping;

	public IdentityMappingHandler(ModelSet modelSet,
			RVLInterpreter rvlInterpreter, Model modelAvm) {
		super(modelSet, rvlInterpreter, modelAvm);
	}

	@Override
	void encodeStatement(Statement statement)
			throws InsufficientMappingSpecificationException, MappingException {
		
		Resource subject = statement.getSubject();
		Node sourceValue = statement.getObject();

		// For each statement, create a startNode GO representing the subject (if not exists)
		GraphicObjectX subjectNode = rvlInterpreter.createOrGetGraphicObject(subject);
		
		LOGGER.finest("Created GO for subject: " + subject.toString());
		
		encodeStatement(statement,this.mapping,subjectNode,sourceValue);
	}
	
	/**
	 * 
	 * @param statement
	 * @param mappingAttribute
	 * @param go
	 * @param sourceValue
	 * @throws InsufficientMappingSpecificationException
	 * @throws MappingException
	 */
	public void encodeStatement(Statement statement, IdentityMappingX mappingAttribute, GraphicObjectX go, Node sourceValue)
			throws InsufficientMappingSpecificationException, MappingException {
		
		logStatementDetails(LOGGER,statement);
		
		Node targetValue = null;
		
		Node sp = statement.getPredicate();
		
		Model modelData = modelSet.getModel(OGVICProcess.GRAPH_DATA);

		
		// TODO hack: we are always settings textvalue here
		// (as will be the case in 99%), while also color values may have been passed!
		GraphicAttribute tga = GraphicAttribute.getInstance(OGVICProcess.getInstance().getModelAVM(), GraphicObject.TEXTVALUE);
		
		if (tga.asURI() == GraphicObject.TEXTVALUE) {

				// if the source value is a literal just use it directly
				try {
					
					targetValue = sourceValue.asLiteral();
					return;
					
				} catch (ClassCastException e ) {}
				
				// handle source value as a resource
				
				Resource sourceResource;
				
				try {
					sourceResource = sourceValue.asResource();
				} catch (ClassCastException e) {
					throw new MappingException("Cannot cast " + sourceValue + "to a Resource. ");
				}

				if (sp.asURI().toString().equals(RVL.LABEL)) {

					// special treatment of the source property rvl:label
					// (will use rdfs:label or use the local name for URIs instead)
					targetValue =  new PlainLiteralImpl(AVMUtils.getGoodNodeLabel(sourceResource, modelData));

				} else if (sp.asURI().toString().equals(RVL.ID_AND_TYPES)) {
					
					// special treatment of the source property rvl:IDandTypes
					
					Set<Resource> typesSet = ModelUtils.getTypes(modelData, sourceResource);
					String types = "";
	
					if (typesSet.isEmpty()) {
						types = " (untyped)";
					} else {
						for (Resource resource : typesSet) {
							types += " : " + AVMUtils.getGoodNodeLabel(resource, modelData);
						}
					}

					targetValue = new PlainLiteralImpl(AVMUtils.getGoodNodeLabel(sourceResource, modelData) + types); //TODO: handle blank nodes!

				} else if (sp.equals(RDF.ID)) {
					
					// special treatment of the source property rdf:ID
					
					try {
						targetValue = new PlainLiteralImpl(sourceResource.asURI().toString()); //TODO: handle blank nodes!
					} catch (Exception e ) {
						throw new MappingException("Could not create target text literal for identity mapping from " + sourceValue);
					}
					
				} else {
	
					// for all other source properties, get the first (random) value
	
					ClosableIterator<Statement> it = modelSet.findStatements(
							OGVICProcess.GRAPH_DATA,
							sourceResource,
							sp.asURI(),
							Variable.ANY
							);
	
					if (it.hasNext()) {
						
						Node object = it.next().getObject();
						
						try {
							// if the object is a literal, such as a label
							targetValue = object.asLiteral();
						} catch (ClassCastException e ) {
							// if the object is a URI //TODO: handle blank nodes!
							targetValue = new PlainLiteralImpl(object.asURI().toString());
						}
					}
				}

			// remove existing shapes (incl. default shape) - these are now overridden by the text-shape
			go.removeAllShapenamed();
			
		} else {
			throw new NotImplementedMappingFeatureException("Currently only those IdentityMappings can be handled that map to textvalue");
		}
			
		rvlInterpreter.applyGraphicValueToGO(tga, targetValue, sourceValue, go);
	
	}

	/**
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
