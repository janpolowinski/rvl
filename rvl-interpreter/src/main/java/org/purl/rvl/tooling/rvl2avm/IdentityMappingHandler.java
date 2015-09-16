package org.purl.rvl.tooling.rvl2avm;

import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdfreactor.schema.rdfs.Property;
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
import org.purl.rvl.tooling.commons.Graph;
import org.purl.rvl.tooling.commons.Settings;
import org.purl.rvl.tooling.commons.utils.ModelUtils;
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
	void encodeStatement(Statement statement)
			throws InsufficientMappingSpecificationException, MappingException {
		
		Resource subject = statement.getSubject();
		Node sourceValue = statement.getObject();

		// For each statement, create a startNode GO representing the subject (if not exists)
		GraphicObjectX subjectNode = rvlInterpreter.createOrGetGraphicObject(subject);
		
		LOGGER.finest("Created GO for subject: " + subject.toString());
		
		encodeStatement(statement, this.mapping, subjectNode, sourceValue);
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
		
		Node sp = mappingAttribute.getSourceProperty();
		
		Model modelData = modelSet.getModel(Graph.GRAPH_DATA);

		
		// TODO hack: we are always settings textvalue here
		// (as will be the case in 99%), while also color values may have been passed!
		//GraphicAttribute tga = GraphicAttribute.getInstance(OGVICProcess.getInstance().getModelAVM(), GraphicObject.TEXTVALUE);
		Property tga = new Property(modelAVM, GraphicObject.TEXTVALUE, false);
		
		if (tga.asURI() != GraphicObject.TEXTVALUE) {
			throw new NotImplementedMappingFeatureException("Currently only those IdentityMappings can be handled that map to textvalue");
		}
		
		if (isLiteral(sourceValue)) {
			
			// source value is a literal -> just use it directly
			
			targetValue = sourceValue.asLiteral();
			
		} else {	
			
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
				targetValue =  new PlainLiteralImpl(ModelUtils.getGoodNodeLabel(sourceResource, modelData));

			} else if (sp.asURI().toString().equals(RVL.ID_AND_TYPES)) {
				
				// special treatment of the source property rvl:IDandTypes
				
				Set<Resource> typesSet = ModelUtils.getTypes(modelData, sourceResource);
				String types = "";

				if (typesSet.isEmpty()) {
					types = " (untyped)";
				} else {
					for (Resource resource : typesSet) {
						types += " : " + ModelUtils.getGoodNodeLabel(resource, modelData);
					}
				}

				targetValue = new PlainLiteralImpl(ModelUtils.getGoodNodeLabel(sourceResource, modelData) + types); //TODO: handle blank nodes!

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
						Graph.GRAPH_DATA,
						sourceResource,
						sp.asURI(),
						Variable.ANY
						);

				if (it.hasNext()) {
					
					Node object = it.next().getObject();
					
					try {
						// if the object is a literal, such as a label
						targetValue = sourceValue = object.asLiteral();
					} catch (ClassCastException e ) {
						// if the object is not a Literal
						Literal valueAsLiteral;
						try {
							valueAsLiteral = new PlainLiteralImpl(object.asURI().toString());
						} catch (ClassCastException e1) {
							throw new MappingException("Blank node passed to identity mapping? ", e1);
						}
						targetValue = sourceValue = valueAsLiteral;
					}
				}
			}
		}

		// remove existing shapes (incl. default shape) - these are now overridden by the text-shape
		go.removeAllShapenamed();
		
		if (null != targetValue) {
			rvlInterpreter.applyGraphicValueToGO(tga, targetValue, sourceValue, go);
		} else {
			LOGGER.warning("target value was null and could not be set while interpreting identity mapping for statement " + statement);
		}
	
	}

	/**
	 * Helper method for distinguishing literals from resources, since there seems to be no such method in RDF2GO.
	 * @param sourceValue
	 * @return
	 */
	private static boolean isLiteral(Node sourceValue) {
		try {
			sourceValue.asLiteral();
			return true;
		} catch (ClassCastException e ) {}
		return false;
	}

	/**
	 * @param mapping
	 * @throws MappingException
	 */
	public void handleIdentityMapping(IdentityMappingX mapping) throws MappingException {

		this.mapping = mapping;
		
		try {
			stmtSetIterator = DataQuery.findRelationsOnInstanceOrClassLevel(
					modelSet, Graph.GRAPH_DATA,
					mapping,
					true, null, null).iterator();
			
		} catch (InsufficientMappingSpecificationException e) {
			throw new MappingException("Problem getting Identity-mapping-statements " +
					"for " + mapping + ": " + e.getMessage());
		}


		if (null == stmtSetIterator) {
			LOGGER.severe("Statement iterator was null, no relations could be interpreted for "
					+ mapping);
		} else if (!stmtSetIterator.hasNext()) {
			LOGGER.severe("Statement iterator was empty, no relations could be interpreted for "
					+ mapping);
		} else {

			while (stmtSetIterator.hasNext()
					&& processedGraphicRelations < Settings.MAX_GRAPHIC_RELATIONS_PER_MAPPING) {

				Statement statement = (Statement) stmtSetIterator.next();

					try {
						encodeStatement(statement);
					} catch (InsufficientMappingSpecificationException e) {
						throw new MappingException("Problem encoding statement " 
							+ statement + ": " + e.getMessage());
					}

				processedGraphicRelations++;

			}

		}
		
	}


}
