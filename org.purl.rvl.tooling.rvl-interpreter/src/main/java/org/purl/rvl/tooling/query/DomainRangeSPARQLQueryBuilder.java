package org.purl.rvl.tooling.query;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Property;

/**
 * @author Jan Polowinski
 *
 */
public class DomainRangeSPARQLQueryBuilder extends SPARQLQueryBuilder {

	public DomainRangeSPARQLQueryBuilder(URI spURI) {
		super(spURI);
	}
	
	protected void statementSPARQL(URI spURI){
		query
			// including statements of subproperties of spURI or spURI itself
			.append(" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " ")
			.append(" ?p " + org.ontoware.rdf2go.vocabulary.RDFS.domain.toSPARQL() +  " ?s  . ")
			.append(" ?p " + org.ontoware.rdf2go.vocabulary.RDFS.range.toSPARQL() +  " ?o"  );
			// only most specific
			//.append(" FILTER NOT EXISTS { ")
			//.append(" ?s ?pp ?o . ")
			//.append(" ?pp " + Property.SUBPROPERTYOF.toSPARQL() + "+ ?p ")
			//.append(" FILTER(?pp != ?p) ")
			//.append(" } ")
			;
	}

}
