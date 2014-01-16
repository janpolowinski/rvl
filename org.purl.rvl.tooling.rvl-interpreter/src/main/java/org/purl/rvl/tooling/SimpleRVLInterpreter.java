package org.purl.rvl.tooling;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.purl.rvl.java.exception.InsufficientMappingSpecificationExecption;
import org.purl.rvl.java.gen.rvl.GraphicAttribute;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;
import org.purl.rvl.java.gen.viso.graphic.Color;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.gen.viso.graphic.Shape;
import org.purl.rvl.java.gen.viso.graphic.Thing1;
import org.purl.rvl.java.rvl.PropertyMapping;
import org.purl.rvl.java.rvl.PropertyToGO2ORMapping;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping;
import org.purl.rvl.java.viso.graphic.GraphicObject;
import org.purl.rvl.java.viso.graphic.GraphicSpace;
import org.purl.rvl.java.viso.graphic.ShapeX;

import org.json.simple.JSONObject;

public class SimpleRVLInterpreter {
	
	protected Model model;
	private Model modelVISO;
	private Random random;
	private Map<org.ontoware.rdf2go.model.node.Resource,GraphicObject> resourceGraphicObjectMap; 
	
	private final static Logger LOGGER = Logger.getLogger(SimpleRVLInterpreter.class .getName()); 
	static final String NL =  System.getProperty("line.separator");

	public SimpleRVLInterpreter(Model model, Model modelVISO) {
		super();
		this.model = model;
		this.modelVISO = modelVISO;
		this.random = new Random();
		this.resourceGraphicObjectMap = new HashMap<org.ontoware.rdf2go.model.node.Resource, GraphicObject>();
	}
	
	/**
	 * Interprets the P2GO2OR mappings.
	 * Creates new GO for all affected resources.
	 * TODO: merge/reuse GOs
	 */
	public void interpretP2GO2ORMappings() {
		
		// get all P2GO2OR mappings to linking and create n-ary linking relations
		Set<PropertyToGO2ORMapping> setOfMappingsToLinking = getAllMappingsToLinking();
		
		// for each mapping
		for (Iterator<PropertyToGO2ORMapping> iterator = setOfMappingsToLinking
				.iterator(); iterator.hasNext();) {
			PropertyToGO2ORMapping p2go2orm = (PropertyToGO2ORMapping) iterator.next();
			LOGGER.fine("Interpreting the mapping: " + NL + 
					     p2go2orm.toString());
			
			PropertyMapping pm = (PropertyMapping) p2go2orm.castTo(PropertyMapping.class);
			Set<org.ontoware.rdf2go.model.node.Resource> subjectSet;
			try {
				subjectSet = pm.getAffectedResources();
				Property sp = pm.getAllSourceproperty_as().firstValue();
				
				// for each affected resource
				for (Iterator<org.ontoware.rdf2go.model.node.Resource> iterator2 = subjectSet.iterator(); iterator2.hasNext();) {
					
					org.ontoware.rdf2go.model.node.Resource resource = iterator2.next().asResource(); // strange: unlike in the toString() method of PM, we cannot simply cast to resource here, only to URI!

					// create a startNode GO for each affected resource or get the GO, if it already exists
				    //GraphicObject startNode = new GraphicObject(model,"http://purl.org/rvl/example-avm/GO_" + random.nextInt(), true);
				    GraphicObject startNode = createOrGetGraphicObject(resource);

					// Get the (first) object and create an endNode GO for the object
			    	Node object = null;
				    ClosableIterator<Statement> resSpStmtIt = model.findStatements(resource, sp.asURI(), Variable.ANY);
					while (resSpStmtIt.hasNext()) {
						Statement statement = (Statement) resSpStmtIt.next();
						object = statement.getObject();
						LOGGER.finest("Creating endNode for: " + //NL + 
								     object.toString());
					}
			    	GraphicObject endNode = createOrGetGraphicObject((org.ontoware.rdf2go.model.node.Resource)object);
			    	
			    	// create the linking relation
			    	DirectedLinking dlRel = new DirectedLinking(model, true);
			    	
					// create a connector and add exemplary color
					GraphicObject connector = new GraphicObject(model, true);
					connector.setColornamed(new org.purl.rvl.java.viso.graphic.Color(model, "http://purl.org/viso/graphic/Red", true));
					//connector.setColorhslhue(new Float(155));
					//connector.setColorhslsaturation(new Float(100));
					//connector.setColorhsllightness(new Float(50));
					connector.setLabel("only a connector, should be filtered out");
					
					// configure the relation
					dlRel.setStartnode(startNode);
					dlRel.setEndnode(endNode);
					dlRel.setLinkingconnector(connector);
					startNode.setLinkedto(dlRel);
					endNode.setLinkedfrom(dlRel);
				}
	
			} catch (InsufficientMappingSpecificationExecption e) {
				LOGGER.warning(e.getMessage());
				LOGGER.warning("--> No resources will be affected by mapping " + pm );
				//e.printStackTrace();
			}
	
		}
		
		LOGGER.fine("The size of the Resource-to-GraphicObject map is " + resourceGraphicObjectMap.size()+".");
		
		
	}

