package org.purl.rvl.tooling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.process.FileRegistry;
import org.purl.rvl.tooling.process.OGVICProcess;


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
	


	public void initTestModels() throws ModelRuntimeException {
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

		try {
			readFromAnySyntax(modelVISO,OGVICProcess.VISO_LOCAL_REL);
			readFromAnySyntax(model,OGVICProcess.VISO_LOCAL_REL);
			readFromAnySyntax(model,OGVICProcess.RVL_LOCAL_REL);
			readFromAnySyntax(model,ExampleData.RVL_EXAMPLE);
			readFromAnySyntax(model,ExampleMapping.RVL_EXAMPLE);
			
		} catch (Exception e) {
			LOGGER.severe("Problem reading one of the RDF files into the model: " + e);
		}
	}
	

	
	
	public void initRDF2GoModels(FileRegistry ontologyFileRegistry, FileRegistry dataFileRegistry, FileRegistry mappingFileRegistry )  {
		
		// create the RDF2GO Models
		model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		model.open();
		modelVISO = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelVISO.open();	
		
		for (Iterator<File> iterator = ontologyFileRegistry.getFiles().iterator(); iterator.hasNext();) {
			File file = (File) iterator.next();
			LOGGER.finer("Found registered ontology file: " + file.getAbsolutePath().toString());
			readFromAnySyntax(model,file);
			readFromAnySyntax(modelVISO,file);
		}
		
		for (Iterator<File> iterator = dataFileRegistry.getFiles().iterator(); iterator.hasNext();) {
			File file = (File) iterator.next();
			LOGGER.finer("Found registered data file: " + file.getAbsolutePath().toString());
			readFromAnySyntax(model,file);
		}
		
		for (Iterator<File> iterator = mappingFileRegistry.getFiles().iterator(); iterator.hasNext();) {
			File file = (File) iterator.next();
			LOGGER.finer("Found registered mapping file: " + file.getAbsolutePath().toString());
			readFromAnySyntax(model,file);
		}
		
	}
	
	/**
	 * Only reads the AVM from the tmp file into the model
	 * 
	 * @throws ModelRuntimeException
	 */
	public void initFromTmpAVMFile() throws ModelRuntimeException {
		model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		model.open();
		modelVISO = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelVISO.open();

		try {
			readFromAnySyntax(model,OGVICProcess.TMP_AVM_MODEL_FILE_NAME);
			readFromAnySyntax(modelVISO,OGVICProcess.VISO_LOCAL_REL);
		} catch (Exception e) {
			LOGGER.severe("Problem reading the tmp AVM model from file: " + e);
		}
	}

	
	private static void readFromAnySyntax(Model model, String fileName) {
		
		File file = new File(fileName);
		readFromAnySyntax(model, file);

	}
	
	
	
	private static void readFromAnySyntax(Model model, File file) {

		try {
			
			String extension = FilenameUtils.getExtension(file.getName());
			
			if (extension.equals("ttl") || extension.equals("n3")) {
				model.readFrom(new FileReader(file),
						Syntax.Turtle);
			} else {
				model.readFrom(new FileReader(file),
						Syntax.RdfXml);
			}
		
			LOGGER.info("Reading file into (some) model: " + file.getPath());
			
		} catch (FileNotFoundException e) {
			LOGGER.info("File could not be read into the model, since it wasn't found: " +  file.getPath());
		} catch (IOException e) {
			LOGGER.info("File could not be read into the model: " +  file.getPath());
			e.printStackTrace();
		}

	}
}
