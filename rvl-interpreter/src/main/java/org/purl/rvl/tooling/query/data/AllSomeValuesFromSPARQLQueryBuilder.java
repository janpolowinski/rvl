package org.purl.rvl.tooling.query.data;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.owl.Restriction;
import org.ontoware.rdfreactor.schema.rdfs.Class;
import org.ontoware.rdfreactor.schema.rdfs.Property;

/**
 * @author Jan Polowinski
 *
 */
public class AllSomeValuesFromSPARQLQueryBuilder extends DataQueryBuilder {

	private Property inheritedBy;

	public AllSomeValuesFromSPARQLQueryBuilder(URI spURI) {
		super(spURI);
	}
	
	protected void statementSPARQL(URI spURI){
		query
			// including statements of subproperties of spURI or spURI itself
			.append(" ?s " + Class.SUBCLASSOF.toSPARQL() + " ?restrictionClass . ")
			.append(" ?restrictionClass a " + Restriction.RDFS_CLASS.toSPARQL() + " . ") 
			.append(" ?p " + Property.SUBPROPERTYOF.toSPARQL() + "* " + spURI.toSPARQL() + " . ")
			.append(" ?restrictionClass " + Restriction.ONPROPERTY.toSPARQL() + " ?p . ");
			
		if (null!= inheritedBy) query.append(" ?restrictionClass " + inheritedBy.toSPARQL() +  " ?o  ");
		else {
			query.append(" {  ");
			query.append(" ?restrictionClass " + Restriction.ALLVALUESFROM.toSPARQL() +  " ?o  ");
			query.append(" } UNION { ");
			query.append(" ?restrictionClass " + Restriction.SOMEVALUESFROM.toSPARQL() +  " ?o  ");
			query.append(" }  ");
		}
			// only most specific
			//.append(" FILTER NOT EXISTS { ")
			//.append(" ?s ?pp ?o . ")
			//.append(" ?pp " + Property.SUBPROPERTYOF.toSPARQL() + "+ ?p ")
			//.append(" FILTER(?pp != ?p) ")
			//.append(" } ")
			;
	}
	
	protected void spoVarTripleSPARQL(){
		//query.append(" ?s ?p ?o . ");
	}
	
	public void setInheritedBy(Property inheritedBy) {
		this.inheritedBy = inheritedBy;
	}

}
