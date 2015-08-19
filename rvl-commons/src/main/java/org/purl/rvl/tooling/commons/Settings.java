/**
 * 
 */
package org.purl.rvl.tooling.commons;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

/**
 * Provides universal names for the various graphs used in RVL tooling.
 * 
 * @author Jan
 *
 */
public class Settings {
	
	// LOGGING
	private final static Logger LOGGER = Logger.getLogger(Settings.class.getName()); 

	// GRAPH URIs
	public static final URI GRAPH_MAPPING = new URIImpl("http://purl.org/rvl/example/mapping/");
	public static final URI GRAPH_DATA = new URIImpl("http://purl.org/rvl/example/data/");
	public static final URI GRAPH_RVL_SCHEMA = new URIImpl("http://purl.org/rvl/");
	public static final URI GRAPH_VISO = new URIImpl("http://purl.org/viso/");
	public static final URI GRAPH_AVM = new URIImpl("http://purl.org/rvl/avm/");
	
	// SETTINGS
	public static int MAX_GRAPHIC_RELATIONS_PER_MAPPING = 5000;
	
	static {
	
    /* SETTINGS FROM PROPERTIES-FILE */
    
	Properties properties = new Properties();
	
	try {
		
	  properties.load(new FileInputStream("/semvis.properties")); // TODO: this is taken from the maven project, which executes the program, not always from the interpreter project! 
	  
	  Settings.MAX_GRAPHIC_RELATIONS_PER_MAPPING = Integer.parseInt(properties.get("org.purl.rvl.tooling.max-graphic-relations-per-mapping").toString());
	  //USE_CASE_FOLDER = properties.get("org.purl.rvl.tooling.use-case-folder").toString();
	  
	} catch (Exception e) {
		
		LOGGER.severe("Could not load settings from properties-file. Reason: " + e.getMessage());
		e.printStackTrace();
		System.exit(0);
		
	}
	
 }
}
