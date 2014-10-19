package org.purl.rvl.java;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * @author Jan Polowinski
 * 
 */

public final class VISODATA {

	public static final String NS = "http://purl.org/viso/data/";

	public final static URI HAS_NOMINAL_VALUE = new URIImpl("http://purl.org/viso/data/has_nominal_value");
	public final static URI HAS_ORDINAL_VALUE = new URIImpl("http://purl.org/viso/data/has_ordinal_value");
	public final static URI HAS_QUANTITATIVE_VALUE = new URIImpl("http://purl.org/viso/data/has_quantitative_value");

}
