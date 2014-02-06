package org.purl.rvl.tooling.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterable;
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
import org.purl.rvl.tooling.process.OGVICProcess;

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
	
	public static Set<Statement> findStatementsPreferingThoseUsingASubProperty(
			Model model, URI spURI) {
		
			Set<Statement> stmtSet = new HashSet<Statement>();
		
		try {
	
			String query = "" + 
					" SELECT DISTINCT ?s ?p ?o " + 
					" WHERE { " +
					" ?s ?p ?o . " + 
					" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " " +
					" FILTER NOT EXISTS { " + 
							" ?s ?pp ?o . " + 
					        " ?pp " + Property.SUBPROPERTYOF.toSPARQL() + "+ ?p " +		 
					" FILTER(?pp != ?p) " +
					" } " +
					" FILTER(?s != ?o) " + // TODO: this stops reflexive arcs completely! make optional
					" FILTER isIRI(?s) " + // TODO: this stops blank nodes as subjects ...
					" FILTER isIRI(?o) " + // .. or opbjects! make optional!
					" } " + 
					" LIMIT " + OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING + " ";
			LOGGER.fine("Query statements with property (respectively most specific subproperty of) :" + spURI);
			LOGGER.finest("Query :" + query);
			
			/*
			 SELECT DISTINCT ?s ?p ?o WHERE { 
				?s ?p ?o .
				?p rdfs:subPropertyOf* <http://purl.org/rvl/example-data/cites> . 
				FILTER NOT EXISTS {
				    ?s ?pp ?o  .
	   				?pp rdfs:subPropertyOf+ ?p . 
					FILTER (?pp != ?p)
				}
			}
			*/
			
			QueryResultTable explMapResults = model.sparqlSelect(query);
			
			for (QueryRow row : explMapResults) {
				LOGGER.finest("fetched SPARQL result row: " + row);
				try {
					Statement stmt = new StatementImpl(null, row.getValue("s").asURI(), row.getValue("p").asURI(), row.getValue("o"));
					LOGGER.finest("build Statement: " + stmt.toString());
					if(row.getValue("s").asURI().toString().startsWith(OGVICProcess.getInstance().getUriStart())) {
						stmtSet.add(stmt);
						LOGGER.finer("added Statement: " + stmt.toString());
					} else {
						LOGGER.finer("skipped Statement: " + stmt.toString());
					}
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

	public static Set<Statement> findRelationsOnClassLevel2(
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
					"?restrictionClass " + Restriction.ALLVALUESFROM.toSPARQL() +  " ?o . " + 
					"} ";
			LOGGER.finer("Query for getting relations on class level for " + spURI);
			LOGGER.finest("Query: " + query);

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
