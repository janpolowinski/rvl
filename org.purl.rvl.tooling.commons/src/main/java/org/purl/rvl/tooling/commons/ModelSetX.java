/**
 * 
 */
package org.purl.rvl.tooling.commons;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;

/**
 * Wraps a ModelSet to allow for conveniently accessing the various models such as data
 * and mapping model from a single place.
 * Besides convenience the more important reason, why we wrap the ModelSet is that for some reason
 * the set of namespaces (needed for the data model) cannot be retrieved from the Jena implementation of a ModelSet (but is empty).
 * Therefore, the data model is stored (redundantly) separately. 
 * 
 * @author Jan
 *
 */
public class ModelSetX {
	
	private ModelSet modelSet;
	private Model modelData;

	/**
	 * @param modelSet
	 */
	public ModelSetX(ModelSet modelSet) {
		super();
		this.modelSet = modelSet;
	}
	
	/**
	 * @param modelSet
	 */
	public ModelSetX(ModelSet modelSet, Model  modelData) {
		super();
		this.modelSet = modelSet;
		this.modelData = modelData;
	}

	/**
	 * @return the modelSet
	 */
	public ModelSet getModelSet() {
		return modelSet;
	}

	/**
	 * @return the modelData
	 */
	public Model getModelData() {
		return modelData;
	}

	/**
	 * @param modelData the modelData to set
	 */
	public void setModelData(Model modelData) {
		this.modelData = modelData;
	}


}
