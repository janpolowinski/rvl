package org.purl.rvl.tooling.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.ReactorResult;
import org.ontoware.rdfreactor.schema.bootstrap.Property;
import org.ontoware.rdfreactor.schema.owl.Restriction;
import org.ontoware.rdfreactor.schema.rdfs.Class;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.openrdf.repository.sparql.query.SPARQLQuery;
import org.purl.rvl.java.rvl.Mapping;
import org.purl.rvl.java.rvlsmall.Thing1;

public class RVLUtils {
	
	private final static Logger LOGGER = Logger.getLogger(RVLUtils.class.getName()); 
	
	public static void listAllMappings(Model model) {

		System.out.println("");
		System.out.println("List of all mappings in the model" +
				" (including subclasses when reasoning is on):");
		System.out.println("");

		// get references for all objects of the Mapping class
		ReactorResult<? extends org.purl.rvl.java.gen.rvl.Mapping> rrMappings = Mapping
				.getAllInstances_as(model);

		// get and print all mapping instances
		ClosableIterator<? extends org.purl.rvl.java.gen.rvl.Mapping> mappingIterator = rrMappings
				.asClosableIterator();
		Mapping mapping;
		while (mappingIterator.hasNext()) {
			mapping = (Mapping) mappingIterator.next().castTo(Mapping.class);
			mappingToStringAsSpecificAsPossible(mapping);
		}
	}

	  public static void printMappingWithURI(Model model, String uriString){
		  
			System.out.println("");
			System.out.println("Trying to get and print mapping with the URI " + uriString + ":");
			System.out.println("");
			
		  	org.purl.rvl.java.gen.rvl.Mapping mapping = Mapping.getInstance(model, new URIImpl(uriString));
		  	System.out.println(mappingToStringAsSpecificAsPossible((Mapping) mapping.castTo(Mapping.class)));
	  }
	  
	  public static void printMapping(org.purl.rvl.java.gen.rvl.Mapping mapping){  
		  printMapping((Mapping)mapping.castTo(Mapping.class));
	  }
	  
	  
	  public static void printMapping(Mapping mapping){
		  
			System.out.println("");
			System.out.println("Mapping details: ");
			System.out.println("");
			
			System.out.println(mappingToStringAsSpecificAsPossible((Mapping) mapping.castTo(Mapping.class)));
	  }
	  
	  public static String mappingToStringAsSpecificAsPossible(Mapping mapping){
		
		 String s = "";
		  
		// print as P2GAM (value mappings ... )
		if(mapping.isInstanceof(org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping.RDFS_CLASS)) {
			org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping p2gam = 
					(org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping) mapping.castTo(
							org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping.class);
			s += p2gam;
		}
		// print as P2GO2ORM (submappings ... )
		else if(mapping.isInstanceof(org.purl.rvl.java.rvl.PropertyToGO2ORMapping.RDFS_CLASS)) {
			org.purl.rvl.java.rvl.PropertyToGO2ORMapping p2go2orm = 
					(org.purl.rvl.java.rvl.PropertyToGO2ORMapping) mapping.castTo(
							org.purl.rvl.java.rvl.PropertyToGO2ORMapping.class);
			s += p2go2orm;
		}
		// print as general mapping
		else {
			s += mapping;
		}
		
		return s;
		
	  }

	public static Iterator<Statement> findStatementsPreferingThoseUsingASubProperty(
			Model model,
			org.ontoware.rdf2go.model.node.Resource subjectResource, URI spURI,
			Variable any) {
		
		Set<Statement> stmtSet = new HashSet<Statement>();
		
		try{
		
			// somehow this query does not behave like in topbraid. filtering does not replace the general statements by specific ones
			String query = "" + 
					"SELECT DISTINCT ?p ?o " + 
					"WHERE { " +
					subjectResource.toSPARQL() + " ?p ?o . " + 
					"?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " " +
					//" FILTER NOT EXISTS { ?pp " + Property.SUBPROPERTYOF.toSPARQL() + " " + spURI.toSPARQL() + " . " +
					//					  subjectResource.toSPARQL() + " ?pp ?o . " + 
					//" FILTER(?pp != ?p) " +
					//" } " +
					"} ";
			LOGGER.finer("Query for getting statements including those using a subproperty of " + spURI);
			LOGGER.finest("Query :" + query);
			
			/*
				SELECT DISTINCT ?s ?p ?o WHERE { 
				<http://purl.org/rvl/example-data/Amazing_Grace> ?p ?o .
				?p rdfs:subPropertyOf* <http://purl.org/rvl/example-data/cites> . 
				FILTER NOT EXISTS {
	   				?pp rdfs:subPropertyOf <http://purl.org/rvl/example-data/cites> . 
				    <http://purl.org/rvl/example-data/Amazing_Grace> ?pp ?o  
						FILTER (?p != ?pp)
				}
			} 
			} 
			
			*/
			
			QueryResultTable explMapResults = model.sparqlSelect(query);
			for (QueryRow row : explMapResults) {
				LOGGER.finest("fetched SPARQL result row: " + row);
				Statement stmt = new StatementImpl(null, subjectResource, row.getValue("p").asURI(), row.getValue("o"));
				LOGGER.finer("build Statement: " + stmt.toString());
				stmtSet.add(stmt);
			}
		
		} catch (UnsupportedOperationException e){
			LOGGER.warning("Problem with query to get statements for linking (blank node?): " + e.getStackTrace());
		}
		
		return stmtSet.iterator();
	}

	public static Set<Statement> findRelationsOnClassLevel(
			Model model,
			//org.ontoware.rdf2go.model.node.Resource subjectResource,
			URI spURI) {
		
		QueryResultTable results = null;
		Set<Statement> stmtSet = new HashSet<Statement>();

		
		try{
			
			String query = "" + 
					"SELECT DISTINCT ?s ?o " + 
					"WHERE { " +
					//subjectResource.toSPARQL() + " " + Class.SUBCLASSOF.toSPARQL() + " ?restrictionClass . " +
					"?s  " + Class.SUBCLASSOF.toSPARQL() + " ?restrictionClass . " +
					"?restrictionClass a " + Restriction.RDFS_CLASS.toSPARQL() + " . " +  
					"?restrictionClass " + Restriction.ONPROPERTY.toSPARQL() + " " + spURI.toSPARQL() + " . " + 
					"?restrictionClass " + Restriction.SOMEVALUESFROM.toSPARQL() +  " ?o . " + 
					"} ";
			LOGGER.finer("Query for getting relations on class level for " + spURI);
			LOGGER.finest("Query: " + query);
			
			/*
			WHERE {
      ?this rdfs:subClassOf ?restrictionClass .
    ?restrictionClass a owl:Restriction .
    ?restrictionClass owl:onProperty <http://purl.org/obo/owl/OBO_REL#part_of> .
    ?restrictionClass owl:someValuesFrom ?whole .
    ?this rdfs:label ?thisLabel .
			}
			*/

			results = model.sparqlSelect(query);
			
			for (QueryRow row : results) {
				LOGGER.finest("fetched SPARQL result row: " + row);
				try {
					Statement stmt = new StatementImpl(null, row.getValue("s").asURI(), spURI, row.getValue("o"));
					LOGGER.finer("build Statement: " + stmt.toString());
					stmtSet.add(stmt);
				} catch (Exception e) {
					LOGGER.warning("Problem building Statement for : " + row );
				}
			}
				
		} catch (UnsupportedOperationException e){
			LOGGER.warning("Problem with query to get relations on class level (blank node?): " + e.getStackTrace());
		}
		
		return stmtSet;
	}

}
