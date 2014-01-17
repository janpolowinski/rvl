package org.purl.rvl.tooling.util;

import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.ReactorResult;
import org.purl.rvl.java.rvl.Mapping;

public class RVLUtils {
	
	private final static Logger LOGGER = Logger.getLogger(RVLUtils.class.getName()); 
	
	public static void listAllMappings(Model model) {

		System.out.println("");
		System.out.println("List of all mappings in the model" +
				" (including subclasses when reasoning is on):");
		System.out.println("");

		// get references for all objects of the Mapping class
		ReactorResult<? extends org.purl.rvl.java.gen.rvl.Mapping> rrMappings = Mapping
				.getAllInstances_as(model);

		// get and print all mapping instances
		ClosableIterator<? extends org.purl.rvl.java.gen.rvl.Mapping> mappingIterator = rrMappings
				.asClosableIterator();
		Mapping mapping;
		while (mappingIterator.hasNext()) {
			mapping = (Mapping) mappingIterator.next().castTo(Mapping.class);
			printMappingAsSpecificAsPossible(mapping);
		}
	}

	  public static void printMappingWithURI(Model model, String uriString){
		  
			System.out.println("");
			System.out.println("Trying to get and print mapping with the URI " + uriString + ":");
			System.out.println("");
			
		  	org.purl.rvl.java.gen.rvl.Mapping mapping = Mapping.getInstance(model, new URIImpl(uriString));
			printMappingAsSpecificAsPossible((Mapping) mapping.castTo(Mapping.class));
	  }
	  
	  private static void printMappingAsSpecificAsPossible(Mapping mapping){
		  
		// print as P2GAM (value mappings ... )
		if(mapping.isInstanceof(org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping.RDFS_CLASS)) {
			org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping p2gam = 
					(org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping) mapping.castTo(
							org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping.class);
			System.out.println(p2gam);
		}
		// print as P2GO2ORM (submappings ... )
		else if(mapping.isInstanceof(org.purl.rvl.java.rvl.PropertyToGO2ORMapping.RDFS_CLASS)) {
			org.purl.rvl.java.rvl.PropertyToGO2ORMapping p2go2orm = 
					(org.purl.rvl.java.rvl.PropertyToGO2ORMapping) mapping.castTo(
							org.purl.rvl.java.rvl.PropertyToGO2ORMapping.class);
			System.out.println(p2go2orm);
		}
		// print as general mapping
		else {
			System.out.println(mapping);
		}
		
	  }

}
