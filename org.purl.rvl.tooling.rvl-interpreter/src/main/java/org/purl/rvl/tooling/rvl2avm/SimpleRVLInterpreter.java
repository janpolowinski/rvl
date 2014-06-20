package org.purl.rvl.tooling.rvl2avm;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Property;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.java.gen.viso.graphic.Containment;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.gen.viso.graphic.GraphicAttribute;
import org.purl.rvl.java.gen.viso.graphic.Labeling;
import org.purl.rvl.java.gen.viso.graphic.UndirectedLinking;
import org.purl.rvl.java.rvl.IdentityMappingX;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.query.data.DataQuery;
import org.purl.rvl.tooling.query.mapping.MappingQuery;
import org.purl.rvl.tooling.util.RVLUtils;

/**
 * @author Jan Polowinski
 *
 */
public class SimpleRVLInterpreter  extends RVLInterpreterBase {
	

	
	final static Logger LOGGER = Logger.getLogger(SimpleRVLInterpreter.class .getName()); 

	/* (non-Javadoc)
	 * @see org.purl.rvl.tooling.rvl2avm.RVLInterpreterBase#interpretMappings()
	 */
	@Override
	protected void interpretMappingsInternal() {
		
		if (null==modelSet) {
			LOGGER.severe("Cannot interprete mappings, since model set is null.");
			return;
		}
		
		interpretSimpleP2GArvlMappings();
		interpretNormalP2GArvlMappings(); 
		interpretP2GO2ORMappings();
		//performDefaultLabelMappingForAllGOs();
		interpretIdentityMappings();
	}

	/**
	 * Interprets the P2GO2OR mappings.
	 * TODO: Implement other GR than linking
	 */
	protected void interpretP2GO2ORMappings() {
		
		// get all P2GO2OR mappings to linking and create n-ary linking relations
		//Set<PropertyToGO2ORMappingX> setOfMappingsToLinking = getAllP2GOTORMappingsTo(DirectedLinking.RDFS_CLASS); // 
		
		Set<PropertyToGO2ORMappingX> mappings = MappingQuery.getAllP2GOTORMappings(modelMappings);
		
		LOGGER.info(NL + "Found " + mappings.size() + " PGOTOR mappings (enabled and disabled mappings).");
		
		// for each mapping
		for (Iterator<PropertyToGO2ORMappingX> iterator = mappings
				.iterator(); iterator.hasNext();) {
			
			PropertyToGO2ORMappingX p2go2orm = (PropertyToGO2ORMappingX) iterator.next();
			
			// skip disabled
			if (p2go2orm.isDisabled()) {
				LOGGER.info("Ignored disabled P2GO2OR mapping " + p2go2orm.asURI());
				continue;
			}
			
			LOGGER.info("Interpret P2GOTOR mapping " + p2go2orm.asURI() );
			try {
				LOGGER.info(p2go2orm.toStringDetailed() );
			} catch (Exception e) {}
			
			
			try {
				if (p2go2orm.getTargetGraphicRelation().equals(Labeling.RDFS_CLASS)) {
					new MappingToLabelingHandler(modelSet, this, modelAVM).handleP2GOTORMapping(p2go2orm);
				}
				else if (p2go2orm.getTargetGraphicRelation().equals(DirectedLinking.RDFS_CLASS)
						|| p2go2orm.getTargetGraphicRelation().equals(UndirectedLinking.RDFS_CLASS)) {
					new MappingToLinkingHandler(modelSet, this, modelAVM).handleP2GOTORMapping(p2go2orm);
				}
//				else if (p2go2orm.getTargetGraphicRelation().equals(UndirectedLinking.RDFS_CLASS)) {
//					LOGGER.info("Ignored Mapping to Undirected Linking. Undirected Linking not yet implemented");
//				}
				else if (p2go2orm.getTargetGraphicRelation().equals(Containment.RDFS_CLASS)) {
					new MappingToContainmentHandler(modelSet, this, modelAVM).handleP2GOTORMapping(p2go2orm);
					//LOGGER.info("Ignored Mapping to Containment. Containment not yet implemented");
				}
				else  {
					try {
						LOGGER.info("Ignored mapping to " + p2go2orm.getTargetGraphicRelation() + ". Graphic relation not yet implemented");
					} catch (InsufficientMappingSpecificationException e) {
						LOGGER.severe("Ignored mapping. Graphic relation not yet implemented.");
					}
				}
				
			} catch (MappingException e) {
				LOGGER.severe("P2GOTOR mappings could not be interpreted: " + e.getMessage());
			} catch (InsufficientMappingSpecificationException e) {
				LOGGER.severe("Could not start mapping interpretation: " + e.getMessage());
			}

		}
		
		LOGGER.fine("The size of the Resource-to-GraphicObjectX map is " + resourceGraphicObjectMap.size()+".");
	}


