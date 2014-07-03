package org.purl.rvl.java.rvl.mapping;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.Node;

public class ValueMapperOO extends ValueMapper {
	
	private final static Logger LOGGER = Logger.getLogger(ValueMapperOO.class.getName()); 
	
	private List<Node> sourceValuesOrderedSet;
	private List<Node> targetValuesList;

	
	public ValueMapperOO(List<Node> sourceValuesOrderedSet, List<Node> targetValuesList) {
		super();
		this.sourceValuesOrderedSet = sourceValuesOrderedSet;
		this.targetValuesList = targetValuesList;
	}
	
	@Override
	public Set<CalculatedValueMapping> calculateValueMappings() {

		Iterator<Node> svIt = sourceValuesOrderedSet.iterator();
		Iterator<Node> tvIt = targetValuesList.iterator();
				
		if (sourceValuesOrderedSet.size() <= targetValuesList.size() ) {
			
			// TODO ignored stretching for the moment
			while (svIt.hasNext() && tvIt.hasNext()) {
				
				Node sv = svIt.next();
				Node tv = tvIt.next();
				
				cvms.add(new CalculatedValueMapping(sv.asURI(), tv.asURI()));
			}
		} 
		else { // sourceValuesOrderedSet.size() > targetValuesList.size()

			while (svIt.hasNext() && tvIt.hasNext()) {
				
				LOGGER.warning("Not enough distinct target values defined (" + targetValuesList.size() + ") " +
						"for the number of source values (" +  sourceValuesOrderedSet.size() + "), " +
						"will cycle values.");

				Node sv = svIt.next();
				Node tv = tvIt.next();

				cvms.add(new CalculatedValueMapping(sv.asURI(), tv.asURI()));
				
				// reset tv iterator if necessary
				if(!tvIt.hasNext()) tvIt = targetValuesList.iterator();
			}
		}
		
		return cvms;
		
	}

}
