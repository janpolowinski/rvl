package org.purl.rvl.tooling.rvl2avm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.ontoware.aifbcommons.collection.ClosableIterable;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.owl.Restriction;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.java.exception.InsufficientMappingSpecificationExecption;
import org.purl.rvl.java.gen.rvl.GraphicAttribute;
import org.purl.rvl.java.gen.rvl.Mapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;
import org.purl.rvl.java.gen.rvl.Sub_mappingrelation;
import org.purl.rvl.java.gen.viso.graphic.Color;
import org.purl.rvl.java.gen.viso.graphic.Containment;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.gen.viso.graphic.Shape;
import org.purl.rvl.java.gen.viso.graphic.Thing1;
import org.purl.rvl.java.gen.viso.graphic.UndirectedLinking;
import org.purl.rvl.java.rvl.PropertyMapping;
import org.purl.rvl.java.rvl.PropertyToGO2ORMapping;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping;
import org.purl.rvl.java.viso.graphic.GraphicObject;
import org.purl.rvl.java.viso.graphic.ShapeX;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.RVLUtils;

public class SimpleRVLInterpreter  extends RVLInterpreterBase {
	

	
	private final static Logger LOGGER = Logger.getLogger(SimpleRVLInterpreter.class .getName()); 

	public SimpleRVLInterpreter() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.rvl2avm.RVLInterpreterBase#interpretMappings()
	 */
	@Override
	protected void interpretMappingsInternal() {
		
		if (null==model) {
			LOGGER.severe("Cannot interprete mappings, since model is null.");
			return;
		}
		
		interpretSimpleP2GArvlMappings();
		interpretNormalP2GArvlMappings();
		interpretP2GO2ORMappings();
		interpretResourceLabelAsGOLabelForAllCreatedResources();
	}

