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
	public void interpretMappings() {
		super.interpretMappings();
		LOGGER.info("Interpreting mappings using " + this.getClass().getName());
		interpretSimpleP2GArvlMappings();
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
			interpretMappingToLinking(p2go2orm);
			//interpretClassLevelRelations(p2go2orm);
	
		}
		
		LOGGER.fine("The size of the Resource-to-GraphicObject map is " + resourceGraphicObjectMap.size()+".");
	}

	
	protected void interpretClassLevelRelations(PropertyToGO2ORMapping p2go2orm) {
		
		Property sp = p2go2orm.getAllSourceproperty_as().firstValue();
		boolean invertSourceProperty = p2go2orm.hasInvertsourceproperty();
		
		//RVLUtils.findRelationsOnClassLevel(model, subjectResource, sp.asURI());
		Set<Statement> stmtSet =  RVLUtils.findRelationsOnClassLevel(model, sp.asURI());
		
		
		for (Iterator<Statement> iterator = stmtSet.iterator(); iterator.hasNext();) {
			Statement statement = (Statement) iterator.next();
						
			try {
			
				org.ontoware.rdf2go.model.node.Resource subject = statement.getSubject();
				org.ontoware.rdf2go.model.node.Resource object = statement.getObject().asResource();
				
				LOGGER.finest("Subject label " + AVMUtils.getLocalName(model,subject));
				LOGGER.finest("Object label " + AVMUtils.getLocalName(model,object));
	
				LOGGER.fine("Statement to be mapped : " + statement);

				// For each statement, create a startNode GO representing the subject (if not exists)
			    GraphicObject subjectNode = createOrGetGraphicObject(subject);
		    	LOGGER.finest("Assigned startNode for: " + subject.toString());
				
				// For each statement, create an endNode GO representing the object (if not exists)
		    	//Node object = statement.getObject();
				
				GraphicObject objectNode = createOrGetGraphicObject((org.ontoware.rdf2go.model.node.Resource)object);
		    	LOGGER.finest("Assigned endNode for: " + object.toString());
		    	
		    	// create the linking relation
		    	DirectedLinking dlRel = new DirectedLinking(model, true);
		    	
				// create a connector and add default color
				GraphicObject connector = new GraphicObject(model, true);

				// check for sub-mappings and modify the connector accordingly (-> generalize!)
				//checkForSubmappingsAndApplyToConnector(pm,statement,connector);
				
				
				// configure the relation
				if(invertSourceProperty) {
					LOGGER.info("The 'inverse' of the source property (" + sp.asURI() + ") will be used, according to mapping settings.");
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
				LOGGER.finest("Problem creating GOS");
				LOGGER.finest(e.getMessage());
			}
		}
		
	}

	protected void interpretMappingToLinking(PropertyToGO2ORMapping p2go2orm) {
		
		PropertyMapping pm = (PropertyMapping) p2go2orm.castTo(PropertyMapping.class);
		LOGGER.fine("Interpreting the mapping: " + NL + p2go2orm.toString());
		
		try {
			// get the subjects affected by the mapping // TODO: here we could also work with statements using the source property directly?!
			Set<org.ontoware.rdf2go.model.node.Resource> subjectSet = pm.getAffectedResources();
			Property sp = pm.getAllSourceproperty_as().firstValue();
			
			// for each affected resource // TODO do we need the effected resources here at all, or can we directly work on statements?
			for (Iterator<org.ontoware.rdf2go.model.node.Resource> iterator2 = subjectSet.iterator(); iterator2.hasNext();) {
				
				org.ontoware.rdf2go.model.node.Resource subjectResource = iterator2.next().asResource(); // strange: unlike in the toString() method of PM, we cannot simply cast to resource here, only to URI!
		    	
				// temp ignore blank nodes
				boolean nodeIsBlankNode = true;
				try {
					subjectResource.asURI();
					nodeIsBlankNode = false;
				} catch (ClassCastException e) {
					LOGGER.finer("ignoring blank node when creating linking relations: " + subjectResource);
				}
				if (!nodeIsBlankNode) {
				
			    	// problem: while iterating a statement iterator no instances may be created 
			    	// --> concurrend modification exception
			    	// solution: first create a set of statements, then iterate
			    	Set<Statement> resSpStmtSet = new HashSet<Statement>();
			    	//ClosableIterator<Statement> resSpStmtIt = model.findStatements(subjectResource, sp.asURI(), Variable.ANY); // will ignore statements using a subproperty of sp
			    	Iterator<Statement> resSpStmtIt = RVLUtils.findStatementsPreferingThoseUsingASubProperty(model, subjectResource, sp.asURI(), Variable.ANY); // should include statements using a subproperty of sp
					while (resSpStmtIt.hasNext()) {
						Statement statement = (Statement) resSpStmtIt.next();
						resSpStmtSet.add(statement);
					}
					
					for (Iterator<Statement> resSpStmtSetIt = resSpStmtSet.iterator(); resSpStmtSetIt
							.hasNext();) {
						Statement statement = (Statement) resSpStmtSetIt.next();
						LOGGER.fine("Statement to be mapped : " + statement);
	
						// For each statement, create a startNode GO representing the subject (if not exists)
					    GraphicObject startNode = createOrGetGraphicObject(subjectResource);
				    	LOGGER.finest("Assigned startNode for: " + subjectResource.toString());
						
						// For each statement, create an endNode GO representing the object (if not exists)
				    	Node object = statement.getObject();
						
						GraphicObject endNode = createOrGetGraphicObject((org.ontoware.rdf2go.model.node.Resource)object);
				    	LOGGER.finest("Assigned endNode for: " + object.toString());
				    	
				    	// create the linking relation
				    	DirectedLinking dlRel = new DirectedLinking(model, true);
				    	
						// create a connector and add default color
						GraphicObject connector = new GraphicObject(model, true);
	
						// check for sub-mappings and modify the connector accordingly (-> generalize!)
						checkForSubmappingsAndApplyToConnector(pm,statement,connector);
						
						// configure the relation
						dlRel.setStartnode(startNode);
						dlRel.setEndnode(endNode);
						dlRel.setLinkingconnector(connector);
						startNode.setLinkedto(dlRel);
						endNode.setLinkedfrom(dlRel);	
					}
				}
			}

		} catch (InsufficientMappingSpecificationExecption e) {
			LOGGER.warning(e.getMessage());
			LOGGER.warning("--> No resources will be affected by mapping " + pm );
			//e.printStackTrace();
		}
	}

	protected void checkForSubmappingsAndApplyToConnector(PropertyMapping pm, Statement mainStatement, GraphicObject connector) {
		
		String label = "";
		
		if(pm.hasSub_mapping()){
			Sub_mappingrelation smr = pm.getAllSub_mapping_as().firstValue();

			if(smr.hasSub_mapping() && smr.hasOnrole()){
				Mapping subMapping = smr.getAllSub_mapping_as().firstValue();
				
				label += " ... to mapping: " + subMapping ; // wrong return type and wrong methode name, but seems to work
				label += " ... on role: " + smr.getAllOnrole_as().firstValue() ;
				connector.setColornamed(new org.purl.rvl.java.viso.graphic.Color(model, "http://purl.org/viso/graphic/Yellow", true));
				
				
				label += RVLUtils.mappingToStringAsSpecificAsPossible((org.purl.rvl.java.rvl.Mapping)subMapping.castTo(org.purl.rvl.java.rvl.Mapping.class)) + NL ;

				PropertyToGraphicAttributeMapping p2gam = (PropertyToGraphicAttributeMapping)subMapping.castTo(PropertyToGraphicAttributeMapping.class);
				
				// get the subproperties as subjects of the new mapping --> do this in the calculation of value mappings instead
		
				if(p2gam.hasValuemapping()) {
				
					Map<Node, Node> svUriTVuriMap = p2gam.getExplicitlyMappedValues();	
					
					if(!svUriTVuriMap.isEmpty()){
						
						LOGGER.info("map of sv and tv for submapping:");
						
						for (Entry<Node, Node> entry : svUriTVuriMap.entrySet()) {
							Node sv = entry.getKey();
							Node tv = entry.getValue();
							label += "source and target values: "+sv+" --> "+tv+"" + NL;
							LOGGER.info(sv+" --> "+tv);
						}
						
					}
					///Node triplePartValue = ...
					
					//Node property = (Node) model.getProperty(new URIImpl("http://purl.org/rvl/example-data/cites"));
					
					URI predicate = mainStatement.getPredicate();
					Node colorNode = svUriTVuriMap.get(predicate);
					Color color = Color.getInstance(model, colorNode.asResource());
					LOGGER.info("submapping: selected color for predicate (" + predicate + "): " + color.toString());
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
	protected void interpretSimpleP2GArvlMappings() {
		
		if (null==model) {
			LOGGER.severe("Cannot interprete mappings, since model is null.");
			return;
		}
		
		Set<PropertyToGraphicAttributeMapping> setOfSimpleP2GAMappings = getAllP2GAMappingsWithExplicitMappings();
		
		// for each simple mapping
		for (Iterator<PropertyToGraphicAttributeMapping> iterator = setOfSimpleP2GAMappings
				.iterator(); iterator.hasNext();) {
			PropertyToGraphicAttributeMapping p2gam = (PropertyToGraphicAttributeMapping) iterator.next();
			//LOGGER.info(p2gam);
			
			// get the mapping table SV->TV
			Map<Node, Node> svUriTVuriMap = p2gam.getExplicitlyMappedValues();	
			
			PropertyMapping pm = (PropertyMapping) p2gam.castTo(PropertyMapping.class);
			try {
				Set<org.ontoware.rdf2go.model.node.Resource> subjectSet;
				subjectSet = pm.getAffectedResources();
				Property sp = pm.getAllSourceproperty_as().firstValue();
				GraphicAttribute tga = p2gam.getAllTargetattribute_as().firstValue();
				
				// for each affected resource
				for (Iterator<org.ontoware.rdf2go.model.node.Resource> iterator2 = subjectSet.iterator(); iterator2.hasNext();) {
					org.ontoware.rdf2go.model.node.Resource resource = iterator2.next().asResource(); // strange: unlike in the toString() method of PM, we cannot simply cast to resource here, only to URI!
					//LOGGER.info("affects: " + resource +  NL);
					
					// create a GO for each affected resource
				    GraphicObject go = createOrGetGraphicObject(resource);
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
				}
			} catch (InsufficientMappingSpecificationExecption e) {
				LOGGER.warning(e.getMessage());
				LOGGER.warning("--> No resources will be affected by mapping " + pm );
				//e.printStackTrace();	
			} 
			
		}
	}
	
	/**
	 * Iterates through all GOs in the GO map and performs a default label mapping on them
	 */
	protected void interpretResourceLabelAsGOLabelForAllCreatedResources(){
		for (Map.Entry<org.ontoware.rdf2go.model.node.Resource,GraphicObject> entry : resourceGraphicObjectMap.entrySet()) {
			//LOGGER.info(entry.getKey() + " with value " + entry.getValue());
			// perform the default label mapping, when not already set
		    // TODO this is simply using rdfs:label of the GOs now, not the n-ary graphic labeling!
		    // only rdfreactor resources have labels ...
			GraphicObject go = entry.getValue();
			org.ontoware.rdf2go.model.node.Resource resource = entry.getKey();
			if(!go.hasLabels()) {
				performDefaultLabelMapping(go,resource);
			}
		}
	}
	
	/**
	 * Sets the label of a GO to the resources (first) label
	 * @param go
	 * @param resource
	 */
	private void performDefaultLabelMapping(GraphicObject go,
			org.ontoware.rdf2go.model.node.Resource resource) {
		
		//LOGGER.finest("Problems getting represented resource, no label generated for GO " + this.asURI());

		try {
			go.setLabel(AVMUtils.getOrGenerateDefaultLabelString(model, resource));
		} catch (Exception e) {
			LOGGER.finest("No label could be assigned for resource " + resource + " to GO " + go.asURI().toString() + e.getMessage());
			e.printStackTrace();
		}
	}

}
