/**
 * 
 */
package org.purl.rvl.tooling.commons;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.purl.rvl.tooling.commons.utils.FileResourceUtils;

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
	
	    /* SETTINGS FROM DEFAULTS / USER PROPERTIES-FILE */
	    
		Properties defaults = new Properties();
		Properties properties = new Properties();
		
		try {
			defaults.load(FileResourceUtils.getFromWithinJars("/semvis.properties.template"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Could not load default settings from properties.template-file. Reason: " + e.getMessage(), e.getStackTrace());
		}
	
		try {
			properties.load(new FileInputStream("/semvis.properties"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Could not load user settings from properties-file. Reason: " + e.getMessage()
					+ " Will use default values from template instead.", e.getStackTrace());
		}
	
		try {
			MAX_GRAPHIC_RELATIONS_PER_MAPPING = Integer.parseInt(properties.get("org.purl.rvl.tooling.max-graphic-relations-per-mapping").toString());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Could not load MAX_GRAPHIC_RELATIONS_PER_MAPPING from properties-file. Reason: " + e.getMessage()
					+ " Will try to use default value from template instead.", e.getStackTrace());
			try {
				MAX_GRAPHIC_RELATIONS_PER_MAPPING = Integer.parseInt(defaults.get("org.purl.rvl.tooling.max-graphic-relations-per-mapping").toString());
			} catch (Exception ed) {
				LOGGER.log(Level.SEVERE, "Could not load MAX_GRAPHIC_RELATIONS_PER_MAPPING from properties-file. Reason: " + ed.getMessage(), ed.getStackTrace());
			}
		}
		
		// USE_CASE_FOLDER = properties.get("org.purl.rvl.tooling.use-case-folder").toString();
	}
}
