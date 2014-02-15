package org.purl.rvl.java.mapping;

import org.ontoware.rdf2go.model.node.Node;

/**
 * @author Jan Polowinski
 *
 */
public class CalculatedValueMapping {
	
	private Node sourceValue;
	private Node targetValue;
	
	public CalculatedValueMapping(Node sourceValue, Node targetValue) {
		super();
		this.sourceValue = sourceValue;
		this.targetValue = targetValue;
	}
	
	/**
	 * @return the sourceValue
	 */
	public Node getSourceValue() {
		return sourceValue;
	}
	/**
	 * @param sourceValue the sourceValue to set
	 */
	public void setSourceValue(Node sourceValue) {
		this.sourceValue = sourceValue;
	}
	/**
	 * @return the targetValue
	 */
	public Node getTargetValue() {
		return targetValue;
	}
	/**
	 * @param targetValue the targetValue to set
	 */
	public void setTargetValue(Node targetValue) {
		this.targetValue = targetValue;
	}
	
	@Override
	public String toString() {
		return "(" + sourceValue + " --> " + targetValue + ")";
	}
	

}
