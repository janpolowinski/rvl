package org.purl.rvl.tooling;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.collections.MapUtils;
import org.apache.xerces.util.XML11Char;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.NamespaceSupport;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.purl.rvl.tooling.codegen.rdfreactor.OntologyFile;
import org.purl.rvl.tooling.process.ExampleData;
import org.purl.rvl.tooling.process.ExampleMapping;
import org.purl.rvl.tooling.process.FileRegistry;
import org.purl.rvl.tooling.process.OGVICProcess;
import org.purl.rvl.tooling.util.ModelUtils;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.vocabulary.XSD;


/**
 * @author Jan Polowinski
 *
 */
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
			ModelUtils.readFromAnySyntax(modelRVLSchema,OntologyFile.RVL);
			ModelUtils.readFromAnySyntax(modelVISO,OntologyFile.VISO_GRAPHIC);
			ModelUtils.readFromAnySyntax(modelMappings,ExampleMapping.RVL_EXAMPLE);
			ModelUtils.readFromAnySyntax(modelData,ExampleData.RVL_EXAMPLE); 
			
			modelMappings.addModel(modelRVLSchema); // TODO temp hack!
			//modelMappings.addModel(modelVISO); // TODO temp hack!
			//modelMappings.addModel(modelData); // TODO temp hack!
			
		} catch (Exception e) {
			LOGGER.severe("Problem reading one of the RDF files into a model: " + e);
		}
	}
	
	
	/** Extra model for VISO
	 * 
	 */
	public void initVISOModel() {
		modelVISO = RDF2Go.getModelFactory().createModel(Reasoning.none); // no reasoning seems to be OK here 
		// temp. turned on reasoning to allow for reasoning that linking_node subPropertyOf linkingDirected_endNode, 
		// however that does not seem to work due to other issues (e.g. Linking_Directed is not a subclass of Linking_Undirected) anyway.
		
		modelVISO.open();
		String visoFileName = OntologyFile.VISO_GRAPHIC;
		ModelUtils.readFromAnySyntax(modelVISO,visoFileName);
		LOGGER.info("Read VISO-graphic into VISO model: " + visoFileName);
	
		modelSet.addModel(modelVISO, OGVICProcess.GRAPH_VISO);
	}

	public void initRVLModel() {
		// extra model for RVL (schema)
		modelRVLSchema = RDF2Go.getModelFactory().createModel(Reasoning.none); // no reasoning seems to be OK here
		modelRVLSchema.open();
		String rvlFileName = OntologyFile.RVL;
		ModelUtils.readFromAnySyntax(modelRVLSchema,rvlFileName);
		LOGGER.info("Read RVL schmema into RVL schema model: " + rvlFileName);
		
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
				LOGGER.info("Reading file into mappings model: " + file.getAbsolutePath());
				ModelUtils.readFromAnySyntax(modelMappings,file);
			}
		}

		// add the extra mapping statements that will be inferred based on the RVL schema
		//modelMappings.addModel(ModelUtils.getExtraStatementModel(modelMappings,modelRVLSchema)); // TODO does not work properly when JENA used. Works as expected for SESAME Impl of RDF2GO
		modelMappings.addModel(modelRVLSchema); // for now simply add the whole RVL schema to the mappings model and make it a reasoning model
		
		com.hp.hpl.jena.rdf.model.Model jenaModelMapping = (com.hp.hpl.jena.rdf.model.Model)modelMappings.getUnderlyingModelImplementation();
		LOGGER.info(jenaModelMapping.getNsPrefixMap().toString());
		
		addStandardPrefixesForCommonNamespaces(modelMappings);
		
		jenaModelMapping = (com.hp.hpl.jena.rdf.model.Model)modelMappings.getUnderlyingModelImplementation();
		LOGGER.info(jenaModelMapping.getNsPrefixMap().toString());
			
		modelSet.addModel(modelMappings, OGVICProcess.GRAPH_MAPPING);
		
		ModelSet ms = modelSet;
		Dataset ds =  ((com.hp.hpl.jena.query.Dataset) ms.getUnderlyingModelSetImplementation());

		LOGGER.info(ds.getNamedModel(OGVICProcess.GRAPH_DATA.toString()).getNsPrefixMap().toString());
		LOGGER.info(ds.getNamedModel(OGVICProcess.GRAPH_RVL_SCHEMA.toString()).getNsPrefixMap().toString());
		LOGGER.info(ds.getNamedModel(OGVICProcess.GRAPH_MAPPING.toString()).getNsPrefixMap().toString());
		LOGGER.info(ds.getDefaultModel().getNsPrefixMap().toString());


		
		//modelMappings.addModel(modelVISO); // TODO temp hack!
		//modelMappings.addModel(modelData); // TODO temp hack!
		
		//modelData.addModel(modelRVLSchema); // TODO temp hack!
		//modelData.addModel(modelVISO); // TODO temp hack!
		//modelData.addModel(modelMappings); // TODO temp hack!
		
		// combine models to a model set with different named graph		
	}

	
	
	/**
	 * @param dataFileRegistry
	 * @param reasoning - Reasoning used while building the data model. Note: the final data model will still have reasoning off!
	 */
	/**
	 * @param dataFileRegistry
	 * @param reasoning
	 */
	public void initDataModel(FileRegistry dataFileRegistry, Reasoning reasoning)  {
		
		// clean up
		modelSet.removeModel(OGVICProcess.GRAPH_DATA);
		
		// temp model for data to apply reasoning if desired
		Model reasoningDataModel = RDF2Go.getModelFactory().createModel(reasoning);
		reasoningDataModel.open();
		//ModelUtils.printModelInfo("model data no reasoning", reasoningDataModel, true);

		if (null != dataFileRegistry) {
			for (Iterator<File> iterator = dataFileRegistry.getFiles().iterator(); iterator.hasNext();) {
				File file = (File) iterator.next();
				LOGGER.info("Reading file into temp data model (Reasoning " + reasoning + " ): " + file.getAbsolutePath());
				ModelUtils.readFromAnySyntax(reasoningDataModel,file);
			}
		}
		
		// helper model with RDFS-Triples only
		Model rdfsTriplesModel = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		rdfsTriplesModel.open();
		//ModelUtils.printModelInfo("rdfs triples model", rdfsTriplesModel, true);

		// cleaned data model without RDFS triples (they are not to be visualized!)
		// please note: the final model will have reasoning off!
		Model cleanedDataModel = RDF2Go.getModelFactory().createModel(Reasoning.none);
		cleanedDataModel.open();
		cleanedDataModel.addModel(reasoningDataModel);
		cleanedDataModel.removeAll(rdfsTriplesModel.iterator()); 
		//ModelUtils.printModelInfo("model data cleaned", cleanedDataModel, true);

		modelData = cleanedDataModel; // TODO: still needed?
		
		com.hp.hpl.jena.rdf.model.Model jenaModelData = (com.hp.hpl.jena.rdf.model.Model)modelData.getUnderlyingModelImplementation();
		LOGGER.info(jenaModelData.getNsPrefixMap().toString());

		
		addStandardPrefixesForCommonNamespaces(modelData);
		addStandardPrefixesForCommonNamespaces(modelSet);
		
		jenaModelData = (com.hp.hpl.jena.rdf.model.Model)modelData.getUnderlyingModelImplementation();
		LOGGER.info(jenaModelData.getNsPrefixMap().toString());

		modelSet.addModel(modelData, OGVICProcess.GRAPH_DATA);
		
		addStandardPrefixesForCommonNamespaces(modelSet);
		
		Dataset ds =  ((com.hp.hpl.jena.query.Dataset) modelSet.getUnderlyingModelSetImplementation());
		

		LOGGER.info(ds.getNamedModel(OGVICProcess.GRAPH_DATA.toString()).getNsPrefixMap().toString());
		LOGGER.info(ds.getNamedModel(OGVICProcess.GRAPH_RVL_SCHEMA.toString()).getNsPrefixMap().toString());
		LOGGER.info(ds.getNamedModel(OGVICProcess.GRAPH_MAPPING.toString()).getNsPrefixMap().toString());
		LOGGER.info(ds.getDefaultModel().getNsPrefixMap().toString());

				
		// cache inferred files for speeding up future starts
		// since multiple files may be used to infer the models, 
		// a new file name per use case / project is needed
	}
	
	/**
	 * Adds the preferred prefixes for common namespaces like RDFS, OWL ...
	 * 
	 * @param modelSet - the model to enrich with the default namespace settings
	 */
	private void addStandardPrefixesForCommonNamespaces(NamespaceSupport nameSpaceSupportable) {

		nameSpaceSupportable.setNamespace("rdf", RDF.RDF_NS);
		nameSpaceSupportable.setNamespace("rdfs", RDFS.RDFS_NS);
		nameSpaceSupportable.setNamespace("owl", OWL.OWL_NS);
		nameSpaceSupportable.setNamespace("xsd", org.ontoware.rdf2go.vocabulary.XSD.XS_URIPREFIX);
		
		Map<String, String> namespace = nameSpaceSupportable.getNamespaces();
		
		LOGGER.info("Added standard prefixes to model/modelSet " + nameSpaceSupportable + NL + 
					MapUtils.toProperties(namespace).toString());
		
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
		// modelAVM.addModel(modelVISO); enable when needed (cf. comment at VISO model)
		
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
			LOGGER.info("Read AVM from file: " + fileName);
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
