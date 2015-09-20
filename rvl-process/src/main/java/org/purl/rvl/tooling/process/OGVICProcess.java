package org.purl.rvl.tooling.process;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.purl.rvl.exception.D3GeneratorException;
import org.purl.rvl.exception.EmptyGeneratedException;
import org.purl.rvl.exception.JsonExceptionWrapper;
import org.purl.rvl.exception.OGVICModelsException;
import org.purl.rvl.exception.OGVICProcessException;
import org.purl.rvl.exception.OGVICRepositoryException;
import org.purl.rvl.exception.OGVICSystemInitException;
import org.purl.rvl.tooling.avm2d3.D3Generator;
import org.purl.rvl.tooling.avm2d3.D3GeneratorDeepLabelsJSON;
import org.purl.rvl.tooling.codegen.rdfreactor.OntologyFile;
import org.purl.rvl.tooling.commons.FileRegistry;
import org.purl.rvl.tooling.model.ModelManager;
import org.purl.rvl.tooling.rvl2avm.RVLInterpreter;
import org.purl.rvl.tooling.rvl2avm.SimpleRVLInterpreter;

import com.cedarsoftware.util.io.JsonWriter;


/**
 * @author Jan Polowinski
 *
 */
public class OGVICProcess {
	
	private static OGVICProcess instance = null;
	
	public static boolean REGENERATE_AVM = true;
	public static boolean WRITE_AVM = true;
	public static boolean WRITE_MAPPING_MODEL = false;
	public static boolean WRITE_JSON = true;
	
	
	// TMP LOCAL FILES AND FOLDER SETTINGS
	//public static String USE_CASE_FOLDER = ""; // now use cases in examples project ; now set in properties-file
	public static final String GEN_MODEL_FILE_FOLDER = "gen";
	protected static final String TMP_RVL_MODEL_FILE_NAME = GEN_MODEL_FILE_FOLDER + "/" + "tempRvl.ttl";
	protected static final String TMP_MAPPING_MODEL_FILE_NAME = GEN_MODEL_FILE_FOLDER + "/" + "tempMappingModel.ttl";
	public static final String TMP_AVM_MODEL_FILE_NAME = GEN_MODEL_FILE_FOLDER + "/" + "tempAVM.ttl";
	
	// OTHER MEMBERS
	private ModelManager modelManager;
	protected VisProject currentProject;
	private VisProject previousVisualizedProject;
	//protected static FakeRVLInterpreter avmBuilder;
	protected D3Generator d3Generator;
	protected RVLInterpreter rvlInterpreter;
	protected String d3GraphicFile;

	private final  FileRegistry ontologyFileRegistry = new FileRegistry("ontology files"); // RVL, VISO_GRAPHIC ,...
	private final  FileRegistry dataFileRegistry = new FileRegistry("data files"); // DATA
	private final  FileRegistry mappingFileRegistry = new FileRegistry("mapping files"); // Mapping files (each interpreted as a mapping set)
	
	
	private boolean writeAVM = WRITE_AVM;
	private boolean writeMappingModel = WRITE_MAPPING_MODEL;
	private Reasoning reasoningDataModel = Reasoning.rdfs;

	private VisProject amvBootstrappingProject;

	// LOGGING
	private final static Logger LOGGER = Logger.getLogger(OGVICProcess.class.getName()); 
//	private final static Logger LOGGER_RVL_PACKAGE = Logger.getLogger("org.purl.rvl"); 
	
	static final String NL =  System.getProperty("line.separator");
	