	/**
	 * Interprets the P2GO2OR mappings.
	 * TODO: Implement other GR than linking
	 */
	protected void interpretP2GO2ORMappings() {
		
		// get all P2GO2OR mappings to linking and create n-ary linking relations
		//Set<PropertyToGO2ORMapping> setOfMappingsToLinking = getAllP2GOTORMappingsTo(DirectedLinking.RDFS_CLASS); // 
		
		Set<PropertyToGO2ORMapping> mappings = getAllP2GOTORMappings();
		
		LOGGER.info(NL + "Found " + mappings.size() + " PGOTOR mappings (enabled and disabled mappings).");
		
		// for each mapping
		for (Iterator<PropertyToGO2ORMapping> iterator = mappings
				.iterator(); iterator.hasNext();) {
			
			PropertyToGO2ORMapping p2go2orm = (PropertyToGO2ORMapping) iterator.next();
			
			// skip disabled
			if (p2go2orm.isDisabled()) {
				LOGGER.info("Ignored disabled P2GO2OR mapping " + p2go2orm.asURI());
				continue;
			}
			
			try {
				
				if (p2go2orm.getTargetGraphicRelation().equals(DirectedLinking.RDFS_CLASS) || p2go2orm.getTargetGraphicRelation().equals(UndirectedLinking.RDFS_CLASS)) {
					interpretMappingToLinking(p2go2orm);
				}
//				else if (p2go2orm.getTargetGraphicRelation().equals(UndirectedLinking.RDFS_CLASS)) {
//					LOGGER.info("Ignored Mapping to Undirected Linking. Undirected Linking not yet implemented");
//				}
				else if (p2go2orm.getTargetGraphicRelation().equals(Containment.RDFS_CLASS)) {
					//interpretMappingToLinking(p2go2orm);
					LOGGER.info("Ignored Mapping to Containment. Containment not yet implemented");
				}
				else  {
					LOGGER.info("Ignord mapping to " + p2go2orm.getTargetGraphicRelation() + ". Graphic relation not yet implemented");
				}
				
			} catch (InsufficientMappingSpecificationExecption e) {
				LOGGER.severe("Could not interpret P2GOTOR mapping " +  p2go2orm.asURI() + ". " + e.getMessage());
			}

		}
		
		LOGGER.fine("The size of the Resource-to-GraphicObject map is " + resourceGraphicObjectMap.size()+".");
	}

	
	protected void interpretMappingToLinking(PropertyToGO2ORMapping p2go2orm) {

		Property sp = null;
		boolean invertSourceProperty = false;
		Iterator<Statement> stmtSetIterator = null;

		// check some settings and skip if mapping incomplete
		try {
			sp = p2go2orm.getSourceProperty();
			if (null==sp) throw new InsufficientMappingSpecificationExecption();
			
			invertSourceProperty = p2go2orm.isInvertSourceProperty();
			
			LOGGER.fine("Interpreting the mapping to Linking: " + NL + p2go2orm.toString());
			LOGGER.fine("The 'inverse' of the source property (" + sp.asURI() + ") will be used, according to mapping settings.");
		}
		catch (InsufficientMappingSpecificationExecption e) {
			LOGGER.warning(e.getMessage());
			return;
		}

		
		// consider inherited relations, including those between classes (someValueFrom ...)
		if(p2go2orm.hasInheritedby()) {
			try{
				Property inheritedBy = p2go2orm.getInheritedBy();
				stmtSetIterator = RVLUtils.findRelationsOnClassLevel(model, sp.asURI(), inheritedBy).iterator();

			}
			catch (Exception e) {
				LOGGER.severe("Problem evaluating inheritedBy setting - not a Property?");
				return;
			}
		}
		else {
			 // should include statements using a subproperty of sp
			 stmtSetIterator = RVLUtils.findStatementsPreferingThoseUsingASubProperty(model, sp.asURI()).iterator(); 
		}
		
		
		int processedGraphicRelations = 0;	
		
		while (stmtSetIterator.hasNext() && processedGraphicRelations < OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING) {
			
			Statement statement = (Statement) stmtSetIterator.next();
						
			try {
				Resource subject = statement.getSubject();
				Resource object = statement.getObject().asResource();
				
				LOGGER.finest("Subject label " + AVMUtils.getLocalName(modelAVM,subject));
				LOGGER.finest("Object label " + AVMUtils.getLocalName(modelAVM,object));
	
				LOGGER.fine("Statement to be mapped : " + statement);

				// For each statement, create a startNode GO representing the subject (if not exists)
			    GraphicObject subjectNode = createOrGetGraphicObject(subject);
		    	LOGGER.finest("Created GO for subject: " + subject.toString());
				
				// For each statement, create an endNode GO representing the object (if not exists)
		    	//Node object = statement.getObject();
				
				GraphicObject objectNode = createOrGetGraphicObject(object);
		    	LOGGER.finest("Created GO for object: " + object.toString());
		    	
				// create a connector and add default color
				GraphicObject connector = new GraphicObject(modelAVM, true);

				// check for sub-mappings and modify the connector accordingly (-> generalize!)
				if(p2go2orm.hasSub_mapping()){
					applySubmappingToConnector(p2go2orm,statement,connector);
				}
				
				// directed linking
				if (p2go2orm.getTargetGraphicRelation().equals(DirectedLinking.RDFS_CLASS)) {
					
			    	// create the directed linking relation
			    	DirectedLinking dlRel = new DirectedLinking(modelAVM, true);
			    	
					// configure the relation
					if(invertSourceProperty) {
						dlRel.setEndnode(subjectNode);
						dlRel.setStartnode(objectNode);
						subjectNode.setLinkedfrom(dlRel);
						objectNode.setLinkedto(dlRel);
					} else {
						dlRel.setStartnode(subjectNode);
						dlRel.setEndnode(objectNode);
						subjectNode.setLinkedto(dlRel);
						objectNode.setLinkedfrom(dlRel);
					}
					
					dlRel.setLinkingconnector(connector);
					
				} else { // undirected linking
					
					// create the undirected linking relation
			    	UndirectedLinking udlRel = new UndirectedLinking(modelAVM, true);
			    	
					// configure the relation
					udlRel.addLinkingnode(subjectNode);
					udlRel.addLinkingnode(objectNode);
					subjectNode.setLinkedwith(udlRel);
					objectNode.setLinkedwith(udlRel);
					
					udlRel.setLinkingconnector(connector);

				}
				
			}
			catch (Exception e) {
				LOGGER.warning("Problem creating GOs: " + e.getMessage());
			}
			
			processedGraphicRelations++;	
		}
		
	}


