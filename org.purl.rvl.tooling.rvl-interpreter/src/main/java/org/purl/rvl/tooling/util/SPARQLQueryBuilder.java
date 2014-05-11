package org.purl.rvl.tooling.util;

import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.owl.Restriction;
import org.ontoware.rdfreactor.schema.rdfs.Class;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.tooling.process.OGVICProcess;

public class SPARQLQueryBuilder {
	
	SPARQLStringBuilder query;
	
	private Resource subject;
	private Resource object;
	private URI graphURI;
	private URI spURI;
	private String selectorSPARQLString;

	
	
	public void startQuerySPARQL(){
				//" SELECT DISTINCT ?src ?s ?p ?o " + 
				//" " + fromGraphString + 
				query.append(" SELECT DISTINCT ?s ?p ?o ");
				
				// note: without GRAPH phrase below, only FROM works, not FROM NAMED
				//" FROM NAMED " + fromGraph.toSPARQL() + " " + 
				query.append(" WHERE { ");
	}
	
	
	private void constrainToSubjectSPARQL(Resource subject){
		query.append(" FILTER (?s = " +  subject.toSPARQL() + ") ");
	}
	
	private void constrainToObjectSPARQL(Resource object){
		query.append(" FILTER (?o = " +  object.toSPARQL() + ") ");
	}
	
	private void constrainToGraphSPARQL(URI graphURI){
		query.append(" GRAPH " + graphURI.toSPARQL() + " { ");
	}
	
	protected void spoVarTripleSPARQL(){
		query.append(" ?s ?p ?o . ");
	}
	
