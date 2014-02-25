package org.purl.rvl.tooling.rvl2avm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

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
import org.purl.rvl.java.gen.rvl.GraphicObjectToObjectRelation;
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

public abstract class RVLInterpreterBase {
	
	protected Model model;
	protected Model modelAVM;
	protected Map<org.ontoware.rdf2go.model.node.Resource,GraphicObject> resourceGraphicObjectMap; 
	protected Random random;
	
	protected OGVICProcess ogvicProcess = OGVICProcess.getInstance();

	
	private final static Logger LOGGER = Logger.getLogger(RVLInterpreterBase.class .getName()); 
	static final String NL =  System.getProperty("line.separator");

	public RVLInterpreterBase() {
		super();
	}
	

	public void init(Model model, Model modelAVM) {
		this.model = model;
		this.modelAVM = modelAVM;
		this.random = new Random();
		this.resourceGraphicObjectMap = new HashMap<org.ontoware.rdf2go.model.node.Resource, GraphicObject>();
	}


	/**
	 * Interpret all supported RVL mappings 
	 */
	public void interpretMappings() {
		LOGGER.info("Starting mapping interpretation ... ");
		LOGGER.info("Interpreting mappings using " + this.getClass().getName());
		interpretMappingsInternal();
	}

	abstract protected void interpretMappingsInternal();


	/**
	 * Creates a GraphicObject for a Resource or returns the existing GraphicObject, if already created before
	 * @param resource
	 * @return the GraphicObject representing the resource
	 */
	protected GraphicObject createOrGetGraphicObject(org.ontoware.rdf2go.model.node.Resource resource) {
		if (resourceGraphicObjectMap.containsKey(resource)) {
			LOGGER.finest("Found existing GO for " + resource);
			return resourceGraphicObjectMap.get(resource);
		} 
		else {
			GraphicObject go = new GraphicObject(modelAVM,"http://purl.org/rvl/example-avm/GO_" + random.nextInt(), true);
			go.setRepresents(resource);
			resourceGraphicObjectMap.put(resource, go);
			LOGGER.finer("Newly created GO for " + resource);
			return go;
		}
	}

	/**
	 * Get all the mappings that require no calculation, because they only have explicit 1-1-value-mappings
	 */
	protected Set<PropertyToGraphicAttributeMapping> getAllP2GAMappingsWithExplicitMappings(){
		
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
	
	
	/**
	 * Get all the mappings that require calculation, because they have not only explicit 1-1-value-mappings
	 */
	protected Set<PropertyToGraphicAttributeMapping> getAllP2GAMappingsWithSomeValueMappings(){
		
		Set<PropertyToGraphicAttributeMapping> mappingSet = new HashSet<PropertyToGraphicAttributeMapping>();

		String queryString = "" +
				"SELECT DISTINCT ?p2gam " +
				"WHERE { " +
				"    ?p2gam a <" + PropertyToGraphicAttributeMapping.RDFS_CLASS + "> . " +
				"    ?p2gam <" + PropertyToGraphicAttributeMapping.VALUEMAPPING + "> ?vm . " +
//				"	{ " +
//				"	SELECT ?vm  (COUNT(?sv) AS ?svCount) " +
//				"       WHERE " +
//				"       { " +
//				"	 		  ?vm <" + PropertyToGraphicAttributeMapping.SOURCEVALUE + "> ?sv  " +
//				"       } " +
//				"        GROUP BY ?vm " +
//				"	} " +
//				"    FILTER (!(?svCount = 1 )) " +
				"} " ;
		
		
		QueryResultTable results = model.sparqlSelect(queryString);
		for(QueryRow row : results) {
			Property_to_Graphic_AttributeMapping p2gam = Property_to_Graphic_AttributeMapping.getInstance(model, (URI)row.getValue("p2gam"));
			mappingSet.add((PropertyToGraphicAttributeMapping)p2gam.castTo(PropertyToGraphicAttributeMapping.class));
		}
		
		return mappingSet;
	}
	
	/*
	protected Set<PropertyToGO2ORMapping> getAllMappingsToLinking() {
		
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
			//LOGGER.info("Found mapping to linking: " + row.getValue("mapping").toString());
		}
		
		return mappingSet;
	}
	
	*/
	
	protected Set<PropertyToGO2ORMapping> getAllP2GOTORMappingsTo(URI gotor) {
		
		Set<PropertyToGO2ORMapping> mappingSet = new HashSet<PropertyToGO2ORMapping>();
		
		// constraining target GOTOR is optional
		String gotorString;
		if (null == gotor) {
			gotorString = " ?tgotor ";
		} else {
			gotorString = gotor.toSPARQL();
		}

		String queryString = "" +
				"SELECT DISTINCT ?mapping " +
				"WHERE { " +
				"    ?mapping a <" + PropertyToGO2ORMapping.RDFS_CLASS + "> . " +
				"    ?mapping " + PropertyToGO2ORMapping.TARGETOBJECT_TO_OBJECTRELATION.toSPARQL() + " " + gotorString + " . " +
				"} " ;
		
		LOGGER.finer("SPARQL: query all mappings to " + gotorString + ":" + NL + 
				     queryString);
		
		QueryResultTable results = model.sparqlSelect(queryString);
		//for(QueryRow row : results) {LOGGER.info(row); }
		//for(String var : results.getVariables()) { LOGGER.info(var); }
		
		for(QueryRow row : results) {
			Property_to_Graphic_Object_to_Object_RelationMapping mapping = Property_to_Graphic_Object_to_Object_RelationMapping.getInstance(model, (URI)row.getValue("mapping"));
			mappingSet.add((PropertyToGO2ORMapping)mapping.castTo(PropertyToGO2ORMapping.class));
			//LOGGER.info("Found mapping to linking: " + row.getValue("mapping").toString());
		}
		
		return mappingSet;
	}
	
	
	protected Set<PropertyToGO2ORMapping> getAllP2GOTORMappings() {
		
		return getAllP2GOTORMappingsTo(null);
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