	/**
	 * Interprets the normal P2GA mappings, i.e. those with need for calculating value mappings. 
	 * Creates GO for all affected resources if they don't exist already.
	 */
	protected void interpretNormalP2GArvlMappings() {
		
		Set<PropertyToGraphicAttributeMappingX> setOfP2GAMappings = MappingQuery.getAllP2GAMappingsWithSomeValueMappings(modelMappings);
		
		LOGGER.info(NL + "Found " +setOfP2GAMappings.size()+ " normal P2GA mappings.");
		
		// for each normal P2GA mapping
		for (Iterator<PropertyToGraphicAttributeMappingX> iterator = setOfP2GAMappings
				.iterator(); iterator.hasNext();) {
			
			PropertyToGraphicAttributeMappingX p2gam = (PropertyToGraphicAttributeMappingX) iterator.next();
			
			// caching
			p2gam = (PropertyToGraphicAttributeMappingX) 
					RVLUtils.tryReplaceWithCashedInstanceForSameURI(p2gam, PropertyMappingX.class)
					.castTo(PropertyToGraphicAttributeMappingX.class);
			
			if (p2gam.isDisabled()) {
				LOGGER.info("Ignored disabled normal P2GAM mapping " + p2gam.toStringSummary() );
				continue;
			}
			
			interpretNormalP2GArvlMapping(p2gam);
		}

		LOGGER.fine("The size of the Resource-to-GraphicObjectX map is " + resourceGraphicObjectMap.size()+".");
		
	}

	
	/**
	 * Interprets a normal P2GA mapping with an implicit value mapping and triggers calculating its values. 
	 * ONLY ONE VALUE MAPPING is currently handled! Therefore, explicit value mappings, which are usually more than one are handled by 
	 * interpretSimpleP2GAMappings.
	 * Creates GO for all affected resources if they don't exist already.
	 * @param p2gam 
	 */
	protected void interpretNormalP2GArvlMapping(PropertyToGraphicAttributeMappingX p2gam) {

		LOGGER.info("Interpret P2GAM mapping " + p2gam.toStringSummary() );

		try {
			
			GraphicAttribute tga = p2gam.getTargetAttribute();

		    // get a statement set 
		    Set<Statement> stmtSet = DataQuery.findRelationsOnInstanceOrClassLevel(
		    		modelSet,
		    		OGVICProcess.GRAPH_DATA,
		    		(PropertyMappingX) p2gam.castTo(PropertyMappingX.class),
		    		true,
		    		null,
		    		null
		    		); 

			// get the mapping table SV->TV
			Map<Node, Node> svUriTVuriMap = p2gam.getCalculatedValues(stmtSet);	
		    
		    
		    // for all statements check whether there is a tv for the sv
		    for (Iterator<Statement> stmtSetIt = stmtSet.iterator(); stmtSetIt
					.hasNext();) {
		    	
		    	Statement statement = (Statement) stmtSetIt.next();
		    	
				// create a GO for each subject of the statement
			    GraphicObjectX go = createOrGetGraphicObject(statement.getSubject());

		    	Node sv = statement.getObject();

				LOGGER.finest("trying to find and apply value mapping for sv " + sv.toString());
				
				// get the target value for the sv
		    	Node tv = svUriTVuriMap.get(sv);
		    	
		    	// if we found a tv for the sv
		    	if (null != tv) {
			    	applyGraphicValueToGO(tga, tv, sv, go);	
			    	
			    	applyInheritanceOfTargetValue(p2gam, statement.getSubject(), tv);
		    	}
		    	
			}
		    
		} catch (InsufficientMappingSpecificationException e) {
			LOGGER.warning("No resources will be affected by mapping " + p2gam.asURI() + " (" + e.getMessage() + ")" );
		} 
			
	}
	
