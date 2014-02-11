package org.purl.rvl.java.rvl;

import org.ontoware.rdf2go.model.node.Node;
import org.purl.rvl.java.gen.rvl.Interval;

public class IntervalX {

private Interval intervalGen;

static final String NL = System.getProperty("line.separator");

/**
 * @param intervalGen
 */
public IntervalX(Interval intervalGen) {
	super();
	this.intervalGen = intervalGen;
}

/**
 * @return the intervalGen
 */
public Interval getIntervalGen() {

	return intervalGen;
}

public Node getLowerBoundExcl() {
	if (intervalGen.hasLowerboundexclusive()) {
		return intervalGen.getAllLowerboundexclusive_asNode_().firstValue();
	} else return null;
}

public Node getLowerBoundIncl() {
	if (intervalGen.hasLowerboundinclusive()) {
		return intervalGen.getAllLowerboundinclusive_asNode_().firstValue();
	} else return null;
}

public Node getUpperBoundExcl() {
	if (intervalGen.hasUpperboundexclusive()) {
		return intervalGen.getAllUpperboundexclusive_asNode_().firstValue();
	} else return null;
}

public Node getUpperBoundIncl() {
	if (intervalGen.hasUpperboundinclusive()) {
		return intervalGen.getAllUpperboundinclusive_asNode_().firstValue();
	} else return null;
}

public String toString(){
	
	String s = "";
	
	s += " lower bound incl: " + getLowerBoundIncl() ;
	s += ", lower bound excl: " + getLowerBoundExcl() ;
	s += ", upper bound incl: " + getUpperBoundIncl() ;
	s += ", upper bound excl: " + getUpperBoundExcl() ;
	
	return s;
	
}

}