	/**
	 * Sets the color of the connector according to evtl. existent submappings
	 * 
	 * @param p2go2orm
	 * @param mainStatement
	 * @param connector
	 */
	protected void applySubmappingToConnector(PropertyToGO2ORMapping p2go2orm, Statement mainStatement, GraphicObject connector) {
		
		String label = "";
		Sub_mappingrelation smr = p2go2orm.getAllSub_mapping_as().firstValue();

		if(smr.hasSub_mapping() && smr.hasOnrole()){
			
			Mapping subMapping = smr.getAllSub_mapping_as().firstValue();
			
			label += " ... to mapping: " + subMapping ; // wrong return type and wrong methode name, but seems to work
			label += " ... on role: " + smr.getAllOnrole_as().firstValue() ;
			label += RVLUtils.mappingToStringAsSpecificAsPossible((org.purl.rvl.java.rvl.Mapping)subMapping.castTo(org.purl.rvl.java.rvl.Mapping.class)) + NL ;

			PropertyToGraphicAttributeMapping p2gam = (PropertyToGraphicAttributeMapping)subMapping.castTo(PropertyToGraphicAttributeMapping.class);
			
			GraphicAttribute tga = null;
			
			try {
				tga = p2gam.getTargetAttribute();
			} catch (InsufficientMappingSpecificationExecption e) {
				LOGGER.warning("Submapping could not be applied. Reason: " + e.getMessage());
				return;
			}
			
			// get the subproperties as subjects of the new mapping --> do this in the calculation of value mappings instead
	
			if (null != tga && p2gam.hasValuemapping()) {
			
				Map<Node, Node> svUriTVuriMap = p2gam.getExplicitlyMappedValues();	
				
				if(!svUriTVuriMap.isEmpty()){
					
					LOGGER.finest("map of sv and tv for submapping:");
					
					for (Entry<Node, Node> entry : svUriTVuriMap.entrySet()) {
						Node sv = entry.getKey();
						Node tv = entry.getValue();
						label += "source and target values: "+sv+" --> "+tv+"" + NL;
						LOGGER.finest(sv+" --> "+tv);
					}
					
				}
				///Node triplePartValue = ...
				
				//Node property = (Node) model.getProperty(new URIImpl("http://purl.org/rvl/example-data/cites"));
				
				URI predicate = mainStatement.getPredicate();
				Node colorNode = svUriTVuriMap.get(predicate);
				
				// if we found a tv for the sv
		    	if (null != colorNode && null != predicate) {
		    		applyGraphicValueToGO(tga, colorNode, predicate, connector);
		    	}
				
			}
			
		}
		if(smr.hasOntriplepart()) {
			label += " ... on triple part: " + smr.getAllOntriplepart_as().firstValue() ;
		}

		connector.setLabel("Connector with an applied submapping: " + label);
		
		//connector.setColorhslhue(new Float(155));
		//connector.setColorhslsaturation(new Float(100));
		//connector.setColorhsllightness(new Float(50));
	}

	
	/**
	 * Interprets the normal P2GA mappings, i.e. those with need for calculating value mappings. 
	 * Creates GO for all affected resources if they don't exist already.
	 */
	protected void interpretNormalP2GArvlMappings() {
		
		Set<PropertyToGraphicAttributeMapping> setOfP2GAMappings = getAllP2GAMappingsWithSomeValueMappings();
		
		LOGGER.info(NL + "Found " +setOfP2GAMappings.size()+ " normal P2GA mappings.");
		
		// for each normal P2GA mapping
		for (Iterator<PropertyToGraphicAttributeMapping> iterator = setOfP2GAMappings
				.iterator(); iterator.hasNext();) {
			
			PropertyToGraphicAttributeMapping p2gam = (PropertyToGraphicAttributeMapping) iterator.next();
			
			if (p2gam.isDisabled()) {
				LOGGER.info("Ignored disabled normal P2GAM mapping " + p2gam.asURI() );
				continue;
			}
			
			interpretNormalP2GArvlMapping(p2gam);
		}

		LOGGER.fine("The size of the Resource-to-GraphicObject map is " + resourceGraphicObjectMap.size()+".");
		
	}

	
	/**
	 * Interprets a normal P2GA mapping
	 * Creates GO for all affected resources if they don't exist already.
	 * @param p2gam 
	 */
	protected void interpretNormalP2GArvlMapping(PropertyToGraphicAttributeMapping p2gam) {

		LOGGER.info("Interpret P2GAM mapping " + p2gam.asURI() );

		try {
			
			GraphicAttribute tga = p2gam.getTargetAttribute();

		    // get a statement set 
		    Set<Statement> stmtSet = RVLUtils.findRelationsOnInstanceOrClassLevel(model, p2gam, null, null); 

			// get the mapping table SV->TV
			Map<Node, Node> svUriTVuriMap = p2gam.getCalculatedValues(stmtSet);	
		    
		    
		    // for all statements check whether there is a tv for the sv
		    for (Iterator<Statement> stmtSetIt = stmtSet.iterator(); stmtSetIt
					.hasNext();) {
		    	
		    	Statement statement = (Statement) stmtSetIt.next();
		    	
				// create a GO for each subject of the statement
			    GraphicObject go = createOrGetGraphicObject(statement.getSubject());

		    	Node sv = statement.getObject();

				LOGGER.finest("trying to find and apply value mapping for sv " + sv.toString());
				
				// get the target value for the sv
		    	Node tv = svUriTVuriMap.get(sv);
		    	
		    	// if we found a tv for the sv
		    	if (null != tv) {
			    	applyGraphicValueToGO(tga, tv, sv, go);	
		    	}
		    	
			}
		    
		} catch (InsufficientMappingSpecificationExecption e) {
			LOGGER.warning("No resources will be affected by mapping " + p2gam.asURI() + " (" + e.getMessage() + ")" );
		} 
			
	}

