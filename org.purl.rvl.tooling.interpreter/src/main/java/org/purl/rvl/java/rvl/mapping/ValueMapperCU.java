package org.purl.rvl.java.rvl.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.purl.rvl.exception.UnexpressiveMappingSpecificationException;
import org.purl.rvl.java.rvl.ValueMappingX;

public class ValueMapperCU extends ValueMapper {
	
	private final static Logger LOGGER = Logger.getLogger(ValueMapperCU.class.getName()); 
	
	public ValueMapperCU() {
		super();
	}
	
	@Override
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
				
				LOGGER.finest("Selecting discrete, unordered tv for continuous sv-value " + svValue);
				
				List<Node> targetValuesUnorderedSetList = new ArrayList<Node>(valueMapping.getTargetValuesUnorderedSet());
				
				Node tv = null;
				
				// TODO evaluate out o range settings - crop / cut settings
			
				if (svValue <= svLowerBoundValue || svValue >= svUpperBoundValue) {
					
					// no value mappings calculated for out of range values 
					
				} else { 
		
					discreteStepCount = numberOfTv;
					
					float svRange = svUpperBoundValue-svLowerBoundValue;
					
					if (discreteStepCount >= 2) {
						
						LOGGER.finest("discrete step count: " + discreteStepCount );
						
						discreteStepSizeSv = svRange/(discreteStepCount);
						LOGGER.finest("discrete step size sv: " + discreteStepSizeSv );
						
						int svStep = ((int) ((svValue - svLowerBoundValue) / discreteStepSizeSv)); // this does not work for sv equal svLowerBoundValue (case caught above)
						tv = (Node)targetValuesUnorderedSetList.get(svStep);				
						LOGGER.finest("tv: " + tv );
						
					}
					else if (discreteStepCount == 1) {
						tv = targetValuesUnorderedSetList.get(0);		
					} else { // (0 target values)
						throw new UnexpressiveMappingSpecificationException("No value mappings could be calculated! Discretization steps must be greater or equal 2");
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

}