    static {
  	
    	// now all done in logging.properties file:
    	/*
		//LOGGER.setLevel(Level.SEVERE); 
		LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.SEVERE); 
		//LogManager.getLogManager().getLogger(LOGGER_RVL_PACKAGE.getName()).setLevel(Level.FINER);

		
		// In order to show log entrys of the fine level, we need to create a new handler as well
        ConsoleHandler handler = new ConsoleHandler();
        // PUBLISH this level
        handler.setLevel(Level.FINEST);
        
        CustomRecordFormatter formatter = new CustomRecordFormatter();
        //handler.setFormatter(formatter); // out-comment this line to use the normal formatting with method and date
        
        LOGGER_RVL_PACKAGE.setUseParentHandlers(false); // otherwise double output of log entries
        LOGGER_RVL_PACKAGE.addHandler(handler);
		
		*/
    	
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
	 * @throws OGVICRepositoryException 
	 * @throws IOException 
	 */
	private OGVICProcess() throws OGVICRepositoryException, OGVICSystemInitException {
		init();
	}
	
	public static OGVICProcess getInstance() throws OGVICRepositoryException, OGVICSystemInitException {
		if (instance == null) {
	        instance = new OGVICProcess();
	    }
	    return instance;
	}

	private void init() throws OGVICRepositoryException, OGVICSystemInitException {
		
		// explicitly specify to use a specific ontology api here:
		 RDF2Go.register( new org.ontoware.rdf2go.impl.jena.ModelFactoryImpl());
		 //RDF2Go.register( new org.openrdf.rdf2go.RepositoryModelFactory() ); // must be called as early as this - too late in modelManager // sesame backend causes problems when getting target value lists!! probably because of targetvalues_abstract property ...
		// if not specified, RDF2Go.getModelFactory() looks into your classpath
		// for ModelFactoryImpls to register.

		modelManager = ModelManager.getInstance();
		modelManager.initInternalModels();
		initAVMBootstrappingProject();
	}
	
	public void initDataAndMappingsModel(VisProject project) throws OGVICRepositoryException {
		modelManager.initDataModel(project.getDataFileRegistry(),project.getReasoningDataModel());
		modelManager.initMappingsModel(project.getMappingFileRegistry());
	}
	
	public void loadProject(VisProject project) throws OGVICRepositoryException {
		
		this.currentProject = project;
		
		LOGGER.finest("Clearing internal models (AVM, data, mappings)");
		
		modelManager.clearMappingAndDataModels();
		modelManager.clearAVMModel();
		setDefaultInterpreter();
		setDefaultD3Generator();
		
		/*
		if (null !=  getModelAVM() && 
			null != getModelData() && 
			null != getModelMappings() &&
			getModelAVM().isEmpty() && 
			getModelData().isEmpty() && 
			getModelMappings().isEmpty()
		) {
			LOGGER.finest("Internal models are empty now.");
			Model model = getModelSet().getModel(OGVICProcess.GRAPH_DATA);
			if (model.isEmpty()) {
				LOGGER.finest("data model in model set is empty");
			}
		}*/

		// build the RDF models needed for the process
		initDataAndMappingsModel(project);
		
		// try to get interpreter from project
//		if (null != project.getInterpreter()){
//			setD3Interpreter(project.getInterpreter());
//		}
		
		// try to get generator from project
		if (null != project.getD3Generator()) {
			// TODO pass project to generator
			D3Generator d3Generator = project.getD3Generator();
			String graphicType = project.getDefaultGraphicType();
			if (null != graphicType && !graphicType.isEmpty()) {
				d3Generator.setGraphicType(graphicType);
			}
			d3Generator.setGraphicID(project.getId());
			setD3Generator(d3Generator);
		}

		// try to get html file for d3 rendering from project
		if (null != project.getDefaultGraphicType()){
			setD3GraphicFile(project.getD3GraphicFile());
		} else if (null != d3Generator) {
			setD3GraphicFile(d3Generator.getD3GraphicFile());
		} else {
			LOGGER.severe("D3 html file was not set, using default one.");
			System.exit(0);
		}
		
		previousVisualizedProject = project;
		
	}

	private void readAVMFromFile(ModelManager modelBuilder) {
		LOGGER.info("AVM regeneration OFF! Will not interpret any new mappings, but load AVM from " + TMP_AVM_MODEL_FILE_NAME);	
		modelBuilder.initAVMModelFromFile(OGVICProcess.TMP_AVM_MODEL_FILE_NAME);
	}

	/**
	 * Saves the AVM Model to a tmp file 
	 * TODO: does not currently filter out non-avm triples!
	 */
	public void writeAVMToFile() {
	
		modelManager.writeAVMToFile(OGVICProcess.TMP_AVM_MODEL_FILE_NAME);
	}
	
	/**
	 * Saves the mapping model to a tmp file 
	 */
	public void writeMappingModelToFile() {
	
		try {
			String fileName = OGVICProcess.TMP_MAPPING_MODEL_FILE_NAME;
			FileWriter writer = new FileWriter(fileName);
			
			getModelMappings().writeTo(writer, Syntax.Turtle);
			writer.flush();
			writer.close();
			
			LOGGER.info("Mapping model written to " + fileName + " as Turtle");
			
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Couldn't write mapping model to file: " + e.getMessage(),
					e.getStackTrace());
		}
	}

