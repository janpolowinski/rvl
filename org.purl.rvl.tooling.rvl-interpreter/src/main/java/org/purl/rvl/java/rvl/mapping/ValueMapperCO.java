package org.purl.rvl.java.rvl.mapping;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.purl.rvl.exception.UnexpressiveMappingSpecificationException;
import org.purl.rvl.java.rvl.ValueMappingX;

public class ValueMapperCO extends ValueMapper {
	
	private final static Logger LOGGER = Logger.getLogger(ValueMapperCO.class.getName()); 
	
	public ValueMapperCO() {
		super();
	}
	
	public Set<CalculatedValueMapping> calculateValueMappings(ValueMappingX valueMapping) {
		
		
		int discreteStepCount = -1; 
		float discreteStepSizeSv = -1; 
		
		float svLowerBoundValue;
		float svUpperBoundValue;
		
		int numberOfTv ;
		
		// get the number of target values
		if (null != valueMapping.getTargetValuesList()) {
			numberOfTv = valueMapping.getTargetValuesList().size();
		} 
		else {
			numberOfTv = valueMapping.getTargetValuesUnorderedSet().size();
		}		
		
		try {
			svLowerBoundValue = valueMapping.getSourceValuesContinuousInterval().getLowerBoundAsFloat();
			LOGGER.finest("sv lower bound: " + svLowerBoundValue );
			svUpperBoundValue = valueMapping.getSourceValuesContinuousInterval().getUpperBoundAsFloat();
			LOGGER.finest("sv upper bound: " + svUpperBoundValue );
			
			for (Iterator<Statement> iterator = valueMapping.getAffectedStatements().iterator(); iterator.hasNext();) {
				
				Statement statement = (Statement) iterator.next();
				
				DatatypeLiteral sv = statement.getObject().asDatatypeLiteral();
				float svValue = Float.parseFloat(sv.getValue());
				
				LOGGER.finest("Selecting discrete tv for continuous sv-value " + svValue);
				
				Node tv = null;
				
				// TODO evaluate out of range settings - crop / cut settings
				
				if (svValue <= svLowerBoundValue) {
					
					tv = valueMapping.getTargetValuesList().get(0);
					
				} else if (svValue >= svUpperBoundValue) {
					
					tv = valueMapping.getTargetValuesList().get(valueMapping.getTargetValuesList().size()-1);
					
				} else { 
				
					if (valueMapping.hasDiscreteStepCount()) {
						
						discreteStepCount = valueMapping.getDiscreteStepCount(); // TODO
						
					} else {
						
						discreteStepCount = numberOfTv;
						
						float svRange = svUpperBoundValue-svLowerBoundValue;
						
						if (discreteStepCount >= 2) {
							
							LOGGER.finest("discrete step count: " + discreteStepCount );
							
							discreteStepSizeSv = svRange/(discreteStepCount);
							LOGGER.finest("discrete step size sv: " + discreteStepSizeSv );
							
							int tvListPosition = ((int) ((svValue - svLowerBoundValue) / discreteStepSizeSv)); // this does not work for sv equal svLowerBoundValue (case caught above)
							tv = valueMapping.getTargetValuesList().get(tvListPosition);				
							LOGGER.finest("tv: " + tv );
							
						}
						else {
							throw new UnexpressiveMappingSpecificationException("No value mappings could be calculated! Discretization steps must be greater or equal 2");
						}
		
					}
				}
				
				if (null != tv) {
					cvms.add(new CalculatedValueMapping(sv, tv));
				}
				
			}
			
		} catch (UnexpressiveMappingSpecificationException e) {
			LOGGER.warning(e.getMessage());
		} catch (Exception e) {
			LOGGER.finest("sv lower/upper bound  or sv itself is not a literal");
		}
		
		return cvms;
		
	}

	@Override
	public Set<CalculatedValueMapping> calculateValueMappings() throws UnexpressiveMappingSpecificationException {
		// TODO decide on one variant
		return null;
	}

}
