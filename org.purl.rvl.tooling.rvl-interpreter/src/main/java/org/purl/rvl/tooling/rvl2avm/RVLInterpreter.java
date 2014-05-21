package org.purl.rvl.tooling.rvl2avm;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;

/**
 * @author Jan Polowinski
 *
 */
public interface RVLInterpreter {

	public abstract void init(Model modelAVM, ModelSet modelSet);

	/**
	 * Interpret all supported RVL mappings 
	 */
	public abstract void interpretMappings();

}