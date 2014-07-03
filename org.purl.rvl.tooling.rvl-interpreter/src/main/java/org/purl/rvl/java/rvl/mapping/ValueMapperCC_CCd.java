package org.purl.rvl.java.rvl.mapping;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.purl.rvl.exception.UnexpressiveMappingSpecificationException;
import org.purl.rvl.java.rvl.ValueMappingX;

public class ValueMapperCC_CCd extends ValueMapper {
	
	private final static Logger LOGGER = Logger.getLogger(ValueMapperCC_CCd.class.getName()); 
	
	public ValueMapperCC_CCd() {
		super();
	}
	
	public Set<CalculatedValueMapping> calculateValueMappings(ValueMappingX valueMapping) {
		
		int discreteStepCount = -1; 
		float discreteStepSizeSv = -1; 
		float discreteStepSizeTv = -1; 
		
		float svLowerBoundValue;
		float svUpperBoundValue;
		float tvLowerBoundValue;
		float tvUpperBoundValue;

		try {
			svLowerBoundValue = valueMapping.getSourceValuesContinuousInterval().getLowerBoundAsFloat();
			LOGGER.finest("sv lower bound: " + svLowerBoundValue );
			svUpperBoundValue = valueMapping.getSourceValuesContinuousInterval().getUpperBoundAsFloat();
			LOGGER.finest("sv upper bound: " + svUpperBoundValue );
			tvLowerBoundValue = valueMapping.getTargetValuesContinuousInterval().getLowerBoundAsFloat();
			LOGGER.finest("tv lower bound: " + tvLowerBoundValue );
			tvUpperBoundValue = valueMapping.getTargetValuesContinuousInterval().getUpperBoundAsFloat();
			LOGGER.finest("tv upper bound: " + tvUpperBoundValue );
			
			for (Iterator<Statement> iterator = valueMapping.getAffectedStatements().iterator(); iterator.hasNext();) {
				
				Statement statement = (Statement) iterator.next();
				
				Literal sv = statement.getObject().asLiteral();
				float svValue = Float.parseFloat(sv.getValue());
				float tvValue;
				
				LOGGER.finest("Calculating continuous tv for continuous sv-value " + svValue);
				
				Literal tvLiteral = null;
				
				// TODO evaluate out of range settings - crop / cut settings
				
				if (svValue <= svLowerBoundValue) {
					
					tvValue = tvLowerBoundValue;
					
				} else if (svValue >= svUpperBoundValue) {
					
					tvValue = tvUpperBoundValue;
					
				} else { // normal case within ranges
				
					if (valueMapping.isDiscretize() && valueMapping.hasDiscreteStepCount()) {
						
						float svRange = svUpperBoundValue-svLowerBoundValue;
						float tvRange = tvUpperBoundValue-tvLowerBoundValue;
						
						discreteStepCount = valueMapping.getDiscreteStepCount();
						
						if (discreteStepCount >= 2) {
							
							LOGGER.finest("discrete step count: " + discreteStepCount );
							
							discreteStepSizeSv = svRange/(discreteStepCount);
							LOGGER.finest("discrete step size sv: " + discreteStepSizeSv );
							
							discreteStepSizeTv = tvRange/(discreteStepCount-1);
							LOGGER.finest("discrete step size tv: " + discreteStepSizeTv );
							
							// the follwoing does not work for sv equal svLowerBoundValue (case caught above)
							tvValue = tvLowerBoundValue + ((int) ((svValue - svLowerBoundValue) / discreteStepSizeSv)) * discreteStepSizeTv; // TODO this does apparently not work for step count  = {0,1} -> div/0 -> catch earlier
							
							LOGGER.finest("tvValue: " + tvValue );
						}
						else {
							throw new UnexpressiveMappingSpecificationException("No value mappings could be calculated! Discretization steps must be greater or equal 2");
						}
						
					} else {
							float svRange = svUpperBoundValue-svLowerBoundValue;
							float tvRange = tvUpperBoundValue-tvLowerBoundValue;
							float stretchFactor =  tvRange/svRange;
							tvValue = svValue * stretchFactor;
					}
				}
				
				tvLiteral = new DatatypeLiteralImpl(tvValue + "", new URIImpl("http://www.w3.org/2001/XMLSchema#float"));
				
				if (null != tvLiteral) {
					cvms.add(new CalculatedValueMapping(sv, tvLiteral));
				}
				
			}
			
		} catch (UnexpressiveMappingSpecificationException e) {
			LOGGER.warning(e.getMessage());
		} catch (Exception e) {
			// TODO fix this bad exception handling
			LOGGER.warning("sv/tv lower/upper bound  or sv itself is not a literal or stepcount is < 2," +
					" will try to get values from the resource ... TOBEIMPLEMENTED");
		}
		
		return cvms;
		
	}

	@Override
	public Set<CalculatedValueMapping> calculateValueMappings() throws UnexpressiveMappingSpecificationException {
		// TODO decide on one variant
		return null;
	}

}
