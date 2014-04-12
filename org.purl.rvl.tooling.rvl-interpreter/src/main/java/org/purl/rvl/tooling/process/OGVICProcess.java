package org.purl.rvl.tooling.process;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.purl.rvl.tooling.ModelBuilder;
import org.purl.rvl.tooling.avm.D3GeneratorBase;
import org.purl.rvl.tooling.avm.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.rvl2avm.RVLInterpreterBase;
import org.purl.rvl.tooling.rvl2avm.SimpleRVLInterpreter;
import org.purl.rvl.tooling.util.CustomRecordFormatter;


public class OGVICProcess {
	
	private static OGVICProcess instance = null;
	
	// SETTINGS
	public static int MAX_GRAPHIC_RELATIONS_PER_MAPPING = 100;
	public static boolean REGENERATE_AVM = true;
	public static boolean WRITE_AVM = false;
	public static boolean WRITE_JSON = true;
	
	// LOCAL RDF FILES
	public static final String RVL_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl.owl";
	public static final String VISO_LOCAL_REL = "../org.purl.rvl.vocabulary/viso-branch/viso-graphic-inference.ttl";
	
	// LOCAL FILES AND FOLDER SETTINGS
	public static final String USE_CASE_FOLDER = "/Users/Jan/Projekte/Beruf/Promotion/Recherche/CaseStudies";
	//public static final String USE_CASE_FOLDER = "/Users/Jan/Documents/EclipseWorkspace/SemVisRecherche/CaseStudies";
	public static final String GEN_MODEL_FILE_FOLDER = "../org.purl.rvl.vocabulary/gen";
	public static final String GEN_MODEL_FILE_FOLDER_D3_JSON = "../org.purl.rvl.tooling.d3vis/gen/json";
	protected static final String TMP_RVL_MODEL_FILE_NAME = GEN_MODEL_FILE_FOLDER + "/" + "tempRvl.ttl";
	public static final String TMP_AVM_MODEL_FILE_NAME = GEN_MODEL_FILE_FOLDER + "/" + "tempAVM.ttl";
	
	// GRAPH URIs
	public static final URI GRAPH_MAPPING = new URIImpl("http://purl.org/rvl/example/mapping/");
	public static final URI GRAPH_DATA = new URIImpl("http://purl.org/rvl/example/data/");
	public static final URI GRAPH_RVL_SCHEMA = new URIImpl("http://purl.org/rvl/");
	public static final URI GRAPH_VISO = new URIImpl("http://purl.org/viso/");

	// MODELS AND MODELSETS
	protected Model modelAVM;
	
	/*
	protected static Model model;
	protected static Model modelVISO;
	protected static Model modelData;
	protected static Model modelMappings;
	*/
	
	// OTHER MEMBERS
	ModelBuilder modelBuilder;
	private boolean writeAVM = WRITE_AVM;
	//protected static FakeRVLInterpreter avmBuilder;
	protected D3GeneratorBase d3Generator;
	protected RVLInterpreterBase rvlInterpreter;
	private final  FileRegistry ontologyFileRegistry = new FileRegistry(); // RVL, VISO ,...
	private final  FileRegistry dataFileRegistry = new FileRegistry(); // DATA
	private final  FileRegistry mappingFileRegistry = new FileRegistry(); // Mapping files (each interpreted as a mapping set)
	private String uriStart = "";
	private String jsonFileNameRel = "";
	
	private Reasoning reasoningDataModel = Reasoning.rdfs;


	// LOGGING
	private final static Logger LOGGER = Logger.getLogger(OGVICProcess.class.getName()); 
	private final static Logger LOGGER_RVL_PACKAGE = Logger.getLogger("org.purl.rvl"); 
	
