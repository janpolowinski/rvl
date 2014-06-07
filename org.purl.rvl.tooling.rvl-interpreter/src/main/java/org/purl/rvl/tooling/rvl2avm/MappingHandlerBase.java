/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import java.util.Iterator;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;

/**
 * @author Jan Polowinski
 *
 */
public abstract class MappingHandlerBase implements MappingHandler  {
	
	protected ModelSet modelSet;
	protected Model modelAVM;
	
	protected RVLInterpreter rvlInterpreter;
	
	int processedGraphicRelations = 0;
	protected Iterator<Statement> stmtSetIterator;
	
	public MappingHandlerBase(ModelSet modelSet, RVLInterpreter rvlInterpreter, Model modelAvm) {
		super();
		this.modelSet = modelSet;
		this.rvlInterpreter = rvlInterpreter;
		this.modelAVM = modelAvm;
	}

	protected abstract void encodeStatement(Statement statement)
			throws InsufficientMappingSpecificationException;

}