	public void runOGVICProcess() throws OGVICProcessException {
		if (currentProject.isGenFromAvmDirty()) {
			resetProcess();
			interpreteRVL2AVM();	
			try {
				transformAVMToD3();
//				if (isWriteAVM()) writeAVMToFile();  doesn't work under tomcat, define tmp folder?: http://stackoverflow.com/questions/1969711/best-practice-to-store-temporary-data-for-a-webapp
				if (isWriteMappingModel()) writeMappingModelToFile();
			} catch (D3GeneratorException | OGVICModelsException e) {
				throw new OGVICProcessException("Couldn't run process. " + e.getMessage(), e);
			}
		} else {
			LOGGER.info("Returning old JSON generated from the AVM without running the transformations, since no changes could be detected.");
		}
	}

	public void runOGVICProcessForTesting() throws D3GeneratorException, OGVICModelsException {
		interpreteRVL2AVM();	
		transformAVMToD3();
	}
	
	/**
	 * Reset the artifacts generated during the last process run to avoid that old stuff will be returned.
	 * TODO: Should the process really persist at all?
	 */
	private void resetProcess() {
		// not necessary now, since artifacts stored in VisProject atm
	}

	private void interpreteRVL2AVM() {
		rvlInterpreter.init(modelManager);
		rvlInterpreter.interpretMappings();
	}

	/**
	 * TODO: This method now returns the JSON to be used on tests. 
	 * Writing it to a file at the same time can be seen as an unwanted side-effect now.
	 * 
	 * @return - the AVM as JSON to be used as d3 "data"
	 * @throws D3GeneratorException 
	 * @throws OGVICModelsException 
	 */
	private void transformAVMToD3() throws D3GeneratorException, OGVICModelsException {
		d3Generator.init(modelManager);
		String generatedD3json = d3Generator.generateJSONforD3();
		try {
			generatedD3json = JsonWriter.formatJson(generatedD3json);
		} catch (IOException e) {
			LOGGER.warning("problem with pretty printing JSON (skipped) : " + e.getMessage());
		}
		LOGGER.fine("generated D3-JSON data is: " + NL +  generatedD3json);
		currentProject.setGeneratedD3json(generatedD3json);
//		d3Generator.writeJSONToFile(generatedD3json, getJsonFileNameRel()); // doesn't work on tomcat, only needed for static vis
	}


	
	private void setDefaultInterpreter() {
		LOGGER.info("Setting interpreter to default.");
		// rvlInterpreter = new FakeRVLInterpreter();
		setRvlInterpreter(new SimpleRVLInterpreter());
	}

	private void setDefaultD3Generator() {
		LOGGER.info("Setting D3-generator to default.");
		setD3Generator(new D3GeneratorDeepLabelsJSON());
	}

	/**
	 * @return the rvlInterpreter
	 */
	public RVLInterpreter getRvlInterpreter() {
		return rvlInterpreter;
	}

	/**
	 * @param rvlInterpreter the rvlInterpreter to set
	 */
	public void setRvlInterpreter(RVLInterpreter rvlInterpreter) {
		this.rvlInterpreter = rvlInterpreter;
	}

	public void setD3Generator(D3Generator d3Generator) {
		this.d3Generator = d3Generator;
	}

	public void registerMappingFile(String fileName) throws OGVICRepositoryException {
		try {
			this.mappingFileRegistry.addFile(fileName);
		} catch (FileNotFoundException e) {
			throw new OGVICRepositoryException("mapping repository", e.getMessage() , e);
		}
	}
	
	public void registerOntologyFile(String fileName) throws OGVICRepositoryException {
		try {
			this.ontologyFileRegistry.addFile(fileName);
		} catch (FileNotFoundException e) {
			throw new OGVICRepositoryException("ontology repository", e.getMessage() , e);
		}
	}
	
	public void registerDataFile(String fileName) throws OGVICRepositoryException {
		try {
			this.dataFileRegistry.addFile(fileName);
		} catch (FileNotFoundException e) {
			throw new OGVICRepositoryException("data repository", e.getMessage() , e);
		}
	}

	public Model getModelVISO() {
		return modelManager.getVISOModel();
	}
	
	public Model getModelData() {
		return modelManager.getDataModel();
	}
	
