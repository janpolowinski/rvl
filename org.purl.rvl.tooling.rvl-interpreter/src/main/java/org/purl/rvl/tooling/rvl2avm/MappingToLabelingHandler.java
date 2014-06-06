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
import org.purl.rvl.java.gen.viso.graphic.Labeling;
import org.purl.rvl.java.gen.viso.graphic.Superimposition;
import org.purl.rvl.java.rvl.PropertyMappingX;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;
import org.purl.rvl.java.viso.graphic.ShapeX;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.RVLUtils;

/**
 * @author Jan Polowinski
 *
 */
public class MappingToLabelingHandler extends MappingHandler {
	
	public MappingToLabelingHandler(ModelSet modelSet, RVLInterpreter rvlInterpreter, Model modelAVM) {
		super(modelSet,rvlInterpreter,modelAVM);
	}

	private final static Logger LOGGER = Logger.getLogger(MappingToLabelingHandler.class .getName()); 

	public void interpretMappingToLabeling(
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
			LOGGER.severe("Statement iterator was null, no labeling relations could be interpreted for " + p2go2orm.asURI());
			return;
		}		
		
		while (stmtSetIterator.hasNext() && processedGraphicRelations < OGVICProcess.MAX_GRAPHIC_RELATIONS_PER_MAPPING) {
			
			Statement statement = (Statement) stmtSetIterator.next();
						
			try {
				Resource subject = statement.getSubject();
				Resource object = statement.getObject().asResource();
				
				LOGGER.finest("Subject label " + AVMUtils.getGoodLabel(subject,modelAVM));
				LOGGER.finest("Object label " + AVMUtils.getGoodLabel(object,modelAVM));
	
				LOGGER.fine("Statement to be mapped : " + statement);
				
				// For each statement, create a startNode GO representing the subject (if not exists)
			    GraphicObjectX subjectNode = rvlInterpreter.createOrGetGraphicObject(subject);
		    	LOGGER.finest("Created GO for subject: " + subject.toString());
				
				// For each statement, create an endNode GO representing the object (if not exists)	
				GraphicObjectX label = new GraphicObjectX(modelAVM, false); // create an additional object here, don't reuse existing ones!
		    	LOGGER.finest("Created new Label-GO for object: " + object.toString());
		    	
		      	Labeling rel = new Labeling(modelAVM,"http://purl.org/rvl/example-avm/GR_" + rvlInterpreter.createNewInternalID(), true);
		    	rel.setLabel(AVMUtils.getGoodLabel(p2go2orm.getTargetGraphicRelation(), modelAVM));

		    	subjectNode.setLabeledwith(rel);
		    	rel.setLabelinglabel(label);
		    	rel.setLabelingattachedBy(Superimposition.RDFS_CLASS); // passing a node here
		    	rel.setLabelingbase(subjectNode);
		    	
		    	// set default shape of icon labels
		    	label.setShapenamed(new ShapeX(modelAVM, "http://purl.org/viso/shape/commons/Circle", false));
		    	
		    	// submappings
				if(p2go2orm.hasSub_mapping()){
					rvlInterpreter.applySubmappings(p2go2orm,statement,rel);
				}
				
			} catch (Exception e) {
				LOGGER.warning("Problem creating GOs: " + e.getMessage());
			}
		}
		
	}

}
