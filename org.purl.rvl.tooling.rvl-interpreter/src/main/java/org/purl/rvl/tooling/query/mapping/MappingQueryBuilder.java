/**
 * 
 */
package org.purl.rvl.tooling.query.mapping;

import org.ontoware.rdf2go.model.node.URI;
import org.purl.rvl.java.gen.rvl.Property_to_Graphic_Object_to_Object_RelationMapping;
import org.purl.rvl.java.rvl.MappingX;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.query.SPARQLQueryBuilder;

/**
 * @author Jan Polowinski
 *
 */
public class MappingQueryBuilder extends SPARQLQueryBuilder {
	
	URI mappingTypeURI;
	URI targetGraphicRelation;

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
	
	public void constrainToTargetGR(URI targetGraphicRelation){
		this.targetGraphicRelation = targetGraphicRelation;
	}
	
	protected void constrainToTypeSPARQL(){
		query.append(" ?mapping a " + mappingTypeURI.toSPARQL() + " . ");
	}

	protected void constrainToTargetGRsparql(){
		query.append(" ?mapping " 
			+ Property_to_Graphic_Object_to_Object_RelationMapping.TARGETOBJECT_TO_OBJECTRELATION.toSPARQL() 
			+ targetGraphicRelation.toSPARQL() + " . ");
	}
	
	protected void limitSPARQL(){
		//query.append(" LIMIT " + OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING + " ");
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
		if (null!=graphURI) 			constrainToGraphSPARQL(graphURI);
		if (null!=mappingTypeURI) 			constrainToTypeSPARQL();
		if (null!=targetGraphicRelation) 	constrainToTargetGRsparql();
											//statementSPARQL(spURI);
											//filterOnlyIRIsForSubjectAndObjectSPARQL();
		if (null!=graphURI) 			closeGraphSPARQL();
									endQuerySPARQL();
									limitSPARQL();
		
		return query.toString();
	}

}
