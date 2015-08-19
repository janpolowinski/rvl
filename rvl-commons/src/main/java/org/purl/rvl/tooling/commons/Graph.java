/**
 * 
 */
package org.purl.rvl.tooling.commons;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Provides universal names for the various graphs used in RVL tooling.
 * 
 * @author Jan
 *
 */
public class Graph {

	// GRAPH URIs
	public static final URI GRAPH_MAPPING = new URIImpl("http://purl.org/rvl/example/mapping/");
	public static final URI GRAPH_DATA = new URIImpl("http://purl.org/rvl/example/data/");
	public static final URI GRAPH_RVL_SCHEMA = new URIImpl("http://purl.org/rvl/");
	public static final URI GRAPH_VISO = new URIImpl("http://purl.org/viso/");
	public static final URI GRAPH_AVM = new URIImpl("http://purl.org/rvl/avm/");
	
	

}
