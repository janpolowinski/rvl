/**
 * 
 */
package org.purl.rvl.java.rvl.mapping;

import java.util.HashSet;
import java.util.Set;

import org.purl.rvl.exception.MappingException;
import org.purl.rvl.java.rvl.ValueMappingX;

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
	 * @throws MappingException 
	 */
	public abstract Set<CalculatedValueMapping> calculateValueMappings(ValueMappingX valueMapping) throws MappingException ;

	protected ValueMapper() {
		super();
		cvms = new HashSet<CalculatedValueMapping>();
	} 

}