	/**
	 * Interprets only the simple P2GA mappings, i.e. those without need for calculating value mappings. 
	 * Unlike interpretNormalP2GAMappings, multiple VMs are considered.
	 * Creates GO for all affected resources if they don't exist already.
	 */
	protected void interpretSimpleP2GArvlMappings() {
		
		Set<PropertyToGraphicAttributeMappingX> setOfSimpleP2GAMappings = MappingQuery.getAllP2GAMappingsWithExplicitMappings(modelMappings);
		
		LOGGER.info(NL + "Found " +setOfSimpleP2GAMappings.size()+ " simple P2GA mappings.");
		
		// for each simple mapping
		for (Iterator<PropertyToGraphicAttributeMappingX> iterator = setOfSimpleP2GAMappings
				.iterator(); iterator.hasNext();) {
			
			PropertyToGraphicAttributeMappingX p2gam = (PropertyToGraphicAttributeMappingX) iterator.next();
			
			// caching
			p2gam = (PropertyToGraphicAttributeMappingX) 
					RVLUtils.tryReplaceWithCashedInstanceForSameURI(p2gam, PropertyMappingX.class)
					.castTo(PropertyToGraphicAttributeMappingX.class);
			
			if (p2gam.isDisabled()) {
				LOGGER.info("Ignored disabled simple P2GAM mapping " + p2gam );
				continue;
			}

			LOGGER.info("Interpret simple P2GAM mapping " + p2gam );
			
			// get the mapping table SV->TV
			//Map<Node, Node> svUriTVuriMap = p2gam.getExplicitlyMappedValues();	
			
			// get the extended mapping table offering also tv for subclasses of the sv 
			// TODO generalize this to other transitive properties
			Map<Node, Node> svUriTVuriMap = RVLUtils.extendMappingTable(modelSet, p2gam.getExplicitlyMappedValues(), new Property(modelData, RDFS.subClassOf, false) );	
			
			try {
				GraphicAttribute tga = p2gam.getTargetAttribute();
	
			    Set<Statement> theStatementWithOurObject = DataQuery.findRelationsOnInstanceOrClassLevel(
			    		modelSet,
			    		OGVICProcess.GRAPH_DATA,
			    		(PropertyMappingX) p2gam.castTo(PropertyMappingX.class),
			    		true,
			    		null,
			    		null
			    		); 

			    for (Iterator<Statement> stmtSetIt = theStatementWithOurObject.iterator(); stmtSetIt
						.hasNext();) {
					Statement statement = (Statement) stmtSetIt.next();
					
					// create a GO for each subject
				    GraphicObjectX go = createOrGetGraphicObject(statement.getSubject());
				    
			    	Node sv = statement.getObject(); 
			    	Resource subject = statement.getSubject();
							
					// get the target value for the sv
			    	Node tv = svUriTVuriMap.get(sv);
			    	
			    	// if we found a tv for the sv
			    	if (null != tv) {
			    		
			    		// apply the target value to the GO itself
			    		
			    		applyGraphicValueToGO(tga, tv, sv, go);
			    		
			    		applyInheritanceOfTargetValue(p2gam, subject, tv); 
			    	}
			    }
			
			} catch (InsufficientMappingSpecificationException e) {
				LOGGER.warning("No resources will be affected by mapping " + p2gam + " (" + e.getMessage() + ")" );
			} 
			
		}
	} 

	/**
	 * Interprets IdentityMappings, i.e. mappings where the source value will be
	 * passed through to the target attribute without changing it.
	 * @throws MappingException 
	 */
	protected void interpretIdentityMappings() {

		Set<IdentityMappingX> mappingSet = MappingQuery
				.getAllIdentityMappings(modelMappings);

		LOGGER.info(NL + "Found " + mappingSet.size()
				+ " identity mappings.");

		// for each identity mapping
		for (Iterator<IdentityMappingX> iterator = mappingSet.iterator(); iterator.hasNext();) {

			IdentityMappingX mapping =  (IdentityMappingX) iterator.next();

			// caching
			mapping = (IdentityMappingX) 
					RVLUtils.tryReplaceWithCashedInstanceForSameURI(mapping, PropertyMappingX.class)
					.castTo(IdentityMappingX.class);

			if (mapping.isDisabled()) {
				LOGGER.info("Ignored disabled mapping "
						+ mapping.toStringSummary());
				continue;
			}

			try {
				new IdentityMappingHandler(modelSet, this, modelAVM).handleIdentityMapping(mapping);
			} catch (MappingException e) {
				LOGGER.severe("Identity mapping could not be interpreted: " + e.getMessage());
			}
		}

		LOGGER.fine("The size of the Resource-to-GraphicObjectX map is "
				+ resourceGraphicObjectMap.size() + ".");

	}

	

}
