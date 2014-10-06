package org.purl.rvl.tooling.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.purl.rvl.exception.OGVICRepositoryException;
import org.purl.rvl.exception.d3.D3GeneratorException;
import org.purl.rvl.tooling.ModelBuilder;
import org.purl.rvl.tooling.avm2d3.D3Generator;
import org.purl.rvl.tooling.avm2d3.D3GeneratorDeepLabelsJSON;
import org.purl.rvl.tooling.rvl2avm.RVLInterpreter;
import org.purl.rvl.tooling.rvl2avm.SimpleRVLInterpreter;
import org.purl.rvl.tooling.util.CustomRecordFormatter;

import com.cedarsoftware.util.io.JsonWriter;


/**
 * @author Jan Polowinski
 *
 */
public class OGVICProcess {
	
	private static OGVICProcess instance = null;
	
	// SETTINGS
	public static int MAX_GRAPHIC_RELATIONS_PER_MAPPING = 5000;
	public static boolean REGENERATE_AVM = true;
	public static boolean WRITE_AVM = true;
	public static boolean WRITE_MAPPING_MODEL = false;
	public static boolean WRITE_JSON = true;
	
	//public static final String WEB_SERVER_ROOT = "../org.purl.rvl.tooling.d3vis/src/main/resources/web/"; // use for local testing with a webserver reading this dir
	public static final String WEB_SERVER_ROOT = ""; // standard for jar building and deployment
	
	// TMP LOCAL FILES AND FOLDER SETTINGS
	//public static String USE_CASE_FOLDER = ""; // now use cases in examples project ; now set in properties-file
	public static final String GEN_MODEL_FILE_FOLDER = "gen";
	public static final String GEN_MODEL_FILE_FOLDER_D3_JSON = WEB_SERVER_ROOT + GEN_MODEL_FILE_FOLDER + "/" + "json";
	protected static final String TMP_RVL_MODEL_FILE_NAME = GEN_MODEL_FILE_FOLDER + "/" + "tempRvl.ttl";
	protected static final String TMP_MAPPING_MODEL_FILE_NAME = GEN_MODEL_FILE_FOLDER + "/" + "tempMappingModel.ttl";
	public static final String TMP_AVM_MODEL_FILE_NAME = GEN_MODEL_FILE_FOLDER + "/" + "tempAVM.ttl";
	public static final String D3_HTML_FOLDER_NAME = WEB_SERVER_ROOT + GEN_MODEL_FILE_FOLDER + "/" + "html";
	
	// FOLDERS TO CALL WITHIN JARS
	private static final String D3_EXAMPLE_GRAPHICS_FOLDER_NAME = "/web/example-html";
	
	
	// GRAPH URIs
	public static final URI GRAPH_MAPPING = new URIImpl("http://purl.org/rvl/example/mapping/");
	public static final URI GRAPH_DATA = new URIImpl("http://purl.org/rvl/example/data/");
	public static final URI GRAPH_RVL_SCHEMA = new URIImpl("http://purl.org/rvl/");
	public static final URI GRAPH_VISO = new URIImpl("http://purl.org/viso/");
	public static final URI GRAPH_AVM = new URIImpl("http://purl.org/rvl/avm/");

	// MODELS AND MODELSETS
	protected Model modelAVM;
	
	// OTHER MEMBERS
	ModelBuilder modelBuilder;
	String generatedD3json;

	//protected VisProject currentProject; // TODO always use settings from project directly?
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


	// LOGGING
	private final static Logger LOGGER = Logger.getLogger(OGVICProcess.class.getName()); 
	private final static Logger LOGGER_RVL_PACKAGE = Logger.getLogger("org.purl.rvl"); 
	
	static final String NL =  System.getProperty("line.separator");




	
	
    static {
  	
		//LOGGER.setLevel(Level.SEVERE); 
		//LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.SEVERE); 
		LogManager.getLogManager().getLogger(LOGGER_RVL_PACKAGE.getName()).setLevel(Level.FINER);

		
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
        
        /* SETTINGS FROM PROPERTIES-FILE */
        
    	Properties properties = new Properties();
    	
    	try {
    		
    	  properties.load(new FileInputStream("ogvic.properties")); // TODO: this is taken from the maven project, which executes the program, not always from the interpreter project! 
    	  
    	  MAX_GRAPHIC_RELATIONS_PER_MAPPING = Integer.parseInt(properties.get("org.purl.rvl.tooling.max-graphic-relations-per-mapping").toString());
    	  //USE_CASE_FOLDER = properties.get("org.purl.rvl.tooling.use-case-folder").toString();
    	  
    	} catch (Exception e) {
    		
    		LOGGER.severe("Could not load settings from properties-file. Reason: " + e.getMessage());
    		e.printStackTrace();
    		System.exit(0);
    		
    	}
    	
     }
    
    
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	private OGVICProcess() {
		init();
	}
	
	public static OGVICProcess getInstance() {
		if (instance == null) {
	        instance = new OGVICProcess();
	    }
	    return instance;
	}

	private void init() {
		
		// explicitly specify to use a specific ontology api here:
		 RDF2Go.register( new org.ontoware.rdf2go.impl.jena.ModelFactoryImpl());
		 //RDF2Go.register( new org.openrdf.rdf2go.RepositoryModelFactory() ); // must be called as early as this - too late in modelBuilder // sesame backend causes problems when getting target value lists!! probably because of targetvalues_abstract property ...
		// if not specified, RDF2Go.getModelFactory() looks into your classpath
		// for ModelFactoryImpls to register.

		modelBuilder = new ModelBuilder();
		
		try {
			initInternalModels();
		} catch (OGVICRepositoryException e) {
			LOGGER.severe("Problem initialising internal models " +  e.getMessage());
		}
	}
	