	public Model getModelMappings() {
		return modelManager.getMappingsModel();
	}
	
	/*
	public Model getModel() {
		return modelManager.getModel();
	}*/
	
	public ModelSet getModelSet() {
		return modelManager.getModelSet();
	}
	

	private boolean isWriteAVM() {
		return this.writeAVM;
	}

	private boolean isWriteMappingModel() {
		return this.writeMappingModel;
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

	public String getD3GraphicFile() {
		return d3GraphicFile;
	}

	public void setD3GraphicFile(String d3GraphicFile) {
		this.d3GraphicFile = d3GraphicFile;
	}
	
	
	//////////////////////////////////////////////////////////////////
	// "Bootstrapping" the latest generated AVM (if there is one already)
	///////////////////////////////////////////////////////////////////
	
	/**
	 * Inits the (meta-) visualisation project for the AVM, except for adding data.
	 * The data will be taken from the AVM.
	 * 
	 * @throws FileNotFoundException
	 */
	private void initAVMBootstrappingProject() throws OGVICSystemInitException {
		
		amvBootstrappingProject = new VisProject("avm");
		//avmBootstrap.setWriteAVM(false);
		try {
			amvBootstrappingProject.registerMappingFile(ExampleMapping.AVM_EXAMPLE_BOOTSTRAP);
			amvBootstrappingProject.registerDataFile(ExampleData.AVM_EXTRA_DATA);
			amvBootstrappingProject.setReasoningDataModel(Reasoning.rdfs);
		} catch (FileNotFoundException e) {
			throw new OGVICSystemInitException("Couldn't init the AVM Bootstrapping "
					+ "(used for meta-visualizing the AVM model)", e);
		}
		amvBootstrappingProject.setDescription("Abstract Visual Model (AVM) of the last generated graphic");
//		try {
//			avmBootstrap.registerDataFile(ExampleData.AVM);
//		} catch (FileNotFoundException e) {
//			LOGGER.fine("AVM file was not not found, probably because this is the first run.");
//			avmBootstrap.setDescription("Make sure to run some other project first.");
//		}
		//avmBootstrap.setRvlInterpreter(new SimpleRVLInterpreter());
		amvBootstrappingProject.setD3Generator(new D3GeneratorDeepLabelsJSON());
		//avmBootstrap.setD3Generator(new D3GeneratorTreeJSON());
	}

	/**
	 * Visualizes the last AVM that was created during
	 * a normal (non-AVM) visualization process and returns the result as D3-JSON.
	 * 
	 * @return the AVM as D3-JSON
	 * @throws OGVICProcessException
	 */
	public String runAVMBootstrappingVis() throws OGVICProcessException {
		
		String json = "";
		
		if (null == previousVisualizedProject || previousVisualizedProject.getId() != "avm") {
			
			// prevent avms of avms being visualised: just do the avm bootstrapping
			// if the last project was not an avm bootstrapping project already
		
			try {
				registerOntologyFile(OntologyFile.VISO_GRAPHIC);
				registerOntologyFile(OntologyFile.RVL);
	
				VisProject project = getAVMBootstrappingProject();
				ModelManager manager = ModelManager.getInstance();
				
				manager.savePreviousAVM();
				loadProject(project); // clears the avm and data model!
				manager.addPreviousAvmToDataModel();
	
				runOGVICProcess();
				
			} catch (OGVICRepositoryException | OGVICSystemInitException e1) {
				throw new OGVICProcessException("Could not run the AVM bootstrapping"
						+ " (for meta-visualizing the AVM)", e1);
			}
		
		} else {
			LOGGER.warning("Will use the old AVM, since we do not allow for visualing"
					+ " the AVM of an AVM visualisation.");
		}
		
		try {
			json = currentProject.getGeneratedD3json();
		} catch (EmptyGeneratedException e) {
			LOGGER.warning(JsonExceptionWrapper.wrapAsJSONException(e.getMessage() + " Proceeding anyway"));
		}
		
		return json;
	}

	private VisProject getAVMBootstrappingProject() throws OGVICSystemInitException {
		if (null == this.amvBootstrappingProject) {
			initAVMBootstrappingProject();
		}
		return this.amvBootstrappingProject;
	}

	public String getGeneratedD3json() throws OGVICProcessException, EmptyGeneratedException {
		return currentProject.getGeneratedD3json();
	}

}
