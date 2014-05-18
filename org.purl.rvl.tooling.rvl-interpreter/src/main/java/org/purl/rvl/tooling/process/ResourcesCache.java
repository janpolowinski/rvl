package org.purl.rvl.tooling.process;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;


public class ResourcesCache {
	
	private static ResourcesCache instance = null;

	private final static Logger LOGGER = Logger.getLogger(ResourcesCache.class.getName()); 
	
	static final String NL =  System.getProperty("line.separator");
	
	private Map<String,Object> cache;
   
    
	
	/**
	 * @param args
	 */
	private ResourcesCache() {
		cache = new HashMap<String, Object>();
	}
	

	public static ResourcesCache getInstance() {
		if (instance == null) {
            instance = new ResourcesCache();
        }
        return instance;
	}
	
	public void put(String uri, Object object) {
		cache.put(uri, object);
	}
	
	public void put(URI uri, Object object) {
		put(uri.toString(), object);
	}

	public Object getFromCache(String uri){
		return cache.get(uri);
	}
	
	public Object getFromCache(URI uri){
		return getFromCache(uri.toString());
	}


	public Object tryReplaceOrCache(URI uri, Object object) {
		
		if (cache.containsKey(uri.toString())) {
			return getFromCache(uri);
		} else {
			put(uri, object);
			return object;
		}
	}


	public Object tryReplaceOrCache(
			Resource resource) {
		try {
			URI uri = resource.asURI();
			return tryReplaceOrCache(uri, resource);
		}
		catch (ClassCastException e) {
			LOGGER.info("Could not get blank node resource from cache. Getting resources from cache only works for resources with URIs.");
			return resource;
		}
		
	}
	
	
}
