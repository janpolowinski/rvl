package org.purl.rvl.java.mapping;

/**
 * @author Jan Polowinski
 *
 */
public class CalculatedValueMapping {
	
	private String sourceValue;
	private String targetValue;
	
	public CalculatedValueMapping(String sourceValue, String targetValue) {
		super();
		this.sourceValue = sourceValue;
		this.targetValue = targetValue;
	}
	
	/**
	 * @return the sourceValue
	 */
	public String getSourceValue() {
		return sourceValue;
	}
	/**
	 * @param sourceValue the sourceValue to set
	 */
	public void setSourceValue(String sourceValue) {
		this.sourceValue = sourceValue;
	}
	/**
	 * @return the targetValue
	 */
	public String getTargetValue() {
		return targetValue;
	}
	/**
	 * @param targetValue the targetValue to set
	 */
	public void setTargetValue(String targetValue) {
		this.targetValue = targetValue;
	}
	
	@Override
	public String toString() {
		return "(" + sourceValue + " --> " + targetValue + ")";
	}
	

}
