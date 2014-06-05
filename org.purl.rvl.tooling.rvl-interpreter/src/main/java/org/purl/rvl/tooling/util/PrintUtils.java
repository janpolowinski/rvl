package org.purl.rvl.tooling.util;

import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.ReactorResult;
import org.ontoware.rdfreactor.schema.rdfs.Class;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.purl.rvl.java.gen.rvl.Mapping;
import org.purl.rvl.java.gen.viso.graphic.Color;
import org.purl.rvl.java.gen.viso.graphic.GraphicObject;
import org.purl.rvl.java.rvl.MappingX;
import org.purl.rvl.java.rvl.PropertyToGO2ORMappingX;
import org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX;
import org.purl.rvl.java.viso.graphic.ColorX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;

public class PrintUtils {
	
	static final String NL =  System.getProperty("line.separator");
	
	public static <T> String prettyPrint(Set<Node> objectNodes){
		
		String s = "";
		
		for (Node node : objectNodes) {
			s += node + NL;
		}
		
		return s;		
	}

	public static void printMappingWithURI(Model model, String uriString){
		  
			System.out.println("");
			System.out.println("Trying to get and print mapping with the URI " + uriString + ":");
			System.out.println("");
			
		  	org.purl.rvl.java.gen.rvl.Mapping mapping = MappingX.getInstance(model, new URIImpl(uriString));
		  	System.out.println(PrintUtils.mappingToStringAsSpecificAsPossible((MappingX) mapping.castTo(MappingX.class)));
	  }

	public static void printMapping(org.purl.rvl.java.gen.rvl.Mapping mapping){  
		  printMapping((MappingX)mapping.castTo(MappingX.class));
	  }

	public static void printMapping(MappingX mapping){
		  
			System.out.println("");
			System.out.println("Mapping details: ");
			System.out.println("");
			
			System.out.println(PrintUtils.mappingToStringAsSpecificAsPossible((MappingX) mapping.castTo(MappingX.class)));
	  }

	public static void listAllMappings(Model model) {
	
		System.out.println("");
		System.out.println("List of all mappings in the model" +
				" (including subclasses when reasoning is on):");
		System.out.println("");
	
		// get references for all objects of the MappingX class
		ReactorResult<? extends org.purl.rvl.java.gen.rvl.Mapping> rrMappings = MappingX
				.getAllInstances_as(model);
	
		// get and print all mapping instances
		ClosableIterator<? extends org.purl.rvl.java.gen.rvl.Mapping> mappingIterator = rrMappings
				.asClosableIterator();
		MappingX mapping;
		while (mappingIterator.hasNext()) {
			mapping = (MappingX) mappingIterator.next().castTo(MappingX.class);
			if(!mapping.isDisabled()) {
				//mappingToStringAsSpecificAsPossible(mapping);
				System.out.println(mappingToStringAsSpecificAsPossible(mapping)); // TODO causes exception
				//System.out.println(mapping.toString());
			}
		}
	}

	public static String mappingToStringAsSpecificAsPossible(MappingX mapping){
		
		 String s = "";
		  
		// print as P2GAM (value mappings ... )
		if(mapping.isInstanceof(org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX.RDFS_CLASS)) {
			org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX p2gam = 
					(org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX) mapping.castTo(
							org.purl.rvl.java.rvl.PropertyToGraphicAttributeMappingX.class);
			s += p2gam.toStringDetailed();
		}
		// print as P2GO2ORM (submappings ... )
		else if(mapping.isInstanceof(org.purl.rvl.java.rvl.PropertyToGO2ORMappingX.RDFS_CLASS)) {
			org.purl.rvl.java.rvl.PropertyToGO2ORMappingX p2go2orm = 
					(org.purl.rvl.java.rvl.PropertyToGO2ORMappingX) mapping.castTo(
							org.purl.rvl.java.rvl.PropertyToGO2ORMappingX.class);
			s += p2go2orm.toStringDetailed();
		}
		// print as general mapping
		else {
			s += mapping.toStringDetailed();
		}
		
		return s;
		
	  }

	public static void listAllColors(Model model) {
		System.out.println("List of all colors in the model:");
		System.out.println();
		
		final ClosableIterator<? extends Color> goIt = 
				Color.getAllInstances_as(model).asClosableIterator();
		while (goIt.hasNext()) {
			final ColorX color = (ColorX) goIt.next().castTo(ColorX.class);
			AVMUtils.LOGGER.info(color.toString());
		}	
	}

	/**
	 * List all Graphic Objects in the model
	 */
	static void listAllGOs(Model model) {	
		
		System.out.println("List of all Graphic Objects in the model:");
		System.out.println();
		
		final ClosableIterator<? extends GraphicObject> goIt = 
				GraphicObject.getAllInstances_as(model).asClosableIterator();
		while (goIt.hasNext()) {
			final GraphicObjectX go = (GraphicObjectX) goIt.next().castTo(GraphicObjectX.class);
			System.out.println(go);
		}		
	}

	/**
	 * List all Resources in the model
	 */
	static void listAllResourcesWithTheirTypes(Model model){	
		
		System.out.println("List of all rdfreactor ... Resources in the model:");
		System.out.println();
		
		ClosableIterator<? extends Resource> resIt = 
			Resource.getAllInstance_as(model).asClosableIterator();
		while (resIt.hasNext()) {
			Resource res = (Resource) resIt.next();
	
			AVMUtils.LOGGER.info(res.toString());
			//LOGGER.info("Types:" + go.getAllType_as().asArray()[0].asURI());
			
			for (org.ontoware.rdfreactor.schema.rdfs.Class type : res.getAllType_as().asList()) {
				try {
					AVMUtils.LOGGER.info("T: " + type.asURI());
				} catch (ClassCastException e) {
					AVMUtils.LOGGER.severe("evtl. blanknote");
				}
			}
		}		
	}

}
