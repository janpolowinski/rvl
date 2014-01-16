package org.purl.rvl.tooling.util;

import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdfreactor.runtime.ReactorResult;

public class RVLUtils {
	
	private final static Logger LOGGER = Logger.getLogger(RVLUtils.class.getName()); 
	
	public static void listAllMappings(Model model) {
		// get references for all objects of the Mapping class, by calling a static method upon this class
		   ReactorResult <?extends org.purl.rvl.java.gen.rvl.Mapping> rrMappings = org.purl.rvl.java.rvl.Mapping.getAllInstances_as(model);
		   org.purl.rvl.java.rvl.Mapping existingMapping;

		   // print all mapping instances
		   System.out.println("All Instances of Mappings (including subclasses when reasoning is on):");
		   
			ClosableIterator<? extends org.purl.rvl.java.gen.rvl.Mapping> mappingIterator = rrMappings.asClosableIterator();
			while (mappingIterator.hasNext()) {
				existingMapping = (org.purl.rvl.java.rvl.Mapping) mappingIterator.next().castTo(org.purl.rvl.java.rvl.Mapping.class);

				// print P2GAM specific info (value mappings ... )
				if(existingMapping.isInstanceof(org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping.RDFS_CLASS)) {
					org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping p2gam = 
							(org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping) existingMapping.castTo(
									org.purl.rvl.java.rvl.PropertyToGraphicAttributeMapping.class);
					System.out.println(p2gam);
				}
				// print P2GO2ORM specific info (submappings ... )
				else if(existingMapping.isInstanceof(org.purl.rvl.java.rvl.PropertyToGO2ORMapping.RDFS_CLASS)) {
					org.purl.rvl.java.rvl.PropertyToGO2ORMapping p2go2orm = 
							(org.purl.rvl.java.rvl.PropertyToGO2ORMapping) existingMapping.castTo(
									org.purl.rvl.java.rvl.PropertyToGO2ORMapping.class);
					System.out.println(p2go2orm);
				}/*
				// print PM specific info (source and target property)
				else if (existingMapping.isInstanceof(org.purl.rvl.java.gen.rvl.PropertyMapping.RDFS_CLASS)) {
					org.purl.rvl.java.rvl.PropertyMapping pm = 
							(org.purl.rvl.java.rvl.PropertyMapping) existingMapping.castTo(
									org.purl.rvl.java.rvl.PropertyMapping.class);
					System.out.println(pm);
				}
				// print only M specific info
				else {
					System.out.println(existingMapping);
				}*/
			}
		
		// The same using a Array:		
		//		   Mapping[] mappingsArray = rrMappings.asArray();
		//		   for (int i = 0; i < mappingsArray.length; i++) {
		//			   existingMapping = mappingsArray[i];
		//			   existingMappingLabel = existingMapping.getAllLabel_as().firstValue();
		//			   if (null!=existingMappingLabel) { 
		//				   System.out.println(existingMappingLabel);
		//			   }
		//			   else {
		//				   System.out.println("Mapping without label (" + existingMapping + ")");
		//			   }
		//		   }	   
	}
	
	
//	  PropertyMapping r = PropertyMapping.getInstance(model, new URIImpl("http://purl.org/rvl/example-mappings/PMWithExplicitValueMappingsAsBlankNodes"));
//	  System.out.println("PM: " + r);
//	  System.out.println("LLLLLLLLLLLLLLLLLLLLLLLLLLLL" + System.getProperty("line.separator"));


}
