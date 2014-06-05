package org.purl.rvl.tooling.util;

import java.util.Set;

import org.ontoware.rdf2go.model.node.Node;

public class PrintUtils {
	
	static final String NL =  System.getProperty("line.separator");
	
	public static <T> String prettyPrint(Set<Node> objectNodes){
		
		String s = "";
		
		for (Node node : objectNodes) {
			s += node + NL;
		}
		
		return s;		
	}

}
