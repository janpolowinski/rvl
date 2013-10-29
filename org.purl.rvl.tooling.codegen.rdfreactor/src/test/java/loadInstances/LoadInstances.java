package loadInstances;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelException;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.*;
import org.ontoware.rdf2go.model.node.Resource;
import org.purl.rvl.interpreter.rvl.Mapping;

public class LoadInstances {
	
	final public static String RVL_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-mappings.ttl"; // HACK: references the rvl.owl in the vocabularies project
	
	private static Model model;
	
	private static void init() throws ModelRuntimeException {
		// specify to use Jena here:
		//RDF2Go.register( new org.ontoware.rdf2go.impl.jena24.ModelFactoryImpl() );
		// if not specified, RDF2Go.getModelFactory() looks into your classpath for ModelFactoryImpls to register.
		
		model = RDF2Go.getModelFactory().createModel();
		model.open();
	}
	
	public static void main(String[] args) {
		
		try { 	init(); }
		catch (Exception e) {
			System.err.println(e);
		}
		 {
		  Random random = new Random();
		  // create the RDF2GO Model
		   //Model model = new ModelFactoryImpl().createModel();
		   String rdfStoreFileName = RVL_LOCAL_REL;
		   // if the File already exists, the existing triples are read and added to the model
		   File rdfStoreFile = new File(rdfStoreFileName);
		   if (rdfStoreFile.exists()) {
		    try {
		     FileReader reader = new FileReader(rdfStoreFile);
		     model.readFrom(reader, Syntax.Turtle);
		    } catch (IOException e) {
		     e.printStackTrace();
		    }
		   } else {
		    // File will be created on save only
		   }
		   // get references for all objects of a certain class, by calling a static method upon this class
		   ClosableIterator<Resource> taskArray = Mapping.getAllInstances(model);
		   // print all instances
		   while (taskArray.hasNext()) {
		    System.out.println(taskArray.next());
		   }
		   // create 10 new instances
		   for (int i = 0; i < 10; i++) {
		    // create a new ID
		    int aID = random.nextInt(100000);
		    Mapping _NepomukTask = new Mapping(model,"http://purl.org/rvl/example-mappings#Mapping" + aID, true);
		    _NepomukTask.setLabel("This is a further task." + i);
		    _NepomukTask.setIncludeinlegend(true);
		    // save back model to file
		    try {
		     String outFileName = rdfStoreFileName;
		     FileWriter writer = new FileWriter(outFileName);
		     //model.writeTo(writer, Syntax.Turtle);
		    } catch (IOException e) {
		     e.printStackTrace();
		    }
		    // close the model
		    // model.close();
		    // -NO!!! since there is more than one Thread, close would be performed before the data is added to the model, resulting in a NullPointerException of the RDF2GO model
		   }
		 }
		}

}
