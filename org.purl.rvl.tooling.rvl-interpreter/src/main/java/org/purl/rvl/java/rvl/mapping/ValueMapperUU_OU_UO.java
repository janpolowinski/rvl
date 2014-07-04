package org.purl.rvl.java.rvl.mapping;

import java.util.Iterator;
import java.util.Set;

import org.ontoware.rdf2go.model.node.Node;
import org.purl.rvl.exception.UnexpressiveMappingSpecificationException;
import org.purl.rvl.java.rvl.ValueMappingX;
//import java.util.logging.Logger;

public class ValueMapperUU_OU_UO extends ValueMapper {
	
	//private final static Logger LOGGER = Logger.getLogger(ValueMapperUU_OU_UO.class.getName()); 

	
	@Override
	public Set<CalculatedValueMapping> calculateValueMappings(ValueMappingX valueMapping) 
			throws UnexpressiveMappingSpecificationException {

		Iterator<Node> svIt;
		Iterator<Node> tvIt;
		int numberOfSv ;
		int numberOfTv ;
		
		if (null != valueMapping.getSourceValuesOrderedSet()) {
			svIt = valueMapping.getSourceValuesOrderedSet().iterator();
			numberOfSv = valueMapping.getSourceValuesOrderedSet().size();
		} 
		else {
			svIt = valueMapping.getSourceValuesUnorderedSet().iterator();
			numberOfSv = valueMapping.getSourceValuesUnorderedSet().size();
		}
		
		if (null != valueMapping.getTargetValuesList()) {
			tvIt = valueMapping.getTargetValuesList().iterator();
			numberOfTv = valueMapping.getTargetValuesList().size();	
		} else {
			tvIt = valueMapping.getTargetValuesUnorderedSet().iterator();
			numberOfTv = valueMapping.getTargetValuesUnorderedSet().size();	
		}


		if (numberOfTv == 1) { // all sv get the (same) tv
			
			Node tv = tvIt.next();
			
			while (svIt.hasNext()) {
				
				Node sv = svIt.next();
				cvms.add(new CalculatedValueMapping(sv.asURI(), tv.asURI()));
			}

		} else if (numberOfSv <= numberOfTv) {
			
			// TODO ignored shuffling to random for the moment
			while (svIt.hasNext() && tvIt.hasNext()) {
				
				Node sv = svIt.next();
				Node tv = tvIt.next();
				
				cvms.add(new CalculatedValueMapping(sv, tv));
			}

		} else { // numberOfSv > numberOfTv
			
			throw new UnexpressiveMappingSpecificationException();

		}
		
		return cvms;
		
	}

}
