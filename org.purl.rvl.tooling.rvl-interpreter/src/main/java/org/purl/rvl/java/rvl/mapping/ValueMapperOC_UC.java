package org.purl.rvl.java.rvl.mapping;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.purl.rvl.java.rvl.IntervalX;

public class ValueMapperOC_UC extends ValueMapper {
	
	private final static Logger LOGGER = Logger.getLogger(ValueMapperOC_UC.class.getName()); 
	
	private Set<Node> sourceValuesUnorderedSet;
	private List<Node> sourceValuesOrderedSet;
	private IntervalX targetValuesContinuousInterval;

	
	public ValueMapperOC_UC(List<Node> sourceValuesOrderedSet, Set<Node> sourceValuesUnorderedSet, IntervalX targetValuesContinuousInterval) {
		super();
		this.sourceValuesUnorderedSet = sourceValuesUnorderedSet;
		this.sourceValuesOrderedSet = sourceValuesOrderedSet;
		this.targetValuesContinuousInterval = targetValuesContinuousInterval;
	}
	
	@Override
	public Set<CalculatedValueMapping> calculateValueMappings() {

		Iterator<Node> svIt;
		int numberOfSv ;
		int numberOfTv ;
		float lowerBoundValue;
		float upperBoundValue;
		int discreteStepCount = 1;
		float discreteStepSize = -1; 

		if (null != sourceValuesOrderedSet) {
			svIt = sourceValuesOrderedSet.iterator();
			numberOfSv = sourceValuesOrderedSet.size();
		} 
		else {
			svIt = sourceValuesUnorderedSet.iterator();
			numberOfSv = sourceValuesUnorderedSet.size();
		}
		
		// TODO check for discrete step count here
		numberOfTv = numberOfSv;
		discreteStepCount = numberOfTv;
		LOGGER.finest("discrete step count: " + discreteStepCount );

		try {
			
			lowerBoundValue = targetValuesContinuousInterval.getLowerBoundAsFloat();
			LOGGER.finest("lower bound: " + lowerBoundValue );
			
			upperBoundValue = targetValuesContinuousInterval.getUpperBoundAsFloat();
			LOGGER.finest("upper bound: " + upperBoundValue );
			
			discreteStepSize = (upperBoundValue - lowerBoundValue)/(discreteStepCount-1);
			LOGGER.finest("discrete step size: " + discreteStepSize );
			
			int step = 0;
			
			while (svIt.hasNext()) {
				
				Node sv = svIt.next();
				Literal tvLiteral = new DatatypeLiteralImpl((lowerBoundValue + step*discreteStepSize) + "", new URIImpl("http://www.w3.org/2001/XMLSchema#float"));
				cvms.add(new CalculatedValueMapping(sv.asURI(), tvLiteral));
				step++;
			}
			
		} catch (Exception e) {
			LOGGER.finest("lower or upper bound is not a literal," +
					" will try to get values from the resource ... TOBEIMPLEMENTED");
		}
		
		return cvms;
		
	}

}
