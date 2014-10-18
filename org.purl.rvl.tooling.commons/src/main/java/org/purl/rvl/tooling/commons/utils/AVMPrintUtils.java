package org.purl.rvl.tooling.commons.utils;

import java.util.Set;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.purl.rvl.java.gen.viso.graphic.Color;
import org.purl.rvl.java.gen.viso.graphic.GraphicObject;
import org.purl.rvl.java.viso.graphic.ColorX;
import org.purl.rvl.java.viso.graphic.GraphicObjectX;

public class AVMPrintUtils {
	
	final static Logger LOGGER = Logger.getLogger(AVMPrintUtils.class.getName()); 
	
	static final String NL =  System.getProperty("line.separator");

	public static <T> String prettyPrint(Set<Node> objectNodes){
		
		String s = "";
		
		for (Node node : objectNodes) {
			s += node + NL;
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
			LOGGER.info(color.toString());
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
	
			LOGGER.info(res.toString());
			//LOGGER.info("Types:" + go.getAllType_as().asArray()[0].asURI());
			
			for (org.ontoware.rdfreactor.schema.rdfs.Class type : res.getAllType_as().asList()) {
				try {
					LOGGER.info("T: " + type.asURI());
				} catch (ClassCastException e) {
					LOGGER.severe("evtl. blanknote");
				}
			}
		}		
	}

}
