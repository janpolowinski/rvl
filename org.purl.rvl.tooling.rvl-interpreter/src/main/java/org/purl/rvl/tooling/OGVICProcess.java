package org.purl.rvl.tooling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.purl.rvl.java.viso.graphic.GraphicObject;
import org.purl.rvl.java.viso.graphic.GraphicSpace;
import org.purl.rvl.tooling.avm.D3GeneratorBase;
import org.purl.rvl.tooling.avm.D3GeneratorSimpleJSON;
import org.purl.rvl.tooling.avm.D3JacksonBinding;
import org.purl.rvl.tooling.avm.ExampleAVMBuilder;
import org.purl.rvl.tooling.util.AVMUtils;
import org.purl.rvl.tooling.util.CustomRecordFormatter;


public class OGVICProcess {
	
	protected static Model model;
	protected static Model modelVISO;
	protected static ExampleAVMBuilder builder;
	protected static D3GeneratorBase d3Generator;
		
	//public static String JSON_FILE_NAME_REL = "../org.purl.rvl.tooling.d3vis/examples/force-layouted-nodes/data.json";
	public static String JSON_FILE_NAME_REL = "../org.purl.rvl.tooling.d3vis/examples/force-directed-graph/data.json";
	
	public static final String REM_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-mappings.ttl";
	public static final String REXD_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-data.ttl";
	public static final String RVL_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl.owl";
	public static final String VISO_LOCAL_REL = "../org.purl.rvl.vocabulary/viso-branch/viso-graphic-inference.ttl";
	
	public static final String GEN_MODEL_FILE_FOLDER = "gen";
	protected static final String TMP_RVL_MODEL_FILE_NAME = GEN_MODEL_FILE_FOLDER + "tempRvl.ttl";
	public static final String TMP_AVM_MODEL_FILE_NAME = GEN_MODEL_FILE_FOLDER + "tempAVM.ttl";


	private final static Logger LOGGER = Logger.getLogger(OGVICProcess.class .getName()); 
	private final static Logger LOGGER_RVL_PACKAGE = Logger.getLogger("org.purl.rvl"); 
	
	static final String NL =  System.getProperty("line.separator");
	
	
    static {
    	  	
		//LOGGER.setLevel(Level.SEVERE); 
		//LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.SEVERE); 
		LogManager.getLogManager().getLogger(LOGGER_RVL_PACKAGE.getName()).setLevel(Level.FINE);

		
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
	public static void main(String[] args) {
		
		initRDF2GoModels();

		///*
		builder = new ExampleAVMBuilder(model, modelVISO);
		builder.createTestGraphicObjects();
		builder.createTestLinkingDirectedRelations();
		//*/
		
		SimpleRVLInterpreter rvlInterpreter = new SimpleRVLInterpreter(model, modelVISO);
		rvlInterpreter.interpretSimpleP2GArvlMappings();
		rvlInterpreter.interpretP2GO2ORMappings();
		rvlInterpreter.interpretResourceLabelAsGOLabelForAllCreatedResources();		
		
		
		d3Generator = new D3GeneratorSimpleJSON(model, modelVISO);
		String json = ((D3GeneratorSimpleJSON)d3Generator).generateJSONforD3();
		LOGGER.fine("JSON data is: " + NL +  json);
		d3Generator.writeJSONToFile(json);
		d3Generator.writeAVMToFile();
		

		/*
		d3Generator = new D3JacksonBinding(model, modelVISO);
		Set<GraphicObject> goTestSet = builder.createTestGraphicObjects();
		GraphicSpace graphicSpace = new GraphicSpace(goTestSet);
		try {
			((D3JacksonBinding)d3Generator).writeGraphicSpaceAsJSON(graphicSpace, new File(JSON_FILE_NAME_REL));
		} catch (Exception e) {
			LOGGER.info("Problem writing to JSON file");
			e.printStackTrace();
		}
		*/
		

		//AVMUtils.listAllGOs(model);
		//AVMUtils.listAllColors(modelVISO);
		
		//writeRDFModelToFile(model,TMP_AVM_MODEL_FILE_NAME);
		
	    // close the model
	    // model.close();
	    // -NO!!! since there is more than one Thread, close would be performed before the data is added to the model, resulting in a NullPointerException of the RDF2GO model

	}



	private static void initRDF2GoModels() {
		model = ModelBuilder.getModel();
		modelVISO = ModelBuilder.getVISOModel();
	}
	
	/**
	 * Write a model to file in Turtle serialisation
	 * @param fileName
	 */
	private static void writeRDFModelToFile(Model modelToWrite, String fileName) {
		try {
		 FileWriter writer = new FileWriter(fileName);
		 modelToWrite.writeTo(writer, Syntax.Turtle);
		} catch (IOException e) {
		 e.printStackTrace();
		}
	}

}
