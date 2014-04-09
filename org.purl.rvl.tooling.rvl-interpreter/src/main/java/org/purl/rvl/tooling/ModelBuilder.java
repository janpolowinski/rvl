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
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.process.FileRegistry;
import org.purl.rvl.tooling.process.OGVICProcess;


public class ModelBuilder {
	
	//private Model model;
	private Model modelVISO;
	private Model modelData;
	private Model modelMappings;
	private Model modelRVLSchema;
	private Model modelAVM;
	
	private ModelSet modelSet;
	
	private final static Logger LOGGER = Logger.getLogger(ModelBuilder.class.getName()); 
	static final String NL =  System.getProperty("line.separator");
	

	/*public Model getModel(){
		return model;
		}*/
	
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
			
		// create the RDF2GO Models
		//model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		//model.open();
		
		// extra model for VISO
		modelVISO = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelVISO.open();

		// extra model for RVL (schema)
		modelRVLSchema = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelRVLSchema.open();
		
		// extra model for mappings
		modelMappings = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelMappings.open();
		
		// extra model for data
		modelData = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelData.open();



		try {
			readFromAnySyntax(modelRVLSchema,OGVICProcess.RVL_LOCAL_REL);
			readFromAnySyntax(modelVISO,OGVICProcess.VISO_LOCAL_REL);
			readFromAnySyntax(modelMappings,ExampleMapping.RVL_EXAMPLE_MINI);
			readFromAnySyntax(modelData,ExampleData.RVL_EXAMPLE); 
			
			modelMappings.addModel(modelRVLSchema); // TODO temp hack!
			//modelMappings.addModel(modelVISO); // TODO temp hack!
			//modelMappings.addModel(modelData); // TODO temp hack!
			
		} catch (Exception e) {
			LOGGER.severe("Problem reading one of the RDF files into a model: " + e);
		}
	}
	

	
	
	public void initRDF2GoModels(FileRegistry ontologyFileRegistry, FileRegistry dataFileRegistry, FileRegistry mappingFileRegistry )  {
		
		// create the RDF2GO Models
		//model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		//model.open();
		
		// extra model for VISO
		modelVISO = RDF2Go.getModelFactory().createModel(Reasoning.none); // no reasoning seems to be OK here
		modelVISO.open();
		
		// extra model for data
		modelData = RDF2Go.getModelFactory().createModel(Reasoning.rdfs); // no reasoning seems to be OK here?? -> no colors of nodes are missing in ro-instance uss case
		modelData.open();
		
		// extra model for mappings
		modelMappings = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelMappings.open();
		
		// extra model for RVL (schema)
		modelRVLSchema = RDF2Go.getModelFactory().createModel(Reasoning.none); // no reasoning seems to be OK here
		modelRVLSchema.open();
		
		// empty model to hold the AVM
		modelAVM = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelAVM.open();	
		
//		if (null != ontologyFileRegistry) {
//			for (Iterator<File> iterator = ontologyFileRegistry.getFiles().iterator(); iterator.hasNext();) {
//				File file = (File) iterator.next();
//				LOGGER.finer("Found registered ontology file: " + file.getAbsolutePath().toString());
//				readFromAnySyntax(model,file);
//				readFromAnySyntax(modelVISO,file);
//			}
//		}
		
		if (null != dataFileRegistry) {
			for (Iterator<File> iterator = dataFileRegistry.getFiles().iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				LOGGER.finer("Found registered data file: " + file.getAbsolutePath().toString());
				//readFromAnySyntax(model,file);
				readFromAnySyntax(modelData,file);
			}
		}
		
		if (null != mappingFileRegistry) {
			for (Iterator<File> iterator = mappingFileRegistry.getFiles().iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				LOGGER.finer("Found registered mapping file: " + file.getAbsolutePath().toString());
				//readFromAnySyntax(model,file);
				readFromAnySyntax(modelMappings,file);
			}
		}
		
		// read schemas to extra models //  TODO clean up ontology section above (will be redundant)
		
		readFromAnySyntax(modelRVLSchema,OGVICProcess.RVL_LOCAL_REL);
		readFromAnySyntax(modelVISO,OGVICProcess.VISO_LOCAL_REL);
		
		modelMappings.addModel(modelRVLSchema); // TODO temp hack! // necessary!
		//modelMappings.addModel(modelVISO); // TODO temp hack!
		//modelMappings.addModel(modelData); // TODO temp hack!
		
		//modelData.addModel(modelRVLSchema); // TODO temp hack!
		//modelData.addModel(modelVISO); // TODO temp hack!
		//modelData.addModel(modelMappings); // TODO temp hack!
		
		// combine models to a model set with different named graphs
		
		modelSet = RDF2Go.getModelFactory().createModelSet();
		modelSet.open();
		modelSet.addModel(modelData, OGVICProcess.GRAPH_DATA);
		modelSet.addModel(modelMappings, OGVICProcess.GRAPH_MAPPING);
		modelSet.addModel(modelRVLSchema, OGVICProcess.GRAPH_RVL_SCHEMA);
		modelSet.addModel(modelVISO, OGVICProcess.GRAPH_VISO);
		//modelSet.addModel(modelRVL, OGVICProcess.GRAPH_RVL);
		//modelSet.addModel(enrichedMappings, GRAPH_MAPPING_ENRICHED_WITH_RVL);

		
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

	/**
	 * @return the modelSet
	 */
	public ModelSet getModelSet() {
		return modelSet;
	}

	/**
	 * @param modelSet the modelSet to set
	 */
	public void setModelSet(ModelSet modelSet) {
		this.modelSet = modelSet;
	}

}
