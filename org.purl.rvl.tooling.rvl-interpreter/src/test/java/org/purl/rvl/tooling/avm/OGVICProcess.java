package org.purl.rvl.tooling.avm;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.purl.rvl.interpreter.viso.graphic.Color;
import org.purl.rvl.interpreter.viso.graphic.GraphicObject;


public class OGVICProcess {
	
	protected static Model model;
	protected static Model modelVISO;
	protected static ExampleAVMBuilder builder;
	protected static D3GeneratorBase d3Generator;
	
	protected static final String tmpAvmModelFileName = "avm.ttl";
	//protected static String jsonFileRelName = "../org.purl.rvl.tooling.d3vis/examples/force-layouted-nodes/data.json";
	protected static String jsonFileRelName = "../org.purl.rvl.tooling.d3vis/examples/force-directed-graph/data.json";
	
	public static final String REM_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-mappings.ttl";
	public static final String REXD_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl-example-data.ttl";
	public static final String RVL_LOCAL_REL = "../org.purl.rvl.vocabulary/rvl.owl";
	public static final String VISO_LOCAL_REL = "../org.purl.rvl.vocabulary/viso-branch/viso-graphic-inference.ttl";

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
		
		initRDF2GoModel();

		//builder = new ExampleAVMBuilder(model, modelVISO);
		//builder.createTestGraphicObjects();
		//builder.createTestLinkingDirectedRelations();
		
		
		
		//SimpleRVLInterpreter rvlInterpreter = new SimpleRVLInterpreter(model, modelVISO);
		//rvlInterpreter.interpretSimpleP2GArvlMappings();
		//rvlInterpreter.interpretP2GO2ORMappings();
		//rvlInterpreter.interpretResourceLabelAsGOLabelForAllCreatedResources();		
		
		/*
		d3Generator = new D3GeneratorSimpleJSON(model, modelVISO);
		String json = ((D3GeneratorSimpleJSON)d3Generator).generateJSONforD3();
		LOGGER.fine("JSON data is: " + NL +  json);
		d3Generator.writeJSONToFile(json);
		//d3Generator.writeAVMToFile();
		*/

		/*
		d3Generator = new D3JacksonBinding(model, modelVISO);
		Set<GraphicObject> goTestSet = builder.createTestGraphicObjects();
		GraphicSpace graphicSpace = new GraphicSpace(goTestSet);
		try {
			((D3JacksonBinding)d3Generator).writeGraphicSpaceAsJSON(graphicSpace, new File(jsonFileRelName));
		} catch (Exception e) {
			LOGGER.info("Problem writing to JSON file");
			e.printStackTrace();
		}
		*/

		//listAllGOs();
		listAllColors();
		
	}
	

	private static void listAllColors() {
		System.out.println("List of all colors in the VISO model:");
		System.out.println();
		
		ClosableIterator<? extends org.purl.rvl.interpreter.gen.viso.graphic.Color> goIt = 
				org.purl.rvl.interpreter.gen.viso.graphic.Color.getAllInstances_as(modelVISO).asClosableIterator();
		while (goIt.hasNext()) {
			Color color = (Color) goIt.next().castTo(Color.class);
			LOGGER.info(color.toString());
		}	
	}


	protected static void initRDF2GoModel() throws ModelRuntimeException {
		// explicitly specify to use a specific ontology api here:
		// RDF2Go.register( new org.ontoware.rdf2go.impl.jena.ModelFactoryImpl());
		// RDF2Go.register( new org.openrdf.rdf2go.RepositoryModelFactory() );
		// if not specified, RDF2Go.getModelFactory() looks into your classpath
		// for ModelFactoryImpls to register.
	
		// create the RDF2GO Models
		model = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		model.open();
		modelVISO = RDF2Go.getModelFactory().createModel(Reasoning.rdfs);
		modelVISO.open();
	
			try {
				modelVISO.readFrom(new FileReader(VISO_LOCAL_REL),
						Syntax.Turtle);
				model.readFrom(new FileReader(VISO_LOCAL_REL),
						Syntax.Turtle);
				model.readFrom(new FileReader(RVL_LOCAL_REL),
						Syntax.RdfXml);
				model.readFrom(new FileReader(REXD_LOCAL_REL),
						Syntax.Turtle);
				model.readFrom(new FileReader(REM_LOCAL_REL),
						Syntax.Turtle);
			} catch (IOException e) {
				LOGGER.severe("Problem reading one of the RDF files into the model: " + e);
			}
			
			ExampleAVMBuilder builder = new ExampleAVMBuilder(model,modelVISO);
	
	}
	
	/**
	 * List all Graphic Objects in the model
	 */
	protected static void listAllGOs() {	
		
		System.out.println("List of all Graphic Objects in the model:");
		System.out.println();
		
		ClosableIterator<? extends org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject> goIt = 
				org.purl.rvl.interpreter.gen.viso.graphic.GraphicObject.getAllInstances_as(model).asClosableIterator();
		while (goIt.hasNext()) {
			GraphicObject go = (GraphicObject) goIt.next().castTo(GraphicObject.class);
			System.out.println(go);
		}		
	}
	
	/**
	 * List all Resources in the model
	 */
	private static void listAllResourcesWithTheirTypes(){	
		ClosableIterator<? extends Resource> resIt = 
			Resource.getAllInstance_as(model).asClosableIterator();
		while (resIt.hasNext()) {
			Resource res = (Resource) resIt.next();
	
			LOGGER.info(res.toString());
			//LOGGER.info("Types:" + go.getAllType_as().asArray()[0].asURI());
			
			for (org.ontoware.rdfreactor.schema.rdfs.Class type : res.getAllType_as().asList()) {
				try {
					LOGGER.info("T: " + type.asURI());
				} catch (ClassCastException e) {
					LOGGER.severe("evtl. blanknote");
				}
			}
		}		
	}

}
