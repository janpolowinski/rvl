/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import java.util.Iterator;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.java.gen.viso.graphic.Containment;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.RVLUtils;

/**
 * @author Jan Polowinski
 * TODO cloned from linking, much duplicated code
 */
public class MappingToContainmentHandler extends MappingHandlerBase {
	
	public MappingToContainmentHandler(ModelSet modelSet, RVLInterpreter rvlInterpreter, Model modelAVM) {
		super(modelSet,rvlInterpreter,modelAVM);
	}

	private final static Logger LOGGER = Logger.getLogger(MappingToContainmentHandler.class .getName()); 

	public void handleP2GOTORMapping(
			PropertyToGO2ORMappingX p2go2orm)
			throws InsufficientMappingSpecificationException {
		
		Iterator<Statement> stmtSetIterator = RVLUtils.findRelationsOnInstanceOrClassLevel(
				modelSet,
				OGVICProcess.GRAPH_DATA,
				(PropertyMappingX) p2go2orm.castTo(PropertyMappingX.class),
				true,
				null,
				null
				).iterator();
		
		int processedGraphicRelations = 0;	
		
		if(null==stmtSetIterator) {
			LOGGER.severe("Statement iterator was null, no containment relations could be interpreted for " + p2go2orm.asURI());
			return;
		}
		
		while (stmtSetIterator.hasNext() && processedGraphicRelations < OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING) {
			
			Statement statement = (Statement) stmtSetIterator.next();
						
			try {
				Resource subject = statement.getSubject();
				Resource object = statement.getObject().asResource();
				//Node object = statement.getObject();
				
				LOGGER.finest("Subject label " + AVMUtils.getGoodLabel(subject,modelAVM));
				LOGGER.finest("Object label " + AVMUtils.getGoodLabel(object,modelAVM));
				LOGGER.fine("Statement to be mapped : " + statement);

				// For each statement, create a container GO representing the subject (if not exists)
			    GraphicObjectX subjectContainer = rvlInterpreter.createOrGetGraphicObject(subject);

				// For each statement, create a containee GO representing the object (if not exists)
				GraphicObjectX objectContainee = rvlInterpreter.createOrGetGraphicObject(object);
				
		    	LOGGER.finest("Created GO for subject: " + subject.toString());
		    	LOGGER.finest("Created GO for object: " + object.toString());

				// generic graphic relation needed for submappings 
				// (could also be some super class of containment ,...)
				Resource rel = null;
									
		    	// create the containment relation
		    	//Containment dlRel = new Containment(modelAVM, true);
		    	Containment containmentRel = new Containment(modelAVM,"http://purl.org/rvl/example-avm/GR_" + rvlInterpreter.createNewInternalID(), true);
		    	containmentRel.setLabel(AVMUtils.getGoodLabel(p2go2orm.getTargetGraphicRelation(), modelAVM));
		    	
				// configure the relation
				if(!p2go2orm.isInvertSourceProperty()) {
					containmentRel.setContainmentcontainer(subjectContainer);
					containmentRel.setContainmentcontainee(objectContainee);
					subjectContainer.addContains(containmentRel);
					objectContainee.addContainedby(containmentRel);
				} else {
					containmentRel.setContainmentcontainee(subjectContainer);
					containmentRel.setContainmentcontainer(objectContainee);
					subjectContainer.addContainedby(containmentRel);
					objectContainee.addContains(containmentRel);
				}
				
				rel=containmentRel;
					
				// submappings
				if(p2go2orm.hasSub_mapping()){
					
					if(null != rel) {
						rvlInterpreter.applySubmappings(p2go2orm,statement,rel); // Containment etc need to be subclasses of (n-ary) GraphicRelation
					} else {
						LOGGER.warning("Submapping existed, but could not be applied, since no parent graphic relation was provided.");
					}
				}
				
			}
			catch (Exception e) {
				LOGGER.warning("Problem creating GOs: " + e.getMessage());
			}
			
			processedGraphicRelations++;	
		}
		
	}

}
