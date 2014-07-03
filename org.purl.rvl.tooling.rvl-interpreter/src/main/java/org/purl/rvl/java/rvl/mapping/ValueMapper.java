/**
 * 
 */
package org.purl.rvl.java.rvl.mapping;

import java.util.HashSet;
import java.util.Set;

import org.purl.rvl.exception.UnexpressiveMappingSpecificationException;

/**
 * @author Jan Polowinski
 * Does the actual value mapping calculations. Subclasses calculate the various cases that 
 * emerge from combining different collections or intervals of source and target values 
 * with different scales of measurement.
 *
 */

public abstract class ValueMapper {
	
	protected HashSet<CalculatedValueMapping> cvms;

	/**
	 * @return - the set of CalculatedValueMapping 's 
	 * @throws UnexpressiveMappingSpecificationException 
	 */
	public abstract Set<CalculatedValueMapping> calculateValueMappings() throws UnexpressiveMappingSpecificationException ;

	protected ValueMapper() {
		super();
		cvms = new HashSet<CalculatedValueMapping>();
	} 

}