	private void applyGraphicValueToGO(GraphicAttribute tga,
			Node tv, Node sv, GraphicObject go) {
		
		if (null != tga && null != tv && null != sv && null != go ) {
			
			LOGGER.finest("Setting tv " + tv + " for sv " + sv);
			
			// if we are mapping to named colors
		    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/color_named")) {
		    	Color color = Color.getInstance(model, tv.asURI());
		    	go.setColornamed(color);
		    	LOGGER.finer("Set color named to " + color + " for sv " + sv);
		    }
		    
			// if we are mapping to lightness
		    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/color_hsl_lightness")) {
		    	go.setColorhsllightness(tv);
		    	LOGGER.finer("Set color hsl lightness to " + tv.toString() + " for sv " + sv);
		    }
		    
			// if we are mapping to named shapes
		    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/shape_named")) {
		    	Shape shape = ShapeX.getInstance(model, tv.asURI());
		    	go.setShapenamed(shape);
		    	LOGGER.finer("Set shape to " + shape + " for sv " + sv + NL);
		    }
		}
		
		else {
			LOGGER.warning("Could not set target value, since one of the required parameters was null.");
		}
	}
	
	

	/**
	 * Interprets the simple P2GA mappings, i.e. those without need for calculating value mappings. 
	 * Creates GO for all affected resources if they don't exist already.
	 */
	protected void interpretSimpleP2GArvlMappings() {
		
		Set<PropertyToGraphicAttributeMapping> setOfSimpleP2GAMappings = getAllP2GAMappingsWithExplicitMappings();
		
		LOGGER.info(NL + "Found " +setOfSimpleP2GAMappings.size()+ " simple P2GA mappings.");
		
		// for each simple mapping
		for (Iterator<PropertyToGraphicAttributeMapping> iterator = setOfSimpleP2GAMappings
				.iterator(); iterator.hasNext();) {
			
			PropertyToGraphicAttributeMapping p2gam = (PropertyToGraphicAttributeMapping) iterator.next();
			
			if (p2gam.isDisabled()) {
				LOGGER.info("Ignored disabled simple P2GAM mapping " + p2gam.asURI() );
				continue;
			}

			LOGGER.info("Interpret simple P2GAM mapping " + p2gam.asURI() );
			
			// get the mapping table SV->TV
			Map<Node, Node> svUriTVuriMap = p2gam.getExplicitlyMappedValues();	
			
			try {
				GraphicAttribute tga = p2gam.getTargetAttribute();
	
			    Set<Statement> theStatementWithOurObject = RVLUtils.findRelationsOnInstanceOrClassLevel(model, p2gam, null, null); 

			    for (Iterator<Statement> stmtSetIt = theStatementWithOurObject.iterator(); stmtSetIt
						.hasNext();) {
					Statement statement = (Statement) stmtSetIt.next();
					
					// create a GO for each subject
				    GraphicObject go = createOrGetGraphicObject(statement.getSubject());
				    
			    	Node sv = statement.getObject(); 
							
					// get the target value for the sv
			    	Node tv = svUriTVuriMap.get(sv);
			    	
			    	// if we found a tv for the sv
			    	if (null != tv) {
			    		applyGraphicValueToGO(tga, tv, sv, go);
			    	}
			    }
			
			} catch (InsufficientMappingSpecificationExecption e) {
				LOGGER.warning("No resources will be affected by mapping " + p2gam.asURI() + " (" + e.getMessage() + ")" );
			} 
			
		}
	}
	

}
