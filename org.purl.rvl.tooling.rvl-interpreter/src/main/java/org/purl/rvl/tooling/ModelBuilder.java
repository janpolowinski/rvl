package org.purl.rvl.tooling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Diff;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Variable;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.process.FileRegistry;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.util.ModelUtils;

import com.hp.hpl.jena.rdf.model.impl.ModelListenerAdapter;


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
	

	/**
	 * 
	 */
	public ModelBuilder() {
		super();
		initModelSet();
	}

	/*public Model getModel(){
		return model;
		}*/
	
	public void initModelSet(){
		
		modelSet = RDF2Go.getModelFactory().createModelSet();
		modelSet.open();
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
		modelData = RDF2Go.getModelFactory().createModel(OGVICProcess.getInstance().getReasoningDataModel());
		modelData.open();



		try {
			ModelUtils.readFromAnySyntax(modelRVLSchema,OGVICProcess.RVL_LOCAL_REL);
			ModelUtils.readFromAnySyntax(modelVISO,OGVICProcess.VISO_LOCAL_REL);
			ModelUtils.readFromAnySyntax(modelMappings,ExampleMapping.RVL_EXAMPLE_MINI);
			ModelUtils.readFromAnySyntax(modelData,ExampleData.RVL_EXAMPLE); 
			
			modelMappings.addModel(modelRVLSchema); // TODO temp hack!
			//modelMappings.addModel(modelVISO); // TODO temp hack!
			//modelMappings.addModel(modelData); // TODO temp hack!
			
		} catch (Exception e) {
			LOGGER.severe("Problem reading one of the RDF files into a model: " + e);
		}
	}
	
	
	public void initVISOModel() {
		// extra model for VISO
		modelVISO = RDF2Go.getModelFactory().createModel(Reasoning.none); // no reasoning seems to be OK here
		modelVISO.open();
		ModelUtils.readFromAnySyntax(modelVISO,OGVICProcess.VISO_LOCAL_REL);
	
		modelSet.addModel(modelVISO, OGVICProcess.GRAPH_VISO);
	}

	public void initRVLModel() {
		// extra model for RVL (schema)
		modelRVLSchema = RDF2Go.getModelFactory().createModel(Reasoning.none); // no reasoning seems to be OK here
		modelRVLSchema.open();
		ModelUtils.readFromAnySyntax(modelRVLSchema,OGVICProcess.RVL_LOCAL_REL);
		
		modelSet.addModel(modelRVLSchema, OGVICProcess.GRAPH_RVL_SCHEMA);
	}

	public void initMappingsModel(FileRegistry mappingFileRegistry )  {
		
		modelSet.removeModel(OGVICProcess.GRAPH_MAPPING);
		
		// extra model for mappings
		modelMappings = RDF2Go.getModelFactory().createModel(Reasoning.rdfs); // no reasoning seems to be OK here, when mapping model is extended correctly using getExtraStatementModel below (see comments on problems with JENA impl)
		modelMappings.open();
		
		if (null != mappingFileRegistry) {
			for (Iterator<File> iterator = mappingFileRegistry.getFiles().iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				LOGGER.finer("Found registered mapping file: " + file.getAbsolutePath().toString());
				//readFromAnySyntax(model,file);
				ModelUtils.readFromAnySyntax(modelMappings,file);
			}
		}

		// add the extra mapping statements that will be inferred based on the RVL schema
		//modelMappings.addModel(ModelUtils.getExtraStatementModel(modelMappings,modelRVLSchema)); // TODO does not work properly when JENA used. Works as expected for SESAME Impl of RDF2GO
		modelMappings.addModel(modelRVLSchema); // for now simply add the whole RVL schema to the mappings model and make it a reasoning model
		
		modelSet.addModel(modelMappings, OGVICProcess.GRAPH_MAPPING);

		
		//modelMappings.addModel(modelVISO); // TODO temp hack!
		//modelMappings.addModel(modelData); // TODO temp hack!
		
		//modelData.addModel(modelRVLSchema); // TODO temp hack!
		//modelData.addModel(modelVISO); // TODO temp hack!
		//modelData.addModel(modelMappings); // TODO temp hack!
		
		// combine models to a model set with different named graph		
	}

	
	
	public void initDataModel(FileRegistry dataFileRegistry, Reasoning reasoning)  {
		
		modelSet.removeModel(OGVICProcess.GRAPH_DATA);
		
		// extra model for data
		modelData = RDF2Go.getModelFactory().createModel(reasoning); // no reasoning seems to be OK here?? -> no colors of nodes are missing in ro-instance uss case
		modelData.open();

		if (null != dataFileRegistry) {
			for (Iterator<File> iterator = dataFileRegistry.getFiles().iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				LOGGER.finer("Found registered data file: " + file.getAbsolutePath().toString());
				//readFromAnySyntax(model,file);
				ModelUtils.readFromAnySyntax(modelData,file);
			}
		}

		modelSet.addModel(modelData, OGVICProcess.GRAPH_DATA);
				
		// cache inferred files for speeding up future starts
		
		// since multiple files may be used to infer the models, 
		// a new file name per use case / project is needed
	}
	
	public void clearMappingAndDataModels() {
		if (null!= modelMappings)
			modelMappings.removeAll();
		if (null!= modelData)
			modelData.removeAll();
	}

	public Model initAVMModel() {
		
		modelSet.removeModel(OGVICProcess.GRAPH_AVM);
		
		// empty model to hold the AVM
		modelAVM = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelAVM.open();	
		
		modelSet.addModel(modelAVM, OGVICProcess.GRAPH_AVM);
		
		return modelAVM;
	}

	/**
	 * Only reads the AVM from the tmp file into the model
	 * @return 
	 * 
	 * @throws ModelRuntimeException
	 */
	public Model initAVMModelFromFile(String fileName) throws ModelRuntimeException {
		
		modelAVM = RDF2Go.getModelFactory().createModel(Reasoning.none);
		modelAVM.open();
	
		try {
			ModelUtils.readFromAnySyntax(modelAVM,fileName);
		} catch (Exception e) {
			LOGGER.severe("Problem reading the tmp AVM model from file: " + e);
		}
		
		return modelAVM;
	}

	public void clearAVMModel() {
		if (null!= modelAVM)
			modelAVM.removeAll();
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



}
