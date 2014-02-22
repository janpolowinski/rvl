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
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.owl.Restriction;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.purl.rvl.java.exception.InsufficientMappingSpecificationExecption;
import org.purl.rvl.java.gen.rvl.GraphicAttribute;
import org.purl.rvl.java.gen.rvl.Mapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;
import org.purl.rvl.java.gen.rvl.Sub_mappingrelation;
import org.purl.rvl.java.gen.viso.graphic.Color;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.gen.viso.graphic.Shape;
import org.purl.rvl.java.gen.viso.graphic.Thing1;
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
	 * Interprets the P2GO2OR mappings. (ONLY LINKING AT THE MOMENT -> GENERALIZE)
	 * Creates new GO for all affected resources if they don't exist already.
	 * TODO: merge/reuse GOs
	 */
	protected void interpretP2GO2ORMappings() {
		
		// get all P2GO2OR mappings to linking and create n-ary linking relations
		Set<PropertyToGO2ORMapping> setOfMappingsToLinking = getAllMappingsToLinking();
		
		LOGGER.info("Will evaluate " +setOfMappingsToLinking.size()+ " PGOTOR mappings.");
		
		// for each mapping
		for (Iterator<PropertyToGO2ORMapping> iterator = setOfMappingsToLinking
				.iterator(); iterator.hasNext();) {
			
			PropertyToGO2ORMapping p2go2orm = (PropertyToGO2ORMapping) iterator.next();
			
			if (p2go2orm.isDisabled()) {
				LOGGER.info("Ignored disabled P2GO2OR mapping " + p2go2orm.asURI());
				continue;
			}
			
			interpretMappingToLinking(p2go2orm);
		}
		
		LOGGER.fine("The size of the Resource-to-GraphicObject map is " + resourceGraphicObjectMap.size()+".");
	}

	
	protected void interpretMappingToLinking(PropertyToGO2ORMapping p2go2orm) {

		Property sp = null;
		boolean invertSourceProperty = false;
		Iterator<Statement> stmtSetIterator = null;

		// check some settings and skip if mapping incomplete
		try {
			sp = p2go2orm.getAllSourceproperty_as().firstValue();
			if (null==sp) throw new InsufficientMappingSpecificationExecption();
			
			invertSourceProperty = p2go2orm.isInvertSourceProperty();
			
			PropertyMapping pm = (PropertyMapping) p2go2orm.castTo(PropertyMapping.class);
			LOGGER.fine("Interpreting the mapping: " + NL + pm.toString());
			LOGGER.fine("The 'inverse' of the source property (" + sp.asURI() + ") will be used, according to mapping settings.");
		}
		catch (InsufficientMappingSpecificationExecption e) {
			LOGGER.warning(e.getMessage());
			return;
		}

		
		// consider inherited relations, including those between classes (someValueFrom ...)
		if(p2go2orm.hasInheritedby()) {
			try{
				Property inheritedBy = (Property)p2go2orm.getAllInheritedby_as().firstValue().castTo(Property.class);
				stmtSetIterator = RVLUtils.findRelationsOnClassLevel(model, sp.asURI(), inheritedBy).iterator();

			}
			catch (Exception e) {
				LOGGER.warning("Problem evaluating inheritedBy setting - not a Property?");
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
			
				org.ontoware.rdf2go.model.node.Resource subject = statement.getSubject();
				org.ontoware.rdf2go.model.node.Resource object = statement.getObject().asResource();
				
				LOGGER.finest("Subject label " + AVMUtils.getLocalName(modelAVM,subject));
				LOGGER.finest("Object label " + AVMUtils.getLocalName(modelAVM,object));
	
				LOGGER.fine("Statement to be mapped : " + statement);

				// For each statement, create a startNode GO representing the subject (if not exists)
			    GraphicObject subjectNode = createOrGetGraphicObject(subject);
		    	LOGGER.finest("Created GO for subject: " + subject.toString());
				
				// For each statement, create an endNode GO representing the object (if not exists)
		    	//Node object = statement.getObject();
				
				GraphicObject objectNode = createOrGetGraphicObject((org.ontoware.rdf2go.model.node.Resource)object);
		    	LOGGER.finest("Created GO for object: " + object.toString());
		    	
		    	// create the linking relation
		    	DirectedLinking dlRel = new DirectedLinking(modelAVM, true);
		    	
				// create a connector and add default color
				GraphicObject connector = new GraphicObject(modelAVM, true);

				// check for sub-mappings and modify the connector accordingly (-> generalize!)
				if(p2go2orm.hasSub_mapping()){
					applySubmappingToConnector(p2go2orm,statement,connector);
				}

				
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
				
			}
			catch (Exception e) {
				LOGGER.warning("Problem creating GOs: " + e.getMessage());
			}
			
			processedGraphicRelations++;	
		}
		
	}

