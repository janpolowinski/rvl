package org.purl.rvl.tooling;

import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;


public class ModelBuilder {
	
	protected static Model model;
	protected static Model modelVISO;
	
	protected static final String tmpAvmModelFileName = "avm.ttl";
	//protected static String JSON_FILE_NAME_REL = "../org.purl.rvl.tooling.d3vis/examples/force-layouted-nodes/data.json";
	protected static String jsonFileRelName = "../org.purl.rvl.tooling.d3vis/examples/force-directed-graph/data.json";
	
	public static final String REM_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-mappings.ttl";
	public static final String REXD_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-data.ttl";
	public static final String RVL_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl.owl";
	public static final String VISO_LOCAL_REL = "../org.purl.rvl.vocabulary/viso-branch/viso-graphic-inference.ttl";

	private final static Logger LOGGER = Logger.getLogger(ModelBuilder.class.getName()); 
	
	static final String NL =  System.getProperty("line.separator");
	

	static{
		initRDF2GoModels();
	}

	public static Model getModel(){
		return model;
		}
	
	public static Model getVISOModel(){
		return modelVISO;
		}
	


	protected static void initRDF2GoModels() throws ModelRuntimeException {
		// explicitly specify to use a specific ontology api here:
		// RDF2Go.register( new org.ontoware.rdf2go.impl.jena.ModelFactoryImpl());
		// RDF2Go.register( new org.openrdf.rdf2go.RepositoryModelFactory() );
		// if not specified, RDF2Go.getModelFactory() looks into your classpath
		// for ModelFactoryImpls to register.
	
		// create the RDF2GO Models
		model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		model.open();
		modelVISO = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelVISO.open();
		
			/*
		   // if the File already exists, the existing triples are read and added to the model
		   File mappingInstancesFile = new File(mappingInstancesFileName);
		   if (mappingInstancesFile.exists()) {
			...
				   } else {
	    	// File will be created on save only
	   		}
			*/   
		  
	
		try {
			modelVISO.readFrom(new FileReader(VISO_LOCAL_REL),
					Syntax.Turtle);
			model.readFrom(new FileReader(VISO_LOCAL_REL),
					Syntax.Turtle);
			model.readFrom(new FileReader(RVL_LOCAL_REL),
					Syntax.RdfXml);
			model.readFrom(new FileReader(REXD_LOCAL_REL),
					Syntax.Turtle);
			model.readFrom(new FileReader(REM_LOCAL_REL),
					Syntax.Turtle);
		} catch (IOException e) {
			LOGGER.severe("Problem reading one of the RDF files into the model: " + e);
		}
	}


}
