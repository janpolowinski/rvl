package org.purl.rvl.tooling.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Sparqlable;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.java.gen.rvl.Thing1;
import org.purl.rvl.tooling.process.ResourcesCache;
import org.purl.rvl.tooling.query.data.DataQuery;

/**
 * @author Jan Polowinski
 *
 */
public class RVLUtils {
	
	final static Logger LOGGER = Logger.getLogger(RVLUtils.class.getName()); 
	
	
	
	/*public static Set<Statement> findRelationsOnInstanceOrClassLevel(
			Model model,
			URI fromGraph,
			PropertyMappingX pm
			) throws InsufficientMappingSpecificationException {
		
		return findRelationsOnInstanceOrClassLevel(model, fromGraph, pm, false, null, null);
		
	}*/
	
	
	public static List<Node> rdfs2JavaList(org.ontoware.rdfreactor.schema.rdfs.List rdfsList) {
		
		List<Node> javaList = rdfs2InvertedJavaList(rdfsList);
		Collections.reverse(javaList);
		
		return javaList ;
		
	}
	
	private static List<Node> rdfs2InvertedJavaList(org.ontoware.rdfreactor.schema.rdfs.List rdfsList) {
		
		List<Node> javaList = null;
		
		Node headRdfsList = rdfsList.getAllFirst_asNode_().firstValue();
		org.ontoware.rdfreactor.schema.rdfs.List restRdfsList = rdfsList.getAllRest_as().firstValue();
		
		if (headRdfsList == null) {
			javaList = new LinkedList<Node>();
		}
		else {
			javaList = rdfs2InvertedJavaList(restRdfsList);
			javaList.add(headRdfsList);
		}
		
//		LOGGER.finest("Java list of nodes: " + javaList);
		return javaList;
		
	}
	
	public static <T extends Thing1> T tryReplaceWithCashedInstanceForSameURI(Thing1 instance, Class<T> clasz) {
		
		T castedInstance = (T) instance.castTo(clasz);
		
		return (T) ResourcesCache.getInstance().tryReplaceOrCache(castedInstance);
	}
	
	public static <T extends org.purl.rvl.java.gen.viso.graphic.Thing1> T tryReplaceWithCashedInstanceForSameURI_for_VISO_Resources(org.purl.rvl.java.gen.viso.graphic.Thing1 instance, Class<T> clasz) {
		
		T castedInstance = (T) instance.castTo(clasz);
		
		return (T) ResourcesCache.getInstance().tryReplaceOrCache(castedInstance);
	}

	/**
	 * Extends a value mapping table to include source values related to the explicitly mapped source value 
	 * via the property 'property'.
	 * 
	 * @param modelOrModelSet
	 * @param explicitlyMappedValues
	 * @param property
	 * @return the extended value mapping map
	 */
	public static Map<Node, Node> extendMappingTable(Sparqlable modelOrModelSet,
			Map<Node, Node> explicitlyMappedValues, Property property) {
		
		// storing new implicitly mapped values
		Map<Resource, Node> implicitlyMappedValues = new HashMap<Resource, Node>();
		
		// iterate explicitly mapped values and store related values 
		// to the implicitly mapped values (using the same target value)
		Set<Entry<Node,Node>> explicitlyMappedValuesSet = explicitlyMappedValues.entrySet();
		
		for (Entry<Node, Node> mappedValuePair : explicitlyMappedValuesSet) {
			
			try {
			
				// TODO use getDirectlyRelatedResources  
				// to avoid assigning a more general value after a specific values has already be assigned
				// this needs recursion when only directly related resources are returned
				Set<Resource> directSubValues = DataQuery.getRelatedResources(modelOrModelSet, mappedValuePair.getKey().asResource(), property);
				//Set<Resource> directSubValues = DataQuery.getDirectlyRelatedResources(modelOrModelSet, mappedValuePair.getKey().asResource(), property);
				
				for (Resource directSubValue : directSubValues) {
					
					implicitlyMappedValues.put(directSubValue, mappedValuePair.getValue());
					
				}
				
			} catch (ClassCastException e ) {
				// not all key are resources, there may also be literals
			}
			
		}
		
		explicitlyMappedValues.putAll(implicitlyMappedValues);
		return explicitlyMappedValues;
		
	}

}













/**
 * Currently not in use! old code for getting statements directly with the
 * API without SPARQL. maybe reuse later, when performance should matter not
 * usable in most cases, since many filter and restrictions usually apply
 * for statement selection
 * 
 * @param modelOrModelSet
 * @param subject
 * @param object
 * @param spURI
 * @param classSelector
 * @return
 */
/*
protected static Collection<? extends Statement> findStatementsIncludingSubPropertyStatementsWithoutSPARQL(
		Sparqlable modelOrModelSet,
		org.ontoware.rdf2go.model.node.Resource subject,
		org.ontoware.rdf2go.model.node.Node object, URI spURI,
		org.ontoware.rdf2go.model.node.Resource classSelector) {
	
	Set<Statement> statementSet = new HashSet<Statement>();

	Model dataModel;
	try {
		ModelSet modelSet = (ModelSet) modelOrModelSet;
		dataModel = modelSet.getModel(OGVICProcess.GRAPH_DATA);
	} catch (Exception e) {
		LOGGER.severe("Could not get data model from modelOrModelSet, " +
				"will use modelOrModelSet as if is was the data graph (model)");
		dataModel = (Model) modelOrModelSet;
	}

	ClosableIterator<Statement> it = dataModel.findStatements(
			null == subject ? Variable.ANY : subject, spURI,
			null == object ? Variable.ANY : object);

	while (it.hasNext()) {

		Statement statement = it.next();

		// check starts with constraint (workaround) and subjectFilter
		if ((null == classSelector || RVLUtils.hasType(dataModel,
				statement.getSubject(), classSelector))) {
			statementSet.add(statement);
			LOGGER.finest("added Statement (matching subfilter): "
					+ statement.toString());
		} else {
			LOGGER.finest("skipped Statement (not matching subfilter): "
					+ statement.toString());
		}

	}
	
	return statementSet;
}*/