	static final String NL =  System.getProperty("line.separator");
	
	
    static {
    	  	
		//LOGGER.setLevel(Level.SEVERE); 
		//LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.SEVERE); 
		LogManager.getLogManager().getLogger(LOGGER_RVL_PACKAGE.getName()).setLevel(Level.FINEST);

		
		// In order to show log entrys of the fine level, we need to create a new handler as well
        ConsoleHandler handler = new ConsoleHandler();
        // PUBLISH this level
        handler.setLevel(Level.FINEST);
        
        CustomRecordFormatter formatter = new CustomRecordFormatter();
        handler.setFormatter(formatter); // out-comment this line to use the normal formatting with method and date
        
        LOGGER_RVL_PACKAGE.setUseParentHandlers(false); // otherwise double output of log entries
        LOGGER_RVL_PACKAGE.addHandler(handler);
		
		/*
		// properties file read, but does not seem to be evaluatet correctly -> no logs shown in console
		Properties preferences = new Properties();
		try {
		    FileInputStream configFile = new FileInputStream("logging.properties");
		    preferences.load(configFile);
		    LogManager.getLogManager().readConfiguration(configFile);
			//LogManager.getLogManager().getLogger(LOGGER_RVL_PACKAGE.getName()).setLevel(Level.ALL);
		} catch (IOException ex)
		{
		    System.out.println("WARNING: Could not open configuration file");
		    System.out.println("WARNING: Logging not configured (console output only)");
		}
		*/
        }
    
    
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	private OGVICProcess() {
	}
	
	public void runOGVICProcess(){
		
		// explicitly specify to use a specific ontology api here:
		 RDF2Go.register( new org.ontoware.rdf2go.impl.jena.ModelFactoryImpl());
		 //RDF2Go.register( new org.openrdf.rdf2go.RepositoryModelFactory() ); // must be called as early as this - too late in modelBuilder // sesame backend causes problems when getting target value lists!! probably because of targetvalues_abstract property ...
		// if not specified, RDF2Go.getModelFactory() looks into your classpath
		// for ModelFactoryImpls to register.

		
		modelBuilder = new ModelBuilder();
		
		if (REGENERATE_AVM) {
			
			// build the RDF models needed for the process
			try {
				modelBuilder.initRDF2GoModels(ontologyFileRegistry, dataFileRegistry, mappingFileRegistry);
			} catch (Exception e) {
				LOGGER.severe("Problem building the model");
				e.printStackTrace();
				return;
			} 
			/*
			model = modelBuilder.getModel();
			modelVISO = modelBuilder.getVISOModel();
			*/
			
			modelAVM = modelBuilder.getAVMModel();
			
			// create and set an interpreter, if not already set
			if (null==rvlInterpreter) {
				LOGGER.warning("RVL interpreter was not set, using default one.");
				//rvlInterpreter = new FakeRVLInterpreter();
				rvlInterpreter = new SimpleRVLInterpreter();
			}
			rvlInterpreter.init(
					//getModel(),
					getModelAVM(),
					getModelSet()
					);
			
			// interprete RVL mappings
			interpreteRVL2AVM();	
			
		}
		else if (WRITE_JSON) {
			readAVMFromFile(modelBuilder);
			modelBuilder.initVISOModel(ontologyFileRegistry);
			//modelVISO = modelBuilder.getVISOModel();
		}
		
		if (WRITE_JSON) {
		
			// create and set a generator, if not already set
			if (null==d3Generator) {
				LOGGER.warning("JSON generator was not set, using default one.");
				setD3Generator(new D3GeneratorSimpleJSON());
			}
			d3Generator.init(getModelAVM());
	
			
			// transform AVM 2 JSON
			String json = transformAVM2JSON();
			LOGGER.info("JSON data is: " + NL +  json);
			d3Generator.writeJSONToFile(json);
			
		}
			
		// write the AVM to a file (this is done in the end, since it takes much time)
		if (REGENERATE_AVM && writeAVM) {
			writeAVMToFile();
		}
		


		
	    // close the model
	    // model.close();
	    // -NO!!! since there is more than one Thread, close would be performed before the data is added to the model, resulting in a NullPointerException of the RDF2GO model
	}

