package org.purl.rvl.java.rvl.mapping;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.purl.rvl.java.rvl.ValueMappingX;

public class ValueMapperOC_UC extends ValueMapper {
	
	private final static Logger LOGGER = Logger.getLogger(ValueMapperOC_UC.class.getName()); 

	
	@Override
	public Set<CalculatedValueMapping> calculateValueMappings(ValueMappingX valueMapping) {

		Iterator<Node> svIt;
		int numberOfSv ;
		int numberOfTv ;
		float lowerBoundValue;
		float upperBoundValue;
		int discreteStepCount = 1;
		float discreteStepSize = -1; 

		if (null != valueMapping.getSourceValuesOrderedSet()) {
			svIt = valueMapping.getSourceValuesOrderedSet().iterator();
			numberOfSv = valueMapping.getSourceValuesOrderedSet().size();
		} 
		else {
			svIt = valueMapping.getSourceValuesUnorderedSet().iterator();
			numberOfSv = valueMapping.getSourceValuesUnorderedSet().size();
		}
		
		// TODO check for discrete step count here
		numberOfTv = numberOfSv;
		discreteStepCount = numberOfTv;
		LOGGER.finest("discrete step count: " + discreteStepCount );

		try {
			
			lowerBoundValue = valueMapping.getTargetValuesContinuousInterval().getLowerBoundAsFloat();
			LOGGER.finest("lower bound: " + lowerBoundValue );
			
			upperBoundValue = valueMapping.getTargetValuesContinuousInterval().getUpperBoundAsFloat();
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