	protected void statementSPARQL(URI spURI){
		query
			// including statements of subproperties of spURI or spURI itself
			.append(" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " ")
			// only most specific
			.append(" FILTER NOT EXISTS { ")
			.append(" ?s ?pp ?o . ")
			.append(" ?pp " + Property.SUBPROPERTYOF.toSPARQL() + "+ ?p ")
			.append(" FILTER(?pp != ?p) ")
			.append(" } ")
			;
	}
	
	// most specific
	private void selectorSPARQL(){
		query.append(" " + selectorSPARQLString + " " ); // this string is expected to be a set of " s? <p> <o> . " triples)
	}
	
	
	
	
	
	
	private void filterOnlyIRIsForSubjectAndObjectSPARQL(){
		query.append(" FILTER isIRI(?s) "); // TODO: this stops blank nodes as subjects ... ;
		query.append(" FILTER isIRI(?o) "); // TODO: this stops blank nodes as objects ... ;
	}
	
	private void filterNoReflexiveStatementsSPARQL(){
		query.append(" FILTER(?s != ?o) "); // TODO: this stops reflexive arcs completely! make optional
	}
	

	
	private void closeSwiftBracketSPARQL(){
		query.append(" } ");
	}

	private void closeGraphSPARQL(){
		query.append(" } ");
	}
	
	private void endQuerySPARQL(){
		closeSwiftBracketSPARQL();
	}
	
	private void limitSPARQL(){
		query.append(" LIMIT " + OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING + " ");
	}


	public SPARQLQueryBuilder(URI spURI) {
		super();
		this.spURI = spURI;
	}
	
	public String buildQuery(){
		
		query = new SPARQLStringBuilder();
		
								startQuerySPARQL();
		if (null!=graphURI) 		constrainToGraphSPARQL(graphURI);
										spoVarTripleSPARQL();
		if (null!=selectorSPARQLString)	selectorSPARQL();
										statementSPARQL(spURI);
		if (null!=object) 				constrainToObjectSPARQL(object);
		if (null!=subject) 				constrainToSubjectSPARQL(subject);
										filterNoReflexiveStatementsSPARQL();
										filterOnlyIRIsForSubjectAndObjectSPARQL();
		if (null!=graphURI) 		closeGraphSPARQL();
								endQuerySPARQL();
								limitSPARQL();
		
		return query.toString();
	}
	
	public void constrainToSubject(Resource subject){
		this.subject = subject;
	}
	
	public void constrainToObject(Resource object){
		this.object = object;
	}
	
	public void constrainToGraph(URI graphURI){
		this.graphURI = graphURI;
	}
	
	public void constrainToSubjectBySelector(String selectorSPARQLString){
		this.selectorSPARQLString = selectorSPARQLString;
	}

	
	// selecting only most specific statements pattern:
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
	
	// findStatementsPreferingThoseUsingASubProperty
	/*
	String query = "" + 
	" SELECT DISTINCT ?s ?p ?o " +
	//" FROM NAMED " + fromGraph.toSPARQL() + " " + 
	" WHERE { " +
	" GRAPH " + fromGraph.toSPARQL() + " { " +
	" ?s ?p ?o . " + 
	" " + selectorSPARQLString + " " + // this string is expected to be a set of " s? <p> <o> . " triples
	" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " " +
	" FILTER NOT EXISTS { " + 
			" ?s ?pp ?o . " + 
	        " ?pp " + Property.SUBPROPERTYOF.toSPARQL() + "+ ?p " +		 
	" FILTER(?pp != ?p) " +
	" } " +
	" FILTER(?s != ?o) " + // TODO: this stops reflexive arcs completely! make optional
	" FILTER isIRI(?s) " + // TODO: this stops blank nodes as subjects ...
	" FILTER isIRI(?o) " +  // .. or objects! make optional!
	" } " + 
	" } " + 
	" LIMIT " + OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING + " ";
	
	*/
	
	@Override
	public String toString() {
		return buildQuery();
	}


	public class SPARQLStringBuilder {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		protected SPARQLStringBuilder append(String sparqlLine){
			stringBuilder.append(sparqlLine).append(System.lineSeparator());
			return this;
		}
		
		public String toString() {
			return stringBuilder.toString();
		}
		
	}

	
	// selecting only most specific statements pattern:
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
	
	// findStatementsPreferingThoseUsingASubProperty
	/*
	String query = "" + 
	" SELECT DISTINCT ?s ?p ?o " +
	//" FROM NAMED " + fromGraph.toSPARQL() + " " + 
	" WHERE { " +
	" GRAPH " + fromGraph.toSPARQL() + " { " +
	" ?s ?p ?o . " + 
	" " + selectorSPARQLString + " " + // this string is expected to be a set of " s? <p> <o> . " triples
	" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " " +
	" FILTER NOT EXISTS { " + 
			" ?s ?pp ?o . " + 
	        " ?pp " + Property.SUBPROPERTYOF.toSPARQL() + "+ ?p " +		 
	" FILTER(?pp != ?p) " +
	" } " +
	" FILTER(?s != ?o) " + // TODO: this stops reflexive arcs completely! make optional
	" FILTER isIRI(?s) " + // TODO: this stops blank nodes as subjects ...
	" FILTER isIRI(?o) " +  // .. or objects! make optional!
	" } " + 
	" } " + 
	" LIMIT " + OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING + " ";
	
	*/
	
	// class level ALL/SOME/VALUES
	/*
	String queryOld = "" + 
			//" SELECT DISTINCT ?src ?s ?p ?o " + 
			//" " + fromGraphString + 
			" SELECT DISTINCT ?s ?p ?o " + 
			//" FROM NAMED " + fromGraph.toSPARQL() + " " + 
			" WHERE { " +
			//" GRAPH ?src { " +
			" GRAPH " + fromGraph.toSPARQL() + " { " +
			" ?s " + Class.SUBCLASSOF.toSPARQL() + " ?restrictionClass . " +
			" ?restrictionClass a " + Restriction.RDFS_CLASS.toSPARQL() + " . " +  
			" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " . " +
			" ?restrictionClass " + Restriction.ONPROPERTY.toSPARQL() + " ?p . " + 
			" ?restrictionClass " + inheritedBy.toSPARQL() +  " ?o  " +  
			" FILTER isIRI(?s) " + // TODO: this stops blank nodes as subjects ... ;
			" FILTER isIRI(?o) " ; // TODO: this stops blank nodes as subjects ... ;
			// constrain object and subject of the "statements" if set
			if (null!=subject) {queryOld += " FILTER (?s = " +  subject.toSPARQL() + ") "; }
			if (null!=object) {queryOld += " FILTER (?o = " + object.toSPARQL() + ") "; }
			queryOld += 
			" } " +
			" } ";
*/
	// class level domain-range
	/*query = "" + 
	//" SELECT DISTINCT ?src ?s ?p ?o " + 
	//" " + fromGraphString + 
	" SELECT DISTINCT ?s ?p ?o " + 
	//" FROM NAMED " + fromGraph.toSPARQL() + " " + 
	" WHERE { " +
	//" GRAPH ?src { " +
	" GRAPH " + fromGraph.toSPARQL() + " { " +
	" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " . " +
	" ?p " + org.ontoware.rdf2go.vocabulary.RDFS.domain.toSPARQL() +  " ?s  . " +  
	" ?p " + org.ontoware.rdf2go.vocabulary.RDFS.range.toSPARQL() +  " ?o  " +  
	" FILTER isIRI(?s) " + // TODO: this stops blank nodes as subjects ... ;
	" FILTER isIRI(?o) "; // TODO: this stops blank nodes as objects ... ;
	// constrain object and subject of the "statements" if set
	if (null!=subject) {query += " FILTER (?s = " +  subject.toSPARQL() + ") "; }
	if (null!=object) {query += " FILTER (?o = " + object.toSPARQL() + ") "; }
	query += 
	" } " +
	" } ";*/
}