	/**
	 * Init models like AVM, RVL and VISO model - not data and mapping models
	 * @throws OGVICRepositoryException 
	 */
	private void initInternalModels() throws OGVICRepositoryException {
		
		//if (REGENERATE_AVM) {
		//	this.modelAVM = modelBuilder.initAVMModel();
		//}
		//else {
		//	this.modelAVM = readAVMFromFile(modelBuilder);
		//}
		
		modelBuilder.initVISOModel();
		this.modelAVM = modelBuilder.initAVMModel();
		modelBuilder.initRVLModel();
		
	}
	
	public void initDataAndMappingsModel(VisProject project) throws OGVICRepositoryException {
		modelBuilder.initDataModel(project.getDataFileRegistry(),project.getReasoningDataModel());
		modelBuilder.initMappingsModel(project.getMappingFileRegistry());
	}
	
	public void loadProject(VisProject project) throws OGVICRepositoryException {
		
		//this.currentProject = project; // TODO: think about just referencing a current project instead of copying all settings to the process
		
		LOGGER.finest("Clearing internal models (AVM, data, mappings)");
		
		modelBuilder.clearMappingAndDataModels();
		modelBuilder.clearAVMModel();
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
		if (null != project.getD3Generator()){
			setD3Generator(project.getD3Generator());
		}

		// try to get html file for d3 rendering from project
		if (null != project.getD3GraphicFile()){
			setD3GraphicFile(project.getD3GraphicFile());
		} else if (null != d3Generator) {
			setD3GraphicFile(d3Generator.getDefaultD3GraphicFile());
		} else {
			LOGGER.severe("D3 html file was not set, using default one.");
			System.exit(0);
		}
		
	}

	private Model readAVMFromFile(ModelBuilder modelBuilder) {
		LOGGER.info("AVM regeneration OFF! Will not interpret any new mappings, but load AVM from " + TMP_AVM_MODEL_FILE_NAME);	
		modelAVM = modelBuilder.initAVMModelFromFile(OGVICProcess.TMP_AVM_MODEL_FILE_NAME);
		return modelAVM;
	}

	/**
	 * Saves the AVM Model to a tmp file 
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
			e.printStackTrace();
		}
	}

	public void runOGVICProcess() throws D3GeneratorException{
		interpreteRVL2AVM();	
		transformAVMToD3();
		populateD3HTMLFolder();
		if (isWriteAVM()) writeAVMToFile();
		if (isWriteMappingModel()) writeMappingModelToFile();
	}
	
	public void runOGVICProcessForTesting() throws D3GeneratorException{
		interpreteRVL2AVM();	
		transformAVMToD3();
	}

	public String getGeneratedD3json() {
		return generatedD3json;
	}


	private void interpreteRVL2AVM() {
		rvlInterpreter.init(getModelAVM(), getModelSet());
		rvlInterpreter.interpretMappings();
	}

	/**
	 * TODO: This method now returns the JSON to be used on tests. 
	 * Writing it to a file at the same time can be seen as an unwanted side-effect now.
	 * 
	 * @return - the AVM as JSON to be used as d3 "data"
	 * @throws D3GeneratorException 
	 */
	private void transformAVMToD3() throws D3GeneratorException {
		d3Generator.init(getModelAVM());
		generatedD3json = d3Generator.generateJSONforD3();
		try {
			generatedD3json = JsonWriter.formatJson(generatedD3json);
		} catch (IOException e) {
			LOGGER.warning("problem with pretty printing JSON (skipped) : " + e.getMessage());
		}
		LOGGER.fine("JSON data is: " + NL +  generatedD3json);
		d3Generator.writeJSONToFile(generatedD3json);
	}

	private void populateD3HTMLFolder() {

		File originLocation = new File (D3_EXAMPLE_GRAPHICS_FOLDER_NAME + "/" + getD3GraphicFile());
		File targetLocation = new File (D3_HTML_FOLDER_NAME + "/index.html");
		
		try {
			
			// TODO get from jar as input stream ...
			
			InputStream htmlFileStream = this.getClass().getResourceAsStream(originLocation.getPath());
			String htmlFileContent = IOUtils.toString(htmlFileStream, "utf-8");
			
			//FileUtils.copyFile(originLocation, targetLocation);
			
			FileWriter writer = new FileWriter(targetLocation);
			writer.write(htmlFileContent);
			writer.flush();
			writer.close();
			
			LOGGER.finer(
					"D3 HTML file copied from " + 
					originLocation.getPath() + 
					" to " + 
					targetLocation.getPath()
					);
			
		} catch (IOException e) {
			LOGGER.severe("Could not copy HTML file for D3: " + e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e1) {
			LOGGER.severe("Could not copy HTML file for D3: " + e1.getMessage());
			e1.printStackTrace();
		}
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

	public void registerMappingFile(String fileName) throws FileNotFoundException{
		this.mappingFileRegistry.addFile(fileName);
	}
	
	public void registerOntologyFile(String fileName) throws FileNotFoundException{
		this.ontologyFileRegistry.addFile(fileName);
	}
	
	public void registerDataFile(String fileName) throws FileNotFoundException{
		this.dataFileRegistry.addFile(fileName);
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

	public String getJsonFileNameRel() {
		return GEN_MODEL_FILE_FOLDER_D3_JSON + "/" + d3Generator.getGenJSONFileName();
	}

	public String getD3GraphicFile() {
		return d3GraphicFile;
	}

	public void setD3GraphicFile(String d3GraphicFile) {
		this.d3GraphicFile = d3GraphicFile;
	}


}
