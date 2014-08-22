package org.purl.rvl.tooling.rvl2avm;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.java.gen.viso.graphic.Containment;
import org.purl.rvl.java.gen.viso.graphic.DirectedLinking;
import org.purl.rvl.java.gen.viso.graphic.Labeling;
import org.purl.rvl.java.gen.viso.graphic.UndirectedLinking;
import org.purl.rvl.java.rvl.IdentityMappingX;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;
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
		
		interpretP2GAMappings(); 
		interpretP2GO2ORMappings();
		interpretIdentityMappings();
		performDefaultLabelMappingForAllGOs();
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
			}

		}
		
		LOGGER.fine("The size of the Resource-to-GraphicObjectX map is " + resourceGraphicObjectMap.size()+".");
	}


	/**
	 * Interprets a normal P2GA mapping with an implicit value mapping and triggers calculating its values.
	 * TODO: ONLY ONE VALUE MAPPING is currently handled! Therefore, explicit value mappings, which are usually more than one are handled by 
	 * interpretSimpleP2GAMappings. 
	 * Creates GO for all affected resources if they don't exist already.
	 */
	protected void interpretP2GAMappings() {
		
		Set<PropertyToGraphicAttributeMappingX> setOfP2GAMappings = MappingQuery.getP2GAMappingsWithAtLeastOneValueMapping(modelMappings);
		
		LOGGER.info(NL + "Found " +setOfP2GAMappings.size()+ " P2GA mappings.");
		
		// for each P2GA mapping
		for (Iterator<PropertyToGraphicAttributeMappingX> iterator = setOfP2GAMappings
				.iterator(); iterator.hasNext();) {
			
			PropertyToGraphicAttributeMappingX p2gam = (PropertyToGraphicAttributeMappingX) iterator.next();
			
			// caching
			p2gam = (PropertyToGraphicAttributeMappingX) 
					RVLUtils.tryReplaceWithCashedInstanceForSameURI(p2gam, PropertyToGraphicAttributeMappingX.class)
					.castTo(PropertyToGraphicAttributeMappingX.class);
			
			if (p2gam.isDisabled()) {
				LOGGER.info("Ignored disabled P2GAM mapping " + p2gam.toStringSummary() );
				continue;
			}
			
			LOGGER.info("Interpret P2GAM mapping " + p2gam.toStringSummary() );

			try {
				
				new MappingToP2GAMHandler(modelSet, this, modelAVM).handleP2GAMMapping(p2gam);
				
			} catch (MappingException e) {
				LOGGER.warning("No resources will be affected by mapping " + p2gam.asURI() + " (" + e.getMessage() + ")" );
			} 

		}

		LOGGER.fine("The size of the Resource-to-GraphicObjectX map is " + resourceGraphicObjectMap.size()+".");
		
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
			
			//System.out.println(ModelUtils.getTypes(modelSet.getModel(OGVICProcess.GRAPH_MAPPING), mapping));

			// caching
			mapping = RVLUtils.tryReplaceWithCashedInstanceForSameURI(mapping, IdentityMappingX.class);

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
