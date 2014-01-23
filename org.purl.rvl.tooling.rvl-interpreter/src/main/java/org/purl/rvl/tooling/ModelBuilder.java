package org.purl.rvl.tooling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;


public class ModelBuilder {
	
	protected Model model;
	protected Model modelVISO;
	
	private final static Logger LOGGER = Logger.getLogger(ModelBuilder.class.getName()); 
	static final String NL =  System.getProperty("line.separator");
	

	public Model getModel(){
		return model;
		}
	
	public Model getVISOModel(){
		return modelVISO;
		}
	


	public void initRDF2GoModels() throws ModelRuntimeException {
		// explicitly specify to use a specific ontology api here:
		// RDF2Go.register( new org.ontoware.rdf2go.impl.jena.ModelFactoryImpl());
		// RDF2Go.register( new org.openrdf.rdf2go.RepositoryModelFactory() );
		// if not specified, RDF2Go.getModelFactory() looks into your classpath
		// for ModelFactoryImpls to register.
	
		// create the RDF2GO Models
		//model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
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
			
			readFromAnySyntax(modelVISO,OGVICProcess.VISO_LOCAL_REL);
			readFromAnySyntax(model,OGVICProcess.VISO_LOCAL_REL);
			readFromAnySyntax(model,OGVICProcess.RVL_LOCAL_REL);
			readFromAnySyntax(model,OGVICProcess.REXD_LOCAL_REL);
			readFromAnySyntax(model,OGVICProcess.REM_LOCAL_REL);
			
		} catch (IOException e) {
			LOGGER.severe("Problem reading one of the RDF files into the model: " + e);
		}
	}
	
	/**
	 * Only reads the AVM from the tmp file into the model
	 * 
	 * @throws ModelRuntimeException
	 */
	protected void initFromTmpAVMFile() throws ModelRuntimeException {
		model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		model.open();
		modelVISO = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelVISO.open();

		try {
			readFromAnySyntax(model,OGVICProcess.TMP_AVM_MODEL_FILE_NAME);
			readFromAnySyntax(modelVISO,OGVICProcess.VISO_LOCAL_REL);
		} catch (IOException e) {
			LOGGER.severe("Problem reading the tmp AVM model from file: " + e);
		}
	}

	private static void readFromAnySyntax(Model model, String fileName) throws IOException,
			FileNotFoundException {
		
		File file = new File(fileName);
		if (file.exists()) {
			
			String extension = FilenameUtils.getExtension(fileName);
			
			if (extension.equals("ttl") || extension.equals("n3")) {
				model.readFrom(new FileReader(file),
						Syntax.Turtle);
			} else {
				model.readFrom(new FileReader(file),
						Syntax.RdfXml);
			}

			LOGGER.info("Reading file into a model: " + fileName);
			
		} else {
			throw new IOException("File not found: " + fileName);
		}
	}


}
