package org.purl.rvl.tooling.query.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Sparqlable;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.owl.Restriction;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.RDF;
import org.purl.rvl.java.RVL;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.tooling.process.OGVICProcess;

/**
 * @author Jan Polowinski
 *
 */
public class DataQuery {

	final static Logger LOGGER = Logger.getLogger(DataQuery.class.getName());
	static final String NL =  System.getProperty("line.separator");
	
	public static Set<Statement> findStatementsPreferingThoseUsingASubProperty(
			Sparqlable modelOrModelSet,
			URI fromGraph,
			URI spURI,
			String selectorSPARQLString
			) {
		
			Set<Statement> stmtSet = new HashSet<Statement>();
	
		try {
			
			DataQueryBuilder queryBuilder = new DataQueryBuilder(spURI);
			queryBuilder.constrainToGraph(fromGraph);
			queryBuilder.constrainToSubjectBySelector(selectorSPARQLString);
			String queryString = queryBuilder.buildQuery();
	
			LOGGER.fine("Query statements with property (respectively most specific subproperty of) :" + spURI);
			LOGGER.finest("Query :" + queryString);
	
			QueryResultTable explMapResults = modelOrModelSet.sparqlSelect(queryString);
			
			for (QueryRow row : explMapResults) {
				//LOGGER.finest("fetched SPARQL result row: " + row);
				try {
						
					Statement stmt = new StatementImpl(
							fromGraph,
							row.getValue("s").asURI(),
							row.getValue("p").asURI(),
							row.getValue("o")
							);
					
					LOGGER.finest("build Statement: " + stmt.toString());
					
					stmtSet.add(stmt);
					
				} catch (ClassCastException e){
					LOGGER.finer("Skipped statement for linking (blank node casting to URI?): " + e.getMessage());
				}
			}
		
		} catch (UnsupportedOperationException e){
			LOGGER.warning("Problem with query to get statements for linking (blank node?): " + e.getMessage());
		} 
		
		return stmtSet;
	}
	public static Set<Statement> findRelationsOnClassLevel(
			Sparqlable modelOrModelSet,
			URI fromGraph,
			URI spURI, 
			String selectorSPARQLString,
			org.ontoware.rdfreactor.schema.rdfs.Property inheritedBy
			) {
		return findRelationsOnClassLevel(modelOrModelSet, fromGraph, spURI, selectorSPARQLString, inheritedBy, null, null);
	}
	public static Set<Statement> findRelationsOnClassLevel(
			Sparqlable modelOrModelSet,
			URI fromGraph,
			URI spURI, 
			String selectorSPARQLString,
			org.ontoware.rdfreactor.schema.rdfs.Property inheritedBy,
			org.ontoware.rdf2go.model.node.Resource subject,
			org.ontoware.rdf2go.model.node.Node object
			) {
		
		QueryResultTable results = null;
		Set<Statement> stmtSet = new HashSet<Statement>();
		String query;
		
		// for now only support some / all values from + domain / range ...
		if (!(
				inheritedBy.toString().equals(Restriction.SOMEVALUESFROM.toString())
				|| inheritedBy.toString().equals(Restriction.ALLVALUESFROM.toString())
				|| inheritedBy.toString().equals(RVL.TBOX_DOMAIN_RANGE)	
				|| inheritedBy.toString().equals(RVL.TBOX_RESTRICTION)
			)) {
			LOGGER.warning("inheritedBy set to a value that is not (yet) supported.");
			return stmtSet;
		}
	
		DataQueryBuilder queryBuilder;
		
		if (inheritedBy.toString().equals(RVL.TBOX_DOMAIN_RANGE)) {
			
			// inheritedBy -> domain-range
			queryBuilder = new DomainRangeSPARQLQueryBuilder(spURI);
			
		} else {
			
			// inheritedBy -> other supported values (someValuesFrom / allValuesFrom or both (tBoxRestriction) )
			queryBuilder = new AllSomeValuesFromSPARQLQueryBuilder(spURI);
			((AllSomeValuesFromSPARQLQueryBuilder)queryBuilder).setInheritedBy(inheritedBy);
			
		}
			
		queryBuilder.constrainToGraph(fromGraph);
		queryBuilder.constrainToSubjectBySelector(selectorSPARQLString);
		query = queryBuilder.buildQuery();
	
		try {			
						
			LOGGER.finer("Query for getting relations on class level for (subproperties of*) " + spURI);
			LOGGER.finest("Query new: " + query);
	
			results = modelOrModelSet.sparqlSelect(query);
			
			for (QueryRow row : results) {
				//LOGGER.finest("fetched SPARQL result row: " + row);
				try {
					
					/*URI context = null; 
					if (null!=fromGraph){
						context = row.getValue("src").asURI();
					}*/
					
					Statement stmt = new StatementImpl(fromGraph, row.getValue("s").asURI(), row.getValue("p").asURI(), row.getValue("o"));
					LOGGER.finer("build Statement: " + stmt.toString());
					
					stmtSet.add(stmt);
													
				} catch (Exception e) {
					LOGGER.warning("Problem building Statement for : " + row + "(" + e.getMessage() + ")" );
				}
			}
				
		} catch (UnsupportedOperationException e){
			LOGGER.warning("Problem with query to get relations on class level (blank node?): " + e.getMessage());
		} catch (Exception e){
			LOGGER.warning("Problem with query to get relations on class level: " + e.getMessage());
		}
		
		return stmtSet;
	}
	/** NOTE: Hack: Reflexive edges are ignored at filtering
	 * @param modelOrModelSet
	 * @param fromGraph
	 * @param pm
	 * @param onlyMostSpecific
	 * @param subject
	 * @param object
	 * @return
	 * @throws InsufficientMappingSpecificationException
	 */
	public static Set<Statement> findRelationsOnInstanceOrClassLevel(
			Sparqlable modelOrModelSet,
			URI fromGraph,
			PropertyMappingX pm,
			boolean onlyMostSpecific, 
			org.ontoware.rdf2go.model.node.Resource subject,
			org.ontoware.rdf2go.model.node.Node object) throws InsufficientMappingSpecificationException {
		
			Set<Statement> statementSet = new HashSet<Statement>();
		
			URI spURI = pm.getSourceProperty().asURI();
			
			String selectorSPARQLString = "";
			
			if (pm.hasSubjectfilter()) {
				
				selectorSPARQLString = pm.getSubjectFilterString();
			
			}
			
			if (spURI.equals(RDF.ID)) {
				
				// in the special case when rdf:ID was used as a source property, we query for a set of triples (<ID>, rdf:type, rdfs:Resource).
				//statementSet.addAll(DataQuery.findResourceStatements(modelOrModelSet, fromGraph, spURI, selectorSPARQLString)); 
				statementSet.addAll(DataQuery.findRDFidStatements(modelOrModelSet, fromGraph, selectorSPARQLString)); 
				
			} else if (onlyMostSpecific) {
				
				 // get only the most specific statements and exclude those using a super-property instead
				statementSet.addAll(DataQuery.findStatementsPreferingThoseUsingASubProperty(modelOrModelSet, fromGraph, spURI, selectorSPARQLString)); 
				
			} else {
				
				// old code for getting statements directly with the API without SPARQL. maybe reuse later, when performance should matter
				// not usable in most cases, since many filter and restrictions usually apply for statement selection
				LOGGER.severe("Finding statements including less specific statements not implemented.");
				System.exit(1);
				//statementSet.addAll(findStatementsIncludingSubPropertyStatementsWithoutSPARQL(
				//		modelOrModelSet, subject, object, spURI,classSelector));
				
			}
			
			// consider inherited relations, including those between classes (someValueFrom ...)
			if(pm.hasInheritedby()) {
				
				try{
				
				Property inheritedBy = pm.getInheritedBy();
				
				// temp only support some and all values from ...
				if (!(
						inheritedBy.toString().equals(Restriction.SOMEVALUESFROM.toString())
						|| inheritedBy.toString().equals(Restriction.ALLVALUESFROM.toString())
						|| inheritedBy.toString().equals(RVL.TBOX_RESTRICTION)
						|| inheritedBy.toString().equals(RVL.TBOX_DOMAIN_RANGE)	
					)) {
					LOGGER.finest("inheritedBy set to a value not defining a class level relation or value not yet supported. Will be ignored as a class level relation.");
					return statementSet;
				}
	
				statementSet.addAll(findRelationsOnClassLevel(
							modelOrModelSet,
							fromGraph,
							spURI,
							selectorSPARQLString,
							inheritedBy,
							subject,
							object)
							);
				} catch (Exception e) {
					LOGGER.warning("Problem evaluating inheritedBy setting or getting relations on class level");
				}
			} 
			
			return statementSet;
	}
	
