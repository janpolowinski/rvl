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
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_AttributeMapping;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;

/**
 * @author Jan Polowinski
 *
 */
public class MappingQuery {

	final static Logger LOGGER = Logger.getLogger(MappingQuery.class.getName());
	static final String NL =  System.getProperty("line.separator");

		/**
		 * Get all the mappings that require no calculation, 
		 * because they only have explicit 1-1-value-mappings
		 */
		public static Set<PropertyToGraphicAttributeMappingX> getAllP2GAMappingsWithExplicitMappings(Model modelMappings){
	
			String queryString = "" +
					"SELECT DISTINCT ?mapping " +
					"WHERE { " +
					"    ?mapping a <" + PropertyToGraphicAttributeMappingX.RDFS_CLASS + "> . " +
					"    ?mapping <" + PropertyToGraphicAttributeMappingX.VALUEMAPPING + "> ?vm . " +
					"	{ " +
					"	SELECT ?vm  (COUNT(?sv) AS ?svCount) " +
					"       WHERE " +
					"       { " +
					"	 		  ?vm <" + PropertyToGraphicAttributeMappingX.SOURCEVALUE + "> ?sv  " +
					"       } " +
					"        GROUP BY ?vm " +
					"	} " +
					"    FILTER (?svCount = 1 ) " +
					"} " ;

			return getP2GAMappings(modelMappings, queryString);
		}

	/**
		 * Get all the mappings that require calculation, because they have not only explicit 1-1-value-mappings
		 * TODO: this currently gets all mappings, including the 1-1, therefore it should actually only be called when it is clear that
		 *  the 1-1 case does not apply. 
		 */
		public static Set<PropertyToGraphicAttributeMappingX> getAllP2GAMappingsWithSomeValueMappings(Model modelMappings){
	
			String queryString = "" +
					"SELECT DISTINCT ?mapping " +
					"WHERE { " +
					"    ?mapping a <" + PropertyToGraphicAttributeMappingX.RDFS_CLASS + "> . " +
					"    ?mapping <" + PropertyToGraphicAttributeMappingX.VALUEMAPPING + "> ?vm . " +
	//				"	{ " +
	//				"	SELECT ?vm  (COUNT(?sv) AS ?svCount) " +
	//				"       WHERE " +
	//				"       { " +
	//				"	 		  ?vm <" + PropertyToGraphicAttributeMappingX.SOURCEVALUE + "> ?sv  " +
	//				"       } " +
	//				"        GROUP BY ?vm " +
	//				"	} " +
	//				"    FILTER (!(?svCount = 1 )) " +
					"} " ;
			
			
			return getP2GAMappings(modelMappings, queryString);
		}
	
	public static Set<PropertyToGO2ORMappingX> getAllP2GOTORMappingsTo(Model modelMappings, URI gotor) {

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
				"    ?mapping a <" + PropertyToGO2ORMappingX.RDFS_CLASS + "> . " +
				"    ?mapping " + PropertyToGO2ORMappingX.TARGETOBJECT_TO_OBJECTRELATION.toSPARQL() + " " + gotorString + " . " +
				"} " ;
		
		LOGGER.finer("SPARQL: query all mappings to " + gotorString + ":" + NL + 
				     queryString);
		
		return getP2GOTORMappings(modelMappings, queryString);
	}

	public static Set<PropertyToGO2ORMappingX> getAllP2GOTORMappings(Model modelMappings) {
		
		return getAllP2GOTORMappingsTo(modelMappings, null);
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
			//try {
				Property_to_Graphic_AttributeMapping mapping = Property_to_Graphic_AttributeMapping.getInstance(modelMappings, row.getValue("mapping").asResource());
				mappingSet.add((PropertyToGraphicAttributeMappingX)mapping.castTo(PropertyToGraphicAttributeMappingX.class));
			//} catch (Exception e) {
				LOGGER.warning("P2GAM could not be added to the mapping set.");
				continue;
			//}
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
			Property_to_Graphic_Object_to_Object_RelationMapping mapping = Property_to_Graphic_Object_to_Object_RelationMapping.getInstance(modelMappings, (URI)row.getValue("mapping"));
			mappingSet.add((PropertyToGO2ORMappingX)mapping.castTo(PropertyToGO2ORMappingX.class));
		}
		
		return mappingSet;
	} 
}
