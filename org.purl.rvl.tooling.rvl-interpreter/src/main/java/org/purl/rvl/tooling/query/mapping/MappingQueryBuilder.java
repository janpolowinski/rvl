/**
 * 
 */
package org.purl.rvl.tooling.query.mapping;

import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.tooling.query.SPARQLQueryBuilder;

/**
 * @author Jan Polowinski
 *
 */
public class MappingQueryBuilder extends SPARQLQueryBuilder {
	
	URI mappingTypeURI;

	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.query.SPARQLQueryBuilder#startQuerySPARQL()
	 */
	@Override
	public void startQuerySPARQL() {
		query.append(" SELECT DISTINCT ?mapping ");
		query.append(" WHERE { ");
	}
	
	public void constrainToType(URI rdfsClassURI){
		this.mappingTypeURI = rdfsClassURI;
	}
	
	protected void constrainToTypeSPARQL(){
		query.append(" ?mapping a <" + mappingTypeURI + "> . ");
	}
	

	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.query.SPARQLQueryBuilder#buildQuery()
	 */
	@Override
	public String buildQuery() {
		
		query = new SPARQLStringBuilder();
		
								//addCommonPrefixes();
								addPrefixesFromDataModel();

									startQuerySPARQL();
		if (null!=graphURI) 		constrainToGraphSPARQL(graphURI);
									constrainToTypeSPARQL();
									//spoVarTripleSPARQL();
									//statementSPARQL(spURI);
									//filterOnlyIRIsForSubjectAndObjectSPARQL();
		if (null!=graphURI) 		closeGraphSPARQL();
								endQuerySPARQL();
								limitSPARQL();
		
		return query.toString();
	}

}
