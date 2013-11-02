package org.purl.rvl.interpreter.test;

import info.aduna.iteration.CloseableIteratorIteration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.*;
import org.ontoware.rdf2go.model.node.BlankNode;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.runtime.ReactorResult;
import org.purl.rvl.interpreter.rvl.manual.PropertyToGraphicAttributeMapping;

public class LoadMappingInstances {
	
	final public static String REM_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-mappings.ttl"; // HACK: references the file in the vocabularies project
	final public static String REXD_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-data.ttl"; // HACK: references the file in the vocabularies project
	final public static String RVL_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl.owl"; // HACK: references the file in the vocabularies project
	final public static String VISO_GRAPHIC_ABS = "/Users/Jan/Projekte/SemVis/SemVisTeilprojekte/VISO/modules/graphic/viso-graphic.owl"; // HACK: references the file in the vocabularies project
	
	
	private static Model model;
	private static String mappingInstancesFileName = REM_LOCAL_REL;
	private static String outputFileName = "new.ttl";
	
	private static void init() throws ModelRuntimeException {
		// explicitly specify to use a specific ontology api here:
		// RDF2Go.register( new org.ontoware.rdf2go.impl.jena.ModelFactoryImpl() );
		// RDF2Go.register( new org.openrdf.rdf2go.RepositoryModelFactory() );
		// if not specified, RDF2Go.getModelFactory() looks into your classpath for ModelFactoryImpls to register.

		// create the RDF2GO Model
		model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		model.open();
		
	   // if the File already exists, the existing triples are read and added to the model
	   File mappingInstancesFile = new File(mappingInstancesFileName);
	   if (mappingInstancesFile.exists()) {
	    try {
	     model.readFrom(new FileReader(mappingInstancesFile), Syntax.Turtle);
	     model.readFrom(new FileReader(RVL_LOCAL_REL), Syntax.RdfXml); // add RVL ontology explicitly from local file, since imports cannot be downloaded properly as it seems
	     model.readFrom(new FileReader(REXD_LOCAL_REL), Syntax.Turtle); // add example data explicitly from local file, since imports cannot be downloaded properly as it seems
	     model.readFrom(new FileReader(VISO_GRAPHIC_ABS), Syntax.RdfXml); // add example data explicitly from local file, since imports cannot be downloaded properly as it seems
	    } catch (IOException e) {
	     e.printStackTrace();
	    }
	   } else {
	    // File will be created on save only
	   }
	}
	
	public static void main(String[] args) {
		   
		  init();

		   // get references for all objects of the Mapping class, by calling a static method upon this class
		   ReactorResult <?extends org.purl.rvl.interpreter.rvl.Mapping> rrMappings = org.purl.rvl.interpreter.rvl.manual.Mapping.getAllInstances_as(model);
		   org.purl.rvl.interpreter.rvl.manual.Mapping existingMapping;

		   // print all mapping instances
		   System.out.println("All Instances of Mappings (including subclasses when reasoning is on):");
		   
			ClosableIterator<? extends org.purl.rvl.interpreter.rvl.Mapping> mappingIterator = rrMappings.asClosableIterator();
			while (mappingIterator.hasNext()) {
				existingMapping = (org.purl.rvl.interpreter.rvl.manual.Mapping) mappingIterator.next().castTo(org.purl.rvl.interpreter.rvl.manual.Mapping.class);

				// print P2GAM specific info (value mappings ... )
				if(existingMapping.isInstanceof(org.purl.rvl.interpreter.rvl.manual.PropertyToGraphicAttributeMapping.RDFS_CLASS)) {
					org.purl.rvl.interpreter.rvl.manual.PropertyToGraphicAttributeMapping p2gam = 
							(org.purl.rvl.interpreter.rvl.manual.PropertyToGraphicAttributeMapping) existingMapping.castTo(
									org.purl.rvl.interpreter.rvl.manual.PropertyToGraphicAttributeMapping.class);
					System.out.println(p2gam);
				}
				// print P2GO2ORM specific info (submappings ... )
				else if(existingMapping.isInstanceof(org.purl.rvl.interpreter.rvl.manual.PropertyToGO2ORMapping.RDFS_CLASS)) {
					org.purl.rvl.interpreter.rvl.manual.PropertyToGO2ORMapping p2go2orm = 
							(org.purl.rvl.interpreter.rvl.manual.PropertyToGO2ORMapping) existingMapping.castTo(
									org.purl.rvl.interpreter.rvl.manual.PropertyToGO2ORMapping.class);
					System.out.println(p2go2orm);
				}/*
				// print PM specific info (source and target property)
				else if (existingMapping.isInstanceof(org.purl.rvl.interpreter.rvl.PropertyMapping.RDFS_CLASS)) {
					org.purl.rvl.interpreter.rvl.manual.PropertyMapping pm = 
							(org.purl.rvl.interpreter.rvl.manual.PropertyMapping) existingMapping.castTo(
									org.purl.rvl.interpreter.rvl.manual.PropertyMapping.class);
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
		   
		// testMappingCreation()	
	    // writeModelToFile();
			
	    // close the model
	    // model.close();
	    // -NO!!! since there is more than one Thread, close would be performed before the data is added to the model, resulting in a NullPointerException of the RDF2GO model
	}

	private static void writeModelToFile() {
		try {
		 FileWriter writer = new FileWriter(outputFileName);
		 model.writeTo(writer, Syntax.Turtle);
		} catch (IOException e) {
		 e.printStackTrace();
		}
	}
	
	private void testMappingCreation() {
		Random random = new Random();
		// create 10 new Mapping instances
		for (int i = 0; i < 10; i++) {
			// create a new ID
			int aID = random.nextInt(100000);
			org.purl.rvl.interpreter.rvl.Mapping mapping = new org.purl.rvl.interpreter.rvl.Mapping(
					model,
					"http://purl.org/rvl/example-mappings/Mapping" + aID, true);
			mapping.setLabel("This is a new Mapping " + i);
			mapping.setIncludeinlegend(true);
		}

	}

}