//	protected void oldInterpretMappingToLinkingForAffectedResources(PropertyToGO2ORMapping p2go2orm) {
//		
//		PropertyMapping pm = (PropertyMapping) p2go2orm.castTo(PropertyMapping.class);
//		
//		try {
//			// get the subjects affected by the mapping // TODO: here we could also work with statements using the source property directly?!
//			Set<org.ontoware.rdf2go.model.node.Resource> subjectSet = pm.getAffectedResources();
//			Property sp = pm.getAllSourceproperty_as().firstValue();
//			
//			// for each affected resource // TODO do we need the effected resources here at all, or can we directly work on statements?
//			for (Iterator<org.ontoware.rdf2go.model.node.Resource> iterator2 = subjectSet.iterator(); iterator2.hasNext();) {
//				
//				org.ontoware.rdf2go.model.node.Resource subjectResource = iterator2.next().asResource(); // strange: unlike in the toString() method of PM, we cannot simply cast to resource here, only to URI!
//		    	
//				// temp ignore blank nodes
//				boolean nodeIsBlankNode = true;
//				try {
//					subjectResource.asURI();
//					nodeIsBlankNode = false;
//				} catch (ClassCastException e) {
//					LOGGER.finer("ignoring blank node when creating linking relations: " + subjectResource);
//				}
//				if (!nodeIsBlankNode) {
//				
//			    	// problem: while iterating a statement iterator no instances may be created 
//			    	// --> concurrend modification exception
//			    	// solution: first create a set of statements, then iterate
//			    	Set<Statement> resSpStmtSet = new HashSet<Statement>();
//			    	//ClosableIterator<Statement> resSpStmtIt = model.findStatements(subjectResource, sp.asURI(), Variable.ANY); // will ignore statements using a subproperty of sp
//			    	Iterator<Statement> resSpStmtIt = RVLUtils.oldFindStatementsPreferingThoseUsingASubProperty(model, subjectResource, sp.asURI(),Variable.ANY).iterator(); // should include statements using a subproperty of sp
//					while (resSpStmtIt.hasNext()) {
//						Statement statement = (Statement) resSpStmtIt.next();
//						resSpStmtSet.add(statement);
//					}
//					
//					for (Iterator<Statement> resSpStmtSetIt = resSpStmtSet.iterator(); resSpStmtSetIt
//							.hasNext();) {
//						Statement statement = (Statement) resSpStmtSetIt.next();
//						LOGGER.fine("Statement to be mapped : " + statement);
//	
//						// For each statement, create a startNode GO representing the subject (if not exists)
//					    GraphicObject startNode = createOrGetGraphicObject(subjectResource);
//				    	LOGGER.finest("Assigned startNode for: " + subjectResource.toString());
//						
//						// For each statement, create an endNode GO representing the object (if not exists)
//				    	Node object = statement.getObject();
//						
//						GraphicObject endNode = createOrGetGraphicObject((org.ontoware.rdf2go.model.node.Resource)object);
//				    	LOGGER.finest("Assigned endNode for: " + object.toString());
//				    	
//				    	// create the linking relation
//				    	DirectedLinking dlRel = new DirectedLinking(model, true);
//				    	
//						// create a connector and add default color
//						GraphicObject connector = new GraphicObject(model, true);
//	
//						// check for sub-mappings and modify the connector accordingly (-> generalize!)
//						checkForSubmappingsAndApplyToConnector(p2go2orm,statement,connector);
//						
//						// configure the relation
//						dlRel.setStartnode(startNode);
//						dlRel.setEndnode(endNode);
//						dlRel.setLinkingconnector(connector);
//						startNode.setLinkedto(dlRel);
//						endNode.setLinkedfrom(dlRel);	
//					}
//				}
//			}
//
//		} catch (InsufficientMappingSpecificationExecption e) {
//			LOGGER.warning(e.getMessage());
//			LOGGER.warning("--> No resources will be affected by mapping " + pm );
//			//e.printStackTrace();
//		}
//	}

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
			
			//connector.setColornamed(new org.purl.rvl.java.viso.graphic.Color(model, "http://purl.org/viso/graphic/Yellow", true));

			PropertyToGraphicAttributeMapping p2gam = (PropertyToGraphicAttributeMapping)subMapping.castTo(PropertyToGraphicAttributeMapping.class);
			
			// get the subproperties as subjects of the new mapping --> do this in the calculation of value mappings instead
	
			if(p2gam.hasValuemapping()) {
			
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
				Color color = Color.getInstance(model, colorNode.asResource());
				LOGGER.fine("submapping: selected color for predicate (" + predicate + "): " + color.toString());
				connector.setColornamed(color);
				
			}
			
			/*
	    	Node sv = null;
		    
		    // get the (first) source value of the resource for the mapped property
		    ClosableIterator<Statement> resSpStmtIt = model.findStatements(resource, sp.asURI(), Variable.ANY);
			while (resSpStmtIt.hasNext()) {
				Statement statement = (Statement) resSpStmtIt.next();
				sv = statement.getObject();
				//LOGGER.info(sv);
			}
			
			
						
			// get the target value for the sv
	    	Node tv = svUriTVuriMap.get(sv);
	    	
	    	if(tv!=null) {
		    	// if we are mapping to named colors
			    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/color_named")) {
			    	Color color = Color.getInstance(model, tv.asURI());
			    	go.setColornamed(color);
			    	//LOGGER.info("set color to " + color + " for sv " + sv);
			    }
			    
		    	// if we are mapping to named shapes
			    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/shape_named")) {
			    	Shape shape = ShapeX.getInstance(model, tv.asURI());
			    	go.setShapenamed(shape);
			    	//LOGGER.info("set shape to " + shape + " for sv " + sv + NL);
			    }
	    	}
	    	
	    	*/
		}
		if(smr.hasOntriplepart()) {
			label += " ... on triple part: " + smr.getAllOntriplepart_as().firstValue() ;
		}

		connector.setLabel("Connector with an applied submapping: " + label);
		
		//
		//connector.setColorhslhue(new Float(155));
		//connector.setColorhslsaturation(new Float(100));
		//connector.setColorhsllightness(new Float(50));
	}
	
	
	
	
	/**
	 * Interprets the simple P2GA mappings, i.e. those without need for calculating value mappings. 
	 * Creates GO for all affected resources if they don't exist already.
	 */
	protected void interpretNormalP2GArvlMappings() {

		Set<PropertyToGraphicAttributeMapping> setOfP2GAMappings = getAllP2GAMappingsWithNoExplicitMappings();
		
		// for each normal P2GA mapping
		for (Iterator<PropertyToGraphicAttributeMapping> iterator = setOfP2GAMappings
				.iterator(); iterator.hasNext();) {
			
			PropertyToGraphicAttributeMapping p2gam = (PropertyToGraphicAttributeMapping) iterator.next();
			
			if (p2gam.isDisabled()) {
				LOGGER.info("Ignored disabled normal P2GAM mapping " + p2gam.asURI() );
				continue;
			}

			LOGGER.info("Interpret P2GAM mapping " + p2gam.asURI() );
			

			
			PropertyMapping pm = (PropertyMapping) p2gam.castTo(PropertyMapping.class);
			try {
				Set<org.ontoware.rdf2go.model.node.Resource> subjectSet;
				subjectSet = pm.getAffectedResources();
				Property sp = pm.getSourceProperty();
				GraphicAttribute tga = p2gam.getTargetAttribute();
				
				// for each affected resource
				for (Iterator<org.ontoware.rdf2go.model.node.Resource> iterator2 = subjectSet.iterator(); iterator2.hasNext();) {
					org.ontoware.rdf2go.model.node.Resource resource = iterator2.next().asResource(); // strange: unlike in the toString() method of PM, we cannot simply cast to resource here, only to URI!
					//LOGGER.info("affects: " + resource +  NL);
					

				    

				    // get a statement set here instead
				    //Set<Statement> statementSet = RVLUtils.findStatementsOnInstanceOrClassLevel(model, p2gam); // TODO here subject is not constrained!! won't work
				    Set<Statement> theStatementWithOurObject = RVLUtils.findRelationsOnInstanceOrClassLevel(model, p2gam, resource, null); // TODO here subject is not constrained!! won't work
				    
				    
					// get the mapping table SV->TV // TODO: this should only be done once!!!!s
					Map<Node, Node> svUriTVuriMap = p2gam.getCalculatedValues(theStatementWithOurObject);	
				    
					
					// create a GO for each affected resource // TODO: problem subjectFilter is ignored, since GO will be created already now!! 
				    GraphicObject go = createOrGetGraphicObject(resource);

				    
				    // for all statements check whether there is a tv for the sv
				    for (Iterator<Statement> stmtSetIt = theStatementWithOurObject.iterator(); stmtSetIt
							.hasNext();) {
				    	
				    	Statement statement = (Statement) stmtSetIt.next();
				    	Node sv = statement.getObject(); // useless! will set sv many times

						LOGGER.finest("trying to find and apply value mapping for sv " + sv.toString());
						
						// get the target value for the sv
				    	Node tv = svUriTVuriMap.get(sv);
				    
					    /*
						while (resSpStmtIt.hasNext()) {
							Statement statement = (Statement) resSpStmtIt.next();
							sv = statement.getObject();
							//LOGGER.info(sv);
						}*/
									

				    	
				    	if(tv!=null) {
				    		
							LOGGER.finest("found tv " + tv + " for sv " + sv);
				    		
					    	// if we are mapping to named colors
						    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/color_named")) {
						    	Color color = Color.getInstance(model, tv.asURI());
						    	go.setColornamed(color);
						    	//LOGGER.info("set color to " + color + " for sv " + sv);
						    }
						    
					    	// if we are mapping to lightness
						    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/color_hsl_lightness")) {
						    	go.setColorhsllightness(tv);
						    	LOGGER.finest("set color hsl lightness to " + tv.toString() + " for sv " + sv);
						    }
						    
					    	// if we are mapping to named shapes
						    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/shape_named")) {
						    	Shape shape = ShapeX.getInstance(model, tv.asURI());
						    	go.setShapenamed(shape);
						    	//LOGGER.finest("set shape to " + shape + " for sv " + sv + NL);
						    }
				    	}	
					}
				}
			} catch (InsufficientMappingSpecificationExecption e) {
				LOGGER.warning("No resources will be affected by mapping " + pm.asURI() + " (" + e.getMessage() + ")" );
			} 
			
		}
	}
	
	
	
	

	/**
	 * Interprets the simple P2GA mappings, i.e. those without need for calculating value mappings. 
	 * Creates GO for all affected resources if they don't exist already.
	 */
	protected void interpretSimpleP2GArvlMappings() {
		
		Set<PropertyToGraphicAttributeMapping> setOfSimpleP2GAMappings = getAllP2GAMappingsWithExplicitMappings();
		
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
			
			PropertyMapping pm = (PropertyMapping) p2gam.castTo(PropertyMapping.class);
			try {
				Set<org.ontoware.rdf2go.model.node.Resource> subjectSet;
				subjectSet = pm.getAffectedResources();
				Property sp = pm.getSourceProperty();
				GraphicAttribute tga = p2gam.getTargetAttribute();
				
				// for each affected resource
				for (Iterator<org.ontoware.rdf2go.model.node.Resource> iterator2 = subjectSet.iterator(); iterator2.hasNext();) {
					org.ontoware.rdf2go.model.node.Resource resource = iterator2.next().asResource(); // strange: unlike in the toString() method of PM, we cannot simply cast to resource here, only to URI!
					//LOGGER.info("affects: " + resource +  NL);
					
					// create a GO for each affected resource
				    GraphicObject go = createOrGetGraphicObject(resource);
			    	Node sv = null;
				    

				    // get a statement set here instead
				    //Set<Statement> statementSet = RVLUtils.findStatementsOnInstanceOrClassLevel(model, p2gam); // TODO here subject is not constrained!! won't work
				    Set<Statement> theStatementWithOurObject = RVLUtils.findRelationsOnInstanceOrClassLevel(model, p2gam, resource, null); // TODO here subject is not constrained!! won't work
				    
				    for (Iterator<Statement> stmtSetIt = theStatementWithOurObject.iterator(); stmtSetIt
							.hasNext();) {
						Statement statement = (Statement) stmtSetIt.next();
						sv = statement.getObject(); // useless! will set sv many times
					}
				    
				    /*
					while (resSpStmtIt.hasNext()) {
						Statement statement = (Statement) resSpStmtIt.next();
						sv = statement.getObject();
						//LOGGER.info(sv);
					}*/
								
					// get the target value for the sv
			    	Node tv = svUriTVuriMap.get(sv);
			    	
			    	if(tv!=null) {
				    	// if we are mapping to named colors
					    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/color_named")) {
					    	Color color = Color.getInstance(model, tv.asURI());
					    	go.setColornamed(color);
					    	//LOGGER.info("set color to " + color + " for sv " + sv);
					    }
					    
				    	// if we are mapping to named shapes
					    if(tga.asURI().toString().equals("http://purl.org/viso/graphic/shape_named")) {
					    	Shape shape = ShapeX.getInstance(model, tv.asURI());
					    	go.setShapenamed(shape);
					    	LOGGER.finest("set shape to " + shape + " for sv " + sv + NL);
					    }
			    	}
				}
			} catch (InsufficientMappingSpecificationExecption e) {
				LOGGER.warning("No resources will be affected by mapping " + pm.asURI() + " (" + e.getMessage() + ")" );
			} 
			
		}
	}
	
	
	
	

}
