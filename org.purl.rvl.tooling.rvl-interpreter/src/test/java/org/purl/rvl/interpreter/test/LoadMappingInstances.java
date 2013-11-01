package org.purl.rvl.interpreter.test;

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
import org.ontoware.rdf2go.model.node.Resource;
import org.purl.rvl.interpreter.rvl.*;

public class LoadMappingInstances {
	
	final public static String REM_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-mappings.ttl"; // HACK: references the file in the vocabularies project
	final public static String REXD_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-data.ttl"; // HACK: references the file in the vocabularies project
	final public static String RVL_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl.owl"; // HACK: references the file in the vocabularies project
	
	
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
	}
	
	public static void main(String[] args) {
		  Random random = new Random();
		   
		  init();

		   // if the File already exists, the existing triples are read and added to the model
		   File mappingInstancesFile = new File(mappingInstancesFileName);
		   if (mappingInstancesFile.exists()) {
		    try {
		     model.readFrom(new FileReader(mappingInstancesFile), Syntax.Turtle);
		     model.readFrom(new FileReader(RVL_LOCAL_REL), Syntax.RdfXml); // add RVL ontology explicitly from local file, since imports cannot be downloaded properly as it seems
		     model.readFrom(new FileReader(REXD_LOCAL_REL), Syntax.Turtle); // add example data explicitly from local file, since imports cannot be downloaded properly as it seems
		    } catch (IOException e) {
		     e.printStackTrace();
		    }
		   } else {
		    // File will be created on save only
		   }
		   
		   // get references for all objects of the Mapping class, by calling a static method upon this class
		   ClosableIterator<Resource> mappingIterator = Mapping.getAllInstances(model);
		   
		   // print all mapping instances
		   while (mappingIterator.hasNext()) {
		    System.out.println(mappingIterator.next());
		   }

		   // create 10 new Mapping instances
		   for (int i = 0; i < 10; i++) {
		    // create a new ID
		    int aID = random.nextInt(100000);
		    Mapping mapping = new Mapping(model,"http://purl.org/rvl/example-mappings/Mapping" + aID, true);
		    mapping.setLabel("This is a new Mapping " + i);
		    mapping.setIncludeinlegend(true);
		   }
		   
		   
		    // save back model to file
		    try {
		     FileWriter writer = new FileWriter(outputFileName);
		     model.writeTo(writer, Syntax.Turtle);
		    } catch (IOException e) {
		     e.printStackTrace();
		    }
		    // close the model
		    // model.close();
		    // -NO!!! since there is more than one Thread, close would be performed before the data is added to the model, resulting in a NullPointerException of the RDF2GO model
		   
	}

}