	/**
	 * TODO: directly query for the special statements with the pattern <RESOURCE_URI> rdf:ID <RESOURCE_URI>
	 * 
	 * @param modelOrModelSet
	 * @param fromGraph
	 * @param selectorSPARQLString
	 * @return a collection of statements following the pattern <RESOURCE_URI> rdf:ID <RESOURCE_URI>
	 */
	private static Collection<? extends Statement> findRDFidStatements(Sparqlable modelOrModelSet, URI fromGraph,
			String selectorSPARQLString) {

		Set<Statement> stmtSet = new HashSet<Statement>();
		
		try {
			
			DataQueryBuilder queryBuilder = new RDFidSPARQLQueryBuilder();
			queryBuilder.constrainToGraph(fromGraph);
			queryBuilder.constrainToSubjectBySelector(selectorSPARQLString);
			String queryString = queryBuilder.buildQuery();
	
			LOGGER.fine("Query ID-statements");
			LOGGER.finest("Query :" + queryString);
	
			QueryResultTable explMapResults = modelOrModelSet.sparqlSelect(queryString);
			
			for (QueryRow row : explMapResults) {
				
				try {
						
					Statement stmt = new StatementImpl(
							fromGraph,
							row.getValue("s").asURI(),
							RDF.ID,
							row.getValue("s").asURI()
							);
					
					LOGGER.finest("build Statement: " + stmt.toString());
					
					stmtSet.add(stmt);
					
				} catch (ClassCastException e){
					LOGGER.finer("Skipped statement (blank node casting to URI?): " + e.getMessage());
				}
			}
		
		} catch (UnsupportedOperationException e){
			LOGGER.warning("Problem with query to get statements (blank node?): " + e.getMessage());
		} 
		
		return stmtSet;
	}
	
	
	/**
	 * Returns all resources from the data graph 
	 * related to the baseResource via the property relation.
	 * 
	 * @param modelOrModelSet
	 * @param baseResource
	 * @param relation - a Property
	 * @return a set of related resources
	 */
	public static Set<Resource> getRelatedResources(Sparqlable modelOrModelSet, Resource baseResource,
			Property relation) {
		
		Set<Resource> resourceSet = new HashSet<Resource>();
	
		try {
			
			String queryString = "" + 
					" SELECT DISTINCT ?r " + 
					" WHERE { " +
					" GRAPH " + OGVICProcess.GRAPH_DATA.toSPARQL() + " { " +
					//" ?r " + inheritedBy.toSPARQL()+"* " + subject.toSPARQL() + " . " +  TODO: overwrites everything when owl:Class is mapped
					" ?r " + relation.toSPARQL()+" " + baseResource.toSPARQL()  + 
					" } " +
					" }";
			
	
			LOGGER.finest("Query :" + queryString);
	
			QueryResultTable explMapResults = modelOrModelSet.sparqlSelect(queryString);
			
			for (QueryRow row : explMapResults) {
				
				try {
						
					Resource relatedResource = 	row.getValue("r").asURI();
					LOGGER.finest("added related resource: " + relatedResource.toString());
					resourceSet.add(relatedResource);
					
				} catch (ClassCastException e){
					LOGGER.finer("Problem getting a related resource : " + e.getMessage());
				}
			}
		
		} catch (UnsupportedOperationException e){
			LOGGER.warning("Problem with query to get related resources (blank node?): " + e.getMessage());
		} 
		
		return resourceSet;
	
	}
	
	
	/**
	 * Returns all resources from the data graph directly
	 * related to the baseResource via the property relation.
	 * For a baseResource A, B is only returned, when not A -> X -> B .
	 * 
	 * @param modelOrModelSet
	 * @param baseResource
	 * @param relation - a Property
	 * @return a set of directly related resources
	 */
	public static Set<Resource> getDirectlyRelatedResources(Sparqlable modelOrModelSet, Resource baseResource,
			Property relation) {
		
		Set<Resource> resourceSet = new HashSet<Resource>();
	
		try {
			
			// TODO: not yet tested!
			
			String queryString = "" + 
					" SELECT DISTINCT ?r " + 
					" WHERE { " +
					" GRAPH " + OGVICProcess.GRAPH_DATA.toSPARQL() + " { " +
					" ?r " + relation.toSPARQL() + " " + baseResource.toSPARQL()  + 
					" FILTER NOT EXISTS { " +
					  " ?r " + relation.toSPARQL() + " ?intermediateNode . " + 
					  " ?intermediateNode " + relation.toSPARQL() + "+ " + baseResource.toSPARQL() + " . " + 
					  " FILTER(?r != ?intermediateNode) " + 
					  " FILTER(?intermediateNode != " + baseResource.toSPARQL() + ") " + 
					  " } " +  
					" } " +
					" FILTER (?r != " + baseResource.toSPARQL()  + ") " + 
					" }";
			
	
			LOGGER.finest("Query :" + queryString);
	
			QueryResultTable explMapResults = modelOrModelSet.sparqlSelect(queryString);
			
			for (QueryRow row : explMapResults) {
				
				try {
						
					Resource relatedResource = 	row.getValue("r").asURI();
					LOGGER.finest("added directly related resource: " + relatedResource.toString());
					resourceSet.add(relatedResource);
					
				} catch (ClassCastException e){
					LOGGER.finer("Problem getting a directly related resource : " + e.getMessage());
				}
			}
		
		} catch (UnsupportedOperationException e){
			LOGGER.warning("Problem with query to get directly related resources (blank node?): " + e.getMessage());
		} 
		
		return resourceSet;
	
	}
	

	
}
