package org.purl.rvl.java.rvl.mapping;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.Node;
import org.purl.rvl.java.rvl.ValueMappingX;

public class ValueMapperOO extends ValueMapper {
	
	private final static Logger LOGGER = Logger.getLogger(ValueMapperOO.class.getName()); 
	
	
	@Override
	public Set<CalculatedValueMapping> calculateValueMappings(ValueMappingX valueMapping) {

		Iterator<Node> svIt = valueMapping.getSourceValuesOrderedSet().iterator();
		Iterator<Node> tvIt = valueMapping.getTargetValuesList().iterator();
				
		if (valueMapping.getSourceValuesOrderedSet().size() <= valueMapping.getTargetValuesList().size() ) {
			
			// TODO ignored stretching for the moment
			while (svIt.hasNext() && tvIt.hasNext()) {
				
				Node sv = svIt.next();
				Node tv = tvIt.next();
				
				cvms.add(new CalculatedValueMapping(sv.asURI(), tv.asURI()));
			}
		} 
		else { // valueMapping.getSourceValuesOrderedSet().size() > valueMapping.getTargetValuesList().size()

			while (svIt.hasNext() && tvIt.hasNext()) {
				
				LOGGER.warning("Not enough distinct target values defined (" + valueMapping.getTargetValuesList().size() + ") " +
						"for the number of source values (" +  valueMapping.getSourceValuesOrderedSet().size() + "), " +
						"will cycle values.");

				Node sv = svIt.next();
				Node tv = tvIt.next();

				cvms.add(new CalculatedValueMapping(sv.asURI(), tv.asURI()));
				
				// reset tv iterator if necessary
				if(!tvIt.hasNext()) tvIt = valueMapping.getTargetValuesList().iterator();
			}
		}
		
		return cvms;
		
	}

}
