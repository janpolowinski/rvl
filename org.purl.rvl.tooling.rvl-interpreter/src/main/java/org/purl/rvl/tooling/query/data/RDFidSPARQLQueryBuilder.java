package org.purl.rvl.tooling.query.data;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.purl.rvl.java.RDF;

/**
 * @author Jan Polowinski
 *
 */
public class RDFidSPARQLQueryBuilder extends DataQueryBuilder {
	
	public RDFidSPARQLQueryBuilder() {
		super(RDF.type);
		constrainToPredicate(RDF.type);
		constrainToObject(RDFS.Resource);
	}

	protected void statementSPARQL(URI spURI){
		
	}
	
}
