package org.purl.rvl.java.rvl.mapping;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.Node;
import org.purl.rvl.exception.UnexpressiveMappingSpecificationException;

public class ValueMapperUU_OU_UO extends ValueMapper {
	
	private final static Logger LOGGER = Logger.getLogger(ValueMapperUU_OU_UO.class.getName()); 
	
	private Set<Node> sourceValuesUnorderedSet;
	private List<Node> sourceValuesOrderedSet;
	private List<Node> targetValuesList;
	private Set<Node> targetValuesUnorderedSet;


	
	public ValueMapperUU_OU_UO(
			List<Node> sourceValuesOrderedSet,
			Set<Node> sourceValuesUnorderedSet,
			List<Node> targetValuesList,
			Set<Node> targetValuesUnorderedSet
			) {
		super();

		this.sourceValuesOrderedSet = sourceValuesOrderedSet;
		this.sourceValuesUnorderedSet = sourceValuesUnorderedSet;
		this.targetValuesList = targetValuesList;
		this.targetValuesUnorderedSet = targetValuesUnorderedSet;
	}
	
	@Override
	public Set<CalculatedValueMapping> calculateValueMappings() 
			throws UnexpressiveMappingSpecificationException {

		Iterator<Node> svIt;
		Iterator<Node> tvIt;
		int numberOfSv ;
		int numberOfTv ;
		
		if (null != sourceValuesOrderedSet) {
			svIt = sourceValuesOrderedSet.iterator();
			numberOfSv = sourceValuesOrderedSet.size();
		} 
		else {
			svIt = sourceValuesUnorderedSet.iterator();
			numberOfSv = sourceValuesUnorderedSet.size();
		}
		
		if (null != targetValuesList) {
			tvIt = targetValuesList.iterator();
			numberOfTv = targetValuesList.size();	
		} else {
			tvIt = targetValuesUnorderedSet.iterator();
			numberOfTv = targetValuesUnorderedSet.size();	
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