	private void readAVMFromFile(ModelBuilder modelBuilder) {
		LOGGER.info("AVM regeneration OFF! Will not interpret any new mappings, but load AVM from " + TMP_AVM_MODEL_FILE_NAME);	
		modelBuilder.initFromTmpAVMFile();
		modelAVM = modelBuilder.getAVMModel();
	}

	private void interpreteRVL2AVM() {
		rvlInterpreter.interpretMappings();
	}

	private String transformAVM2JSON() {
		String json = "";
		json = d3Generator.generateJSONforD3();
		return json;
	}

	/**
	 * Write a model to file in Turtle serialisation
	 * @param fileName
	 */
	private static void writeModelToFile(Model modelToWrite, String fileName) {
		
		try {
			
			FileWriter writer = new FileWriter(fileName);
			modelToWrite.writeTo(writer, Syntax.Turtle);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the whole Model to a tmp file 
	 * TODO: does not currently filter out non-avm triples!
	 */
	public void writeAVMToFile() {
	
		try {
			
			String fileName = OGVICProcess.TMP_AVM_MODEL_FILE_NAME;
			FileWriter writer = new FileWriter(fileName);
			
			getModelAVM().writeTo(writer, Syntax.Turtle);
			writer.flush();
			writer.close();
			
			LOGGER.info("AVM written to " + fileName + " as Turtle");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void setD3Generator(D3GeneratorBase d3Generator) {
		this.d3Generator = d3Generator;
		this.setJsonFileNameRel(GEN_MODEL_FILE_FOLDER_D3_JSON + "/" + d3Generator.getGenJSONFileName());
	}

	
	/**
	 * @return the rvlInterpreter
	 */
	public RVLInterpreterBase getRvlInterpreter() {
		return rvlInterpreter;
	}

	/**
	 * @param rvlInterpreter the rvlInterpreter to set
	 */
	public void setRvlInterpreter(RVLInterpreterBase rvlInterpreter) {
		this.rvlInterpreter = rvlInterpreter;
	}

	public void registerMappingFile(String fileName){
		this.mappingFileRegistry.addFile(fileName);
	}
	
	public void registerOntologyFile(String fileName){
		this.ontologyFileRegistry.addFile(fileName);
	}
	
	public void registerDataFile(String fileName){
		this.dataFileRegistry.addFile(fileName);
	}


	/**
	 * @return the uriStart
	 */
//	public String getUriStart() {
//		return uriStart;
//	}

	/**
	 * @param uriStart the uriStart to set
	 */
	public void setUriStart(String uriStart) {
		this.uriStart = uriStart;
	}

	/**
	 * @return the jsonFileNameRel
	 */
	public String getJsonFileNameRel() {
		return jsonFileNameRel;
	}

	/**
	 * @param jsonFileNameRel the jsonFileNameRel to set
	 */
	public void setJsonFileNameRel(String jsonFileNameRel) {
		this.jsonFileNameRel = jsonFileNameRel;
	}

	public static OGVICProcess getInstance() {
		if (instance == null) {
            instance = new OGVICProcess();
        }
        return instance;
	}

	public Model getModelVISO() {
		return modelBuilder.getVISOModel();
	}
	
	public Model getModelData() {
		return modelBuilder.getDataModel();
	}
	
	public Model getModelMappings() {
		return modelBuilder.getMappingsModel();
	}
	
	/*
	public Model getModel() {
		return modelBuilder.getModel();
	}*/
	
	public ModelSet getModelSet() {
		return modelBuilder.getModelSet();
	}
	
	public Model getModelAVM() {
		return this.modelAVM;
	}

	/**
	 * @param writeAVM the writeAVM to set
	 */
	public void setWriteAVM(boolean writeAVM) {
		this.writeAVM = writeAVM;
	}

	/**
	 * Turns reasoning on the data model on/off.
	 * @param reasoningDataModel
	 */
	public void setReasoningDataModel(Reasoning reasoningDataModel) {
		this.reasoningDataModel = reasoningDataModel;
	}
	
	public Reasoning getReasoningDataModel() {
		return this.reasoningDataModel;
	}

}
