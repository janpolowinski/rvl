/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.purl.rvl.tooling.process.OGVICProcess;

/**
 * @author Jan Polowinski
 *
 */
public class MappingHandler {
	
	protected ModelSet modelSet;
	protected Model modelAVM;
	protected Model modelData;
	protected Model modelMappings;
	protected Model modelVISO;
	
	protected RVLInterpreter rvlInterpreter;
	
	public MappingHandler(ModelSet modelSet, RVLInterpreter rvlInterpreter, Model modelAvm) {
		super();
		this.modelSet = modelSet;
		this.rvlInterpreter = rvlInterpreter;
		this.modelAVM = modelAvm;
	}

	public void init(Model modelAVM, ModelSet modelSet) {
			this.modelSet = modelSet;
			//this.modelAVM = modelSet.getModel(OGVICProcess.GRAPH_AVM); // somehow the AVM model seems not to be available here
			this.modelData = modelSet.getModel(OGVICProcess.GRAPH_DATA);
			this.modelMappings = modelSet.getModel(OGVICProcess.GRAPH_MAPPING);
			this.modelVISO = modelSet.getModel(OGVICProcess.GRAPH_VISO);
		}

}
