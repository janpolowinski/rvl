package org.purl.rvl.tooling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

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
	
	private Model model;
	private Model modelVISO;
	private Model modelData;
	private Model modelMappings;
	private Model modelAVM;
	
	private final static Logger LOGGER = Logger.getLogger(ModelBuilder.class.getName()); 
	static final String NL =  System.getProperty("line.separator");
	

	public Model getModel(){
		return model;
		}
	
	public Model getVISOModel(){
		return modelVISO;
		}
	
	public Model getDataModel(){
		return modelData;
		}
	
	public Model getMappingsModel(){
		return modelMappings;
		}
	
	public Model getAVMModel(){
		return modelAVM;
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
		
		// extra model for VISO
		modelVISO = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelVISO.open();
		
		// extra model for data
		modelData = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelData.open();
		
		// extra model for mappings
		modelMappings = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelMappings.open();
		
		// empty model to hold the AVM
		modelAVM = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelAVM.open();	
		
		if (null != ontologyFileRegistry) {
			for (Iterator<File> iterator = ontologyFileRegistry.getFiles().iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				LOGGER.finer("Found registered ontology file: " + file.getAbsolutePath().toString());
				readFromAnySyntax(model,file);
				readFromAnySyntax(modelVISO,file);
			}
		}
		
		if (null != dataFileRegistry) {
			for (Iterator<File> iterator = dataFileRegistry.getFiles().iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				LOGGER.finer("Found registered data file: " + file.getAbsolutePath().toString());
				readFromAnySyntax(model,file);
				//readFromAnySyntax(modelData,file);
			}
		}
		
		if (null != mappingFileRegistry) {
			for (Iterator<File> iterator = mappingFileRegistry.getFiles().iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				LOGGER.finer("Found registered mapping file: " + file.getAbsolutePath().toString());
				readFromAnySyntax(model,file);
				//readFromAnySyntax(modelMappings,file);
			}
		}
		
	}
	
	/**
	 * Only reads the AVM from the tmp file into the model
	 * 
	 * @throws ModelRuntimeException
	 */
	public void initFromTmpAVMFile() throws ModelRuntimeException {
		modelAVM = RDF2Go.getModelFactory().createModel(Reasoning.none);
		modelAVM.open();

		try {
			readFromAnySyntax(modelAVM,OGVICProcess.TMP_AVM_MODEL_FILE_NAME);
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

	public void initVISOModel(FileRegistry ontologyFileRegistry) {
		initRDF2GoModels(ontologyFileRegistry, null, null);
	}

}
