/**
 * 
 */
package org.purl.rvl.tooling.rvl2avm;

import java.util.Iterator;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.purl.rvl.exception.InsufficientMappingSpecificationException;
import org.purl.rvl.exception.MappingException;
import org.purl.rvl.exception.NotImplementedMappingFeatureException;
import org.purl.rvl.tooling.util.AVMUtils;

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
	
	static final String NL =  System.getProperty("line.separator");
	
	public MappingHandlerBase(ModelSet modelSet, RVLInterpreter rvlInterpreter, Model modelAvm) {
		super();
		this.modelSet = modelSet;
		this.rvlInterpreter = rvlInterpreter;
		this.modelAVM = modelAvm;
	}

	abstract void encodeStatement(Statement statement)
			throws InsufficientMappingSpecificationException, NotImplementedMappingFeatureException, MappingException;

	
	
	protected void logStatementDetails(Logger logger, Statement statement) {
		
		logger.fine(NL + "Encoding statement " + statement);
		logger.finest("  Subject label: " + AVMUtils.getGoodNodeLabel(statement.getSubject(), modelAVM));
		logger.finest("  Object label: " + AVMUtils.getGoodNodeLabel(statement.getObject(), modelAVM));
	}
	
}