	/**
	 * Creates a GraphicObject for a Resource or returns the existing GraphicObject, if already created before
	 * @param resource
	 * @return the GraphicObject representing the resource
	 */
	private GraphicObject createOrGetGraphicObject(org.ontoware.rdf2go.model.node.Resource resource) {
		if (resourceGraphicObjectMap.containsKey(resource)) {
			LOGGER.finest("found existing GO for " + resource);
			return resourceGraphicObjectMap.get(resource);
		} 
		else {
			GraphicObject go = new GraphicObject(model,"http://purl.org/rvl/example-avm/GO_" + random.nextInt(), true);
			resourceGraphicObjectMap.put(resource, go);
			LOGGER.finer("Newly created GO for " + resource);
			return go;
		}
	}



	/**
	 * Interprets the simple P2GA mappings, i.e. those without need for calculating value mappings. 
	 * Creates GO for all affected resources.
	 */
	public void interpretSimpleP2GArvlMappings() {
		
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
	
	public void interpretResourceLabelAsGOLabelForAllCreatedResources(){
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
	
	private void performDefaultLabelMapping(GraphicObject go,
			org.ontoware.rdf2go.model.node.Resource resource) {
		// Thing OK? What is domain of rdfs:label? rdfreactor.Resource does not work
		Resource r = Thing1.getInstance(model, resource);
		go.setLabel(r.getAllLabel_as().firstValue());
	}

	/**
	 * Get all the mappings that require no calculation, because they only have explicit 1-1-value-mappings
	 */
	private Set<PropertyToGraphicAttributeMapping> getAllP2GAMappingsWithExplicitMappings(){
		
		Set<PropertyToGraphicAttributeMapping> mappingSet = new HashSet<PropertyToGraphicAttributeMapping>();

		String queryString = "" +
				"SELECT DISTINCT ?p2gam " +
				"WHERE { " +
				"    ?p2gam a <" + PropertyToGraphicAttributeMapping.RDFS_CLASS + "> . " +
				"    ?p2gam <" + PropertyToGraphicAttributeMapping.VALUEMAPPING + "> ?vm . " +
				"	{ " +
				"	SELECT ?vm  (COUNT(?sv) AS ?svCount) " +
				"       WHERE " +
				"       { " +
				"	 		  ?vm <" + PropertyToGraphicAttributeMapping.SOURCEVALUE + "> ?sv  " +
				"       } " +
				"        GROUP BY ?vm " +
				"	} " +
				"    FILTER (?svCount = 1 ) " +
				"} " ;
		
		//LOGGER.info("All mappings with explicit value mappings (VMs with only 1 source value)");
		//LOGGER.info(queryString);
		
		QueryResultTable results = model.sparqlSelect(queryString);
//		for(QueryRow row : results) {LOGGER.info(row); }
//		for(String var : results.getVariables()) { LOGGER.info(var); }
		
		for(QueryRow row : results) {
			Property_to_Graphic_AttributeMapping p2gam = Property_to_Graphic_AttributeMapping.getInstance(model, (URI)row.getValue("p2gam"));
			mappingSet.add((PropertyToGraphicAttributeMapping)p2gam.castTo(PropertyToGraphicAttributeMapping.class));
			//LOGGER.info(row.getValue("p2gam"));
		}
		
		return mappingSet;
	}
	
	private Set<PropertyToGO2ORMapping> getAllMappingsToLinking() {
		
		Set<PropertyToGO2ORMapping> mappingSet = new HashSet<PropertyToGO2ORMapping>();

		String queryString = "" +
				"SELECT DISTINCT ?mapping " +
				"WHERE { " +
				"    ?mapping a <" + PropertyToGO2ORMapping.RDFS_CLASS + "> . " +
				"    ?mapping <" + PropertyToGO2ORMapping.TARGETOBJECT_TO_OBJECTRELATION + "> <" + DirectedLinking.RDFS_CLASS + "> . " +
				"} " ;
		
		LOGGER.finer("SPARQL: query all mappings to Directed Linking:" + NL + 
				     queryString);
		
		QueryResultTable results = model.sparqlSelect(queryString);
		//for(QueryRow row : results) {LOGGER.info(row); }
		//for(String var : results.getVariables()) { LOGGER.info(var); }
		
		for(QueryRow row : results) {
			Property_to_Graphic_Object_to_Object_RelationMapping mapping = Property_to_Graphic_Object_to_Object_RelationMapping.getInstance(model, (URI)row.getValue("mapping"));
			mappingSet.add((PropertyToGO2ORMapping)mapping.castTo(PropertyToGO2ORMapping.class));
			//LOGGER.fine(row.getValue("mapping"));
		}
		
		return mappingSet;
	}

}
