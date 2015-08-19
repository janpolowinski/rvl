package org.purl.rvl.tooling.util;

import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.ReactorResult;
import org.purl.rvl.java.gen.rvl.Mapping;
import org.purl.rvl.java.rvl.MappingX;

public class RVLPrintUtils {
	
	final static Logger LOGGER = Logger.getLogger(RVLPrintUtils.class.getName()); 
	
	static final String NL =  System.getProperty("line.separator");
	
	public static void printMappingWithURI(Model model, String uriString){
		  
			System.out.println("");
			System.out.println("Trying to get and print mapping with the URI " + uriString + ":");
			System.out.println("");
			
		  	org.purl.rvl.java.gen.rvl.Mapping mapping = Mapping.getInstance(model, new URIImpl(uriString));
		  	System.out.println(mapping);
	  }

	public static void printMapping(org.purl.rvl.java.gen.rvl.Mapping mapping){  
		  printMapping((MappingX)mapping.castTo(MappingX.class));
	  }

	public static void printMapping(MappingX mapping){
		  
			System.out.println("");
			System.out.println("Mapping details: ");
			System.out.println("");
			
		  	System.out.println(mapping.toStringAsSpecificAsPossible());
	  }

	public static void listAllMappings(Model model) {
	
		System.out.println("");
		System.out.println("List of all mappings in the model" +
				" (including subclasses when reasoning is on):");
		System.out.println("");
	
		// get references for all objects of the MappingX class
		ReactorResult<? extends org.purl.rvl.java.gen.rvl.Mapping> rrMappings = Mapping
				.getAllInstances_as(model);
	
		// get and print all mapping instances
		ClosableIterator<? extends org.purl.rvl.java.gen.rvl.Mapping> mappingIterator = rrMappings
				.asClosableIterator();
		MappingX mapping;
		while (mappingIterator.hasNext()) {
			mapping = (MappingX) mappingIterator.next().castTo(MappingX.class);
			if(!mapping.isDisabled()) {
				//mappingToStringAsSpecificAsPossible(mapping);
				System.out.println(mapping.toStringAsSpecificAsPossible()); // TODO causes exception
				//System.out.println(mapping.toString());
			}
		}
	}

}
