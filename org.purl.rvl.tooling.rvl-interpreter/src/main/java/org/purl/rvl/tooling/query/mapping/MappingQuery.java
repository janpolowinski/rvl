package org.purl.rvl.tooling.query.mapping;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.exception.MalformedQueryException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.java.gen.rvl.Identitymapping;
import org.purl.rvl.java.gen.rvl.Mapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;
import org.purl.rvl.java.rvl.IdentityMappingX;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;
import org.purl.rvl.java.rvl.ValueMappingX;
import org.purl.rvl.tooling.model.ModelManager;

/**
 * @author Jan Polowinski
 *
 */
public class MappingQuery {

	final static Logger LOGGER = Logger.getLogger(MappingQuery.class.getName());
	static final String NL =  System.getProperty("line.separator");

	
		/**
		 * TODO: cannot handle blank nodes!
		 * Check if a mapping has a value mapping that defines exactly 1 source value 
		 */
		public static boolean askIfHasExplicitValueMapping(Model modelMappings, PropertyToGraphicAttributeMappingX mapping) {
	
			String queryString = "" + NL + 
					"ASK " + NL + 
					"WHERE { " + NL + 
					"    " + mapping.asURI().toSPARQL() + " " + Property_to_Graphic_AttributeMapping.VALUEMAPPING.toSPARQL() + "  ?vm . " + NL + 
					"	{ " + NL + 
					"	SELECT ?vm  (COUNT(?sv) AS ?svCount) " + NL + 
					"       WHERE " + NL + 
					"       { " + NL + 
					"	 		  ?vm <" + ValueMappingX.SOURCEVALUE + "> ?sv  " + NL + 
					"       } " + NL + 
					"        GROUP BY ?vm " + NL + 
					"	} " + NL + 
					"    FILTER (?svCount = 1 ) " + NL + 
					"} " + NL ;
	
			return modelMappings.sparqlAsk(queryString);
		}
	
	
		/**
		 * Get all the mappings that require no calculation, 
		 * because they only have explicit 1-1-value-mappings
		 */
		public static Set<PropertyToGraphicAttributeMappingX> getAllP2GAMappingsWithExplicitMappings(Model modelMappings){
	
			String queryString = "" + NL + 
					"SELECT DISTINCT ?mapping " + NL + 
					"WHERE { " + NL + 
					"    ?mapping a <" + Property_to_Graphic_AttributeMapping.RDFS_CLASS + "> . " + NL + 
					"    ?mapping <" + Property_to_Graphic_AttributeMapping.VALUEMAPPING + "> ?vm . " + NL + 
					"	{ " + NL + 
					"	SELECT ?vm  (COUNT(?sv) AS ?svCount) " + NL + 
					"       WHERE " + NL + 
					"       { " + NL + 
					"	 		  ?vm <" + ValueMappingX.SOURCEVALUE + "> ?sv  " + NL + 
					"       } " + NL + 
					"        GROUP BY ?vm " + NL + 
					"	} " + NL + 
					"    FILTER (?svCount = 1 ) " + NL + 
					"} " + NL ;

			return getP2GAMappings(modelMappings, queryString);
		}

		/**
		 * Get all PropertyToGraphicAttributeMappings that have at least one value mapping.
		 * 
		 * @param mappingModel
		 * @return a Set of PropertyToGraphicAttributeMappingX
		 */
		public static Set<PropertyToGraphicAttributeMappingX> getP2GAMappingsWithAtLeastOneValueMapping(Model mappingModel){
	
			String queryString = "" +
					"SELECT DISTINCT ?mapping " + NL + 
					"WHERE { " + NL + 
					"    ?mapping a <" + Property_to_Graphic_AttributeMapping.RDFS_CLASS + "> . " + NL + 
					"    ?mapping <" + Property_to_Graphic_AttributeMapping.VALUEMAPPING + "> ?vm . " + NL + 
	//				"	{ " +
	//				"	SELECT ?vm  (COUNT(?sv) AS ?svCount) " +
	//				"       WHERE " +
	//				"       { " +
	//				"	 		  ?vm <" + ValueMappingX.SOURCEVALUE + "> ?sv  " +
	//				"       } " +
	//				"        GROUP BY ?vm " +
	//				"	} " +
	//				"    FILTER (!(?svCount = 1 )) " +
					"} " + NL ;
			
			return getP2GAMappings(mappingModel, queryString);
		}
	
