package org.purl.rvl.java.rvl.mapping;

public class TupleSourceValueTargetValue<X, Y> { 
  public final X sourceValue; 
  public final Y targetValue; 
  public TupleSourceValueTargetValue(X x, Y y) { 
    this.sourceValue = x; 
    this.targetValue = y; 
  } 
}