	public static Set<PropertyToGO2ORMappingX> getAllP2GOTORMappingsTo(Model modelMappings, URI gotor) {
		
		MappingQueryBuilder queryBuilder = new MappingQueryBuilder();
		//queryBuilder.constrainToGraph(OGVICProcess.GRAPH_MAPPING);
		queryBuilder.constrainToType(Property_to_Graphic_Object_to_Object_RelationMapping.RDFS_CLASS);
		// constraining target GOTOR is optional
		if (null != gotor) queryBuilder.constrainToTargetGR(gotor);
		String queryString = queryBuilder.buildQuery(ModelManager.getInstance().getDataModel());
		
		return getP2GOTORMappings(modelMappings, queryString);
	}

	public static Set<PropertyToGO2ORMappingX> getAllP2GOTORMappings(Model modelMappings) {
		
		return getAllP2GOTORMappingsTo(modelMappings, null);
	}

	public static Set<IdentityMappingX> getAllIdentityMappings(
			Model modelMappings) {
		
		MappingQueryBuilder queryBuilder = new MappingQueryBuilder();
		//queryBuilder.constrainToGraph(OGVICProcess.GRAPH_MAPPING);
		queryBuilder.constrainToType(Identitymapping.RDFS_CLASS);
		String queryString = queryBuilder.buildQuery(ModelManager.getInstance().getDataModel());
		
		return getIdentityMappings(modelMappings, queryString);
	}

	/**
	 * @param modelMappings
	 * @param mappingSet
	 * @param queryString
	 * @return
	 * @throws MalformedQueryException
	 * @throws ModelRuntimeException
	 */
	protected static Set<PropertyToGraphicAttributeMappingX> getP2GAMappings(
			Model modelMappings,
			String queryString) throws MalformedQueryException,
			ModelRuntimeException {
		
		final Set<PropertyToGraphicAttributeMappingX> mappingSet = new HashSet<PropertyToGraphicAttributeMappingX>();
		
		QueryResultTable results = modelMappings.sparqlSelect(queryString);
		
		for (QueryRow row : results) {
				Property_to_Graphic_AttributeMapping mapping = Property_to_Graphic_AttributeMapping.getInstance(modelMappings, row.getValue("mapping").asResource());
				mappingSet.add(new PropertyToGraphicAttributeMappingX(mapping));
				continue;
		}
		
		return mappingSet;
	}

	/**
	 * @param modelMappings
	 * @param mappingSet
	 * @param queryString
	 * @return
	 * @throws MalformedQueryException
	 * @throws ModelRuntimeException
	 */
	protected static Set<PropertyToGO2ORMappingX> getP2GOTORMappings(
			Model modelMappings,
			String queryString) throws MalformedQueryException,
			ModelRuntimeException {
		
		final Set<PropertyToGO2ORMappingX> mappingSet = new HashSet<PropertyToGO2ORMappingX>();
		
		QueryResultTable results = modelMappings.sparqlSelect(queryString);
		
		for(QueryRow row : results) {
			Property_to_Graphic_Object_to_Object_RelationMapping mapping = Property_to_Graphic_Object_to_Object_RelationMapping.getInstance(modelMappings, row.getValue("mapping").asResource());
			mappingSet.add(new PropertyToGO2ORMappingX(mapping));
		}
		
		return mappingSet;
	}

	protected static Set<IdentityMappingX> getIdentityMappings(
			Model modelMappings, String queryString) {
		
		final Set<IdentityMappingX> mappingSet = new HashSet<IdentityMappingX>();
		
		QueryResultTable results = modelMappings.sparqlSelect(queryString);
		
		for(QueryRow row : results) {
			Identitymapping mapping = Identitymapping.getInstance(modelMappings, row.getValue("mapping").asResource());
			mappingSet.add(new IdentityMappingX(mapping));
		}
		
		return mappingSet;
	} 
}
